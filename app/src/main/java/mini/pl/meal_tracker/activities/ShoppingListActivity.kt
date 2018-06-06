package mini.pl.meal_tracker.activities

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

import kotlinx.android.synthetic.main.activity_shopping_list.*
import mini.pl.meal_tracker.R
import mini.pl.meal_tracker.adapters.ShopItemAdapter
import mini.pl.meal_tracker.data.MealDb
import mini.pl.meal_tracker.data.ShopItem
import mini.pl.meal_tracker.utils.DateUtilsHelper
import org.jetbrains.anko.doAsync
import java.time.ZonedDateTime

class ShoppingListActivity : AppCompatActivity() {

    private var db: MealDb? = null
    private val items = ArrayList<ShopItem>()
    private val adapter = ShopItemAdapter(items)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list)
        setSupportActionBar(toolbar)

        db = MealDb.getInstance(this)

        val recyclerView = findViewById<RecyclerView>(R.id.shopListRecyclerView)

        recyclerView.adapter = adapter
        setUpItems()
        val manager = LinearLayoutManager(recyclerView.context)
        recyclerView.layoutManager = manager
        val dividerItemDecorator = DividerItemDecoration(recyclerView.getContext(),
                manager.orientation)
        recyclerView.addItemDecoration(dividerItemDecorator)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpItems() {
        doAsync {
            val startDate = DateUtilsHelper.toLong(ZonedDateTime.now())
            val endDate = DateUtilsHelper.toLong(DateUtilsHelper.plusDaysAndMinutes(DateUtilsHelper.atTheEndOfTheDay(ZonedDateTime.now()), 48,0))
            val meals = db?.mealDAO()?.getMealWithFood(startDate, endDate)
            val ingredients = mutableListOf<ShopItem>()
            ingredients.addAll(meals?.flatMap { it.toDomain().food.ingredients }?.map{ item -> ShopItem(item, false)}!!)
            runOnUiThread {
                adapter.replaceIngredients(ingredients)
            }
        }
    }



}
