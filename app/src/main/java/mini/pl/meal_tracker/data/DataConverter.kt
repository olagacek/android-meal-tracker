package mini.pl.meal_tracker.data

import android.arch.persistence.room.TypeConverter
import android.os.Build
import android.support.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import mini.pl.meal_tracker.utils.DateUtilsHelper
import java.io.Serializable
import java.time.ZonedDateTime

class DataConverter : Serializable {

    @TypeConverter
    fun fromIngredientList(ingredients: List<String>): String {
        return gson.toJson(ingredients, listStrType)
    }

    @TypeConverter
    fun toIngredientList(ingredients: String): List<String> {
        return gson.fromJson(ingredients, listStrType)
    }

    @TypeConverter
    fun fromNutrientList(nutrients: List<Nutrient>): String {
        return gson.toJson(nutrients, listNutrType)
    }

    @TypeConverter
    fun toNutrientList(nutrient: String): List<Nutrient> {
        return gson.fromJson(nutrient, listNutrType)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromZonedDateTime(date: ZonedDateTime): Long {
        return DateUtilsHelper.toLong(date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toZonedDateTime(time: Long): ZonedDateTime {
        return DateUtilsHelper.toZonedDateTime(time)
    }

    companion object {
        private val gson = Gson()
        private val listStrType = TypeToken.getParameterized(ArrayList::class.java, String::class.java).type;
        private val listNutrType = TypeToken.getParameterized(ArrayList::class.java, Nutrient::class.java).type
    }
}