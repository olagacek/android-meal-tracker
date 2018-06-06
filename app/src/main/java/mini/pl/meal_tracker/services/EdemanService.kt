package mini.pl.meal_tracker.services

import com.squareup.moshi.Moshi
import mini.pl.meal_tracker.data.Food
import java.net.URL
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


class EdemanService {
    fun searchFood(query: String): List<Food> {
        val queryUrl = createFullUrl(query)
        val result = URL(queryUrl).readText()
        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory()).build()
        val responseAdapter = moshi.adapter(Response::class.java)
        return responseAdapter.fromJson(result)!!.hits.map { hit -> hit.recipe }
    }

    private fun createFullUrl(query: String): String {
        return "$API_URL?q=$query&app_id=$API_ID&app_key=$API_KEY"
    }

    companion object {
        private const val API_URL = "https://api.edamam.com/search"
        private const val API_KEY = "e01c6da176c31b01f4320b8befba326c"
        private const val API_ID = "f6551d21"
    }
}

data class Response(val hits: List<HitResponse>)

data class HitResponse(val recipe: Food)
