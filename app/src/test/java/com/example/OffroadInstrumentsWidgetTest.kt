package com.example

import com.example.model.GpsTelemetry
import com.example.model.WidgetStyle
import com.example.model.WidgetType
import com.example.model.preferredWidgetStylesFor
import com.example.util.bearingToArabicDirection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OffroadInstrumentsWidgetTest {

    @Test
    fun `offroad instruments widget type is registered with three preferred styles`() {
        val styles = preferredWidgetStylesFor(WidgetType.OFFROAD_INSTRUMENTS)
        assertEquals(3, styles.size)
        assertTrue(WidgetStyle.OFFROAD_INSTRUMENTS_CARD in styles)
        assertTrue(WidgetStyle.OFFROAD_INSTRUMENTS_COMPACT in styles)
        assertTrue(WidgetStyle.OFFROAD_INSTRUMENTS_FULL in styles)
    }

    @Test
    fun `bearing to arabic direction converts all quadrants correctly`() {
        assertEquals("شمال", bearingToArabicDirection(0f))
        assertEquals("شمال شرقي", bearingToArabicDirection(45f))
        assertEquals("شرق", bearingToArabicDirection(90f))
        assertEquals("جنوب شرقي", bearingToArabicDirection(135f))
        assertEquals("جنوب", bearingToArabicDirection(180f))
        assertEquals("جنوب غربي", bearingToArabicDirection(225f))
        assertEquals("غرب", bearingToArabicDirection(270f))
        assertEquals("شمال غربي", bearingToArabicDirection(315f))
    }

    @Test
    fun `telemetry without fix returns no fabricated values`() {
        val telemetry = GpsTelemetry(hasGpsFix = false, latitude = 0.0, longitude = 0.0)
        assertEquals(false, telemetry.hasGpsFix)
        assertEquals(0.0, telemetry.latitude, 0.001)
    }
}
