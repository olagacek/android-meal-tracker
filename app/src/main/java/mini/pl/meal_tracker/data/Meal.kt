package mini.pl.meal_tracker.data

import java.io.Serializable
import java.time.ZonedDateTime

/**
 * Created by aleksandragacek on 07/04/2018.
 */

data class Meal(val date: ZonedDateTime, val name: String, val food: Food, var id: Long) : Serializable {
    fun toDTO(): MealDTO {
        return MealDTO(id, date, name, food.title)
    }
}