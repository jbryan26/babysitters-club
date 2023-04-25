package com.ujimatech.babysitterzclub

import androidx.annotation.VisibleForTesting
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalTime
import kotlin.math.ceil



class BabysitterSalaryEngine {
    companion object {
        val EARLIEST_START_TIME: LocalTime = LocalTime.of(17, 0)
        val LATEST_END_TIME: LocalTime = LocalTime.of(4, 0)
        val MIDNIGHT_LOCAL_TIME = LocalTime.of(23, 59, 59)
        const val PRE_BEDTIME_RATE = 12
        const val BEDTIME_TO_MIDNIGHT_RATE = 8
        const val POST_MIDNIGHT_RATE = 16
        const val MINUTES_PER_HOUR = 60f
    }
    var wages: BigDecimal = BigDecimal.ZERO

    fun calculateWage(babysitterShift: BabysitterShift) {
        wages = if (isValidTimeRange(babysitterShift)) {
            val preBedtimeHours = getPreBedtimeHours(babysitterShift)
            val bedtimeHours = getBedtimeHours(babysitterShift)
            val postMidnightHours = getPostMidnightHours(babysitterShift)

            val preBedtimeWages = preBedtimeHours * PRE_BEDTIME_RATE
            val bedtimeWages = bedtimeHours * BEDTIME_TO_MIDNIGHT_RATE
            val postMidnightWages = postMidnightHours * POST_MIDNIGHT_RATE

            (preBedtimeWages + bedtimeWages + postMidnightWages).toBigDecimal()
        } else {
            BigDecimal.ZERO
        }
    }

    @VisibleForTesting
    fun getPreBedtimeHours(babysitterShift: BabysitterShift): Int {
        val shiftMinutes =
            Duration.between(babysitterShift.startTime, babysitterShift.bedTime).toMinutes()
        return ceil((shiftMinutes / MINUTES_PER_HOUR).toDouble()).toInt()
    }

    @VisibleForTesting
    fun getBedtimeHours(babysitterShift: BabysitterShift): Int {
        val endBedtime = if (babysitterShift.endTime > LATEST_END_TIME) {
            babysitterShift.endTime
        } else {
            MIDNIGHT_LOCAL_TIME
        }
        val shiftMinutes = Duration.between(babysitterShift.bedTime, endBedtime).toMinutes()
        return ceil((shiftMinutes / MINUTES_PER_HOUR).toDouble()).toInt()
    }

    @VisibleForTesting
    fun getPostMidnightHours(babysitterShift: BabysitterShift): Int {
        val shiftMinutes = Duration.between(LocalTime.MIDNIGHT, babysitterShift.endTime).toMinutes()
        return if (shiftMinutes>0 && (babysitterShift.endTime < LATEST_END_TIME)) {
            ceil((shiftMinutes / MINUTES_PER_HOUR).toDouble()).toInt()
        } else {
            0
        }
    }

    @VisibleForTesting
    fun isValidTimeRange(babysitterShift: BabysitterShift): Boolean {
        return isStartTimeValid(babysitterShift.startTime) && isEndTimeValid(babysitterShift.endTime) && isBedTimeValid(babysitterShift.bedTime)
    }

    @VisibleForTesting
    fun isStartTimeValid(startTime: LocalTime): Boolean {
        return startTime >= EARLIEST_START_TIME
    }

    @VisibleForTesting
    fun isEndTimeValid(endTime: LocalTime): Boolean {
        return endTime <= LATEST_END_TIME || (endTime > EARLIEST_START_TIME && endTime <= MIDNIGHT_LOCAL_TIME)
    }

    @VisibleForTesting
    fun isBedTimeValid(bedTime: LocalTime): Boolean {
        return bedTime >= EARLIEST_START_TIME && bedTime <= MIDNIGHT_LOCAL_TIME
    }
}

data class BabysitterShift(
    val startTime: LocalTime,
    val endTime: LocalTime,
    val bedTime: LocalTime
)