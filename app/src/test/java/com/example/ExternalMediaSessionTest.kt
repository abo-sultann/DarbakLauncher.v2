package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ExternalMediaSessionManager
import com.example.model.MusicPlaybackState
import com.example.model.MusicTrack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [25])
class ExternalMediaSessionTest {

    @Test
    fun `external media session manager handles ungranted or empty sessions gracefully`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val manager = ExternalMediaSessionManager(context)

        val state = manager.checkActiveSessions()
        assertFalse(state.hasActiveExternalSession)
        assertEquals("", state.title)
    }

    @Test
    fun `playback state fields for external sessions default correctly`() {
        val localTrack = MusicTrack(
            id = 1L, title = "مقطع محلي", artist = "فنان محلي", album = "ألبوم", durationMs = 180000L, dataPath = "/music/local.mp3"
        )
        val state = MusicPlaybackState(
            currentTrack = localTrack,
            isPlaying = true,
            isExternalSession = false
        )

        assertFalse(state.isExternalSession)
        assertEquals("مقطع محلي", state.currentTrack?.title)
    }
}
