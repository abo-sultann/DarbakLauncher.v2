package com.example.ui.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TripData
import com.example.model.WidgetStyle
import com.example.model.toFamily
import com.example.ui.components.ResolvedWidgetColors
import com.example.ui.components.resolvedWidgetColors
import com.example.ui.theme.DarbakWidgetDesignTokens
import com.example.ui.theme.WidgetFamily
import com.example.ui.theme.WidgetSizeCategory
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.TextMuted
import java.util.Locale

@Composable
fun TripWidget(
    style: WidgetStyle,
    tripData: TripData,
    onStartTrip: () -> Unit,
    onPauseTrip: () -> Unit,
    onResetTrip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val widgetColors = resolvedWidgetColors()
    val distanceStr = String.format(Locale.US, "%.1f", tripData.distanceKm)
    val movingMinutes = tripData.elapsedMovingTimeSec / 60
    val movingSeconds = tripData.elapsedMovingTimeSec % 60
    val durationStr = String.format(Locale.US, "%02d:%02d", movingMinutes, movingSeconds)

    val family = style.toFamily()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(DarbakWidgetDesignTokens.ContentPadding),
        contentAlignment = Alignment.Center
    ) {
        val sizeCategory = WidgetSizeCategory.from(maxWidth, maxHeight)

        when (family) {
            WidgetFamily.MINIMAL -> {
                TripMinimalLayout(
                    distanceStr = distanceStr,
                    durationStr = durationStr,
                    speedKmH = tripData.currentSpeedKmH,
                    sizeCategory = sizeCategory,
                    widgetColors = widgetColors
                )
            }
            WidgetFamily.DARBAK_CARD -> {
                TripCardLayout(
                    tripData = tripData,
                    distanceStr = distanceStr,
                    durationStr = durationStr,
                    sizeCategory = sizeCategory,
                    onStartTrip = onStartTrip,
                    onPauseTrip = onPauseTrip,
                    onResetTrip = onResetTrip,
                    widgetColors = widgetColors
                )
            }
            WidgetFamily.INSTRUMENT -> {
                TripInstrumentLayout(
                    tripData = tripData,
                    distanceStr = distanceStr,
                    durationStr = durationStr,
                    sizeCategory = sizeCategory,
                    widgetColors = widgetColors
                )
            }
        }
    }
}

@Composable
private fun TripMinimalLayout(
    distanceStr: String,
    durationStr: String,
    speedKmH: Float,
    sizeCategory: WidgetSizeCategory,
    widgetColors: ResolvedWidgetColors
) {
    Row(
        modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("المسافة", style = DarbakWidgetDesignTokens.Typography.labelSmall, color = widgetColors.secondary)
            Text("$distanceStr كم", style = DarbakWidgetDesignTokens.Typography.titleMedium, color = widgetColors.accent)
        }
        VerticalDivider(modifier = Modifier.height(28.dp), color = widgetColors.primary.copy(alpha = 0.2f))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("الزمن", style = DarbakWidgetDesignTokens.Typography.labelSmall, color = widgetColors.secondary)
            Text(durationStr, style = DarbakWidgetDesignTokens.Typography.titleMedium, color = widgetColors.primary)
        }
    }
}

@Composable
private fun TripCardLayout(
    tripData: TripData,
    distanceStr: String,
    durationStr: String,
    sizeCategory: WidgetSizeCategory,
    onStartTrip: () -> Unit,
    onPauseTrip: () -> Unit,
    onResetTrip: () -> Unit,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("بيانات الرحلة", style = DarbakWidgetDesignTokens.Typography.labelSmall, color = widgetColors.accent)
                Text(
                    if (tripData.isRunning) "قيد التسجيل" else "متوقف",
                    style = DarbakWidgetDesignTokens.Typography.labelSmall,
                    color = if (tripData.isRunning) EmeraldSafe else TextMuted
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("$distanceStr كم", style = DarbakWidgetDesignTokens.Typography.titleLarge, color = widgetColors.primary)
                    Text("الزمن: $durationStr", style = DarbakWidgetDesignTokens.Typography.bodySmall, color = widgetColors.secondary)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = if (tripData.isRunning && !tripData.isPaused) onPauseTrip else onStartTrip,
                        modifier = Modifier.size(36.dp).testTag("btn_trip_toggle")
                    ) {
                        Icon(
                            imageVector = if (tripData.isRunning && !tripData.isPaused) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                            contentDescription = "بدء / إيقاف الرحلة",
                            tint = widgetColors.accent,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    IconButton(
                        onClick = onResetTrip,
                        modifier = Modifier.size(36.dp).testTag("btn_trip_reset")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "إعادة ضبط",
                            tint = widgetColors.secondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TripInstrumentLayout(
    tripData: TripData,
    distanceStr: String,
    durationStr: String,
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
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("المسافة", style = DarbakWidgetDesignTokens.Typography.labelSmall, color = widgetColors.secondary)
                Text("$distanceStr كم", style = DarbakWidgetDesignTokens.Typography.titleLarge, color = widgetColors.accent)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("المتوسط", style = DarbakWidgetDesignTokens.Typography.labelSmall, color = widgetColors.secondary)
                Text("${tripData.averageSpeedKmH.toInt()} كم/س", style = DarbakWidgetDesignTokens.Typography.titleLarge, color = widgetColors.primary)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("الزمن", style = DarbakWidgetDesignTokens.Typography.labelSmall, color = widgetColors.secondary)
                Text(durationStr, style = DarbakWidgetDesignTokens.Typography.titleLarge, color = widgetColors.primary)
            }
        }
    }
}
