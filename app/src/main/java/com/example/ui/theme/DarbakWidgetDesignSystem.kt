package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.resolvedWidgetSurface

enum class WidgetFamily(val arabicTitle: String, val description: String) {
    MINIMAL("بسيط (شفاف)", "عرض خفيف بدون إطار فوق خلفية الشاشة"),
    DARBAK_CARD("بطاقة دربك", "تصميم زجاجي/داكن هادئ بهوية دربك"),
    INSTRUMENT("أجهزة قيادة", "عدادات وشاشات قيادة ومؤشرات برية")
}

enum class WidgetSizeCategory {
    COMPACT,
    MEDIUM,
    LARGE_WIDE;

    companion object {
        fun fromDp(widthDp: Dp, heightDp: Dp): WidgetSizeCategory {
            return when {
                heightDp < 105.dp || widthDp < 145.dp -> COMPACT
                widthDp in 145.dp..285.dp && heightDp in 105.dp..185.dp -> MEDIUM
                else -> LARGE_WIDE
            }
        }

        fun from(widthDp: Dp, heightDp: Dp): WidgetSizeCategory = fromDp(widthDp, heightDp)
    }
}

object DarbakWidgetDesignTokens {
    val SpaceXs = 4.dp
    val SpaceSm = 8.dp
    val SpaceMd = 12.dp
    val SpaceLg = 16.dp
    val ContentPadding = 6.dp

    val MinimalRadius = RoundedCornerShape(8.dp)
    val CardRadius = RoundedCornerShape(16.dp)
    val InstrumentRadius = RoundedCornerShape(12.dp)

    fun radiusFor(family: WidgetFamily) = when (family) {
        WidgetFamily.MINIMAL -> MinimalRadius
        WidgetFamily.DARBAK_CARD -> CardRadius
        WidgetFamily.INSTRUMENT -> InstrumentRadius
    }

    object Typography {
        val titleLarge = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary
        )
        val titleMedium = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        val labelMedium = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )
        val labelSmall = TextStyle(
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
        )
        val bodySmall = TextStyle(
            fontSize = 9.sp,
            fontWeight = FontWeight.Normal,
            color = TextSecondary
        )
    }

    val TextStyleDominantNumber = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.Black,
        color = TextPrimary
    )

    val TextStyleUnitLabel = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextSecondary
    )

    val TextStyleTitle = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Black,
        color = TextPrimary
    )

    val TextStyleSecondaryMetric = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        color = TextSecondary
    )

    fun cardSurfaceColor(isNightMode: Boolean, opacity: Float): Color {
        val baseAlpha = if (isNightMode) (.75f + opacity * .20f).coerceAtMost(.96f) else (.55f + opacity * .35f).coerceAtMost(.90f)
        return CarbonDark.copy(alpha = baseAlpha)
    }

    fun instrumentSurfaceColor(isNightMode: Boolean, opacity: Float): Color {
        val baseAlpha = if (isNightMode) (.82f + opacity * .15f).coerceAtMost(.98f) else (.68f + opacity * .28f).coerceAtMost(.94f)
        return Color(0xFF080C12).copy(alpha = baseAlpha)
    }

    @Composable
    fun cardSurface(): Color = resolvedWidgetSurface(CarbonSurface.copy(alpha = 0.90f))

    @Composable
    fun cardBorder(): Color = CarbonCardBorder

    @Composable
    fun instrumentSurface(): Color = resolvedWidgetSurface(Color(0xFF080C12).copy(alpha = 0.92f))

    @Composable
    fun instrumentBorder(): Color = CarbonCardBorder.copy(alpha = 0.6f)
}
