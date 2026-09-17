package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.example.model.AppItem
import com.example.model.GpsTelemetry
import com.example.model.MusicPlaybackState
import com.example.model.TripData
import com.example.model.WidgetStyle
import com.example.ui.theme.CarbonDark
import com.example.ui.theme.Launcher2026Theme
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

    @Test
    fun minimalFamilyWidgetsScreenshot() {
        composeTestRule.setContent {
            Launcher2026Theme {
                Box(modifier = Modifier.size(1024.dp, 600.dp).background(CarbonDark).padding(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.height(100.dp)) {
                            ClockWidget(style = WidgetStyle.CLOCK_MINIMAL, is24Hour = false, modifier = Modifier.weight(1f))
                            SpeedWidget(style = WidgetStyle.SPEED_DIGITAL_LARGE, gpsTelemetry = GpsTelemetry(), tripData = TripData(), modifier = Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.height(100.dp)) {
                            DateWidget(style = WidgetStyle.DATE_ONLY, modifier = Modifier.weight(1f))
                            MusicWidget(style = WidgetStyle.MUSIC_MINIMAL, playbackState = MusicPlaybackState(), onTogglePlayPause = {}, onNext = {}, onPrevious = {}, onSeek = {}, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/widget_minimal_family.png")
    }

    @Test
    fun darbakCardFamilyWidgetsScreenshot() {
        composeTestRule.setContent {
            Launcher2026Theme {
                Box(modifier = Modifier.size(1024.dp, 600.dp).background(CarbonDark).padding(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.height(120.dp)) {
                            ClockWidget(style = WidgetStyle.CLOCK_WITH_DATE, is24Hour = false, modifier = Modifier.weight(1f))
                            SpeedWidget(style = WidgetStyle.SPEED_GAUGE_CIRCULAR, gpsTelemetry = GpsTelemetry(), tripData = TripData(), modifier = Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.height(120.dp)) {
                            TripWidget(style = WidgetStyle.TRIP_CARD, tripData = TripData(), onStartTrip = {}, onPauseTrip = {}, onResetTrip = {}, modifier = Modifier.weight(1f))
                            MusicWidget(style = WidgetStyle.MUSIC_COMPACT, playbackState = MusicPlaybackState(), onTogglePlayPause = {}, onNext = {}, onPrevious = {}, onSeek = {}, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/widget_card_family.png")
    }

    @Test
    fun instrumentFamilyWidgetsScreenshot() {
        composeTestRule.setContent {
            Launcher2026Theme {
                Box(modifier = Modifier.size(1024.dp, 600.dp).background(CarbonDark).padding(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.height(140.dp)) {
                            ClockWidget(style = WidgetStyle.CLOCK_AUTOMOTIVE_LARGE, is24Hour = false, modifier = Modifier.weight(1f))
                            SpeedWidget(style = WidgetStyle.SPEED_DASHBOARD, gpsTelemetry = GpsTelemetry(), tripData = TripData(), modifier = Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.height(140.dp)) {
                            OffroadInstrumentsWidget(style = WidgetStyle.OFFROAD_INSTRUMENTS_CARD, gpsTelemetry = GpsTelemetry(), modifier = Modifier.weight(1f))
                            MusicWidget(style = WidgetStyle.MUSIC_COVER, playbackState = MusicPlaybackState(), onTogglePlayPause = {}, onNext = {}, onPrevious = {}, onSeek = {}, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/widget_instrument_family.png")
    }
}
