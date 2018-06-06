package mini.pl.meal_tracker.activities

import android.annotation.TargetApi
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText

import kotlinx.android.synthetic.main.activity_add_meal.*
import mini.pl.meal_tracker.R
import mini.pl.meal_tracker.adapters.FoodAdapter
import mini.pl.meal_tracker.data.*
import mini.pl.meal_tracker.fragments.DatePickerFragment
import mini.pl.meal_tracker.utils.DateUtilsHelper
import org.jetbrains.anko.doAsync
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AddMealActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private var currentDayButton: Button? = null
    @RequiresApi(Build.VERSION_CODES.O)
    private var currentDate: ZonedDateTime = DateUtilsHelper.atTheBeginningOfTheDay(ZonedDateTime.now())
    private val food = ArrayList<Food>()
    private val adapter = FoodAdapter(false, food)
    private var db: MealDb? = null
    private var chosenHour: Long = 7
    private var chosenMinute: Long = 0
    private var id: Long = 0


    @TargetApi(Build.VERSION_CODES.O)
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        setDate(DateUtilsHelper.atTheBeginningOfTheDay(GregorianCalendar(p1, p2, p3).toZonedDateTime()))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDate(date: ZonedDateTime) {
        val format = SimpleDateFormat("EEEE, dd MMMM", Locale.ENGLISH)
        currentDayButton?.text = format.format( Date.from(date.toInstant()))
        currentDate = date
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_meal)
        setSupportActionBar(toolbar)

        setUpDb()

        setDate(ZonedDateTime.now())

        val rv = findViewById<RecyclerView>(R.id.eneteredMeal)

        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(rv.context)
        setUpAddingFood()



        val timePickerButton = findViewById<Button>(R.id.meal_hour_button)
        timePickerButton.setOnClickListener {
            val timePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourS, minuteS ->
                setUpTime(hourS, minuteS, timePickerButton)
            }, chosenHour.toInt(), chosenMinute.toInt(), false)
            timePickerDialog.show()
        }

        currentDayButton = findViewById<Button>(R.id.meal_date_button)
        currentDayButton?.setOnClickListener { view ->
            val fragment = DatePickerFragment()
            fragment.show(fragmentManager, "")
        }

        if(intent.hasExtra(MEAL)) {
            val meal = intent.extras.getSerializable(MEAL) as Meal
            val mealName = findViewById<EditText>(R.id.mealNameEditText)
            id = meal.id
            mealName.setText(meal.name)
            setUpTime(meal.date.hour, meal.date.minute, timePickerButton)
            setUpFood(meal.food)
            setDate(meal.date)
        } else if (intent.hasExtra(CHOSEN_DATE)) {
            val date = intent.extras.getLong(CHOSEN_DATE)
            setDate(DateUtilsHelper.toZonedDateTime(date))
        }
    }


    private fun setUpAddingFood() {
        val addFoodButton = findViewById<Button>(R.id.add_food)
        addFoodButton.setOnClickListener {
            val intent = Intent(this, SearchFoodActivity::class.java)
            startActivityForResult(intent, SEARCH_ACTIVITY_ID)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SEARCH_ACTIVITY_ID && resultCode == Activity.RESULT_OK) {
            val chosenFood = data?.getSerializableExtra(CHOSEN_FOOD) as? Food
            if (chosenFood != null) {
                setUpFood(chosenFood)
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpTime(hours: Int, minutes: Int, timePickerButton: Button) {
        chosenHour = hours.toLong()
        chosenMinute = minutes.toLong()
        val date = DateUtilsHelper.plusDaysAndMinutes(DateUtilsHelper.toZonedDateTime(0), chosenHour, chosenMinute)
        timePickerButton.text = DateTimeFormatter.ofPattern("hh:mm a").format(date)
    }

    private fun setUpFood(chosenFood: Food) {
        food.removeAll(food)
        food.add(chosenFood)
        adapter.replaceFood(food)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_meal, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.doneCreatingId) {
            val mealName = findViewById<EditText>(R.id.mealNameEditText)
            if(food.isNotEmpty()) {
                if(id != 0L) {
                    updateMeal(Meal(
                            DateUtilsHelper.plusDaysAndMinutes(this.currentDate, chosenHour, chosenMinute),
                            mealName.text.toString(),
                            food.first(),
                            id))
                } else {
                    insertMeal(Meal(DateUtilsHelper.plusDaysAndMinutes(this.currentDate, chosenHour, chosenMinute), mealName.text.toString(), food.first(), 0))
                }
            }
            this.finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpDb() {
        db = MealDb.getInstance(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertMeal(meal: Meal) {
        doAsync {
            db?.mealDAO()?.insertFood(meal.food.toDto())
            db?.mealDAO()?.insertMeal(meal.toDTO())
        }
    }

    private fun updateMeal(meal: Meal) {
        doAsync {
            db?.mealDAO()?.insertFood(meal.food.toDto())
            db?.mealDAO()?.updateMeal(meal.toDTO())
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        MealDb.destroyInstance()
    }

    companion object {
        const val CHOSEN_FOOD = "CHOSEN_FOOD"
        const val SEARCH_ACTIVITY_ID = 1
        const val MEAL = "MEAL"
        const val CHOSEN_DATE = "CHOSEN_DATE"
    }
}
