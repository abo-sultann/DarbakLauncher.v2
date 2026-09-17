package com.example

import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarbakWidgetDesignTokens
import com.example.ui.theme.WidgetFamily
import com.example.ui.theme.WidgetSizeCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class DarbakWidgetDesignSystemTest {

    @Test
    fun `widget size category is derived correctly from bounds`() {
        assertEquals(WidgetSizeCategory.COMPACT, WidgetSizeCategory.fromDp(120.dp, 80.dp))
        assertEquals(WidgetSizeCategory.MEDIUM, WidgetSizeCategory.fromDp(200.dp, 140.dp))
        assertEquals(WidgetSizeCategory.LARGE_WIDE, WidgetSizeCategory.fromDp(320.dp, 220.dp))
    }

    @Test
    fun `design tokens radius corresponds to family`() {
        assertEquals(DarbakWidgetDesignTokens.MinimalRadius, DarbakWidgetDesignTokens.radiusFor(WidgetFamily.MINIMAL))
        assertEquals(DarbakWidgetDesignTokens.CardRadius, DarbakWidgetDesignTokens.radiusFor(WidgetFamily.DARBAK_CARD))
        assertEquals(DarbakWidgetDesignTokens.InstrumentRadius, DarbakWidgetDesignTokens.radiusFor(WidgetFamily.INSTRUMENT))
    }
}
