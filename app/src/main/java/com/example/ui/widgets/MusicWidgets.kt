package com.example.ui.widgets

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
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
fun MusicWidget(
    style: WidgetStyle,
    playbackState: MusicPlaybackState,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val widgetColors = resolvedWidgetColors()
    val track = playbackState.currentTrack
    val title = if (playbackState.isExternalSession && playbackState.externalTitle.isNotBlank()) playbackState.externalTitle else (track?.title ?: "لا توجد موسيقى مشغلة")
    val artist = if (playbackState.isExternalSession && playbackState.externalArtist.isNotBlank()) playbackState.externalArtist else (track?.artist ?: "أضف ملفًا صوتيًا من الإعدادات")
    val isPlaying = playbackState.isPlaying

    val progressFraction = if (playbackState.durationMs > 0) {
        (playbackState.currentPositionMs.toFloat() / playbackState.durationMs.toFloat()).coerceIn(0f, 1f)
    } else 0f

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
                MusicMinimalLayout(
                    title = title,
                    artist = artist,
                    isPlaying = isPlaying,
                    progressFraction = progressFraction,
                    sizeCategory = sizeCategory,
                    onTogglePlayPause = onTogglePlayPause,
                    onNext = onNext,
                    onPrevious = onPrevious,
                    widgetColors = widgetColors
                )
            }
            WidgetFamily.DARBAK_CARD -> {
                MusicCardLayout(
                    title = title,
                    artist = artist,
                    isPlaying = isPlaying,
                    progressFraction = progressFraction,
                    sizeCategory = sizeCategory,
                    onTogglePlayPause = onTogglePlayPause,
                    onNext = onNext,
                    onPrevious = onPrevious,
                    onSeek = onSeek,
                    playbackState = playbackState,
                    widgetColors = widgetColors
                )
            }
            WidgetFamily.INSTRUMENT -> {
                MusicInstrumentLayout(
                    title = title,
                    artist = artist,
                    isPlaying = isPlaying,
                    progressFraction = progressFraction,
                    sizeCategory = sizeCategory,
                    onTogglePlayPause = onTogglePlayPause,
                    onNext = onNext,
                    onPrevious = onPrevious,
                    widgetColors = widgetColors
                )
            }
        }
    }
}

@Composable
private fun MusicMinimalLayout(
    title: String,
    artist: String,
    isPlaying: Boolean,
    progressFraction: Float,
    sizeCategory: WidgetSizeCategory,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    widgetColors: ResolvedWidgetColors
) {
    Row(
        modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        IconButton(onClick = onPrevious, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.SkipNext, contentDescription = "السابق", tint = widgetColors.primary, modifier = Modifier.size(20.dp))
        }
        Surface(
            onClick = onTogglePlayPause,
            color = widgetColors.accent.copy(alpha = 0.16f),
            shape = CircleShape,
            modifier = Modifier.size(if (sizeCategory == WidgetSizeCategory.COMPACT) 36.dp else 42.dp)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "تشغيل/إيقاف",
                    tint = widgetColors.accent,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        IconButton(onClick = onNext, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.SkipPrevious, contentDescription = "التالي", tint = widgetColors.primary, modifier = Modifier.size(20.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, color = widgetColors.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (sizeCategory != WidgetSizeCategory.COMPACT) {
                Text(artist, color = widgetColors.secondary, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun MusicCardLayout(
    title: String,
    artist: String,
    isPlaying: Boolean,
    progressFraction: Float,
    sizeCategory: WidgetSizeCategory,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    playbackState: MusicPlaybackState,
    widgetColors: ResolvedWidgetColors
) {
    Surface(
        color = DarbakWidgetDesignTokens.cardSurface(),
        shape = DarbakWidgetDesignTokens.CardRadius,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarbakWidgetDesignTokens.cardBorder()),
        modifier = Modifier.fillMaxSize()
    ) {
        if (sizeCategory == WidgetSizeCategory.COMPACT) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(title, color = widgetColors.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(artist, color = widgetColors.secondary, fontSize = 8.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                FilledIconButton(
                    onClick = onTogglePlayPause,
                    modifier = Modifier.size(36.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = widgetColors.accent)
                ) {
                    Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, "تشغيل/إيقاف", tint = CarbonDark, modifier = Modifier.size(22.dp))
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxSize().padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(widgetColors.primary.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.GraphicEq, null, tint = widgetColors.accent, modifier = Modifier.size(30.dp))
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                    Text(title, color = widgetColors.primary, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(artist, color = widgetColors.secondary, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)),
                        color = widgetColors.accent,
                        trackColor = widgetColors.primary.copy(alpha = 0.14f)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(onClick = onPrevious, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.SkipNext, "السابق", tint = widgetColors.primary, modifier = Modifier.size(20.dp))
                    }
                    FilledIconButton(
                        onClick = onTogglePlayPause,
                        modifier = Modifier.size(40.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = widgetColors.accent)
                    ) {
                        Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, "تشغيل/إيقاف", tint = CarbonDark, modifier = Modifier.size(24.dp))
                    }
                    IconButton(onClick = onNext, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.SkipPrevious, "التالي", tint = widgetColors.primary, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MusicInstrumentLayout(
    title: String,
    artist: String,
    isPlaying: Boolean,
    progressFraction: Float,
    sizeCategory: WidgetSizeCategory,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
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
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "مشغل الصوت", style = DarbakWidgetDesignTokens.Typography.labelSmall, color = widgetColors.secondary)
                Text(text = title, style = DarbakWidgetDesignTokens.Typography.titleMedium, color = widgetColors.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = artist, style = DarbakWidgetDesignTokens.Typography.bodySmall, color = widgetColors.secondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevious, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.SkipNext, contentDescription = "السابق", tint = widgetColors.primary)
                }
                FilledIconButton(
                    onClick = onTogglePlayPause,
                    modifier = Modifier.size(44.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = widgetColors.accent)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "تشغيل/إيقاف",
                        tint = CarbonDark,
                        modifier = Modifier.size(28.dp)
                    )
                }
                IconButton(onClick = onNext, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "التالي", tint = widgetColors.primary)
                }
            }
        }
    }
}
