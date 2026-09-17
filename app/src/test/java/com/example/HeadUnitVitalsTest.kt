package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.HeadUnitVitalsManager
import com.example.model.WidgetStyle
import com.example.model.WidgetType
import com.example.model.preferredWidgetStylesFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [25])
class HeadUnitVitalsTest {

    @Test
    fun `vitals manager reads system metrics safely`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val manager = HeadUnitVitalsManager(context)
        val vitals = manager.readVitals()

        assertNotNull(vitals)
        assertTrue(vitals.ramUsedPercent in 0..100)
        assertTrue(vitals.storageUsedPercent in 0..100)
    }

    @Test
    fun `head unit vitals widget registered with three preferred styles`() {
        val styles = preferredWidgetStylesFor(WidgetType.HEAD_UNIT_VITALS)
        assertEquals(3, styles.size)
        assertTrue(WidgetStyle.HEAD_UNIT_VITALS_CARD in styles)
        assertTrue(WidgetStyle.HEAD_UNIT_VITALS_COMPACT in styles)
        assertTrue(WidgetStyle.HEAD_UNIT_VITALS_MINI in styles)
    }
}
