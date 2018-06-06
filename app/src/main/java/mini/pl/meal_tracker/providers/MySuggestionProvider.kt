package mini.pl.meal_tracker.providers

import android.content.SearchRecentSuggestionsProvider

class MySuggestionProvider : SearchRecentSuggestionsProvider(){

    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        val AUTHORITY = "mini.pl.meal_tracker.providers.MySuggestionProvider"
        val MODE = DATABASE_MODE_QUERIES
    }
}