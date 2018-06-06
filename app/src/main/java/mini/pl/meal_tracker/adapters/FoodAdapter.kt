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
import mini.pl.meal_tracker.activities.FullMealDetailsActivity
import mini.pl.meal_tracker.data.Food
import org.jetbrains.anko.doAsync
import java.net.URL


class FoodAdapter(val isHourEnabled: Boolean, var foods: List<Food> = ArrayList(), val onClickListener: ((Food) -> View.OnClickListener)? = null) : RecyclerView.Adapter<FoodAdapter.FoodHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FoodHolder {
        val context = parent?.context
        val view = LayoutInflater.from(context)?.inflate(R.layout.food_quick_view, parent, false)
        return FoodHolder(view!!)
    }

    override fun getItemCount(): Int {
        return foods.size
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: FoodHolder?, position: Int) {
        holder!!.bindFood(foods[position], isHourEnabled)
    }

    fun replaceFood(food: List<Food>) {
        foods = food
        notifyDataSetChanged()
    }

    inner class FoodHolder(val v: View) : RecyclerView.ViewHolder(v) {
        private val foodImage = v.findViewById<TextView>(R.id.foodImage)
        private val mealTime = v.findViewById<TextView>(R.id.meal_time)
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bindFood(food: Food, visible: Boolean) {
            doAsync {
                val im = BitmapFactory.decodeStream(URL(food.imageUrl).openConnection().getInputStream())
                runUI(im, food, visible)
            }
        }

        private fun runUI(im: Bitmap, food: Food, visible: Boolean) {
            Handler(Looper.getMainLooper()).post {
                foodImage.background = BitmapDrawable(v.resources, im)
                if (visible) mealTime.visibility = View.VISIBLE
                foodImage.text = food.title
                foodImage.background.setColorFilter(Color.rgb(100, 100, 100), android.graphics.PorterDuff.Mode.MULTIPLY)
                foodImage.setOnLongClickListener {
                    val fullImageIntent = Intent(v.context, FullMealDetailsActivity::class.java)
                    fullImageIntent.putExtra(FullMealDetailsActivity.CURRENT_FOOD, food)
                    fullImageIntent.putExtra(FullMealDetailsActivity.IMAGE_ID, im)
                    v.context.startActivity(fullImageIntent)
                    true
                }
                if(onClickListener != null) {
                    foodImage.setOnClickListener(onClickListener.let { it(food) })
                }
            }

        }

    }
}