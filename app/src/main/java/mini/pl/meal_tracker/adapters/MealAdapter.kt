package mini.pl.meal_tracker.adapters

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.annotation.RequiresApi
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import mini.pl.meal_tracker.R
import mini.pl.meal_tracker.activities.AddMealActivity
import mini.pl.meal_tracker.activities.FullMealDetailsActivity
import mini.pl.meal_tracker.data.Meal
import mini.pl.meal_tracker.data.Food
import org.jetbrains.anko.doAsync
import java.net.URL
import java.time.format.DateTimeFormatter


class MealAdapter(var meals: ArrayList<Meal>): RecyclerView.Adapter<MealAdapter.MealHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MealHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.meal_summary_layout, parent, false)
        return MealHolder(v)
    }

    override fun getItemCount(): Int = meals.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MealHolder?, position: Int) {
        holder!!.addMeal(meals[position])
    }

    fun replaceMeals(meals: ArrayList<Meal>) {
        this.meals = meals
        notifyDataSetChanged()
    }

    fun removeMeal(position: Int): Meal {
        val toRemove = meals[position]
        meals.removeAt(position)
        notifyDataSetChanged()
        return toRemove
    }

    class MealHolder(val v: View) : RecyclerView.ViewHolder(v) {
        private val txtView = v.findViewById<TextView>(R.id.nameTextViewID)
        private val foodImage = v.findViewById<TextView>(R.id.foodImage)
        private var mealTimeTxtView: TextView = v.findViewById<TextView>(R.id.meal_time)

        @RequiresApi(Build.VERSION_CODES.O)
        fun addMeal(meal: Meal) {
            val formattedTime = DateTimeFormatter.ofPattern("hh:mm a").format(meal.date)
            mealTimeTxtView.text = "Meal time: $formattedTime"
            txtView?.text = meal.name
            txtView.setOnClickListener { view ->
                val intent = Intent(view.context, AddMealActivity::class.java)
                intent.putExtra(AddMealActivity.MEAL, meal)
                view.context.startActivity(intent)
            }
            txtView
            setUpFood(meal.food)
        }

        private fun setUpFood(food: Food) {
            doAsync {
                val im = BitmapFactory.decodeStream(URL(food.imageUrl).openConnection().getInputStream())
                runUI(im, food, true)
            }
        }


        private fun runUI(im: Bitmap, food: Food, visible: Boolean) {
            Handler(Looper.getMainLooper()).post {
                foodImage.background = BitmapDrawable(v.resources, im)
                foodImage.text = food.title
                foodImage.background.setColorFilter(Color.rgb(100, 100, 100), android.graphics.PorterDuff.Mode.MULTIPLY)
                foodImage.setOnLongClickListener {
                    val fullImageIntent = Intent(v.context, FullMealDetailsActivity::class.java)
                    fullImageIntent.putExtra(FullMealDetailsActivity.CURRENT_FOOD, food)
                    fullImageIntent.putExtra(FullMealDetailsActivity.IMAGE_ID, im)
                    v.context.startActivity(fullImageIntent)
                    true
                }
            }

        }
    }
}

