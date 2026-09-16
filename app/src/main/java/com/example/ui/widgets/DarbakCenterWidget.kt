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

    Surface(
        color = CarbonDark.copy(alpha = .64f),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, CyanNeon.copy(alpha = .30f)),
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(18.dp))
            .then(if (interactionEnabled) Modifier.clickable { onOpenCenter() } else Modifier)
    ) {
        when (style) {
            WidgetStyle.DARBAK_CENTER_COMPACT -> DarbakCenterCompactView(darbakApps)
            WidgetStyle.DARBAK_CENTER_WIDE -> DarbakCenterWideView(darbakApps)
            else -> DarbakCenterCardView(darbakApps)
        }
    }
}

@Composable
private fun DarbakCenterCardView(apps: List<DarbakAppItem>) {
    val integratedCount = apps.count { it.integrationState == DarbakIntegrationState.FULLY_INTEGRATED && it.isInstalled }
    val warningCount = apps.count { it.status?.isWarning == true || it.status?.isError == true }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            color = CyanNeon.copy(alpha = .12f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Hub,
                    contentDescription = "مركز دربك",
                    tint = CyanNeon,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "مركز دربك",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "$integratedCount من أصل ${apps.size} خدمات مربوطة",
                color = TextSecondary,
                fontSize = 9.sp,
                maxLines = 1
            )
        }

        if (warningCount > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = AmberRacing,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "$warningCount تنبيه",
                    color = AmberRacing,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
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
    val integratedCount = apps.count { it.integrationState == DarbakIntegrationState.FULLY_INTEGRATED && it.isInstalled }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 4.dp),
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
                color = TextPrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "$integratedCount/${apps.size}",
            color = CyanNeon,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun DarbakCenterWideView(apps: List<DarbakAppItem>) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Hub,
                contentDescription = null,
                tint = CyanNeon,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "مركز دربك",
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
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
                        color = if (app.integrationState == DarbakIntegrationState.FULLY_INTEGRATED) TextPrimary else TextMuted,
                        fontSize = 9.sp,
                        fontWeight = if (app.integrationState == DarbakIntegrationState.FULLY_INTEGRATED) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                    Text(
                        text = when (app.integrationState) {
                            DarbakIntegrationState.FULLY_INTEGRATED -> app.status?.summaryText ?: "مربوط"
                            DarbakIntegrationState.REVIEW_REQUIRED -> "يتطلب توثيق"
                            DarbakIntegrationState.UNCONFIGURED -> "غير مهيأ"
                        },
                        color = when {
                            app.integrationState == DarbakIntegrationState.REVIEW_REQUIRED -> AmberRacing
                            app.integrationState == DarbakIntegrationState.UNCONFIGURED -> TextMuted
                            app.status?.isError == true -> Color(0xFFFF5F57)
                            app.status?.isWarning == true -> AmberRacing
                            else -> EmeraldSafe
                        },
                        fontSize = 7.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
