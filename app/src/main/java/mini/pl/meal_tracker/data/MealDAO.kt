package mini.pl.meal_tracker.data

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE

@Dao
interface MealDAO {

    @Insert(onConflict = REPLACE)
    fun insertMeal(mealDTO: MealDTO)

    @Insert(onConflict = REPLACE)
    fun insertFood(foodDTO: FoodDTO)

    @Query("Select * FROM meals INNER JOIN foods ON meals.foodName = foods.title where date > :startDate and date < :endDate ORDER BY date")
    fun getMealWithFood(startDate: Long, endDate: Long): List<MealWithFood>

    @Delete
    fun deleteMeal(mealDTO: MealDTO)

    @Update(onConflict = REPLACE)
    fun updateMeal(mealDTO: MealDTO)

    @Query("Select * FROM foods where foods.title LIKE :foodName COLLATE NOCASE")
    fun searchFood(foodName: String): List<FoodDTO>

    @Query("Select foods.*, count(meals.id) AS cnt FROM foods LEFT JOIN meals on foods.title = meals.foodName " +
            "GROUP BY foods.title ORDER BY cnt  DESC LIMIT 6")
    fun getTopFood(): List<FoodDTO>

}
