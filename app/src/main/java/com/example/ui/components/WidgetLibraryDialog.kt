package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.WidgetStyle
import com.example.model.WidgetType
import com.example.model.preferredWidgetStylesFor
import com.example.model.toFamily
import com.example.ui.theme.*

/**
 * v2 library deliberately exposes visual families, not the long legacy WidgetStyle catalogue.
 * The selected WidgetStyle remains the persisted compatibility key for existing layouts.
 */
@Composable
fun WidgetLibraryDialog(
    initialType: WidgetType? = null,
    initialStyle: WidgetStyle? = null,
    isStyleChangerMode: Boolean = false,
    onDismiss: () -> Unit,
    onSelectStyle: (WidgetStyle) -> Unit
) {
    var selectedType by remember(initialType) { mutableStateOf(initialType ?: WidgetType.CLOCK) }
    val stylesForType = remember(selectedType) { preferredWidgetStylesFor(selectedType) }
    var selectedStyle by remember(selectedType, initialStyle) {
        val current = initialStyle?.takeIf { it.type == selectedType }
        val sameFamily = current?.let { old -> stylesForType.firstOrNull { it.toFamily() == old.toFamily() } }
        mutableStateOf(sameFamily ?: stylesForType.first())
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(.96f).fillMaxHeight(.90f),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CarbonDark),
            border = BorderStroke(1.dp, CarbonCardBorder)
        ) {
            Column(Modifier.fillMaxSize().padding(14.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(
                            if (isStyleChangerMode) "تغيير عائلة الودجت" else "مكتبة ودجتات دربك",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text("ثلاث عائلات واضحة فقط: بسيط • بطاقة دربك • أجهزة قيادة", color = TextSecondary, fontSize = 10.sp)
                    }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "إغلاق", tint = TextSecondary) }
                }

                if (!isStyleChangerMode) {
                    Spacer(Modifier.height(8.dp))
                    LazyRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(WidgetType.values()) { type ->
                            val active = selectedType == type
                            Surface(
                                color = if (active) CyanNeon else CarbonSurface,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, if (active) CyanNeon else CarbonCardBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        selectedType = type
                                        selectedStyle = preferredWidgetStylesFor(type).first()
                                    }
                                    .testTag("tab_type_${type.name}")
                            ) {
                                Text(
                                    type.arabicTitle,
                                    color = if (active) CarbonDark else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text(selectedType.arabicTitle, color = AmberRacing, fontWeight = FontWeight.Black, fontSize = 12.sp)
                Text("اختر العائلة البصرية؛ المقاس يغيّر ترتيب المحتوى تلقائيًا", color = TextSecondary, fontSize = 9.sp)
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    stylesForType.forEach { style ->
                        WidgetFamilyCard(
                            style = style,
                            selected = selectedStyle == style,
                            onClick = { selectedStyle = style },
                            modifier = Modifier.weight(1f).fillMaxHeight(.82f).testTag("style_item_${style.name}")
                        )
                    }
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("إلغاء", color = TextPrimary) }
                    Button(
                        onClick = { onSelectStyle(selectedStyle) },
                        modifier = Modifier.weight(1f).testTag("btn_confirm_widget_style"),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(if (isStyleChangerMode) Icons.Default.Check else Icons.Default.Add, null, tint = CarbonDark, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(5.dp))
                        Text(if (isStyleChangerMode) "تطبيق" else "إضافة", color = CarbonDark, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun WidgetFamilyCard(
    style: WidgetStyle,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val family = style.toFamily()
    val shape = DarbakWidgetDesignTokens.radiusFor(family)
    Surface(
        color = if (selected) CyanNeon.copy(alpha = .08f) else CarbonSurface.copy(alpha = .62f),
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) CyanNeon else CarbonCardBorder),
        modifier = modifier.clip(RoundedCornerShape(15.dp)).clickable(onClick = onClick)
    ) {
        Column(Modifier.fillMaxSize().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                when (family) {
                    WidgetFamily.MINIMAL -> MinimalFamilyPreview(style.type)
                    WidgetFamily.DARBAK_CARD -> CardFamilyPreview(style.type, shape)
                    WidgetFamily.INSTRUMENT -> InstrumentFamilyPreview(style.type, shape)
                }
                if (selected) {
                    Surface(color = CyanNeon, shape = CircleShape, modifier = Modifier.align(Alignment.TopEnd).size(24.dp)) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, null, tint = CarbonDark, modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }
            Spacer(Modifier.height(7.dp))
            Text(family.arabicTitle, color = if (selected) CyanNeon else TextPrimary, fontWeight = FontWeight.Black, fontSize = 13.sp)
            Text(
                family.description,
                color = TextSecondary,
                fontSize = 9.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MinimalFamilyPreview(type: WidgetType) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(previewPrimary(type), color = TextPrimary, fontSize = 34.sp, fontWeight = FontWeight.Black, maxLines = 1)
        Text(previewSecondary(type), color = TextSecondary, fontSize = 9.sp, maxLines = 1)
    }
}

@Composable
private fun CardFamilyPreview(type: WidgetType, shape: androidx.compose.ui.graphics.Shape) {
    Surface(
        color = CarbonCard.copy(alpha = .92f),
        shape = shape,
        border = BorderStroke(1.dp, CarbonCardBorder),
        modifier = Modifier.fillMaxWidth(.90f).height(88.dp)
    ) {
        Column(Modifier.fillMaxSize().padding(10.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(type.arabicTitle, color = CyanNeon, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Icon(previewIcon(type), null, tint = TextSecondary, modifier = Modifier.size(16.dp))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.Bottom) {
                Text(previewPrimary(type), color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text(previewSecondary(type), color = TextSecondary, fontSize = 9.sp)
            }
        }
    }
}

@Composable
private fun InstrumentFamilyPreview(type: WidgetType, shape: androidx.compose.ui.graphics.Shape) {
    Surface(
        color = Color(0xFF080C12),
        shape = shape,
        border = BorderStroke(1.dp, CarbonCardBorder.copy(alpha = .7f)),
        modifier = Modifier.fillMaxWidth(.90f).height(88.dp)
    ) {
        Row(Modifier.fillMaxSize().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(type.arabicTitle, color = TextSecondary, fontSize = 8.sp)
                Text(previewPrimary(type), color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Black)
            }
            Column(horizontalAlignment = Alignment.End) {
                Icon(previewIcon(type), null, tint = CyanNeon, modifier = Modifier.size(24.dp))
                Text(previewSecondary(type), color = TextSecondary, fontSize = 8.sp)
            }
        }
    }
}

private fun previewPrimary(type: WidgetType): String = when (type) {
    WidgetType.CLOCK -> "9:23"
    WidgetType.SPEEDOMETER -> "82"
    WidgetType.DATE -> "28 أغسطس"
    WidgetType.GPS -> "±6 م"
    WidgetType.MUSIC -> "▶"
    WidgetType.MAP -> "1.6 كم"
    WidgetType.TRIP -> "124 كم"
    WidgetType.APPS -> "6"
    WidgetType.CONTROLS -> "▶"
    WidgetType.MAINTENANCE -> "320 كم"
    WidgetType.DARBAK_CENTER -> "4/4"
    WidgetType.OFFROAD_INSTRUMENTS -> "120°"
    WidgetType.HEAD_UNIT_VITALS -> "RAM 54%"
}

private fun previewSecondary(type: WidgetType): String = when (type) {
    WidgetType.CLOCK -> "الخميس"
    WidgetType.SPEEDOMETER -> "كم/س"
    WidgetType.DATE -> "1448 / 4 / 7"
    WidgetType.GPS -> "9 أقمار"
    WidgetType.MUSIC -> "المقطع الحالي"
    WidgetType.MAP -> "اتجاه الوجهة"
    WidgetType.TRIP -> "1:46"
    WidgetType.APPS -> "المفضلة"
    WidgetType.CONTROLS -> "وسائط"
    WidgetType.MAINTENANCE -> "للصيانة"
    WidgetType.DARBAK_CENTER -> "دربك"
    WidgetType.OFFROAD_INSTRUMENTS -> "شمال شرقي"
    WidgetType.HEAD_UNIT_VITALS -> "CPU 18%"
}

private fun previewIcon(type: WidgetType) = when (type) {
    WidgetType.CLOCK -> Icons.Default.AccessTime
    WidgetType.SPEEDOMETER -> Icons.Default.Speed
    WidgetType.DATE -> Icons.Default.CalendarMonth
    WidgetType.GPS -> Icons.Default.GpsFixed
    WidgetType.MUSIC -> Icons.Default.MusicNote
    WidgetType.MAP -> Icons.Default.Map
    WidgetType.TRIP -> Icons.Default.DirectionsCar
    WidgetType.APPS -> Icons.Default.Apps
    WidgetType.CONTROLS -> Icons.Default.Tune
    WidgetType.MAINTENANCE -> Icons.Default.Build
    WidgetType.DARBAK_CENTER -> Icons.Default.Hub
    WidgetType.OFFROAD_INSTRUMENTS -> Icons.Default.Explore
    WidgetType.HEAD_UNIT_VITALS -> Icons.Default.Memory
}
