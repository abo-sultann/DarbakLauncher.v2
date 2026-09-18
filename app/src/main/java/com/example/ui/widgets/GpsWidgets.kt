package com.example.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsNotFixed
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GpsTelemetry
import com.example.model.WidgetStyle
import com.example.model.toFamily
import com.example.ui.components.ResolvedWidgetColors
import com.example.ui.components.resolvedWidgetColors
import com.example.ui.theme.*
import com.example.util.bearingToArabicDirection
import java.util.Locale

@Composable
fun GpsWidget(
    style: WidgetStyle,
    gpsTelemetry: GpsTelemetry,
    modifier: Modifier = Modifier
) {
    val colors = resolvedWidgetColors()
    val family = style.toFamily()
    val hasFix = gpsTelemetry.hasGpsFix
    val latStr = if (hasFix) String.format(Locale.US, "%.5f", gpsTelemetry.latitude) else "—"
    val lngStr = if (hasFix) String.format(Locale.US, "%.5f", gpsTelemetry.longitude) else "—"
    val altitudeStr = if (hasFix) "${gpsTelemetry.altitudeMeters.toInt()} م" else "—"
    val accuracyStr = if (hasFix) "±${gpsTelemetry.accuracyMeters.toInt()} م" else "—"
    val directionStr = if (hasFix) bearingToArabicDirection(gpsTelemetry.bearingDegrees) else "—"
    val satellitesStr = if (hasFix) gpsTelemetry.satellitesCount.toString() else "—"

    BoxWithConstraints(
        modifier = modifier.fillMaxSize().padding(DarbakWidgetDesignTokens.ContentPadding),
        contentAlignment = Alignment.Center
    ) {
        val size = WidgetSizeCategory.from(maxWidth, maxHeight)
        when (family) {
            WidgetFamily.MINIMAL -> GpsMinimalLayout(
                hasFix = hasFix,
                satellites = satellitesStr,
                accuracy = accuracyStr,
                status = gpsTelemetry.statusArabic,
                lat = latStr,
                lng = lngStr,
                size = size,
                colors = colors
            )
            WidgetFamily.DARBAK_CARD -> GpsCardLayout(
                hasFix = hasFix,
                satellites = satellitesStr,
                accuracy = accuracyStr,
                altitude = altitudeStr,
                direction = directionStr,
                lat = latStr,
                lng = lngStr,
                size = size,
                colors = colors
            )
            WidgetFamily.INSTRUMENT -> GpsInstrumentLayout(
                hasFix = hasFix,
                satellites = satellitesStr,
                accuracy = accuracyStr,
                altitude = altitudeStr,
                direction = directionStr,
                size = size,
                colors = colors
            )
        }
    }
}

@Composable
private fun GpsMinimalLayout(
    hasFix: Boolean,
    satellites: String,
    accuracy: String,
    status: String,
    lat: String,
    lng: String,
    size: WidgetSizeCategory,
    colors: ResolvedWidgetColors
) {
    when (size) {
        WidgetSizeCategory.COMPACT -> Row(
            Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                Modifier.size(9.dp).clip(CircleShape)
                    .background(if (hasFix) EmeraldSafe else TextMuted)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                if (hasFix) "GPS • $satellites" else "GPS —",
                color = colors.primary,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                maxLines = 1
            )
        }
        WidgetSizeCategory.MEDIUM -> Row(
            Modifier.fillMaxSize().padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(if (hasFix) Icons.Default.GpsFixed else Icons.Default.GpsNotFixed, null, tint = if (hasFix) EmeraldSafe else colors.secondary, modifier = Modifier.size(19.dp))
                Text(if (hasFix) "GPS متصل" else "بانتظار GPS", color = colors.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Text(accuracy, color = colors.secondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        WidgetSizeCategory.LARGE_WIDE -> Column(
            Modifier.fillMaxSize().padding(4.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(if (hasFix) Icons.Default.GpsFixed else Icons.Default.GpsNotFixed, null, tint = if (hasFix) EmeraldSafe else colors.secondary, modifier = Modifier.size(19.dp))
                    Text(if (hasFix) "GPS متصل" else "GPS غير مثبت", color = colors.primary, fontWeight = FontWeight.Black, fontSize = 13.sp)
                }
                Text(if (hasFix) "$accuracy • $satellites قمر" else status, color = colors.secondary, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            if (hasFix) {
                Spacer(Modifier.height(4.dp))
                Text("$lat  •  $lng", color = colors.secondary, fontSize = 10.sp, maxLines = 1)
            }
        }
    }
}

@Composable
private fun GpsCardLayout(
    hasFix: Boolean,
    satellites: String,
    accuracy: String,
    altitude: String,
    direction: String,
    lat: String,
    lng: String,
    size: WidgetSizeCategory,
    colors: ResolvedWidgetColors
) {
    Surface(
        color = DarbakWidgetDesignTokens.cardSurface(),
        shape = DarbakWidgetDesignTokens.CardRadius,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarbakWidgetDesignTokens.cardBorder()),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            Modifier.fillMaxSize().padding(if (size == WidgetSizeCategory.COMPACT) 7.dp else 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("GPS", style = DarbakWidgetDesignTokens.Typography.labelMedium, color = colors.accent)
                Icon(if (hasFix) Icons.Default.GpsFixed else Icons.Default.GpsNotFixed, null, tint = if (hasFix) EmeraldSafe else TextMuted, modifier = Modifier.size(16.dp))
            }

            when (size) {
                WidgetSizeCategory.COMPACT -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    GpsMetric("الدقة", accuracy, colors)
                    GpsMetric("الأقمار", satellites, colors)
                }
                WidgetSizeCategory.MEDIUM -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    GpsMetric("الدقة", accuracy, colors)
                    GpsMetric("الارتفاع", altitude, colors)
                    GpsMetric("الاتجاه", direction, colors)
                }
                WidgetSizeCategory.LARGE_WIDE -> {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        GpsMetric("الدقة", accuracy, colors)
                        GpsMetric("الارتفاع", altitude, colors)
                        GpsMetric("الأقمار", satellites, colors)
                        GpsMetric("الاتجاه", direction, colors)
                    }
                    if (hasFix) {
                        Text("$lat  •  $lng", style = DarbakWidgetDesignTokens.Typography.bodySmall, color = colors.secondary, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
private fun GpsInstrumentLayout(
    hasFix: Boolean,
    satellites: String,
    accuracy: String,
    altitude: String,
    direction: String,
    size: WidgetSizeCategory,
    colors: ResolvedWidgetColors
) {
    Surface(
        color = DarbakWidgetDesignTokens.instrumentSurface(),
        shape = DarbakWidgetDesignTokens.InstrumentRadius,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarbakWidgetDesignTokens.instrumentBorder()),
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text("دقة GPS", style = DarbakWidgetDesignTokens.Typography.labelSmall, color = colors.secondary)
                Text(
                    accuracy,
                    style = if (size == WidgetSizeCategory.COMPACT) DarbakWidgetDesignTokens.Typography.titleMedium else DarbakWidgetDesignTokens.TextStyleDominantNumber,
                    color = if (hasFix) colors.primary else TextMuted,
                    maxLines = 1
                )
            }
            if (size != WidgetSizeCategory.COMPACT) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(if (hasFix) "$satellites قمر" else "لا توجد إشارة", style = DarbakWidgetDesignTokens.Typography.labelMedium, color = if (hasFix) EmeraldSafe else TextMuted)
                    Text("$altitude • $direction", style = DarbakWidgetDesignTokens.Typography.bodySmall, color = colors.secondary, maxLines = 1)
                }
            }
        }
    }
}

@Composable
fun OffroadInstrumentsWidget(
    style: WidgetStyle,
    gpsTelemetry: GpsTelemetry,
    modifier: Modifier = Modifier
) {
    val colors = resolvedWidgetColors()
    val family = style.toFamily()
    val hasFix = gpsTelemetry.hasGpsFix
    val isMoving = hasFix && gpsTelemetry.speedKmH >= 3f
    val activeHeading = if (isMoving) gpsTelemetry.bearingDegrees else gpsTelemetry.sensorHeadingDegrees
    val headingDegrees = activeHeading?.let { ((it % 360f) + 360f) % 360f }
    val direction = headingDegrees?.let { bearingToArabicDirection(it) } ?: "غير متاح"
    val headingValue = headingDegrees?.let { "${it.toInt()}°" } ?: "—"
    val altitude = if (hasFix) "${gpsTelemetry.altitudeMeters.toInt()} م" else "—"
    val accuracy = if (hasFix) "±${gpsTelemetry.accuracyMeters.toInt()} م" else "—"
    val source = when {
        isMoving -> "اتجاه الحركة"
        headingDegrees != null -> "بوصلة الجهاز"
        else -> "الاتجاه غير متاح"
    }
    val quality = when {
        !hasFix -> "GPS غير مثبت"
        gpsTelemetry.accuracyMeters <= 10f -> "GPS ممتاز"
        gpsTelemetry.accuracyMeters <= 25f -> "GPS جيد"
        else -> "GPS متوسط"
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize().padding(DarbakWidgetDesignTokens.ContentPadding),
        contentAlignment = Alignment.Center
    ) {
        val size = WidgetSizeCategory.from(maxWidth, maxHeight)
        when (family) {
            WidgetFamily.MINIMAL -> OffroadMinimalLayout(
                headingValue, direction, altitude, source, size, colors
            )
            WidgetFamily.DARBAK_CARD -> OffroadCardLayout(
                headingValue, direction, altitude, accuracy, quality, source, size, colors
            )
            WidgetFamily.INSTRUMENT -> OffroadInstrumentLayout(
                headingValue, direction, altitude, accuracy, quality, source, size, colors
            )
        }
    }
}

@Composable
private fun OffroadMinimalLayout(
    heading: String,
    direction: String,
    altitude: String,
    source: String,
    size: WidgetSizeCategory,
    colors: ResolvedWidgetColors
) {
    Row(
        Modifier.fillMaxSize().padding(horizontal = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Icon(Icons.Default.Explore, null, tint = colors.accent, modifier = Modifier.size(20.dp))
            Column {
                Text("$heading $direction", color = colors.primary, fontWeight = FontWeight.Black, fontSize = if (size == WidgetSizeCategory.COMPACT) 12.sp else 15.sp, maxLines = 1)
                if (size == WidgetSizeCategory.LARGE_WIDE) Text(source, color = colors.secondary, fontSize = 8.sp)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(Icons.Default.Terrain, null, tint = colors.accent, modifier = Modifier.size(18.dp))
            Text(altitude, color = colors.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
        }
    }
}

@Composable
private fun OffroadCardLayout(
    heading: String,
    direction: String,
    altitude: String,
    accuracy: String,
    quality: String,
    source: String,
    size: WidgetSizeCategory,
    colors: ResolvedWidgetColors
) {
    Surface(
        color = DarbakWidgetDesignTokens.cardSurface(),
        shape = DarbakWidgetDesignTokens.CardRadius,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarbakWidgetDesignTokens.cardBorder()),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            Modifier.fillMaxSize().padding(if (size == WidgetSizeCategory.COMPACT) 7.dp else 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Icon(Icons.Default.Explore, null, tint = colors.accent, modifier = Modifier.size(17.dp))
                    Text("أجهزة البر", style = DarbakWidgetDesignTokens.Typography.labelMedium, color = colors.accent)
                }
                Text(quality, style = DarbakWidgetDesignTokens.Typography.bodySmall, color = if (quality.contains("غير")) TextMuted else EmeraldSafe)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                GpsMetric("الاتجاه", "$heading $direction", colors)
                GpsMetric("الارتفاع", altitude, colors)
                if (size != WidgetSizeCategory.COMPACT) GpsMetric("الدقة", accuracy, colors)
            }
            if (size == WidgetSizeCategory.LARGE_WIDE) {
                Text(source, style = DarbakWidgetDesignTokens.Typography.bodySmall, color = colors.secondary)
            }
        }
    }
}

@Composable
private fun OffroadInstrumentLayout(
    heading: String,
    direction: String,
    altitude: String,
    accuracy: String,
    quality: String,
    source: String,
    size: WidgetSizeCategory,
    colors: ResolvedWidgetColors
) {
    Surface(
        color = DarbakWidgetDesignTokens.instrumentSurface(),
        shape = DarbakWidgetDesignTokens.InstrumentRadius,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarbakWidgetDesignTokens.instrumentBorder()),
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text("الاتجاه", style = DarbakWidgetDesignTokens.Typography.labelSmall, color = colors.secondary)
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(heading, style = if (size == WidgetSizeCategory.COMPACT) DarbakWidgetDesignTokens.Typography.titleLarge else DarbakWidgetDesignTokens.TextStyleDominantNumber, color = colors.primary, maxLines = 1)
                    Text(direction, style = DarbakWidgetDesignTokens.Typography.labelMedium, color = colors.accent, maxLines = 1)
                }
                if (size == WidgetSizeCategory.LARGE_WIDE) Text(source, style = DarbakWidgetDesignTokens.Typography.bodySmall, color = colors.secondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Terrain, null, tint = colors.accent, modifier = Modifier.size(17.dp))
                    Text(altitude, style = DarbakWidgetDesignTokens.Typography.titleMedium, color = colors.primary)
                }
                if (size != WidgetSizeCategory.COMPACT) {
                    Text("$accuracy • $quality", style = DarbakWidgetDesignTokens.Typography.bodySmall, color = colors.secondary, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun GpsMetric(label: String, value: String, colors: ResolvedWidgetColors) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = DarbakWidgetDesignTokens.Typography.labelSmall, color = colors.secondary, maxLines = 1)
        Text(value, style = DarbakWidgetDesignTokens.Typography.titleMedium, color = colors.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
