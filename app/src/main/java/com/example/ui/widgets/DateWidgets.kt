package com.example.ui.widgets

import android.icu.util.IslamicCalendar
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import java.util.Date
import java.util.Locale

@Composable
fun DateWidget(style: WidgetStyle, modifier: Modifier = Modifier) {
    var now by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = Date()
            delay(60_000L)
        }
    }

    val widgetColors = resolvedWidgetColors()
    val dayName = remember(now) { SimpleDateFormat("EEEE", Locale("ar")).format(now) }
    val dayNumber = remember(now) { SimpleDateFormat("d", Locale("ar")).format(now) }
    val monthYear = remember(now) { SimpleDateFormat("MMMM yyyy", Locale("ar")).format(now) }
    val gregorian = remember(now) { SimpleDateFormat("yyyy/MM/dd", Locale("ar")).format(now) }
    val hijri = remember(now) { formatHijri(now) }

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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        dayNumber,
                        color = widgetColors.accent,
                        fontSize = when (sizeCategory) {
                            WidgetSizeCategory.COMPACT -> 28.sp
                            WidgetSizeCategory.MEDIUM -> 36.sp
                            WidgetSizeCategory.LARGE_WIDE -> 48.sp
                        },
                        fontWeight = FontWeight.Black
                    )
                    Column {
                        Text(monthYear, color = widgetColors.primary, style = DarbakWidgetDesignTokens.Typography.titleMedium, maxLines = 1)
                        if (sizeCategory != WidgetSizeCategory.COMPACT) {
                            Text(dayName, color = widgetColors.secondary, style = DarbakWidgetDesignTokens.Typography.bodySmall)
                        }
                    }
                }
            }
            WidgetFamily.DARBAK_CARD -> {
                Surface(
                    color = DarbakWidgetDesignTokens.cardSurface(),
                    shape = DarbakWidgetDesignTokens.CardRadius,
                    border = BorderStroke(1.dp, DarbakWidgetDesignTokens.cardBorder()),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(Modifier.fillMaxSize().padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (sizeCategory != WidgetSizeCategory.COMPACT) {
                            Surface(color = widgetColors.accent.copy(alpha = .16f), shape = RoundedCornerShape(10.dp), modifier = Modifier.size(40.dp)) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.CalendarMonth, null, tint = widgetColors.accent, modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                            Text(dayName, color = widgetColors.primary, style = DarbakWidgetDesignTokens.Typography.titleMedium, maxLines = 1)
                            Text("$dayNumber $monthYear", color = widgetColors.accent, style = DarbakWidgetDesignTokens.Typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
            WidgetFamily.INSTRUMENT -> {
                Surface(
                    color = DarbakWidgetDesignTokens.instrumentSurface(),
                    shape = DarbakWidgetDesignTokens.InstrumentRadius,
                    border = BorderStroke(1.dp, DarbakWidgetDesignTokens.instrumentBorder()),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                        CalendarColumn("هجري", hijri, widgetColors.accent, widgetColors.primary, DarbakWidgetDesignTokens.Typography.labelSmall.fontSize, DarbakWidgetDesignTokens.Typography.titleMedium.fontSize)
                        if (sizeCategory != WidgetSizeCategory.COMPACT) {
                            Box(Modifier.width(1.dp).height(32.dp)) { Surface(color = CarbonCardBorder, modifier = Modifier.fillMaxSize()) {} }
                            CalendarColumn("ميلادي", gregorian, widgetColors.secondary, widgetColors.primary, DarbakWidgetDesignTokens.Typography.labelSmall.fontSize, DarbakWidgetDesignTokens.Typography.titleMedium.fontSize)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarColumn(
    label: String,
    value: String,
    labelColor: androidx.compose.ui.graphics.Color,
    valueColor: androidx.compose.ui.graphics.Color,
    labelSize: androidx.compose.ui.unit.TextUnit,
    valueSize: androidx.compose.ui.unit.TextUnit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = labelColor, fontSize = labelSize, fontWeight = FontWeight.Bold)
        Text(value, color = valueColor, fontSize = valueSize, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

private fun formatHijri(date: Date): String {
    return try {
        val calendar = IslamicCalendar(Locale("ar", "SA")).apply { time = date }
        val months = arrayOf("محرم", "صفر", "ربيع الأول", "ربيع الآخر", "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان", "رمضان", "شوال", "ذو القعدة", "ذو الحجة")
        val day = calendar.get(IslamicCalendar.DAY_OF_MONTH)
        val month = months[calendar.get(IslamicCalendar.MONTH).coerceIn(0, 11)]
        val year = calendar.get(IslamicCalendar.YEAR)
        "$day $month $year هـ"
    } catch (_: Exception) { "--" }
}
