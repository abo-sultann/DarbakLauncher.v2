package com.example

import com.example.model.DayNightMode
import com.example.model.GpsTelemetry
import com.example.ui.viewmodel.MainViewModel
import com.example.util.SolarCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class ThemeEngineTest {

    @Test
    fun `forced day and forced night modes evaluate correctly`() {
        val telemetry = GpsTelemetry()
        assertFalse(MainViewModel.evaluateNightMode(DayNightMode.FORCED_DAY, telemetry))
        assertTrue(MainViewModel.evaluateNightMode(DayNightMode.FORCED_NIGHT, telemetry))
    }

    @Test
    fun `solar calculator returns null explicitly for zero or invalid coordinates`() {
        val times = SolarCalculator.calculateSolarTimes(0.0, 0.0)
        assertEquals(null, times)
    }

    @Test
    fun `auto solar mode falls back to clock when no gps location is available`() {
        val telemetry = GpsTelemetry(hasGpsFix = false, latitude = 0.0, longitude = 0.0)
        // Without persistent preferences or GPS fix, falls back explicitly to AUTO_CLOCK
        val isNight = MainViewModel.evaluateNightMode(DayNightMode.AUTO_SUNRISE_SUNSET, telemetry)
        val expected = MainViewModel.evaluateNightMode(DayNightMode.AUTO_CLOCK, telemetry)
        assertEquals(expected, isNight)
    }

    @Test
    fun `solar calculator computes reasonable Riyadh sunrise and sunset`() {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Riyadh")).apply {
            set(2026, Calendar.JUNE, 21, 12, 0)
        }
        // Riyadh coordinates: 24.7136° N, 46.6753° E
        val times = SolarCalculator.calculateSolarTimes(24.7136, 46.6753, cal)
        assertTrue(times != null)
        // Sunrise around 5:00 AM (300 min), sunset around 6:45 PM (1125 min)
        assertTrue(times!!.sunriseMinuteOfDay in 270..360)
        assertTrue(times.sunsetMinuteOfDay in 1050..1170)
    }
}
