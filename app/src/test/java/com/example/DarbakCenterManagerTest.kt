package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.DarbakCenterManager
import com.example.data.DarbakMaintenanceStatusProvider
import com.example.data.DarbakStatusProvider
import com.example.model.DarbakAppStatus
import com.example.model.DarbakIntegrationState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [25])
class DarbakCenterManagerTest {
    private lateinit var context: Context
    private lateinit var manager: DarbakCenterManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        manager = DarbakCenterManager(context)
    }

    @Test
    fun testRefreshAppsInitializesEcosystemList() {
        val apps = manager.darbakApps.value
        assertEquals(4, apps.size)

        val maintenance = apps.find { it.id == "darbak_maintenance" }
        assertNotNull(maintenance)
        assertEquals(DarbakMaintenanceStatusProvider.CONFIRMED_MAINTENANCE_PACKAGE, maintenance?.packageName)
        assertEquals(DarbakIntegrationState.FULLY_INTEGRATED, maintenance?.integrationState)

        val hub = apps.find { it.id == "darbak_vehicle_hub" }
        assertNotNull(hub)
        assertNull(hub?.packageName)
        assertEquals(DarbakIntegrationState.REVIEW_REQUIRED, hub?.integrationState)

        val maps = apps.find { it.id == "darbak_maps" }
        assertNotNull(maps)
        assertNull(maps?.packageName)
        assertEquals(DarbakIntegrationState.UNCONFIGURED, maps?.integrationState)
    }

    @Test
    fun testCustomStatusProviderExtensibility() {
        val customPackage = "com.abosultan.darbakcustom"
        val testProvider = object : DarbakStatusProvider {
            override val targetPackageName: String = customPackage
            override val providerUri = null
            override fun queryStatus(context: Context): DarbakAppStatus {
                return DarbakAppStatus(
                    summaryText = "جاهز للعمل",
                    detailText = "متصل بنجاح",
                    alertCount = 0
                )
            }
        }

        manager.registerStatusProvider(testProvider)
        manager.refreshApps()

        val apps = manager.darbakApps.value
        assertNotNull(apps)
    }
}
