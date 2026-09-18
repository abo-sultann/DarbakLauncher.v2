package com.example.ui.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.model.WidgetStyle
import com.example.model.toFamily
import com.example.ui.components.ResolvedWidgetColors
import com.example.ui.components.resolvedWidgetColors
import com.example.ui.theme.*

@Composable
fun AppsWidget(
    style: WidgetStyle,
    installedApps: List<AppItem>,
    onLaunchApp: (String) -> Unit,
    onOpenAppDrawer: () -> Unit,
    interactionEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val widgetColors = resolvedWidgetColors()
    val displayApps = installedApps.filter { !it.isHidden }
    val favoriteApps = displayApps.filter { it.isFavorite }.ifEmpty { displayApps.take(8) }

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
                AppsMinimalLayout(
                    favoriteApps = favoriteApps,
                    interactionEnabled = interactionEnabled,
                    sizeCategory = sizeCategory,
                    onLaunchApp = onLaunchApp,
                    onOpenAppDrawer = onOpenAppDrawer,
                    widgetColors = widgetColors
                )
            }
            WidgetFamily.DARBAK_CARD -> {
                AppsCardLayout(
                    favoriteApps = favoriteApps,
                    interactionEnabled = interactionEnabled,
                    sizeCategory = sizeCategory,
                    onLaunchApp = onLaunchApp,
                    onOpenAppDrawer = onOpenAppDrawer,
                    widgetColors = widgetColors
                )
            }
            WidgetFamily.INSTRUMENT -> {
                AppsInstrumentLayout(
                    favoriteApps = favoriteApps,
                    interactionEnabled = interactionEnabled,
                    sizeCategory = sizeCategory,
                    onLaunchApp = onLaunchApp,
                    onOpenAppDrawer = onOpenAppDrawer,
                    widgetColors = widgetColors
                )
            }
        }
    }
}

@Composable
private fun AppsMinimalLayout(
    favoriteApps: List<AppItem>,
    interactionEnabled: Boolean,
    sizeCategory: WidgetSizeCategory,
    onLaunchApp: (String) -> Unit,
    onOpenAppDrawer: () -> Unit,
    widgetColors: ResolvedWidgetColors
) {
    val count = when (sizeCategory) {
        WidgetSizeCategory.COMPACT -> 4
        WidgetSizeCategory.MEDIUM -> 5
        WidgetSizeCategory.LARGE_WIDE -> 8
    }
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        favoriteApps.take(count).forEach { app ->
            AppShortcutItem(app, showLabel = sizeCategory != WidgetSizeCategory.COMPACT, interactionEnabled = interactionEnabled) {
                onLaunchApp(app.packageName)
            }
        }
    }
}

@Composable
private fun AppsCardLayout(
    favoriteApps: List<AppItem>,
    interactionEnabled: Boolean,
    sizeCategory: WidgetSizeCategory,
    onLaunchApp: (String) -> Unit,
    onOpenAppDrawer: () -> Unit,
    widgetColors: ResolvedWidgetColors
) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("المفضلة السريعة", style = DarbakWidgetDesignTokens.Typography.labelSmall, color = widgetColors.secondary)
                Text(
                    "كل التطبيقات",
                    style = DarbakWidgetDesignTokens.Typography.labelSmall,
                    color = widgetColors.accent,
                    modifier = Modifier.clickable(enabled = interactionEnabled, onClick = onOpenAppDrawer).testTag("btn_widget_all_apps")
                )
            }

            val count = if (sizeCategory == WidgetSizeCategory.COMPACT) 3 else 5
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                favoriteApps.take(count).forEach { app ->
                    AppShortcutItem(app, showLabel = true, interactionEnabled = interactionEnabled) {
                        onLaunchApp(app.packageName)
                    }
                }
            }
        }
    }
}

@Composable
private fun AppsInstrumentLayout(
    favoriteApps: List<AppItem>,
    interactionEnabled: Boolean,
    sizeCategory: WidgetSizeCategory,
    onLaunchApp: (String) -> Unit,
    onOpenAppDrawer: () -> Unit,
    widgetColors: ResolvedWidgetColors
) {
    Surface(
        color = DarbakWidgetDesignTokens.instrumentSurface(),
        shape = DarbakWidgetDesignTokens.InstrumentRadius,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarbakWidgetDesignTokens.instrumentBorder()),
        modifier = Modifier.fillMaxSize()
    ) {
        LazyRow(
            modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(favoriteApps.take(10), key = { it.packageName }) { app ->
                AppShortcutItem(app, showLabel = false, interactionEnabled = interactionEnabled) {
                    onLaunchApp(app.packageName)
                }
            }
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(enabled = interactionEnabled, onClick = onOpenAppDrawer)
                        .padding(4.dp)
                        .testTag("btn_widget_all_apps")
                ) {
                    Surface(
                        color = widgetColors.accent.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, widgetColors.accent),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Apps, "كل التطبيقات", tint = widgetColors.accent, modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppShortcutItem(app: AppItem, showLabel: Boolean, interactionEnabled: Boolean, onClick: () -> Unit) {
    val widgetColors = resolvedWidgetColors()
    val imageBitmap = remember(app.packageName, app.iconBitmap) { app.iconBitmap?.asImageBitmap() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = interactionEnabled, onClick = onClick)
            .padding(2.dp)
            .testTag("app_shortcut_${app.packageName}")
    ) {
        Surface(
            color = DarbakWidgetDesignTokens.cardSurface(),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, widgetColors.secondary.copy(alpha = 0.3f)),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (imageBitmap != null) {
                    Image(bitmap = imageBitmap, contentDescription = app.label, modifier = Modifier.size(28.dp))
                } else {
                    Icon(Icons.Default.Android, contentDescription = app.label, tint = widgetColors.accent, modifier = Modifier.size(22.dp))
                }
            }
        }

        if (showLabel) {
            Spacer(Modifier.height(2.dp))
            Text(app.label, style = DarbakWidgetDesignTokens.Typography.bodySmall, color = widgetColors.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
