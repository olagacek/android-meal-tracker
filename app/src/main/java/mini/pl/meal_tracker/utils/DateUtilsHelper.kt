package mini.pl.meal_tracker.utils

import android.os.Build
import android.support.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

object DateUtilsHelper {
    @RequiresApi(Build.VERSION_CODES.O)
    fun atTheEndOfTheDay(date: ZonedDateTime): ZonedDateTime {
        return date.toLocalDate().atStartOfDay().plusDays(1).atZone(ZoneId.systemDefault())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun atTheBeginningOfTheDay(date: ZonedDateTime): ZonedDateTime {
        return date.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toLong(date: ZonedDateTime): Long {
        return date.toInstant().toEpochMilli()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toZonedDateTime(time: Long): ZonedDateTime {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault())
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun plusDaysAndMinutes(date: ZonedDateTime, hours: Long, minutes: Long) : ZonedDateTime {
        return date.plusHours(hours).plusMinutes(minutes)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fromDate(date: Date): ZonedDateTime {
        return date.toInstant().atZone(ZoneId.systemDefault())
    }

}