package mini.pl.meal_tracker

import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import mini.pl.meal_tracker.activities.MainActivity
import android.support.test.rule.ActivityTestRule
import android.widget.Button
import mini.pl.meal_tracker.activities.AddMealActivity
import mini.pl.meal_tracker.utils.Matcher
import org.junit.Rule
import org.hamcrest.Matchers.`is`


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    var rule = ActivityTestRule(MainActivity::class.java)

    private val matcher = Matcher()

    @Test
    fun startValuesTest() {
        val activity = rule.activity
        val viewById = activity.findViewById<Button>(R.id.currentDayId)
        assertThat(viewById.text.toString(), `is`("Today"))
    }

    @Test
    fun buttonIntentsCorrect() {
        Intents.init()
        Espresso.onView(ViewMatchers.withId(R.id.startViewFab)).perform(ViewActions.click())
        matcher.nextOpenActivityIs(AddMealActivity::class.java)
        Intents.release()
    }
}