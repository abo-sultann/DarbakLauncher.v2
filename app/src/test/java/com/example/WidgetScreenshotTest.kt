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
import com.example.model.*
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
@Config(qualifiers = "w1024dp-h600dp-land-mdpi", sdk = [35])
class WidgetScreenshotTest {
    @get:Rule val composeTestRule = createComposeRule()

    private val gps = GpsTelemetry(
        latitude = 26.35, longitude = 43.97, altitudeMeters = 642.0,
        speedKmH = 78f, bearingDegrees = 63f, accuracyMeters = 6f,
        hasGpsFix = true, satellitesCount = 11, statusArabic = "GPS متصل",
        isSpeedReliable = true, sensorHeadingDegrees = 61f
    )
    private val trip = TripData(
        currentSpeedKmH = 78f, maxSpeedKmH = 112f, averageSpeedKmH = 71f,
        distanceKm = 124.6f, elapsedMovingTimeSec = 6360, elapsedStopTimeSec = 420,
        isRunning = true, validGpsSamples = 450
    )
    private val music = MusicPlaybackState(
        isPlaying = true, currentPositionMs = 65_000L, durationMs = 215_000L,
        volumeLevel = .72f, isExternalSession = true,
        externalTitle = "المقطع الحالي", externalArtist = "الصوت الخارجي"
    )

    @Test fun homeDayScreenshot() = captureHome(false, "src/test/screenshots/home_day.png")
    @Test fun homeNightScreenshot() = captureHome(true, "src/test/screenshots/home_night.png")

    @Test
    fun widgetFamilyShowcaseScreenshot() {
        setScreen {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Minimal", color = TextPrimary)
                Row(Modifier.height(100.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ClockWidget(style = WidgetStyle.CLOCK_MINIMAL, is24Hour = true, modifier = Modifier.weight(1f))
                    SpeedWidget(style = WidgetStyle.SPEED_DIGITAL_LARGE, gpsTelemetry = gps, tripData = trip, modifier = Modifier.weight(1f))
                    GpsWidget(style = WidgetStyle.GPS_INDICATOR_MINI, gpsTelemetry = gps, modifier = Modifier.weight(1f))
                }
                Text("Darbak Card", color = TextPrimary)
                Row(Modifier.height(120.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MusicWidget(style = WidgetStyle.MUSIC_COMPACT, playbackState = music, onTogglePlayPause = {}, onNext = {}, onPrevious = {}, onSeek = {}, modifier = Modifier.weight(1f))
                    TripWidget(style = WidgetStyle.TRIP_CARD, tripData = trip, onStartTrip = {}, onPauseTrip = {}, onResetTrip = {}, modifier = Modifier.weight(1f))
                    OffroadInstrumentsWidget(style = WidgetStyle.OFFROAD_INSTRUMENTS_CARD, gpsTelemetry = gps, modifier = Modifier.weight(1f))
                }
                Text("Instrument", color = TextPrimary)
                Row(Modifier.height(140.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SpeedWidget(style = WidgetStyle.SPEED_DASHBOARD, gpsTelemetry = gps, tripData = trip, modifier = Modifier.weight(1f))
                    OffroadInstrumentsWidget(style = WidgetStyle.OFFROAD_INSTRUMENTS_FULL, gpsTelemetry = gps, modifier = Modifier.weight(1f))
                    MusicWidget(style = WidgetStyle.MUSIC_COVER, playbackState = music, onTogglePlayPause = {}, onNext = {}, onPrevious = {}, onSeek = {}, modifier = Modifier.weight(1f))
                }
            }
        }
        capture("src/test/screenshots/widget_showcase.png")
    }

    @Test fun clockResponsiveSequence() = responsive("src/test/screenshots/responsive_clock.png") { i, m ->
        ClockWidget(style = listOf(WidgetStyle.CLOCK_MINIMAL, WidgetStyle.CLOCK_WITH_DATE, WidgetStyle.CLOCK_AUTOMOTIVE_LARGE)[i], is24Hour = true, modifier = m)
    }
    @Test fun speedResponsiveSequence() = responsive("src/test/screenshots/responsive_speed.png") { i, m ->
        SpeedWidget(style = listOf(WidgetStyle.SPEED_DIGITAL_LARGE, WidgetStyle.SPEED_GAUGE_CIRCULAR, WidgetStyle.SPEED_DASHBOARD)[i], gpsTelemetry = gps, tripData = trip, modifier = m)
    }
    @Test fun musicResponsiveSequence() = responsive("src/test/screenshots/responsive_music.png") { i, m ->
        MusicWidget(style = listOf(WidgetStyle.MUSIC_MINIMAL, WidgetStyle.MUSIC_COMPACT, WidgetStyle.MUSIC_COVER)[i], playbackState = music, onTogglePlayPause = {}, onNext = {}, onPrevious = {}, onSeek = {}, modifier = m)
    }
    @Test fun tripResponsiveSequence() = responsive("src/test/screenshots/responsive_trip.png") { i, m ->
        TripWidget(style = listOf(WidgetStyle.TRIP_SPEED_DISTANCE, WidgetStyle.TRIP_CARD, WidgetStyle.TRIP_DASHBOARD)[i], tripData = trip, onStartTrip = {}, onPauseTrip = {}, onResetTrip = {}, modifier = m)
    }
    @Test fun offroadResponsiveSequence() = responsive("src/test/screenshots/responsive_offroad.png") { i, m ->
        OffroadInstrumentsWidget(style = listOf(WidgetStyle.OFFROAD_INSTRUMENTS_COMPACT, WidgetStyle.OFFROAD_INSTRUMENTS_CARD, WidgetStyle.OFFROAD_INSTRUMENTS_FULL)[i], gpsTelemetry = gps, modifier = m)
    }

    private fun captureHome(isNight: Boolean, path: String) {
        setScreen(background = if (isNight) CarbonDark else Color(0xFF68757D)) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    frame(WidgetType.CLOCK, WidgetStyle.CLOCK_MINIMAL, isNight, Modifier.weight(.8f)) {
                        ClockWidget(style = WidgetStyle.CLOCK_MINIMAL, is24Hour = true, modifier = Modifier.fillMaxSize())
                    }
                    frame(WidgetType.SPEEDOMETER, WidgetStyle.SPEED_DASHBOARD, isNight, Modifier.weight(1f)) {
                        SpeedWidget(style = WidgetStyle.SPEED_DASHBOARD, gpsTelemetry = gps, tripData = trip, modifier = Modifier.fillMaxSize())
                    }
                    frame(WidgetType.OFFROAD_INSTRUMENTS, WidgetStyle.OFFROAD_INSTRUMENTS_FULL, isNight, Modifier.weight(1.2f)) {
                        OffroadInstrumentsWidget(style = WidgetStyle.OFFROAD_INSTRUMENTS_FULL, gpsTelemetry = gps, modifier = Modifier.fillMaxSize())
                    }
                }
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    frame(WidgetType.MUSIC, WidgetStyle.MUSIC_COMPACT, isNight, Modifier.weight(1.2f)) {
                        MusicWidget(style = WidgetStyle.MUSIC_COMPACT, playbackState = music, onTogglePlayPause = {}, onNext = {}, onPrevious = {}, onSeek = {}, modifier = Modifier.fillMaxSize())
                    }
                    frame(WidgetType.TRIP, WidgetStyle.TRIP_CARD, isNight, Modifier.weight(1f)) {
                        TripWidget(style = WidgetStyle.TRIP_CARD, tripData = trip, onStartTrip = {}, onPauseTrip = {}, onResetTrip = {}, modifier = Modifier.fillMaxSize())
                    }
                    frame(WidgetType.GPS, WidgetStyle.GPS_COORDINATES, isNight, Modifier.weight(1f)) {
                        GpsWidget(style = WidgetStyle.GPS_COORDINATES, gpsTelemetry = gps, modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
        capture(path)
    }

    private fun responsive(path: String, widget: @Composable (Int, Modifier) -> Unit) {
        setScreen {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Compact  •  Medium  •  Large / Wide", color = TextPrimary)
                Spacer(Modifier.height(18.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(132.dp, 88.dp)) { widget(0, Modifier.fillMaxSize()) }
                    Box(Modifier.size(220.dp, 145.dp)) { widget(1, Modifier.fillMaxSize()) }
                    Box(Modifier.size(360.dp, 225.dp)) { widget(2, Modifier.fillMaxSize()) }
                }
            }
        }
        capture(path)
    }

    @Composable
    private fun frame(type: WidgetType, style: WidgetStyle, isNight: Boolean, modifier: Modifier, content: @Composable () -> Unit) {
        WidgetFrame(
            widgetItem = WidgetItem(
                id = "shot_${type.name}_$isNight", type = type, style = style,
                surfaceStyle = if (style.toFamily() == com.example.ui.theme.WidgetFamily.MINIMAL) WidgetSurfaceStyle.TRANSPARENT else WidgetSurfaceStyle.GLASS
            ),
            isDesignMode = false, isNightMode = isNight,
            onChangeStyle = {}, onMoveBy = { _, _ -> }, onResizeBy = { _, _ -> }, onTransformFinished = {},
            onOpacityChange = {}, onToggleLock = {}, onBringToFront = {}, onDelete = {}, onDuplicate = {},
            onSetSizePreset = {}, onSurfaceChange = {}, onToggleBorder = {}, onToneChange = {}, onSurfaceOpacityChange = {}, onResetWidget = {},
            modifier = modifier, content = content
        )
    }

    private fun setScreen(background: Color = CarbonDark, content: @Composable () -> Unit) {
        composeTestRule.setContent {
            Launcher2026Theme {
                Box(Modifier.size(1024.dp, 600.dp).background(background).padding(18.dp)) { content() }
            }
        }
    }

    private fun capture(path: String) {
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = path)
    }
}
