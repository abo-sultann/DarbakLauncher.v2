package com.example.util

import java.util.Calendar
import kotlin.math.*

object SolarCalculator {

    data class SolarTimes(
        val sunriseMinuteOfDay: Int, // e.g. 360 = 06:00 AM
        val sunsetMinuteOfDay: Int   // e.g. 1080 = 06:00 PM
    )

    /**
     * Calculates local sunrise and sunset times (in minutes from midnight) for a given location and date.
     * Uses standard zenith angle 90.833° (official sunrise/sunset).
     * Returns null if coordinates are (0,0) or if calculation is unavailable.
     */
    fun calculateSolarTimes(
        latitude: Double,
        longitude: Double,
        calendar: Calendar = Calendar.getInstance()
    ): SolarTimes? {
        if (latitude == 0.0 && longitude == 0.0) return null

        try {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val tzOffsetHours = calendar.timeZone.getOffset(calendar.timeInMillis) / 3_600_000.0

            val sunrise = computeSolarTime(year, month, day, latitude, longitude, tzOffsetHours, isSunrise = true)
            val sunset = computeSolarTime(year, month, day, latitude, longitude, tzOffsetHours, isSunrise = false)

            if (sunrise == null || sunset == null) return null

            return SolarTimes(
                sunriseMinuteOfDay = sunrise.coerceIn(0, 1439),
                sunsetMinuteOfDay = sunset.coerceIn(0, 1439)
            )
        } catch (_: Exception) {
            return null
        }
    }

    private fun computeSolarTime(
        year: Int,
        month: Int,
        day: Int,
        lat: Double,
        lng: Double,
        tzOffsetHours: Double,
        isSunrise: Boolean
    ): Int? {
        val zenith = 90.833 // Official zenith angle
        val n = floor(275.0 * month / 9.0) - floor((month + 9.0) / 12.0) * (1.0 + floor((year - 4.0 * floor(year / 4.0) + 2.0) / 3.0)) + day - 30.0
        val lngHour = lng / 15.0
        val t = if (isSunrise) n + ((6.0 - lngHour) / 24.0) else n + ((18.0 - lngHour) / 24.0)

        val m = (0.9856 * t) - 3.251
        var l = m + (1.916 * sin(toRad(m))) + (0.020 * sin(toRad(2.0 * m))) + 282.634
        l = normalizeDegrees(l)

        var ra = toDeg(atan(0.91764 * tan(toRad(l))))
        ra = normalizeDegrees(ra)

        val lQuadrant = floor(l / 90.0) * 90.0
        val raQuadrant = floor(ra / 90.0) * 90.0
        ra += (lQuadrant - raQuadrant)
        ra /= 15.0

        val sinDec = 0.39782 * sin(toRad(l))
        val cosDec = cos(asin(sinDec))

        val cosH = (cos(toRad(zenith)) - (sinDec * sin(toRad(lat)))) / (cosDec * cos(toRad(lat)))

        if (isSunrise && cosH > 1.0) return null // Sun never rises
        if (!isSunrise && cosH < -1.0) return null // Sun never sets

        val h = if (isSunrise) 360.0 - toDeg(acos(cosH.coerceIn(-1.0, 1.0))) else toDeg(acos(cosH.coerceIn(-1.0, 1.0)))
        val hHours = h / 15.0

        val localMeanTime = hHours + ra - (0.06571 * t) - 6.622
        var utcTime = localMeanTime - lngHour
        utcTime = (utcTime % 24.0 + 24.0) % 24.0

        val localTimeHours = (utcTime + tzOffsetHours + 24.0) % 24.0
        return (localTimeHours * 60.0).roundToInt()
    }

    private fun toRad(deg: Double): Double = Math.toRadians(deg)
    private fun toDeg(rad: Double): Double = Math.toDegrees(rad)
    private fun normalizeDegrees(deg: Double): Double = (deg % 360.0 + 360.0) % 360.0
}
