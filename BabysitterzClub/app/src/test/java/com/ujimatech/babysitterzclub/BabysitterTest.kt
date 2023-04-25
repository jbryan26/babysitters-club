package com.ujimatech.babysitterzclub

import org.junit.Test
import org.junit.Assert.*
import java.math.BigDecimal
import java.time.LocalTime

class BabysitterTest {
    @Test
    fun startTime_after5_isValid() {
        val babysitterSalaryEngine = BabysitterSalaryEngine()
        assertTrue(babysitterSalaryEngine.isStartTimeValid(LocalTime.of(17,0)))
        assertTrue(babysitterSalaryEngine.isStartTimeValid(LocalTime.of(17, 1)))
        assertTrue(babysitterSalaryEngine.isStartTimeValid(LocalTime.of(19, 0)))
        assertTrue(babysitterSalaryEngine.isStartTimeValid(LocalTime.of(23, 59)))
    }

    @Test
    fun startTime_before5_isInvalid() {
        val babysitterSalaryEngine = BabysitterSalaryEngine()
        assertFalse(babysitterSalaryEngine.isStartTimeValid(LocalTime.of(16,59)))
        assertFalse(babysitterSalaryEngine.isStartTimeValid(LocalTime.MIDNIGHT))
        assertFalse(babysitterSalaryEngine.isStartTimeValid(LocalTime.NOON))
    }

    @Test
    fun endTime_before4am_isValid() {
        val babysitterSalaryEngine = BabysitterSalaryEngine()
        with(babysitterSalaryEngine) {
            assertTrue(isEndTimeValid(LocalTime.MIDNIGHT))
            assertTrue(isEndTimeValid(LocalTime.of(3, 59)))
            assertTrue(isEndTimeValid(LocalTime.of(4, 0)))
            assertTrue(isEndTimeValid(LocalTime.of(18, 0)))
        }
    }

    @Test
    fun endTime_before4am_isInvalid() {
        val babysitterSalaryEngine = BabysitterSalaryEngine()
        with(babysitterSalaryEngine) {
            assertFalse(isEndTimeValid(LocalTime.NOON))
            assertFalse(isEndTimeValid(LocalTime.of(4,1)))
        }
    }


    @Test
    fun bedtime_before_midnight_Valid() {
        val babysitterSalaryEngine = BabysitterSalaryEngine()
        with(babysitterSalaryEngine) {
            assertTrue(isBedTimeValid(LocalTime.of(23, 1)))
            assertTrue(isBedTimeValid(LocalTime.of(17,1)))
        }
    }

    @Test
    fun bedtime_after_midnight_isInvalid() {
        val babysitterSalaryEngine = BabysitterSalaryEngine()
        with(babysitterSalaryEngine) {
            assertFalse(isBedTimeValid(LocalTime.of(0, 1)))
            assertFalse(isBedTimeValid(LocalTime.of(4,1)))
        }
    }

    @Test
    fun preBedtimeHours_shiftHoursCalculation() {
        val babysitterSalaryEngine = BabysitterSalaryEngine()
        val babysitterShift = BabysitterShift(
            startTime = LocalTime.of(18, 1),
            endTime = LocalTime.of(3, 0),
            bedTime = LocalTime.of(21, 0)
        )
        assertEquals(3, babysitterSalaryEngine.getPreBedtimeHours(babysitterShift))
    }

    @Test
    fun bedtimeHours_shiftHoursCalculation() {
        val babysitterSalaryEngine = BabysitterSalaryEngine()
        var babysitterShift = BabysitterShift(
            startTime = LocalTime.of(18, 1),
            endTime = LocalTime.of(3, 0),
            bedTime = LocalTime.of(21, 0)
        )
        assertEquals(3, babysitterSalaryEngine.getBedtimeHours(babysitterShift))

        babysitterShift = BabysitterShift(
            startTime = LocalTime.of(18, 1),
            endTime = LocalTime.of(22, 0),
            bedTime = LocalTime.of(21, 0)
        )
        assertEquals(1, babysitterSalaryEngine.getBedtimeHours(babysitterShift))
    }

    @Test
    fun postMidnightHours_shiftHoursCalculation() {
        val babysitterSalaryEngine = BabysitterSalaryEngine()
        var babysitterShift = BabysitterShift(
            startTime = LocalTime.of(18, 1),
            endTime = LocalTime.of(2, 50),
            bedTime = LocalTime.of(21, 0)
        )
        assertEquals(3, babysitterSalaryEngine.getPostMidnightHours(babysitterShift))
        babysitterShift = BabysitterShift(
            startTime = LocalTime.of(18, 0),
            endTime = LocalTime.of(3, 0),
            bedTime = LocalTime.of(21, 0)
        )
        assertEquals(3, babysitterSalaryEngine.getPostMidnightHours(babysitterShift))
        babysitterShift = BabysitterShift(
            startTime = LocalTime.of(18, 0),
            endTime = LocalTime.of(22, 0),
            bedTime = LocalTime.of(21, 0)
        )
        assertEquals(0, babysitterSalaryEngine.getPostMidnightHours(babysitterShift))
    }

    @Test
    fun salaryCalculation_one_hour_each() {
        val babysitterSalaryEngine = BabysitterSalaryEngine()
        val babysitterShift = BabysitterShift(
            startTime = LocalTime.of(22, 0),
            endTime = LocalTime.of(1, 0),
            bedTime = LocalTime.of(23, 0)
        )
        babysitterSalaryEngine.calculateWage(babysitterShift)
        assertEquals(BigDecimal.valueOf(36L), babysitterSalaryEngine.wages)
    }

    @Test
    fun salaryCalculation_no_fractional_hours() {
        val babysitterSalaryEngine = BabysitterSalaryEngine()
        var babysitterShift = BabysitterShift(
            startTime = LocalTime.of(22, 1),
            endTime = LocalTime.of(1, 0),
            bedTime = LocalTime.of(23, 0)
        )
        babysitterSalaryEngine.calculateWage(babysitterShift)
        assertEquals(BigDecimal.valueOf(36L), babysitterSalaryEngine.wages)

        babysitterShift = BabysitterShift(
            startTime = LocalTime.of(22, 1),
            endTime = LocalTime.of(1, 1),
            bedTime = LocalTime.of(23, 0)
        )
        babysitterSalaryEngine.calculateWage(babysitterShift)
        assertEquals(BigDecimal.valueOf(52L), babysitterSalaryEngine.wages)
    }

    @Test
    fun salaryCalculation_same_day_endtime() {
        val babysitterSalaryEngine = BabysitterSalaryEngine()
        val babysitterShift = BabysitterShift(
            startTime = LocalTime.of(17, 0),
            endTime = LocalTime.of(22, 0),
            bedTime = LocalTime.of(21, 0)
        )
        babysitterSalaryEngine.calculateWage(babysitterShift)
        assertEquals(BigDecimal.valueOf(56L), babysitterSalaryEngine.wages)
    }
}