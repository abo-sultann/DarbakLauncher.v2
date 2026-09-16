package com.example.model

/**
 * Status summary for a Darbak ecosystem application.
 */
data class DarbakAppStatus(
    val summaryText: String,
    val detailText: String? = null,
    val alertCount: Int = 0,
    val isWarning: Boolean = false,
    val isError: Boolean = false,
    val extraData: Map<String, String> = emptyMap()
)

/**
 * Integration readiness for a Darbak ecosystem application.
 */
enum class DarbakIntegrationState {
    FULLY_INTEGRATED,
    UNCONFIGURED,
    REVIEW_REQUIRED
}

/**
 * Represents a Darbak ecosystem app registered in Darbak Center.
 */
data class DarbakAppItem(
    val id: String,
    val packageName: String?,
    val titleArabic: String,
    val descriptionArabic: String,
    val isInstalled: Boolean,
    val integrationState: DarbakIntegrationState,
    val status: DarbakAppStatus? = null,
    val providerUri: String? = null,
    val iconResName: String = "ic_car_launcher_icon"
)
