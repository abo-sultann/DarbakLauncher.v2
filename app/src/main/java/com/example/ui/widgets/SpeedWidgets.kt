package com.example.ui.widgets

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GpsTelemetry
import com.example.model.TripData
import com.example.model.WidgetStyle
import com.example.model.toFamily
import com.example.ui.components.ResolvedWidgetColors
import com.example.ui.components.resolvedWidgetColors
import com.example.ui.theme.*

@Composable
fun SpeedWidget(
    style: WidgetStyle,
    gpsTelemetry: GpsTelemetry,
    tripData: TripData,
    speedUnit: String = "كم/س",
    modifier: Modifier = Modifier
) {
    val widgetColors = resolvedWidgetColors()
    val trusted = gpsTelemetry.hasGpsFix && gpsTelemetry.isSpeedReliable
    val currentSpeed = if (trusted) gpsTelemetry.speedKmH.coerceIn(0f, 180f) else 0f
    val animatedSpeed by animateFloatAsState(targetValue = currentSpeed, animationSpec = tween(300), label = "speed")
    val display = if (trusted) animatedSpeed.toInt().toString() else "--"

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
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(
                        text = display,
                        fontSize = when (sizeCategory) {
                            WidgetSizeCategory.COMPACT -> 32.sp
                            WidgetSizeCategory.MEDIUM -> 44.sp
                            WidgetSizeCategory.LARGE_WIDE -> 60.sp
                        },
                        fontWeight = FontWeight.Black,
                        color = widgetColors.primary,
                        maxLines = 1
                    )
                    Text(
                        text = speedUnit,
                        style = DarbakWidgetDesignTokens.Typography.labelSmall,
                        color = widgetColors.secondary
                    )
                }
            }
            WidgetFamily.DARBAK_CARD -> {
                Surface(
                    color = DarbakWidgetDesignTokens.cardSurface(),
                    shape = DarbakWidgetDesignTokens.CardRadius,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarbakWidgetDesignTokens.cardBorder()),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Canvas(Modifier.fillMaxSize().padding(8.dp)) {
                            val stroke = 8.dp.toPx()
                            val diameter = minOf(size.width, size.height) - stroke
                            val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
                            val arcSize = Size(diameter, diameter)
                            drawArc(CarbonCardBorder, 150f, 240f, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Round))
                            val fraction = (animatedSpeed / 180f).coerceIn(0f, 1f)
                            drawArc(
                                Brush.sweepGradient(listOf(widgetColors.accent.copy(alpha = .45f), widgetColors.accent), Offset(size.width / 2f, size.height / 2f)),
                                150f, fraction * 240f, false, topLeft, arcSize, style = Stroke(stroke, cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(display, fontSize = if (sizeCategory == WidgetSizeCategory.COMPACT) 26.sp else 36.sp, fontWeight = FontWeight.Black, color = widgetColors.primary, maxLines = 1)
                            Text(speedUnit, style = DarbakWidgetDesignTokens.Typography.labelSmall, color = widgetColors.accent)
                        }
                    }
                }
            }
            WidgetFamily.INSTRUMENT -> {
                Surface(
                    color = DarbakWidgetDesignTokens.instrumentSurface(),
                    shape = DarbakWidgetDesignTokens.InstrumentRadius,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarbakWidgetDesignTokens.instrumentBorder()),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        Modifier.fillMaxSize().padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("السرعة", style = DarbakWidgetDesignTokens.Typography.labelSmall, color = widgetColors.accent)
                            Icon(Icons.Default.Speed, null, tint = widgetColors.accent, modifier = Modifier.size(16.dp))
                        }
                        Text(display, fontSize = if (sizeCategory == WidgetSizeCategory.COMPACT) 30.sp else 44.sp, fontWeight = FontWeight.Black, color = widgetColors.primary, maxLines = 1)
                        Text("$speedUnit • أعلى ${tripData.maxSpeedKmH.toInt()}", style = DarbakWidgetDesignTokens.Typography.bodySmall, color = widgetColors.secondary, maxLines = 1)
                    }
                }
            }
        }
    }
}
