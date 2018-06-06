package mini.pl.meal_tracker.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mini.pl.meal_tracker.adapters.MealAdapter
import mini.pl.meal_tracker.R
import mini.pl.meal_tracker.data.Meal
import mini.pl.meal_tracker.data.MealDb
import mini.pl.meal_tracker.utils.DateUtilsHelper
import org.jetbrains.anko.doAsync
import java.time.ZonedDateTime


class DayMealsFragment : Fragment() {
    private var date: ZonedDateTime? = null

    private val meals = ArrayList<Meal>()
    private val adapter = MealAdapter(meals)

    private var startDate: Long = 0
    private var endDate: Long = 0

    private var db: MealDb? = null

    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP, ItemTouchHelper.LEFT) {
        override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
            val toRemove = viewHolder?.adapterPosition?.let { adapter.removeMeal(it) }
            toRemove?.let { removeFromDb(it) }
        }

    }
    private val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpDb()
        if (arguments != null) {
            date = DateUtilsHelper.toZonedDateTime(arguments.getLong(DATE))
            startDate = DateUtilsHelper.toLong(DateUtilsHelper.atTheBeginningOfTheDay(date!!))
            endDate = DateUtilsHelper.toLong(DateUtilsHelper.atTheEndOfTheDay(date!!))
            fetchMeals(
                    startDate,
                    endDate
            )
        }

    }

    override fun onResume() {
        fetchMeals(startDate, endDate)
        super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_day_meals, container, false)
        val rv = rootView.findViewById<RecyclerView>(R.id.dayMealViewId)
        rv.layoutManager = LinearLayoutManager(rv.context)
        itemTouchHelper.attachToRecyclerView(rv)

        rv.adapter = adapter
        return rootView
    }

    private fun setUpDb() {
        db = MealDb.getInstance(this.context)
    }

    private fun fetchMeals(startDate: Long, endDate: Long) {
        doAsync {
            val mealData = db?.mealDAO()?.getMealWithFood(startDate, endDate)
            if (mealData != null) {
                activity.runOnUiThread {
                    adapter.replaceMeals(ArrayList(mealData.map { m -> m.toDomain() }))
                }
            }
        }
    }

    private fun removeFromDb(meal: Meal) {
        doAsync {
            db?.mealDAO()?.deleteMeal(meal.toDTO())
        }
    }

    companion object {
        private val DATE = "date"

        fun newInstance(date: Long): DayMealsFragment {
            val fragment = DayMealsFragment()
            val args = Bundle()
            args.putLong(DATE, date)
            fragment.arguments = args
            return fragment
        }
    }
}
