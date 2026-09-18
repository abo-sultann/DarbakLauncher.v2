package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.example.model.GpsTelemetry
import com.example.model.MusicPlaybackState
import com.example.model.TripData
import com.example.model.WidgetItem
import com.example.model.WidgetStyle
import com.example.model.WidgetType
import com.example.ui.components.WidgetFrame
import com.example.ui.theme.CarbonDark
import com.example.ui.theme.Launcher2026Theme
import com.example.ui.theme.TextPrimary
import com.example.ui.widgets.*
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w1024dp-h600dp-land-mdpi", sdk = [25])
class WidgetScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val gps = GpsTelemetry(
        latitude = 26.35,
        longitude = 43.97,
        altitudeMeters = 642.0,
        speedKmH = 78f,
        bearingDegrees = 63f,
        accuracyMeters = 6f,
        hasGpsFix = true,
        satellitesCount = 11,
        statusArabic = "GPS متصل",
        isSpeedReliable = true,
        sensorHeadingDegrees = 61f
    )

    private val trip = TripData(
        currentSpeedKmH = 78f,
        maxSpeedKmH = 112f,
        averageSpeedKmH = 71f,
        distanceKm = 124.6f,
        elapsedMovingTimeSec = 6360,
        elapsedStopTimeSec = 420,
        isRunning = true,
        validGpsSamples = 450
    )

    private val music = MusicPlaybackState(
        isPlaying = true,
        currentPositionMs = 65_000L,
        durationMs = 215_000L,
        volumeLevel = .72f,
        isExternalSession = true,
        externalTitle = "المقطع الحالي",
        externalArtist = "الصوت الخارجي"
    )

    @Test
    fun homeDayScreenshot() = captureHome(isNight = false, file = "src/test/screenshots/home_day.png")

    @Test
    fun homeNightScreenshot() = captureHome(isNight = true, file = "src/test/screenshots/home_night.png")

    @Test
    fun widgetFamilyShowcaseScreenshot() {
        composeTestRule.setContent {
            Launcher2026Theme {
                Box(Modifier.size(1024.dp, 600.dp).background(CarbonDark).padding(18.dp)) {
                    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        FamilyTitle("Minimal")
                        Row(Modifier.height(105.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            ClockWidget(WidgetStyle.CLOCK_MINIMAL, true, Modifier.weight(1f))
                            SpeedWidget(WidgetStyle.SPEED_DIGITAL_LARGE, gps, trip, Modifier.weight(1f))
                            GpsWidget(WidgetStyle.GPS_INDICATOR_MINI, gps, Modifier.weight(1f))
                        }
                        FamilyTitle("Darbak Card")
                        Row(Modifier.height(125.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            MusicWidget(WidgetStyle.MUSIC_COMPACT, music, {}, {}, {}, {}, Modifier.weight(1f))
                            TripWidget(WidgetStyle.TRIP_CARD, trip, {}, {}, {}, Modifier.weight(1f))
                            OffroadInstrumentsWidget(WidgetStyle.OFFROAD_INSTRUMENTS_CARD, gps, Modifier.weight(1f))
                        }
                        FamilyTitle("Instrument")
                        Row(Modifier.height(145.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SpeedWidget(WidgetStyle.SPEED_DASHBOARD, gps, trip, Modifier.weight(1f))
                            OffroadInstrumentsWidget(WidgetStyle.OFFROAD_INSTRUMENTS_FULL, gps, Modifier.weight(1f))
                            MusicWidget(WidgetStyle.MUSIC_COVER, music, {}, {}, {}, {}, Modifier.weight(1f))
                        }
                    }
                }
            }
        }
        capture("src/test/screenshots/widget_showcase.png")
    }

    @Test
    fun clockResponsiveSequence() = responsiveSequence("src/test/screenshots/responsive_clock.png") { style, modifier ->
        ClockWidget(style, true, modifier)
    }

    @Test
    fun speedResponsiveSequence() = responsiveSequence("src/test/screenshots/responsive_speed.png") { style, modifier ->
        SpeedWidget(style, gps, trip, modifier)
    }

    @Test
    fun musicResponsiveSequence() = responsiveSequence("src/test/screenshots/responsive_music.png") { style, modifier ->
        MusicWidget(style, music, {}, {}, {}, {}, modifier)
    }

    @Test
    fun tripResponsiveSequence() = responsiveSequence("src/test/screenshots/responsive_trip.png") { style, modifier ->
        TripWidget(style, trip, {}, {}, {}, modifier)
    }

    @Test
    fun offroadResponsiveSequence() = responsiveSequence("src/test/screenshots/responsive_offroad.png") { style, modifier ->
        OffroadInstrumentsWidget(style, gps, modifier)
    }

    private fun captureHome(isNight: Boolean, file: String) {
        composeTestRule.setContent {
            Launcher2026Theme {
                val background = if (isNight) CarbonDark else Color(0xFF68757D)
                Box(Modifier.size(1024.dp, 600.dp).background(background).padding(18.dp)) {
                    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            FramedWidget(WidgetType.CLOCK, WidgetStyle.CLOCK_MINIMAL, isNight, Modifier.weight(.8f)) {
                                ClockWidget(WidgetStyle.CLOCK_MINIMAL, true, Modifier.fillMaxSize())
                            }
                            FramedWidget(WidgetType.SPEEDOMETER, WidgetStyle.SPEED_DASHBOARD, isNight, Modifier.weight(1f)) {
                                SpeedWidget(WidgetStyle.SPEED_DASHBOARD, gps, trip, Modifier.fillMaxSize())
                            }
                            FramedWidget(WidgetType.OFFROAD_INSTRUMENTS, WidgetStyle.OFFROAD_INSTRUMENTS_FULL, isNight, Modifier.weight(1.25f)) {
                                OffroadInstrumentsWidget(WidgetStyle.OFFROAD_INSTRUMENTS_FULL, gps, Modifier.fillMaxSize())
                            }
                        }
                        Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            FramedWidget(WidgetType.MUSIC, WidgetStyle.MUSIC_COMPACT, isNight, Modifier.weight(1.25f)) {
                                MusicWidget(WidgetStyle.MUSIC_COMPACT, music, {}, {}, {}, {}, Modifier.fillMaxSize())
                            }
                            FramedWidget(WidgetType.TRIP, WidgetStyle.TRIP_CARD, isNight, Modifier.weight(1f)) {
                                TripWidget(WidgetStyle.TRIP_CARD, trip, {}, {}, {}, Modifier.fillMaxSize())
                            }
                            FramedWidget(WidgetType.GPS, WidgetStyle.GPS_COORDINATES, isNight, Modifier.weight(1f)) {
                                GpsWidget(WidgetStyle.GPS_COORDINATES, gps, Modifier.fillMaxSize())
                            }
                        }
                    }
                }
            }
        }
        capture(file)
    }

    private fun responsiveSequence(
        file: String,
        widget: @Composable (WidgetStyle, Modifier) -> Unit
    ) {
        composeTestRule.setContent {
            Launcher2026Theme {
                Box(Modifier.size(1024.dp, 600.dp).background(CarbonDark).padding(20.dp)) {
                    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Compact  •  Medium  •  Large / Wide", color = TextPrimary)
                        Spacer(Modifier.height(18.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(132.dp, 88.dp)) { widget(sequenceStyle(file, 0), Modifier.fillMaxSize()) }
                            Box(Modifier.size(220.dp, 145.dp)) { widget(sequenceStyle(file, 1), Modifier.fillMaxSize()) }
                            Box(Modifier.size(360.dp, 225.dp)) { widget(sequenceStyle(file, 2), Modifier.fillMaxSize()) }
                        }
                    }
                }
            }
        }
        capture(file)
    }

    private fun sequenceStyle(file: String, index: Int): WidgetStyle {
        val styles = when {
            file.contains("clock") -> listOf(WidgetStyle.CLOCK_MINIMAL, WidgetStyle.CLOCK_WITH_DATE, WidgetStyle.CLOCK_AUTOMOTIVE_LARGE)
            file.contains("speed") -> listOf(WidgetStyle.SPEED_DIGITAL_LARGE, WidgetStyle.SPEED_GAUGE_CIRCULAR, WidgetStyle.SPEED_DASHBOARD)
            file.contains("music") -> listOf(WidgetStyle.MUSIC_MINIMAL, WidgetStyle.MUSIC_COMPACT, WidgetStyle.MUSIC_COVER)
            file.contains("trip") -> listOf(WidgetStyle.TRIP_SPEED_DISTANCE, WidgetStyle.TRIP_CARD, WidgetStyle.TRIP_DASHBOARD)
            else -> listOf(WidgetStyle.OFFROAD_INSTRUMENTS_COMPACT, WidgetStyle.OFFROAD_INSTRUMENTS_CARD, WidgetStyle.OFFROAD_INSTRUMENTS_FULL)
        }
        return styles[index]
    }

    @Composable
    private fun FramedWidget(type: WidgetType, style: WidgetStyle, isNight: Boolean, modifier: Modifier, content: @Composable () -> Unit) {
        WidgetFrame(
            widgetItem = WidgetItem(id = "shot_${type.name}_$isNight", type = type, style = style),
            isDesignMode = false,
            isNightMode = isNight,
            onChangeStyle = {},
            onMoveBy = { _, _ -> },
            onResizeBy = { _, _ -> },
            onTransformFinished = {},
            onOpacityChange = {},
            onToggleLock = {},
            onBringToFront = {},
            onDelete = {},
            onDuplicate = {},
            onSetSizePreset = {},
            onSurfaceChange = {},
            onToggleBorder = {},
            onToneChange = {},
            onSurfaceOpacityChange = {},
            onResetWidget = {},
            modifier = modifier,
            content = content
        )
    }

    @Composable
    private fun FamilyTitle(title: String) {
        Text(title, color = TextPrimary)
    }

    private fun capture(path: String) {
        composeTestRule.onRoot().captureRoboImage(filePath = path)
    }
}
