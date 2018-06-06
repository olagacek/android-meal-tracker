package mini.pl.meal_tracker.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.View

import kotlinx.android.synthetic.main.activity_search_food.*
import mini.pl.meal_tracker.R
import mini.pl.meal_tracker.adapters.FoodAdapter
import mini.pl.meal_tracker.data.MealDb
import mini.pl.meal_tracker.data.Food
import mini.pl.meal_tracker.services.EdemanService
import org.jetbrains.anko.doAsync

class SearchFoodActivity : AppCompatActivity() {
    private var db: MealDb? = null

    private fun onClickListener(food: Food): View.OnClickListener {
        return View.OnClickListener {
            val intent = Intent()
            intent.putExtra(AddMealActivity.CHOSEN_FOOD, food)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private val service = EdemanService()

    private val adapter = FoodAdapter(false, onClickListener = { food: Food -> onClickListener(food)})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_food)
        setSupportActionBar(toolbar)
        db = MealDb.getInstance(this)
        val searchView = findViewById<SearchView>(R.id.searchView)
        val rv = findViewById<RecyclerView>(R.id.search_food_id)
        rv.adapter = adapter
        rv.layoutManager = GridLayoutManager(rv.context, 2)
        fetchFrequentMeals()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(p0: String?): Boolean {
                doAsync {
                    val food = service.searchFood(p0!!)
                    val dbFood = db?.mealDAO()?.searchFood("%${p0}%")?.map{it.toDomain()}
                   runUI(dbFood!! + food )
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {

                return true
            }
        })

    }

    private fun fetchFrequentMeals() {
        doAsync {
            runUI(db?.mealDAO()?.getTopFood()!!.map{it.toDomain()})
        }
    }

    fun runUI(foods: List<Food>) {
        runOnUiThread {
            adapter.replaceFood(foods)
        }
    }

}
