package mini.pl.meal_tracker.activities

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import mini.pl.meal_tracker.fragments.DayMealsFragment
import mini.pl.meal_tracker.fragments.NutritionSummaryFragment
import mini.pl.meal_tracker.R
import mini.pl.meal_tracker.utils.DateUtilsHelper
import java.time.ZonedDateTime

class DaySummary : AppCompatActivity() {

    private var date: ZonedDateTime? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_summary)


        val longDate = intent.getLongExtra(DATE_KEY, 0)

        date = DateUtilsHelper.toZonedDateTime(longDate)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val viewPager = findViewById<ViewPager>(R.id.container)
        setUpViewPager(viewPager)

        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        tabLayout.setupWithViewPager(viewPager)

        val fab = findViewById<FloatingActionButton>(R.id.addMealFab)
        fab.setOnClickListener { view ->
            val intent = Intent(view.context, AddMealActivity::class.java)
            intent.putExtra(AddMealActivity.CHOSEN_DATE, DateUtilsHelper.toLong(date!!))
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpViewPager(viewPager: ViewPager) {
        val adapter = SectionsPagerAdapter(supportFragmentManager)

        val dayMeals = DayMealsFragment.newInstance(DateUtilsHelper.toLong(date!!))
        adapter.addFragment(dayMeals, "Meals")
        val startDate = DateUtilsHelper.toLong(DateUtilsHelper.atTheBeginningOfTheDay(date!!))
        val endDate = DateUtilsHelper.toLong(DateUtilsHelper.atTheEndOfTheDay(date!!))
        adapter.addFragment(NutritionSummaryFragment.newInstance(startDate, endDate), "Nutrition summary")
        viewPager.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_day_summary, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == android.R.id.home) {
            this.finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val DATE_KEY = "date"
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val mFragmentList = ArrayList<Fragment>()
        private val fragmentTitlesList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList.get(position)
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            fragmentTitlesList.add(title)
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return fragmentTitlesList.get(position)
        }
    }


}
