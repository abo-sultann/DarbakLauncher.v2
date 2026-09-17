package com.example.ui.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DarbakAppItem
import com.example.model.DarbakIntegrationState
import com.example.model.WidgetStyle
import com.example.model.toFamily
import com.example.ui.components.resolvedWidgetColors
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun DarbakCenterWidget(
    style: WidgetStyle,
    viewModel: MainViewModel,
    interactionEnabled: Boolean = true,
    onOpenCenter: () -> Unit = {}
) {
    val darbakApps by viewModel.darbakApps.collectAsState()
    val widgetColors = resolvedWidgetColors()

    val family = style.toFamily()
    val shape = DarbakWidgetDesignTokens.radiusFor(family)
    val surfaceColor = when (family) {
        WidgetFamily.MINIMAL -> Color.Transparent
        WidgetFamily.DARBAK_CARD -> DarbakWidgetDesignTokens.cardSurface()
        WidgetFamily.INSTRUMENT -> DarbakWidgetDesignTokens.instrumentSurface()
    }
    val borderColor = when (family) {
        WidgetFamily.MINIMAL -> Color.Transparent
        WidgetFamily.DARBAK_CARD -> DarbakWidgetDesignTokens.cardBorder()
        WidgetFamily.INSTRUMENT -> DarbakWidgetDesignTokens.instrumentBorder()
    }

    Surface(
        color = surfaceColor,
        shape = shape,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxSize()
            .padding(DarbakWidgetDesignTokens.ContentPadding)
            .then(if (interactionEnabled) Modifier.clickable { onOpenCenter() } else Modifier)
    ) {
        when (family) {
            WidgetFamily.MINIMAL -> DarbakCenterCompactView(darbakApps)
            WidgetFamily.DARBAK_CARD -> DarbakCenterCardView(darbakApps)
            WidgetFamily.INSTRUMENT -> DarbakCenterWideView(darbakApps)
        }
    }
}

@Composable
private fun DarbakCenterCardView(apps: List<DarbakAppItem>) {
    val widgetColors = resolvedWidgetColors()
    val integratedCount = apps.count { it.integrationState == DarbakIntegrationState.FULLY_INTEGRATED && it.isInstalled }
    val warningCount = apps.count { it.status?.isWarning == true || it.status?.isError == true }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            color = CyanNeon.copy(alpha = .12f),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Hub,
                    contentDescription = "مركز دربك",
                    tint = CyanNeon,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "مركز دربك",
                color = widgetColors.primary,
                style = DarbakWidgetDesignTokens.Typography.labelMedium
            )
            Text(
                text = "$integratedCount/${apps.size} خدمات مربوطة",
                color = widgetColors.secondary,
                style = DarbakWidgetDesignTokens.Typography.bodySmall,
                maxLines = 1
            )
        }

        if (warningCount > 0) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = AmberRacing,
                modifier = Modifier.size(14.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = EmeraldSafe,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun DarbakCenterCompactView(apps: List<DarbakAppItem>) {
    val widgetColors = resolvedWidgetColors()
    val integratedCount = apps.count { it.integrationState == DarbakIntegrationState.FULLY_INTEGRATED && it.isInstalled }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Hub,
                contentDescription = null,
                tint = CyanNeon,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "مركز دربك",
                color = widgetColors.primary,
                style = DarbakWidgetDesignTokens.Typography.labelSmall
            )
        }

        Text(
            text = "$integratedCount/${apps.size}",
            color = CyanNeon,
            style = DarbakWidgetDesignTokens.Typography.labelSmall
        )
    }
}

@Composable
private fun DarbakCenterWideView(apps: List<DarbakAppItem>) {
    val widgetColors = resolvedWidgetColors()
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Hub,
                contentDescription = null,
                tint = CyanNeon,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "مركز دربك",
                color = widgetColors.primary,
                style = DarbakWidgetDesignTokens.Typography.labelMedium
            )
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            apps.forEach { app ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = app.titleArabic.replace("دربك", "").trim(),
                        color = if (app.integrationState == DarbakIntegrationState.FULLY_INTEGRATED) widgetColors.primary else widgetColors.secondary,
                        style = DarbakWidgetDesignTokens.Typography.bodySmall,
                        maxLines = 1
                    )
                    Text(
                        text = when (app.integrationState) {
                            DarbakIntegrationState.FULLY_INTEGRATED -> app.status?.summaryText ?: "مربوط"
                            DarbakIntegrationState.REVIEW_REQUIRED -> "توثيق"
                            DarbakIntegrationState.UNCONFIGURED -> "غير مهيأ"
                        },
                        color = when {
                            app.integrationState == DarbakIntegrationState.REVIEW_REQUIRED -> AmberRacing
                            app.integrationState == DarbakIntegrationState.UNCONFIGURED -> TextMuted
                            app.status?.isError == true -> Color(0xFFFF5F57)
                            app.status?.isWarning == true -> AmberRacing
                            else -> EmeraldSafe
                        },
                        style = DarbakWidgetDesignTokens.Typography.bodySmall,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
