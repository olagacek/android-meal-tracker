package mini.pl.meal_tracker

import android.app.Activity
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.widget.Button
import android.widget.EditText
import mini.pl.meal_tracker.activities.AddMealActivity
import mini.pl.meal_tracker.activities.SearchFoodActivity
import mini.pl.meal_tracker.data.Meal
import mini.pl.meal_tracker.data.Food
import mini.pl.meal_tracker.utils.Matcher
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.Matchers.`is`
import java.time.ZonedDateTime

@RunWith(AndroidJUnit4::class)
class AddMealActivityTest {

    private val matcher = Matcher()


    private val activityWithMealIntent = object : ActivityTestRule<AddMealActivity>(AddMealActivity::class.java) {
        override fun getActivityIntent(): Intent {
            val meal = Meal(ZonedDateTime.now(), "Breakfast", Food("oat", "",
                    emptyList(), 1000.0, "", emptyMap(), 10.0), 0)
            InstrumentationRegistry.getTargetContext();
            val intent = Intent(Intent.ACTION_MAIN);
            intent.putExtra(AddMealActivity.MEAL, meal);
            return intent
        }
    }


    @Rule
    fun ruleWithMeal(): ActivityTestRule<AddMealActivity>  = activityWithMealIntent


    @Test
    fun mealStartValuesTest() {
        val activity = ruleWithMeal().activity
        val mealDate = activity.findViewById<Button>(R.id.meal_date_button)
        assertThat(mealDate.text.toString(), `is`("Wednesday, 06 June"))
        val mealName = activity.findViewById<EditText>(R.id.mealNameEditText)
        assertThat(mealName.text.toString(), `is`("Breakfast"))
    }

    @Test
    fun buttonIntentsTest() {
        Intents.init()
        onView(withId(R.id.add_food)).perform(click())
        matcher.nextOpenActivityIs(SearchFoodActivity::class.java)
        Intents.release()
    }

    @Test
    fun finishActivityTest(){
        Intents.init()
        onView(withId(R.id.doneCreatingId)).perform(click())
        assertTrue(ruleWithMeal().activity.isFinishing)
        Intents.release()
    }

}