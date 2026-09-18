package com.example.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WidgetStyle
import com.example.model.toFamily
import com.example.ui.components.ResolvedWidgetColors
import com.example.ui.components.resolvedWidgetColors
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClockWidget(style: WidgetStyle, is24Hour: Boolean, modifier: Modifier = Modifier) {
    var currentTime by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            delay(1000L)
        }
    }

    val widgetColors = resolvedWidgetColors()
    val timeFormat = if (is24Hour) "HH:mm" else "hh:mm"
    val timeWithSecFormat = if (is24Hour) "HH:mm:ss" else "hh:mm:ss"
    val timeStr = SimpleDateFormat(timeFormat, Locale("ar")).format(currentTime)
    val timeWithSecStr = SimpleDateFormat(timeWithSecFormat, Locale("ar")).format(currentTime)
    val amPmStr = SimpleDateFormat("a", Locale("ar")).format(currentTime)
    val dateStr = SimpleDateFormat("yyyy/MM/dd", Locale("ar")).format(currentTime)
    val dayStr = SimpleDateFormat("EEEE", Locale("ar")).format(currentTime)

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
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        timeStr,
                        fontSize = when (sizeCategory) {
                            WidgetSizeCategory.COMPACT -> 28.sp
                            WidgetSizeCategory.MEDIUM -> 38.sp
                            WidgetSizeCategory.LARGE_WIDE -> 52.sp
                        },
                        fontWeight = FontWeight.Black,
                        color = widgetColors.primary,
                        maxLines = 1
                    )
                    if (!is24Hour && sizeCategory != WidgetSizeCategory.COMPACT) {
                        Text(amPmStr, fontSize = 10.sp, color = widgetColors.secondary, modifier = Modifier.padding(bottom = 4.dp))
                    }
                }
            }
            WidgetFamily.DARBAK_CARD -> {
                Surface(
                    color = DarbakWidgetDesignTokens.cardSurface(),
                    shape = DarbakWidgetDesignTokens.CardRadius,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarbakWidgetDesignTokens.cardBorder()),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        if (sizeCategory != WidgetSizeCategory.COMPACT) {
                            Icon(Icons.Default.AccessTime, null, tint = widgetColors.accent, modifier = Modifier.size(28.dp))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                timeStr,
                                fontSize = if (sizeCategory == WidgetSizeCategory.COMPACT) 26.sp else 34.sp,
                                fontWeight = FontWeight.Bold,
                                color = widgetColors.primary,
                                maxLines = 1
                            )
                            if (sizeCategory != WidgetSizeCategory.COMPACT) {
                                Text("$dayStr • $dateStr", color = widgetColors.accent, style = DarbakWidgetDesignTokens.Typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
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
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                timeStr,
                                fontSize = if (sizeCategory == WidgetSizeCategory.COMPACT) 30.sp else 44.sp,
                                fontWeight = FontWeight.Black,
                                color = widgetColors.accent,
                                maxLines = 1
                            )
                            if (!is24Hour && sizeCategory != WidgetSizeCategory.COMPACT) {
                                Text(amPmStr, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = widgetColors.secondary, modifier = Modifier.padding(bottom = 5.dp))
                            }
                        }
                        if (sizeCategory != WidgetSizeCategory.COMPACT) {
                            Text("$dayStr • $dateStr", color = widgetColors.primary, style = DarbakWidgetDesignTokens.Typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }
    }
}
