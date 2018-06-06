package mini.pl.meal_tracker.data

import android.arch.persistence.room.*
import com.squareup.moshi.Json
import java.io.Serializable
import java.time.ZonedDateTime

data class Food(@Json(name = "label") val title: String, @Json(name = "image") val imageUrl: String,
                @Json(name = "ingredientLines") val ingredients: List<String>,
                @Json(name = "calories") val totalCalories: Double,
                @Json(name = "url") val instructionsUrl: String,
                @Json(name = "totalNutrients") val nutrients: Map<String, Nutrient>,
                @Json(name = "yield") val servings: Double, val recipe : String? = null) : Serializable {
    fun toDto(): FoodDTO = FoodDTO(title, imageUrl, ingredients, totalCalories, instructionsUrl,
            nutrients.map {entry -> entry.value}, servings,  recipe)
}


data class Nutrient(val label: String, val quantity: Double, val unit: String) : Serializable {
    companion object {
        val DefaultNutrientList = listOf<String>("Fat", "Saturated", "Trans", "Carbs", "Fiber",
                "Sugars", "Protein", "Cholesterol", "Sodium", "Calcium", "Magnesium")
    }
}


@Entity(tableName = "foods")
data class FoodDTO(@PrimaryKey val title: String,
                   val imageUrl: String, val ingredients: List<String>,
                   val totalCalories: Double, val instructionsUrl: String, val nutrients: List<Nutrient>,
                   val servings: Double, val recipe: String? = null) {
    fun toDomain(): Food {
        return Food(title, imageUrl, ingredients, totalCalories, instructionsUrl, nutrients.map { nutrient -> nutrient.label to nutrient }.toMap(), servings, recipe)
    }
}


data class NutrientDTO(val label: String, val quantity: Double, val unit: String)

data class Ingredient(val text: String, val weight: Double)

@Entity(
        tableName = "meals",
        foreignKeys = arrayOf(ForeignKey(
                entity = FoodDTO::class,
                parentColumns = arrayOf("title"),
                childColumns = arrayOf("foodName")
        ))
)
data class MealDTO(
        @PrimaryKey(autoGenerate = true) var id: Long,
        val date: ZonedDateTime,
        val mealName: String,
        val foodName: String
)

data class MealWithFood(
        val title: String,
        val imageUrl: String,
        val ingredients: List<String>,
        val totalCalories: Double,
        val instructionsUrl: String,
        val nutrients: List<Nutrient>,
        val servings: Double,
        var id: Long,
        val date: ZonedDateTime,
        val mealName: String,
        val foodName: String
) {
    fun toDomain(): Meal {
        return Meal(
                date, mealName,
                FoodDTO(title, imageUrl, ingredients, totalCalories, instructionsUrl, nutrients, servings).toDomain(), id
        )

    }
}