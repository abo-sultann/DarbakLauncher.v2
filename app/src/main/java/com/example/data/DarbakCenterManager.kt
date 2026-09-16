package com.example.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.example.model.DarbakAppItem
import com.example.model.DarbakAppStatus
import com.example.model.DarbakIntegrationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Interface for status providers that allow Darbak apps to expose status to Darbak Center.
 */
interface DarbakStatusProvider {
    val targetPackageName: String
    val providerUri: Uri?
    fun queryStatus(context: Context): DarbakAppStatus?
}

/**
 * Default provider implementation for Darbak Maintenance using content provider.
 */
class DarbakMaintenanceStatusProvider : DarbakStatusProvider {
    override val targetPackageName: String = CONFIRMED_MAINTENANCE_PACKAGE
    override val providerUri: Uri = Uri.parse("content://com.abosultan.darbakmaintenance.status/status")

    override fun queryStatus(context: Context): DarbakAppStatus? {
        return runCatching {
            val cursor = context.contentResolver.query(providerUri, null, null, null, null) ?: return null
            cursor.use {
                val configuredIdx = it.getColumnIndex("configured")
                val statusIdx = it.getColumnIndex("status")

                var totalItems = 0
                var dueCount = 0
                var soonCount = 0
                var configuredCount = 0

                while (it.moveToNext()) {
                    totalItems++
                    val isConfigured = if (configuredIdx >= 0) it.getInt(configuredIdx) == 1 else false
                    val statusStr = if (statusIdx >= 0) it.getString(statusIdx) else ""
                    if (isConfigured) configuredCount++
                    if (statusStr == "DUE") dueCount++
                    if (statusStr == "SOON") soonCount++
                }

                when {
                    dueCount > 0 -> DarbakAppStatus(
                        summaryText = "$dueCount صيانة مستحقة",
                        detailText = "توجد عناصر تتطلب الصيانة الآن",
                        alertCount = dueCount,
                        isError = true
                    )
                    soonCount > 0 -> DarbakAppStatus(
                        summaryText = "$soonCount صيانة قريبة",
                        detailText = "اقترب موعد بعض الصيانات",
                        alertCount = soonCount,
                        isWarning = true
                    )
                    configuredCount > 0 -> DarbakAppStatus(
                        summaryText = "الحالة جيدة",
                        detailText = "جميع صياناتك مهيأة وبحالة جيدة",
                        alertCount = 0
                    )
                    else -> DarbakAppStatus(
                        summaryText = "بانتظار التهيئة",
                        detailText = "قم بإعداد جداول الصيانة داخل التطبيق",
                        alertCount = 0
                    )
                }
            }
        }.getOrNull()
    }

    companion object {
        const val CONFIRMED_MAINTENANCE_PACKAGE = "com.abosultan.darbakmaintenance"
    }
}

/**
 * Central manager for Darbak Center (مركز دربك).
 * Detects installed Darbak ecosystem apps and resolves status through extensible status providers.
 */
class DarbakCenterManager(private val context: Context) {
    private val packageManager: PackageManager = context.packageManager
    private val providers = mutableMapOf<String, DarbakStatusProvider>()

    private val _darbakApps = MutableStateFlow<List<DarbakAppItem>>(emptyList())
    val darbakApps: StateFlow<List<DarbakAppItem>> = _darbakApps.asStateFlow()

    init {
        registerStatusProvider(DarbakMaintenanceStatusProvider())
    }

    fun registerStatusProvider(provider: DarbakStatusProvider) {
        providers[provider.targetPackageName] = provider
    }

    fun isAppInstalled(packageName: String?): Boolean {
        if (packageName.isNullOrBlank()) return false
        return runCatching {
            packageManager.getPackageInfo(packageName, 0)
            true
        }.getOrDefault(false)
    }

    fun launchApp(packageName: String?): Boolean {
        if (packageName.isNullOrBlank()) return false
        return runCatching {
            val intent = packageManager.getLaunchIntentForPackage(packageName) ?: return false
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        }.getOrDefault(false)
    }

    suspend fun refreshAppsAsync() = withContext(Dispatchers.IO) {
        val knownApps = listOf(
            DarbakAppDefinition(
                id = "darbak_maintenance",
                packageName = DarbakMaintenanceStatusProvider.CONFIRMED_MAINTENANCE_PACKAGE,
                titleArabic = "صيانة دربك",
                descriptionArabic = "متابعة زيوت وصيانة السيارة ومسافات المقطوعات",
                providerUri = "content://com.abosultan.darbakmaintenance.status/status",
                integrationState = DarbakIntegrationState.FULLY_INTEGRATED
            ),
            DarbakAppDefinition(
                id = "darbak_vehicle_hub",
                packageName = null,
                titleArabic = "مركز مركبتي دربك",
                descriptionArabic = "إدارة وحالة أجهزة وواجهات المركبة",
                providerUri = null,
                integrationState = DarbakIntegrationState.REVIEW_REQUIRED
            ),
            DarbakAppDefinition(
                id = "darbak_maps",
                packageName = null,
                titleArabic = "خرائط دربك",
                descriptionArabic = "الملاحة والخرائط الخارجية والدروب",
                providerUri = null,
                integrationState = DarbakIntegrationState.UNCONFIGURED
            ),
            DarbakAppDefinition(
                id = "darb_al_sout2",
                packageName = null,
                titleArabic = "درب الصوت 2",
                descriptionArabic = "مزامنة ملفات الصوت إلى مجلد الأصوات",
                providerUri = null,
                integrationState = DarbakIntegrationState.UNCONFIGURED
            )
        )

        val updatedList = knownApps.map { def ->
            val installed = if (def.packageName != null) isAppInstalled(def.packageName) else false
            val provider = if (def.packageName != null) providers[def.packageName] else null
            val status = if (installed && provider != null) {
                provider.queryStatus(context)
            } else if (def.integrationState != DarbakIntegrationState.FULLY_INTEGRATED) {
                DarbakAppStatus(
                    summaryText = if (def.integrationState == DarbakIntegrationState.REVIEW_REQUIRED)
                        "بانتظار توثيق معرّف الحزمة والـ API"
                    else
                        "غير مهيأ بعد",
                    detailText = "يتطلب توثيق المعرفات والـ Provider للربط الكامل"
                )
            } else null

            DarbakAppItem(
                id = def.id,
                packageName = def.packageName,
                titleArabic = def.titleArabic,
                descriptionArabic = def.descriptionArabic,
                isInstalled = installed,
                integrationState = def.integrationState,
                status = status,
                providerUri = def.providerUri
            )
        }

        _darbakApps.value = updatedList
    }

    private data class DarbakAppDefinition(
        val id: String,
        val packageName: String?,
        val titleArabic: String,
        val descriptionArabic: String,
        val providerUri: String?,
        val integrationState: DarbakIntegrationState
    )

    companion object {
        private const val TAG = "DarbakCenterManager"
    }
}
