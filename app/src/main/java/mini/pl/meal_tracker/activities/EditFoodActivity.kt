package mini.pl.meal_tracker.activities

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import mini.pl.meal_tracker.R
import mini.pl.meal_tracker.adapters.IngredientAdapter
import mini.pl.meal_tracker.data.MealDb
import mini.pl.meal_tracker.data.Food
import org.jetbrains.anko.doAsync

class EditFoodActivity : AppCompatActivity() {

    private var adapter: IngredientAdapter? = null
    private var db: MealDb? = null
    private var originalFood: Food? = null
    private var mealNameEditText: EditText? = null
    private var recipeEditText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_food)

        setUpDb()

        val ingredientsListView = findViewById<RecyclerView>(R.id.ingredientsList)

        originalFood = intent.extras.getSerializable(ORIGINAL_FOOD) as Food

        val ingredients = mutableListOf<String>()

        ingredients.addAll(originalFood!!.ingredients)

        adapter = IngredientAdapter(ingredients, this)


        ingredientsListView.adapter = adapter


        val helper = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                if(direction == ItemTouchHelper.LEFT) {
                    adapter?.removeItem(viewHolder!!.adapterPosition)
                }
            }

        }

        val touchHelper = ItemTouchHelper(helper)
        touchHelper.attachToRecyclerView(ingredientsListView)

        val manager = LinearLayoutManager(ingredientsListView.context)
        ingredientsListView.layoutManager = manager
        val dividerItemDecorator = DividerItemDecoration(ingredientsListView.getContext(),
                manager.orientation)
        ingredientsListView.addItemDecoration(dividerItemDecorator)

        recipeEditText = findViewById<EditText>(R.id.mealRecipeEditText)
        recipeEditText?.setText("", TextView.BufferType.EDITABLE)

        mealNameEditText = findViewById<EditText>(R.id.mealNameEditText)
        mealNameEditText?.setText(originalFood!!.title, TextView.BufferType.EDITABLE)
        setUpAddIngredientButton()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_food, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.doneEditingId) {
            doAsync {
                adapter?.ingredients?.let { originalFood?.copy(title = mealNameEditText?.text.toString(), recipe = recipeEditText?.text.toString(),
                        ingredients = it)?.toDto()?.let { db?.mealDAO()?.insertFood(it) } }
            }
            this.finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpAddIngredientButton() {
        val addIngredientButton = findViewById<ImageButton>(R.id.addIngredientId)
        addIngredientButton.setOnClickListener {
            val promptView = LayoutInflater.from(this).inflate(R.layout.prompt, null)
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setView(promptView)
            val userInput = promptView.findViewById<EditText>(R.id.editTextDialogUserInput)
            alertDialogBuilder.setCancelable(false).setPositiveButton("OK", DialogInterface.OnClickListener { dialog, _ ->
                adapter?.addItem(userInput.text.toString())
                dialog.cancel()
            }).setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, _ ->
                dialog.cancel()
            })
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }

    private fun setUpDb() {
        db = MealDb.getInstance(this)
    }

    companion object {
        const val ORIGINAL_FOOD = "ORIGINAL_FOOD"
    }
}
