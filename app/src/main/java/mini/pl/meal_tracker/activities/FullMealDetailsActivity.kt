package mini.pl.meal_tracker.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.widget.*

import kotlinx.android.synthetic.main.activity_full_meal_details.*
import mini.pl.meal_tracker.R
import mini.pl.meal_tracker.data.Food
import mini.pl.meal_tracker.data.Nutrient
import org.jetbrains.anko.doAsync
import java.net.URL

class FullMealDetailsActivity : AppCompatActivity() {

    private var foodImage: TextView? = null
    private var food: Food? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_meal_details)
        setSupportActionBar(toolbarFullId)

        food = intent.getSerializableExtra(CURRENT_FOOD) as Food

        foodImage = findViewById<TextView>(R.id.foodImage)

        val ingredientsLayout = findViewById<LinearLayout>(R.id.ingredientsLayout)

        food?.ingredients?.map { ingredient ->
            val child = layoutInflater.inflate(R.layout.ingredient_view, null)
            child.findViewById<TextView>(R.id.ingredientTextView)?.text = ingredient
            ingredientsLayout.addView(child)
        }

        val recipeUrl = findViewById<Button>(R.id.recipeUrlButton)
        recipeUrl.setOnClickListener {
            goToUrl(food?.instructionsUrl!!)
        }
        if(food?.recipe != null && food?.recipe != "") {
            val recipeTxtView = findViewById<TextView>(R.id.recipe_id)
            recipeTxtView.text = food?.recipe

        }

        foodImage?.text = food!!.title
        bindFood(food!!)
        val nutritionTextView = findViewById<TextView>(R.id.nutritionDescription)
        val filteredNutrients = food!!.nutrients.map { entry -> entry.value }.filter { nutrient -> Nutrient.DefaultNutrientList.contains(nutrient.label)}
        val nutrientsHtml = filteredNutrients.map { nutrient -> "<b>${nutrient.label}</b>: ${String.format("%.2f", nutrient.quantity / food!!.servings)} ${nutrient.unit}<br/>"}.reduce { one, second ->
            one + second
        }
        nutritionTextView.text = Html.fromHtml(nutrientsHtml)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_full_meal_details, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.editFoodIcon) {
            val intent: Intent = Intent(applicationContext, EditFoodActivity::class.java)
            intent.putExtra(EditFoodActivity.ORIGINAL_FOOD, food!!)
            applicationContext.startActivity(intent)
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun bindFood(food: Food) {
        doAsync {
            val im = BitmapFactory.decodeStream(URL(food.imageUrl).openConnection().getInputStream())
            runUI(im, food)
        }
    }

    private fun runUI(im: Bitmap, food: Food) {
        Handler(Looper.getMainLooper()).post {
            foodImage?.background = BitmapDrawable(this.resources, im)
            foodImage?.background?.setColorFilter(Color.rgb(200, 200, 200), android.graphics.PorterDuff.Mode.MULTIPLY)
            foodImage?.setOnLongClickListener {
                val fullImageIntent = Intent(this, FullMealDetailsActivity::class.java)
                fullImageIntent.putExtra(FullMealDetailsActivity.CURRENT_FOOD, food)
                fullImageIntent.putExtra(FullMealDetailsActivity.IMAGE_ID, im)
                this.startActivity(fullImageIntent)
                true
            }
        }

    }

    private fun goToUrl(url: String) {
        val uriUrl = Uri.parse(url)
        val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
        startActivity(launchBrowser)
    }

    companion object {
        const val CURRENT_FOOD = "CURRENT_FOOD"
        const val IMAGE_ID = "CURRENT_FOOD_IMAGE_ID"
    }
}
