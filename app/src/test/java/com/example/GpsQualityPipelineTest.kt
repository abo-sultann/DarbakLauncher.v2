package com.example

import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.test.core.app.ApplicationProvider
import com.example.data.GpsTelemetryManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [25])
class GpsQualityPipelineTest {

    @Test
    fun `gps pipeline preserves raw fields and applies altitude calibration offset`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val manager = GpsTelemetryManager(context)
        manager.updateAltitudeCalibrationOffset(10f)

        val loc = Location(LocationManager.GPS_PROVIDER).apply {
            latitude = 24.7136
            longitude = 46.6753
            altitude = 500.0
            speed = 10f // 36 km/h
            bearing = 90f
            accuracy = 5f
            time = System.currentTimeMillis()
        }

        manager.onLocationChanged(loc)
        val telemetry = manager.telemetry.value

        assertTrue(telemetry.hasGpsFix)
        assertEquals(24.7136, telemetry.latitude, 0.0001)
        assertEquals(24.7136, telemetry.rawLatitude, 0.0001)
        assertEquals(500.0, telemetry.rawAltitude, 0.0001)
        assertEquals(510.0, telemetry.altitudeMeters, 0.0001) // 500 + 10 calibration offset
        assertEquals(5f, telemetry.accuracyMeters, 0.1f)
        assertEquals(5f, telemetry.rawAccuracyMeters, 0.1f)
    }
}
