package com.example.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GpsTelemetry
import com.example.model.MapItem
import com.example.model.OffroadNavigationTarget
import com.example.model.TripData
import com.example.model.WidgetStyle
import com.example.model.toFamily
import com.example.ui.components.ResolvedWidgetColors
import com.example.ui.components.resolvedWidgetColors
import com.example.ui.theme.DarbakWidgetDesignTokens
import com.example.ui.theme.WidgetFamily
import com.example.ui.theme.WidgetSizeCategory
import com.example.util.bearingToArabicDirection
import java.util.Locale

@Composable
fun MapWidget(
    style: WidgetStyle,
    gpsTelemetry: GpsTelemetry,
    tripData: TripData,
    activeMap: MapItem?,
    navigationTarget: OffroadNavigationTarget? = null,
    targetDistanceMeters: Float? = null,
    targetBearing: Float? = null,
    onOpenFullMap: () -> Unit,
    interactionEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val widgetColors = resolvedWidgetColors()
    val hasTarget = navigationTarget != null && targetDistanceMeters != null
    val direction = if (hasTarget && targetBearing != null) bearingToArabicDirection(targetBearing) else if (gpsTelemetry.hasGpsFix) bearingToArabicDirection(gpsTelemetry.bearingDegrees) else "--"
    val distance = targetDistanceMeters?.let(::formatWidgetDistance)
    val title = navigationTarget?.name ?: activeMap?.name ?: "الخريطة"

    val family = style.toFamily()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .clickable(enabled = interactionEnabled, onClick = onOpenFullMap)
            .padding(DarbakWidgetDesignTokens.ContentPadding),
        contentAlignment = Alignment.Center
    ) {
        val sizeCategory = WidgetSizeCategory.from(maxWidth, maxHeight)

        when (family) {
            WidgetFamily.MINIMAL -> {
                MapMinimalLayout(
                    title = title,
                    distance = distance,
                    direction = direction,
                    hasTarget = hasTarget,
                    targetBearing = targetBearing,
                    gpsTelemetry = gpsTelemetry,
                    sizeCategory = sizeCategory,
                    widgetColors = widgetColors
                )
            }
            WidgetFamily.DARBAK_CARD -> {
                MapCardLayout(
                    title = title,
                    distance = distance,
                    direction = direction,
                    hasTarget = hasTarget,
                    targetBearing = targetBearing,
                    gpsTelemetry = gpsTelemetry,
                    activeMap = activeMap,
                    sizeCategory = sizeCategory,
                    widgetColors = widgetColors
                )
            }
            WidgetFamily.INSTRUMENT -> {
                MapInstrumentLayout(
                    title = title,
                    distance = distance,
                    direction = direction,
                    hasTarget = hasTarget,
                    targetBearing = targetBearing,
                    gpsTelemetry = gpsTelemetry,
                    sizeCategory = sizeCategory,
                    widgetColors = widgetColors
                )
            }
        }
    }
}

@Composable
private fun MapMinimalLayout(
    title: String,
    distance: String?,
    direction: String,
    hasTarget: Boolean,
    targetBearing: Float?,
    gpsTelemetry: GpsTelemetry,
    sizeCategory: WidgetSizeCategory,
    widgetColors: ResolvedWidgetColors
) {
    Row(
        modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            color = widgetColors.accent.copy(alpha = 0.16f),
            shape = CircleShape,
            modifier = Modifier.size(if (sizeCategory == WidgetSizeCategory.COMPACT) 36.dp else 46.dp)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Navigation,
                    null,
                    tint = widgetColors.accent,
                    modifier = Modifier
                        .size(if (sizeCategory == WidgetSizeCategory.COMPACT) 22.dp else 28.dp)
                        .rotate(if (hasTarget) targetBearing ?: 0f else gpsTelemetry.bearingDegrees)
                )
            }
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            Text(
                if (hasTarget) distance ?: "--" else direction,
                color = widgetColors.primary,
                style = if (sizeCategory == WidgetSizeCategory.COMPACT) DarbakWidgetDesignTokens.Typography.titleMedium else DarbakWidgetDesignTokens.Typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                if (hasTarget) direction else if (gpsTelemetry.hasGpsFix) "اضغط لفتح الخريطة" else "بانتظار GPS",
                color = if (hasTarget) widgetColors.accent else widgetColors.secondary,
                style = DarbakWidgetDesignTokens.Typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MapCardLayout(
    title: String,
    distance: String?,
    direction: String,
    hasTarget: Boolean,
    targetBearing: Float?,
    gpsTelemetry: GpsTelemetry,
    activeMap: MapItem?,
    sizeCategory: WidgetSizeCategory,
    widgetColors: ResolvedWidgetColors
) {
    Surface(
        color = DarbakWidgetDesignTokens.cardSurface(),
        shape = DarbakWidgetDesignTokens.CardRadius,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarbakWidgetDesignTokens.cardBorder()),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Map, null, tint = widgetColors.accent, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    title,
                    color = widgetColors.primary,
                    style = DarbakWidgetDesignTokens.Typography.labelMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text("فتح", color = widgetColors.accent, style = DarbakWidgetDesignTokens.Typography.labelSmall)
            }

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Icon(
                    Icons.Default.Navigation,
                    null,
                    tint = widgetColors.accent,
                    modifier = Modifier
                        .size(if (sizeCategory == WidgetSizeCategory.COMPACT) 26.dp else 34.dp)
                        .rotate(if (hasTarget) targetBearing ?: 0f else gpsTelemetry.bearingDegrees)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        if (hasTarget) distance ?: "--" else direction,
                        color = widgetColors.primary,
                        style = if (sizeCategory == WidgetSizeCategory.COMPACT) DarbakWidgetDesignTokens.Typography.titleMedium else DarbakWidgetDesignTokens.Typography.titleLarge
                    )
                    Text(
                        if (hasTarget) direction else if (gpsTelemetry.hasGpsFix) "اتجاه السيارة" else "لا توجد إشارة",
                        color = widgetColors.secondary,
                        style = DarbakWidgetDesignTokens.Typography.bodySmall
                    )
                }
            }

            if (sizeCategory != WidgetSizeCategory.COMPACT) {
                Text(
                    if (activeMap != null) "الخريطة: ${activeMap.name}" else "Mapsforge offline",
                    color = widgetColors.secondary,
                    style = DarbakWidgetDesignTokens.Typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun MapInstrumentLayout(
    title: String,
    distance: String?,
    direction: String,
    hasTarget: Boolean,
    targetBearing: Float?,
    gpsTelemetry: GpsTelemetry,
    sizeCategory: WidgetSizeCategory,
    widgetColors: ResolvedWidgetColors
) {
    Surface(
        color = DarbakWidgetDesignTokens.instrumentSurface(),
        shape = DarbakWidgetDesignTokens.InstrumentRadius,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarbakWidgetDesignTokens.instrumentBorder()),
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center) {
                Text("الملاحة", style = DarbakWidgetDesignTokens.Typography.labelSmall, color = widgetColors.secondary)
                Text(
                    if (hasTarget) distance ?: "--" else direction,
                    style = DarbakWidgetDesignTokens.Typography.titleLarge,
                    color = widgetColors.accent
                )
                Text(
                    title,
                    style = DarbakWidgetDesignTokens.Typography.bodySmall,
                    color = widgetColors.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Navigation,
                    null,
                    tint = widgetColors.accent,
                    modifier = Modifier
                        .size(if (sizeCategory == WidgetSizeCategory.COMPACT) 30.dp else 40.dp)
                        .rotate(if (hasTarget) targetBearing ?: 0f else gpsTelemetry.bearingDegrees)
                )
            }
        }
    }
}

private fun formatWidgetDistance(meters: Float): String = if (meters < 1000f) "${meters.toInt()} م" else String.format(Locale.US, "%.1f كم", meters / 1000f)
