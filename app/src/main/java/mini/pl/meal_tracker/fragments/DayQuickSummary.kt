package mini.pl.meal_tracker.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import mini.pl.meal_tracker.R
import mini.pl.meal_tracker.activities.AddMealActivity
import mini.pl.meal_tracker.activities.FullMealDetailsActivity
import mini.pl.meal_tracker.data.Meal
import mini.pl.meal_tracker.data.MealDb
import mini.pl.meal_tracker.data.Food
import mini.pl.meal_tracker.utils.DateUtilsHelper
import org.jetbrains.anko.doAsync
import java.net.URL
import java.time.format.DateTimeFormatter


class DayQuickSummary : Fragment() {
    private var db: MealDb? = null
    private var txtView : TextView? = null
    private var foodImage : TextView? = null
    private var mealTimeTxtView: TextView? = null
    private var goalCaloriesView: TextView? = null
    private var consumedCaloriesView: TextView? = null
    private var remainingCaloriesView: TextView? = null

    private var startDate: Long = 0
    private var endDate: Long = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_day_quick_summary, container, false)


        startDate = arguments.getLong("startDate")
        endDate = arguments.getLong("endDate")

        txtView = rootView.findViewById<TextView>(R.id.nameTextViewID)
        foodImage = rootView.findViewById<TextView>(R.id.foodImage)
        mealTimeTxtView = rootView.findViewById(R.id.meal_time)
        goalCaloriesView = rootView.findViewById(R.id.goalCaloriesView)
        consumedCaloriesView = rootView.findViewById(R.id.consumedId)
        remainingCaloriesView = rootView.findViewById(R.id.remainingId)

        setUpDb()
        fetchNextMeal(startDate, endDate)

        setUpCalories(rootView.findViewById(R.id.calories_remaining_txt_view))

        return rootView
    }

    private fun setUpCalories(caloriesView: TextView){
        caloriesView.setOnClickListener {
            val promptView = LayoutInflater.from(this.activity).inflate(R.layout.prompt_calories, null)
            val alertDialogBuilder = AlertDialog.Builder(this.activity)
            alertDialogBuilder.setView(promptView)
            val userInput = promptView.findViewById<EditText>(R.id.editTextDialogUserInput)
            alertDialogBuilder.setCancelable(false).setPositiveButton("OK", DialogInterface.OnClickListener { dialog, _ ->
                val sharedPref = this.activity.getSharedPreferences(SHARED_PREF_CALORIES, Context.MODE_PRIVATE)
                sharedPref.edit().putInt(CALORIES_KEY, userInput.text.toString().toInt()).apply()
                dialog.cancel()
                fetchNextMeal(startDate, endDate)
            }).setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, _ ->
                dialog.cancel()
            })
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        fetchNextMeal(startDate, endDate)
        super.onResume()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchNextMeal(startDate: Long, endDate: Long) {
        doAsync {
            val mealData = db?.mealDAO()?.getMealWithFood(startDate, endDate)
            val nextMeal = mealData?.firstOrNull()
            val wholeDayMealData = db?.mealDAO()?.getMealWithFood(
                    DateUtilsHelper.toLong(DateUtilsHelper.atTheBeginningOfTheDay(DateUtilsHelper.toZonedDateTime(startDate))),
                    endDate)
            if(nextMeal != null) {
                activity.runOnUiThread {
                    replaceMeal(nextMeal.toDomain())
                    setUpCaloriesView(wholeDayMealData!!.map{it.toDomain()})
                }

            } else {
                removeMealContainer()
            }
        }
    }

    private fun setUpCaloriesView(meals: List<Meal>) {
        val sharedPref = this.activity.getSharedPreferences(SHARED_PREF_CALORIES, Context.MODE_PRIVATE)
        val goalCaloriesNum = sharedPref.getInt(CALORIES_KEY, 2400)

        val consumedCalories: Int = meals.map{it.food.totalCalories/it.food.servings}.sum().toInt()
        val remainingCalories = goalCaloriesNum - consumedCalories

        goalCaloriesView?.text = formatInt(goalCaloriesNum)
        consumedCaloriesView?.text = "\t\t" + formatInt(consumedCalories)
        remainingCaloriesView?.text = formatInt(remainingCalories)
    }

    private fun formatInt(value: Int): String {
        if(value < 1000) return value.toString()
        return (value / 1000).toString() + " " + String.format("%03d", value % 1000)
    }

    private fun removeMealContainer() {
        Handler(Looper.getMainLooper()).post {
            val viewToRemove = view?.findViewById<ConstraintLayout>(R.id.meal_summary_id_placeholder)
            val viewGroup = viewToRemove?.parent as ViewGroup?
            if(viewGroup != null) {
                val index = viewGroup.indexOfChild(viewToRemove)

                viewGroup.removeView(viewToRemove)
                viewGroup.removeView(txtView)
                val newView = layoutInflater.inflate(R.layout.empty_meals, viewGroup, false)
                viewGroup.addView(newView, index)
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun replaceMeal(meal: Meal) {
        txtView?.text = meal.name
        val formattedTime = DateTimeFormatter.ofPattern("hh:mm a").format(meal.date)
        mealTimeTxtView?.text = "Meal time: ${formattedTime}"
        txtView?.setOnClickListener { view ->
            val intent = Intent(view.context, AddMealActivity::class.java)
            intent.putExtra(AddMealActivity.MEAL, meal)
            view.context.startActivity(intent)
        }
        setUpFood(meal.food)
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }


    private fun setUpDb() {
        db = MealDb.getInstance(this.context)
    }

    private fun setUpFood(food: Food) {
        doAsync {
            val im = BitmapFactory.decodeStream(URL(food.imageUrl).openConnection().getInputStream())
            runUI(im, food, true)
        }
    }


    private fun runUI(im: Bitmap, food: Food, visible: Boolean) {
        Handler(Looper.getMainLooper()).post {
            foodImage?.background = BitmapDrawable(view?.resources, im)
            foodImage?.text = food.title
            foodImage?.background?.setColorFilter(Color.rgb(200, 200, 200), android.graphics.PorterDuff.Mode.MULTIPLY)
            foodImage?.setOnLongClickListener {
                val fullImageIntent = Intent(view?.context, FullMealDetailsActivity::class.java)
                fullImageIntent.putExtra(FullMealDetailsActivity.CURRENT_FOOD, food)
                fullImageIntent.putExtra(FullMealDetailsActivity.IMAGE_ID, im)
                view?.context?.startActivity(fullImageIntent)
                true
            }
        }

    }

    companion object {
        fun newInstance(startDate: Long, endDate: Long): DayQuickSummary {
            val args = Bundle()
            args.putLong("startDate", startDate)
            args.putLong("endDate", endDate)
            val fragment = DayQuickSummary()
            fragment.arguments = args
            return fragment
        }

        const val SHARED_PREF_CALORIES = "CALORIES"
        const val CALORIES_KEY = "CALORIES_KEY"
    }

}
