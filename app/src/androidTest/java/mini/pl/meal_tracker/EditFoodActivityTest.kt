package mini.pl.meal_tracker

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import android.support.v7.widget.RecyclerView
import android.widget.EditText
import mini.pl.meal_tracker.activities.EditFoodActivity
import mini.pl.meal_tracker.adapters.IngredientAdapter
import mini.pl.meal_tracker.data.Food
import mini.pl.meal_tracker.data.Nutrient
import mini.pl.meal_tracker.utils.Matcher
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class EditFoodActivityTest {

    private val matcher = Matcher()

    private val activityWithFoodIntent = object : ActivityTestRule<EditFoodActivity>(EditFoodActivity::class.java) {
        override fun getActivityIntent(): Intent {
            val food = Food("Oat with milk", "",
                    listOf("oat", "milk"), 1000.0, "",
                    mapOf("Fat" to Nutrient("Fat", 2.0, "g")), 10.0)
            InstrumentationRegistry.getTargetContext();
            val intent = Intent(Intent.ACTION_MAIN);
            intent.putExtra(EditFoodActivity.ORIGINAL_FOOD, food);
            return intent
        }
    }

    @Rule
    fun ruleWithMeal(): ActivityTestRule<EditFoodActivity> = activityWithFoodIntent


    @Test
    fun startValuesTest() {
        val activity = ruleWithMeal().activity
        val mealNameEditText = activity.findViewById<EditText>(R.id.mealNameEditText)
        Assert.assertThat(mealNameEditText.text.toString(), Matchers.`is`("Oat with milk"))

        val ingredientsList = activity.findViewById<RecyclerView>(R.id.ingredientsList)
        Assert.assertThat(ingredientsList.adapter, instanceOf(IngredientAdapter::class.java))

        Assert.assertThat(ingredientsList.adapter.itemCount, `is`(2))
    }


    @Test
    fun interactionsTest() {
        Intents.init()
        Espresso.onView(ViewMatchers.withId(R.id.doneEditingId)).perform(ViewActions.click())
        Assert.assertTrue(ruleWithMeal().activity.isFinishing)
        Intents.release()
    }
}