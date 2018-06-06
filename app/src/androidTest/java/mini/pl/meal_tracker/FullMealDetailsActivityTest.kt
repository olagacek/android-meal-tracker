package mini.pl.meal_tracker

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import android.widget.Button
import android.widget.TextView
import mini.pl.meal_tracker.activities.AddMealActivity
import mini.pl.meal_tracker.activities.EditFoodActivity
import mini.pl.meal_tracker.activities.FullMealDetailsActivity
import mini.pl.meal_tracker.data.Food
import mini.pl.meal_tracker.data.Nutrient
import mini.pl.meal_tracker.utils.Matcher
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class FullMealDetailsActivityTest {

    private val matcher = Matcher()

    private val activityWithFoodIntent = object : ActivityTestRule<FullMealDetailsActivity>(FullMealDetailsActivity::class.java) {
        override fun getActivityIntent(): Intent {
            val food = Food("Oat with milk", "",
                    listOf("oat", "milk"), 1000.0, "",
                    mapOf("Fat" to Nutrient("Fat", 2.0, "g")), 10.0)
            InstrumentationRegistry.getTargetContext();
            val intent = Intent(Intent.ACTION_MAIN);
            intent.putExtra(FullMealDetailsActivity.CURRENT_FOOD, food);
            return intent
        }
    }

    @Rule
    fun ruleWithMeal(): ActivityTestRule<FullMealDetailsActivity> = activityWithFoodIntent


    @Test
    fun mealStartValuesTest() {
        val activity = ruleWithMeal().activity
        val viewById = activity.findViewById<TextView>(R.id.foodImage)
        Assert.assertThat(viewById.text.toString(), Matchers.`is`("Oat with milk"))
    }

    @Test
    fun buttonInteractionsTest(){
        Intents.init()
        Espresso.onView(ViewMatchers.withId(R.id.editFoodIcon)).perform(ViewActions.click())
        matcher.nextOpenActivityIs(EditFoodActivity::class.java)
        Intents.release()
    }



}