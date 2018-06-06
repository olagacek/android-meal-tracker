package mini.pl.meal_tracker.activities

import android.annotation.TargetApi
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageButton
import mini.pl.meal_tracker.R
import mini.pl.meal_tracker.fragments.DatePickerFragment
import mini.pl.meal_tracker.fragments.DayQuickSummary
import mini.pl.meal_tracker.utils.DateUtilsHelper
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.*

class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private var currentDayButton: Button? = null
    private var currentDate: Date = Date()


    @TargetApi(Build.VERSION_CODES.O)
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        setDate(GregorianCalendar(p1, p2, p3).getTime())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDate(date: Date) {
        val format = SimpleDateFormat("EEEE, dd MMMM")
        currentDayButton?.text = format.format(date)
        currentDate = date
        val zoned = DateUtilsHelper.fromDate(date)
        val startDate = if (DateUtilsHelper.atTheBeginningOfTheDay(zoned) == DateUtilsHelper.atTheBeginningOfTheDay(ZonedDateTime.now()))
            ZonedDateTime.now() else
            DateUtilsHelper.atTheBeginningOfTheDay(zoned)
        setUpFragment(DateUtilsHelper.toLong(startDate),
                DateUtilsHelper.toLong(DateUtilsHelper.atTheEndOfTheDay(zoned)))
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //set Up fragment
        setUpFragment(
                DateUtilsHelper.toLong(ZonedDateTime.now()),
                DateUtilsHelper.toLong(DateUtilsHelper.atTheEndOfTheDay(ZonedDateTime.now()))
        )


        currentDayButton = findViewById<Button>(R.id.currentDayId)
        val planYourDayButton = findViewById<Button>(R.id.planYourDay)

        planYourDayButton.setOnClickListener {
            val intent = Intent(this, DaySummary::class.java)
            intent.putExtra(DaySummary.DATE_KEY, currentDate.time)
            startActivity(intent)
        }

        val shoppingListButton = findViewById<Button>(R.id.shopList)
        shoppingListButton.setOnClickListener {
            val intent = Intent(this, ShoppingListActivity::class.java)
            startActivity(intent)
        }

        val rightButton = findViewById<ImageButton>(R.id.right)
        val leftButton = findViewById<ImageButton>(R.id.left)
        val c = Calendar.getInstance()

        rightButton.setOnClickListener {
            c.time = currentDate
            c.add(Calendar.DATE, 1)
            setDate(c.time)
        }

        leftButton.setOnClickListener {
            c.time = currentDate
            c.add(Calendar.DATE, -1)
            setDate(c.time)
        }

        currentDayButton?.setOnClickListener {
            val fragment = DatePickerFragment()
            fragment.show(fragmentManager, "")
        }

        val fab = findViewById<FloatingActionButton>(R.id.startViewFab)
        fab.setOnClickListener { view ->
            val intent = Intent(view.context, AddMealActivity::class.java)
            intent.putExtra(AddMealActivity.CHOSEN_DATE, DateUtilsHelper.toLong(DateUtilsHelper.fromDate(currentDate)))
            startActivity(intent)
        }

    }

    private fun setUpFragment(startDate: Long, endDate: Long) {
        val fragment = DayQuickSummary.newInstance(startDate, endDate)
        supportFragmentManager.beginTransaction().replace(R.id.fragmentId, fragment).commit()
    }


}
