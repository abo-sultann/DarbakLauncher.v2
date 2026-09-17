package com.example.data

import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ExternalMediaState(
    val hasActiveExternalSession: Boolean = false,
    val title: String = "",
    val artist: String = "",
    val isPlaying: Boolean = false,
    val packageName: String = ""
)

class ExternalMediaSessionManager(private val context: Context) {
    private val mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as? MediaSessionManager
    private val _externalState = MutableStateFlow(ExternalMediaState())
    val externalState: StateFlow<ExternalMediaState> = _externalState.asStateFlow()

    private var activeController: MediaController? = null

    fun checkActiveSessions(): ExternalMediaState {
        try {
            val msm = mediaSessionManager ?: return ExternalMediaState()
            val controllers = msm.getActiveSessions(null)
            val externalController = controllers.firstOrNull { it.packageName != context.packageName }

            if (externalController != null) {
                activeController = externalController
                val metadata = externalController.metadata
                val pbState = externalController.playbackState

                val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)
                    ?: metadata?.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
                    ?: "وسائط خارجية"
                val artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)
                    ?: metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
                    ?: externalController.packageName

                val isPlaying = pbState?.state == PlaybackState.STATE_PLAYING

                val state = ExternalMediaState(
                    hasActiveExternalSession = true,
                    title = title,
                    artist = artist,
                    isPlaying = isPlaying,
                    packageName = externalController.packageName
                )
                _externalState.value = state
                return state
            }
        } catch (e: SecurityException) {
            // Graceful degradation when Notification Listener permission is not granted on API 25.
        } catch (e: Exception) {
            Log.w("ExternalMediaSession", "Error checking external media sessions", e)
        }

        activeController = null
        val state = ExternalMediaState(hasActiveExternalSession = false)
        _externalState.value = state
        return state
    }

    fun playPause(): Boolean {
        try {
            val controller = activeController ?: return false
            val pbState = controller.playbackState?.state
            if (pbState == PlaybackState.STATE_PLAYING) {
                controller.transportControls.pause()
            } else {
                controller.transportControls.play()
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun next(): Boolean {
        try {
            val controller = activeController ?: return false
            controller.transportControls.skipToNext()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun previous(): Boolean {
        try {
            val controller = activeController ?: return false
            controller.transportControls.skipToPrevious()
            return true
        } catch (e: Exception) {
            return false
        }
    }
}
