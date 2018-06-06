package mini.pl.meal_tracker.data

import android.arch.persistence.room.*
import android.content.Context

@Database(entities = arrayOf(FoodDTO::class, MealDTO::class), version = 1)
@TypeConverters(DataConverter::class)
abstract class MealDb: RoomDatabase() {
    abstract fun mealDAO(): MealDAO

    companion object {

        private var INSTANCE: MealDb? = null

        fun getInstance(context: Context): MealDb? {
            if (INSTANCE == null) {
                synchronized(MealDb::class) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MealDb::class.java, "meal.db")
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}