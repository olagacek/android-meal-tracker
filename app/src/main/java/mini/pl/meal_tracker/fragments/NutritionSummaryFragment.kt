package mini.pl.meal_tracker.fragments

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import mini.pl.meal_tracker.R
import mini.pl.meal_tracker.data.Meal
import mini.pl.meal_tracker.data.MealDb
import mini.pl.meal_tracker.data.Food
import mini.pl.meal_tracker.data.Nutrient
import org.jetbrains.anko.doAsync

class NutritionSummaryFragment : Fragment() {

    private var startDate: Long? = null
    private var endDate: Long? = null
    private var nutritionFacts: TextView? = null
    private var db: MealDb? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            startDate = arguments.getLong(START_DATE)
            endDate = arguments.getLong(END_DATE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_nutrition_summary, container, false)
        setUpDb()
        nutritionFacts = view.findViewById<TextView>(R.id.nutritionFacts)
        val pieChart = view.findViewById<PieChart>(R.id.piechart)
        fetchDataAndSetUpNutrition(pieChart)
        return view
    }

    private fun setUpDb() {
        db = MealDb.getInstance(this.context)
    }

    private fun fetchDataAndSetUpNutrition(pieChart: PieChart) {
        doAsync {
            val meals = db?.mealDAO()?.getMealWithFood(startDate!!, endDate!!)
            runUI(meals!!.map{it.toDomain().food})
            setUpPieChart(pieChart, meals.map { it.toDomain() })
        }
    }

    private fun runUI(foods: List<Food>) {
        Handler(Looper.getMainLooper()).post {
            setUpNutritionSummary(foods)
        }

    }

    private fun setUpNutritionSummary(foods: List<Food>) {
        val calories = foods.map{it.totalCalories/it.servings}.sum()
        val nutrients = foods.flatMap { food -> food.nutrients.map {
            entry -> entry.value.copy(quantity = entry.value.quantity/food.servings)
        }.filter { nutrient -> Nutrient.DefaultNutrientList.contains(nutrient.label)}}.groupBy { it.label }.map {
            (key, value) -> Nutrient(key, value.map{it.quantity}.sum(), value.first().unit)
        }

        if(nutrients.isNotEmpty()) {
            val nutrientsHtml = "<b>Total calories: </b> ${String.format("%.2f",calories)}<br/>" +
                    nutrients
                            .map { nutrient -> "<b>${nutrient.label}</b>: ${String.format("%.2f", nutrient.quantity)} ${nutrient.unit}<br/>"}
                            .reduce { one, second ->
                                one + second
                            }
            nutritionFacts?.text = Html.fromHtml(nutrientsHtml)
        }


    }

    private fun setUpPieChart(pieChart: PieChart, meals: List<Meal>) {
        pieChart.setUsePercentValues(true)

        val yValues = ArrayList<Entry>()
        val xValues = ArrayList<String>()

        var counter = 0
        meals.forEach { meal ->
            yValues.add(Entry((meal.food.totalCalories / meal.food.servings).toFloat(), counter))
            xValues.add(meal.name)
            counter++
        }
        val dataSet = PieDataSet(yValues, "")


        val data = PieData(xValues, dataSet)
        data.setValueFormatter(PercentFormatter())

        dataSet.setColors(ColorTemplate.PASTEL_COLORS)
        pieChart.isDrawHoleEnabled = false
        data.setValueTextSize(15f)
        data.setValueTextColor(Color.WHITE)
        pieChart.setCenterTextSize(15f)
        pieChart.legend.isEnabled = false
        pieChart.setDescription("")
        pieChart.data = data
    }

    companion object {
        private const val START_DATE = "start_date"
        private const val END_DATE = "end_date"

        fun newInstance(startDate: Long, endDate: Long): NutritionSummaryFragment {
            val fragment = NutritionSummaryFragment()
            val args = Bundle()
            args.putLong(START_DATE, startDate)
            args.putLong(END_DATE, endDate)
            fragment.arguments = args
            return fragment
        }
    }
}
