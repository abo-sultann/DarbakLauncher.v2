package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.DarbakAppItem
import com.example.model.DarbakIntegrationState
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun DarbakCenterDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val darbakApps by viewModel.darbakApps.collectAsState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(.92f)
                .fillMaxHeight(.88f)
                .clip(RoundedCornerShape(20.dp)),
            color = CarbonDark,
            border = BorderStroke(1.dp, CyanNeon.copy(alpha = .40f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = CyanNeon.copy(alpha = .15f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Hub,
                                contentDescription = null,
                                tint = CyanNeon,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "مركز دربك / Darbak Center",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "منصة تطبيقات وخدمات دربك للمركبة",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }

                    IconButton(
                        onClick = { viewModel.refreshDarbakApps() },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "تحديث",
                            tint = CyanNeon,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Grid of Darbak Apps
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(darbakApps, key = { it.id }) { app ->
                        DarbakAppCard(
                            app = app,
                            onLaunch = { app.packageName?.let { pkg -> viewModel.launchDarbakApp(pkg) } }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DarbakAppCard(
    app: DarbakAppItem,
    onLaunch: () -> Unit
) {
    val statusColor = when {
        app.integrationState == DarbakIntegrationState.REVIEW_REQUIRED -> AmberRacing
        app.integrationState == DarbakIntegrationState.UNCONFIGURED -> TextMuted
        !app.isInstalled -> TextMuted
        app.status?.isError == true -> Color(0xFFFF5F57)
        app.status?.isWarning == true -> AmberRacing
        else -> EmeraldSafe
    }

    val tagText = when (app.integrationState) {
        DarbakIntegrationState.FULLY_INTEGRATED -> if (app.isInstalled) "مثبّت ومربوط" else "غير مثبّت"
        DarbakIntegrationState.REVIEW_REQUIRED -> "يتطلب مراجعة"
        DarbakIntegrationState.UNCONFIGURED -> "غير متاح بعد"
    }

    Surface(
        color = CarbonSurface.copy(alpha = .78f),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            if (app.integrationState == DarbakIntegrationState.FULLY_INTEGRATED && app.isInstalled)
                CyanNeon.copy(alpha = .20f)
            else
                Color.White.copy(alpha = .08f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = app.titleArabic,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    color = statusColor.copy(alpha = .16f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = tagText,
                        color = statusColor,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = app.descriptionArabic,
                color = TextSecondary,
                fontSize = 9.sp,
                maxLines = 2,
                lineHeight = 12.sp
            )

            if (app.status != null) {
                Surface(
                    color = CarbonDark.copy(alpha = .50f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = app.status.summaryText,
                            color = statusColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        app.status.detailText?.let { detail ->
                            Text(
                                text = detail,
                                color = TextMuted,
                                fontSize = 8.sp
                            )
                        }
                    }
                }
            }

            if (app.integrationState == DarbakIntegrationState.FULLY_INTEGRATED && app.isInstalled) {
                Button(
                    onClick = onLaunch,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Launch,
                        contentDescription = null,
                        tint = CarbonDark,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "فتح التطبيق",
                        color = CarbonDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Surface(
                    color = Color.White.copy(alpha = .04f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = when (app.integrationState) {
                                DarbakIntegrationState.FULLY_INTEGRATED -> "التطبيق غير مثبت على النظام"
                                DarbakIntegrationState.REVIEW_REQUIRED -> "بانتظار توثيق معرّف الحزمة والـ API"
                                DarbakIntegrationState.UNCONFIGURED -> "غير مهيأ بعد في النظام"
                            },
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}
