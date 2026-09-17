package com.example.data

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import android.util.Log
import java.io.File
import java.io.RandomAccessFile
import kotlin.math.roundToInt

data class HeadUnitVitals(
    val ramUsedPercent: Int = 0,
    val ramAvailableMb: Long = 0L,
    val ramTotalMb: Long = 0L,
    val isRamAvailable: Boolean = false,

    val storageUsedPercent: Int = 0,
    val storageAvailableGb: Float = 0f,
    val storageTotalGb: Float = 0f,
    val isStorageAvailable: Boolean = false,

    val cpuLoadPercent: Int = 0,
    val isCpuLoadAvailable: Boolean = false,

    val temperatureCelsius: Float = 0f,
    val isTemperatureAvailable: Boolean = false
)

class HeadUnitVitalsManager(private val context: Context) {

    private var previousCpuIdle: Long = 0L
    private var previousCpuTotal: Long = 0L

    fun readVitals(): HeadUnitVitals {
        val ram = readMemoryVitals()
        val storage = readStorageVitals()
        val cpu = readCpuVitals()
        val temp = readTemperatureVitals()

        return HeadUnitVitals(
            ramUsedPercent = ram.first,
            ramAvailableMb = ram.second,
            ramTotalMb = ram.third,
            isRamAvailable = ram.third > 0L,

            storageUsedPercent = storage.first,
            storageAvailableGb = storage.second,
            storageTotalGb = storage.third,
            isStorageAvailable = storage.third > 0f,

            cpuLoadPercent = cpu.first,
            isCpuLoadAvailable = cpu.second,

            temperatureCelsius = temp.first,
            isTemperatureAvailable = temp.second
        )
    }

    private fun readMemoryVitals(): Triple<Int, Long, Long> = try {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val info = ActivityManager.MemoryInfo()
        if (am != null) {
            am.getMemoryInfo(info)
            val totalMb = info.totalMem / (1024 * 1024)
            val availMb = info.availMem / (1024 * 1024)
            val usedMb = (totalMb - availMb).coerceAtLeast(0L)
            val usedPercent = if (totalMb > 0L) ((usedMb.toDouble() / totalMb.toDouble()) * 100).roundToInt().coerceIn(0, 100) else 0
            Triple(usedPercent, availMb, totalMb)
        } else Triple(0, 0L, 0L)
    } catch (_: Exception) {
        Triple(0, 0L, 0L)
    }

    private fun readStorageVitals(): Triple<Int, Float, Float> = try {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availableBlocks = stat.availableBlocksLong

        val totalBytes = totalBlocks * blockSize
        val availBytes = availableBlocks * blockSize
        val usedBytes = (totalBytes - availBytes).coerceAtLeast(0L)

        val totalGb = totalBytes / (1024f * 1024f * 1024f)
        val availGb = availBytes / (1024f * 1024f * 1024f)
        val usedPercent = if (totalBytes > 0L) ((usedBytes.toDouble() / totalBytes.toDouble()) * 100).roundToInt().coerceIn(0, 100) else 0

        Triple(usedPercent, availGb, totalGb)
    } catch (_: Exception) {
        Triple(0, 0f, 0f)
    }

    private fun readCpuVitals(): Pair<Int, Boolean> = try {
        val reader = RandomAccessFile("/proc/stat", "r")
        val line = reader.readLine()
        reader.close()

        if (!line.isNullOrBlank() && line.startsWith("cpu ")) {
            val toks = line.split("\\s+".toRegex())
            if (toks.size >= 8) {
                val user = toks[1].toLong()
                val nice = toks[2].toLong()
                val system = toks[3].toLong()
                val idle = toks[4].toLong()
                val iowait = toks[5].toLong()
                val irq = toks[6].toLong()
                val softirq = toks[7].toLong()

                val total = user + nice + system + idle + iowait + irq + softirq
                val totalIdle = idle + iowait

                val diffIdle = totalIdle - previousCpuIdle
                val diffTotal = total - previousCpuTotal

                previousCpuIdle = totalIdle
                previousCpuTotal = total

                if (diffTotal > 0L) {
                    val cpuLoad = (((diffTotal - diffIdle).toDouble() / diffTotal.toDouble()) * 100).roundToInt().coerceIn(0, 100)
                    Pair(cpuLoad, true)
                } else Pair(0, false)
            } else Pair(0, false)
        } else Pair(0, false)
    } catch (_: Exception) {
        Pair(0, false)
    }

    private fun readTemperatureVitals(): Pair<Float, Boolean> {
        // Try thermal zone files first
        try {
            for (i in 0..9) {
                val zoneFile = File("/sys/class/thermal/thermal_zone$i/temp")
                if (zoneFile.exists() && zoneFile.canRead()) {
                    val raw = zoneFile.readText().trim()
                    val valFloat = raw.toFloatOrNull()
                    if (valFloat != null) {
                        val temp = if (valFloat > 1000f) valFloat / 1000f else valFloat
                        if (temp in 10f..110f) return Pair(temp, true)
                    }
                }
            }
        } catch (_: Exception) { }

        // Battery temperature fallback
        try {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val intent = context.registerReceiver(null, filter)
            if (intent != null) {
                val tempRaw = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -999)
                if (tempRaw != -999 && tempRaw > 0) {
                    val temp = tempRaw / 10f
                    if (temp in 10f..100f) return Pair(temp, true)
                }
            }
        } catch (_: Exception) { }

        return Pair(0f, false)
    }
}
