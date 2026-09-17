package com.example.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MusicPlaybackState
import com.example.model.WidgetStyle
import com.example.model.toFamily
import com.example.ui.components.ResolvedWidgetColors
import com.example.ui.components.resolvedWidgetColors
import com.example.ui.theme.DarbakWidgetDesignTokens
import com.example.ui.theme.WidgetFamily
import com.example.ui.theme.WidgetSizeCategory
import com.example.ui.theme.CarbonDark

@Composable
fun ControlsWidget(
    style: WidgetStyle,
    playbackState: MusicPlaybackState,
    onVolumeAdjust: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    modifier: Modifier = Modifier
) {
    val widgetColors = resolvedWidgetColors()
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircleControlButton(icon = Icons.Default.VolumeDown, label = "خفض", onClick = { onVolumeAdjust(-1f) })
                    CircleControlButton(
                        icon = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        label = "تشغيل",
                        tint = widgetColors.accent,
                        isPrimary = true,
                        onClick = onTogglePlayPause
                    )
                    CircleControlButton(icon = Icons.Default.VolumeUp, label = "رفع", onClick = { onVolumeAdjust(1f) })
                }
            }
            WidgetFamily.DARBAK_CARD -> {
                Surface(
                    color = DarbakWidgetDesignTokens.cardSurface(),
                    shape = DarbakWidgetDesignTokens.CardRadius,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarbakWidgetDesignTokens.cardBorder()),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "لوحة التحكم السريع", style = DarbakWidgetDesignTokens.Typography.labelSmall, color = widgetColors.accent)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { onVolumeAdjust(-1f) }) {
                                Icon(Icons.Default.VolumeDown, contentDescription = "خفض", tint = widgetColors.primary)
                            }
                            IconButton(onClick = onToggleMute) {
                                Icon(Icons.Default.VolumeOff, contentDescription = "كتم", tint = widgetColors.secondary)
                            }
                            IconButton(onClick = onTogglePlayPause) {
                                Icon(
                                    imageVector = if (playbackState.isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                    contentDescription = "تشغيل",
                                    tint = widgetColors.accent,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            IconButton(onClick = { onVolumeAdjust(1f) }) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "رفع", tint = widgetColors.primary)
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
                    Row(
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onVolumeAdjust(-1f) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarbakWidgetDesignTokens.cardSurface()),
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.VolumeDown, contentDescription = "خفض", tint = widgetColors.primary, modifier = Modifier.size(22.dp))
                        }
                        Button(
                            onClick = onTogglePlayPause,
                            colors = ButtonDefaults.buttonColors(containerColor = widgetColors.accent),
                            modifier = Modifier.weight(1.2f).fillMaxHeight(),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "تشغيل/إيقاف",
                                tint = CarbonDark,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Button(
                            onClick = { onVolumeAdjust(1f) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarbakWidgetDesignTokens.cardSurface()),
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "رفع", tint = widgetColors.primary, modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CircleControlButton(
    icon: ImageVector,
    label: String,
    tint: Color? = null,
    isPrimary: Boolean = false,
    onClick: () -> Unit
) {
    val widgetColors = resolvedWidgetColors()
    Surface(
        color = if (isPrimary) widgetColors.accent.copy(alpha = 0.25f) else DarbakWidgetDesignTokens.cardSurface(),
        shape = CircleShape,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isPrimary) widgetColors.accent else DarbakWidgetDesignTokens.cardBorder()),
        modifier = Modifier.size(40.dp)
    ) {
        IconButton(onClick = onClick) {
            Icon(imageVector = icon, contentDescription = label, tint = tint ?: widgetColors.primary, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun HeadUnitVitalsWidget(
    style: WidgetStyle,
    vitals: com.example.data.HeadUnitVitals,
    modifier: Modifier = Modifier
) {
    val widgetColors = resolvedWidgetColors()
    val ramStr = if (vitals.isRamAvailable) "${vitals.ramUsedPercent}% (${vitals.ramAvailableMb} MB متاح)" else "غير متاح"
    val storageStr = if (vitals.isStorageAvailable) "${vitals.storageUsedPercent}% (${String.format(java.util.Locale.US, "%.1f", vitals.storageAvailableGb)} GB متاح)" else "غير متاح"
    val cpuStr = if (vitals.isCpuLoadAvailable) "${vitals.cpuLoadPercent}%" else "غير متاح"
    val tempStr = if (vitals.isTemperatureAvailable) "${vitals.temperatureCelsius.toInt()}°م (${vitals.temperatureSourceArabic})" else "غير متاح"

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
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Memory, null, tint = widgetColors.accent, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("RAM: ${vitals.ramUsedPercent}%", style = DarbakWidgetDesignTokens.Typography.titleMedium, color = widgetColors.primary)
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
                        modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("RAM", style = DarbakWidgetDesignTokens.Typography.labelSmall, color = widgetColors.secondary)
                            Text(if (vitals.isRamAvailable) "${vitals.ramUsedPercent}%" else "غير متاح", style = DarbakWidgetDesignTokens.Typography.titleMedium, color = widgetColors.primary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("التخزين", style = DarbakWidgetDesignTokens.Typography.labelSmall, color = widgetColors.secondary)
                            Text(if (vitals.isStorageAvailable) "${vitals.storageUsedPercent}%" else "غير متاح", style = DarbakWidgetDesignTokens.Typography.titleMedium, color = widgetColors.primary)
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
                        modifier = Modifier.fillMaxSize().padding(10.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Memory, null, tint = widgetColors.accent, modifier = Modifier.size(16.dp))
                                Text("حالة النظام والجهاز", style = DarbakWidgetDesignTokens.Typography.labelMedium, color = widgetColors.accent)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("ذاكرة RAM", style = DarbakWidgetDesignTokens.Typography.labelSmall, color = widgetColors.secondary)
                                Text(ramStr, style = DarbakWidgetDesignTokens.Typography.bodySmall, color = widgetColors.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Column(Modifier.weight(1f)) {
                                Text("التخزين", style = DarbakWidgetDesignTokens.Typography.labelSmall, color = widgetColors.secondary)
                                Text(storageStr, style = DarbakWidgetDesignTokens.Typography.bodySmall, color = widgetColors.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("استهلاك CPU", style = DarbakWidgetDesignTokens.Typography.labelSmall, color = widgetColors.secondary)
                                Text(cpuStr, style = DarbakWidgetDesignTokens.Typography.bodySmall, color = widgetColors.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Column(Modifier.weight(1f)) {
                                Text("حرارة الجهاز", style = DarbakWidgetDesignTokens.Typography.labelSmall, color = widgetColors.secondary)
                                Text(tempStr, style = DarbakWidgetDesignTokens.Typography.bodySmall, color = widgetColors.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        }
    }
}
