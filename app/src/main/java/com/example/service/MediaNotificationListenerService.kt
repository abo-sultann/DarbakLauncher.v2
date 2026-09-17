package com.example.service

import android.content.ComponentName
import android.content.Context
import android.service.notification.NotificationListenerService
import android.util.Log

class MediaNotificationListenerService : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.i(TAG, "NotificationListener connected for external MediaSession access")
        connectedInstance = this
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.i(TAG, "NotificationListener disconnected")
        if (connectedInstance == this) {
            connectedInstance = null
        }
    }

    companion object {
        private const val TAG = "MediaNotifListener"

        @Volatile
        var connectedInstance: MediaNotificationListenerService? = null
            private set

        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context, MediaNotificationListenerService::class.java)
        }
    }
}
