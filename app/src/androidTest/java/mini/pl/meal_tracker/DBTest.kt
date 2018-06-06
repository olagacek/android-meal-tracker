package mini.pl.meal_tracker

import android.support.test.InstrumentationRegistry
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class DBTest {

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getTargetContext()

    }

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}
