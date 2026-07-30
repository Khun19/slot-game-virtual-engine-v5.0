package com.example.sandbox

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class LogLevel {
    INFO, DEBUG, WARN, ERROR, SYSCALL, HOOK, GPU, NET, FRIDA
}

data class FridaScriptItem(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val code: String,
    val isActive: Boolean = true,
    val hookCount: Int = 2
)

data class FridaEngineState(
    val isGadgetAttached: Boolean = true,
    val port: Int = 27042,
    val activeScriptsCount: Int = 3,
    val totalActiveHooks: Int = 9,
    val speedMultiplier: Float = 1.0f,
    val currentTargetGame: String = "Megaways Gold 888",
    val currentTargetPackage: String = "com.casino.slots.megaways888",
    val scripts: List<FridaScriptItem> = emptyList()
)

data class SandboxLog(
    val timestamp: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()),
    val category: String,
    val message: String,
    val level: LogLevel = LogLevel.INFO
)

data class ApkPackageInfo(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val apkSizeBytes: Long,
    val isRunningInSandbox: Boolean = false,
    val targetFps: Int = 120,
    val iconResId: Int? = null,
    val category: String = "Slot Game",
    val antiDetectEnabled: Boolean = true,
    val localApkPath: String? = null,
    val isDownloaded: Boolean = false,
    val downloadSourceUrl: String? = null,
    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val lastLaunchedTimestamp: Long = System.currentTimeMillis()
)

data class AntiDetectionProfile(
    val imei: String = "867543029108234",
    val macAddress: String = "02:00:00:4A:8B:11",
    val androidId: String = "9774d56d682e549c",
    val deviceModel: String = "Samsung Galaxy S24 Ultra (Sandbox Virtual)",
    val buildFingerprint: String = "google/raven/raven:14/UP1A.231105.003/11018593:user/release-keys",
    val isRootHidden: Boolean = true,
    val isVirtualBoxSpoofed: Boolean = true,
    val activeHooksCount: Int = 32
)

data class GpuEngineConfig(
    val refreshRateHz: Int = 120,
    val gpuApi: String = "Vulkan 1.3 / Direct Pass-Through",
    val lowLatencyNetEnabled: Boolean = true,
    val cpuAffinityCores: String = "Cores 4-7 (High Performance)",
    val renderBypassActive: Boolean = true
)

enum class RtpStatus {
    COLD, NORMAL, WARM, HOT, ULTRA_PAYOUT
}

enum class RtpTrend {
    RISING, FALLING, STABLE
}

data class RtpLiveData(
    val currentRtp: Double = 96.8,
    val baselineRtp: Double = 96.0,
    val highestRtp: Double = 99.1,
    val lowestRtp: Double = 93.4,
    val changeDelta: Double = +1.4,
    val status: RtpStatus = RtpStatus.HOT,
    val trend: RtpTrend = RtpTrend.RISING,
    val spinCount: Int = 184,
    val volatility: String = "HIGH (x5000 Max)",
    val recentHistory: List<Double> = listOf(94.5, 95.2, 95.8, 96.1, 95.9, 96.5, 97.2, 96.8, 97.5, 98.2, 97.8),
    val floatingOverlayEnabled: Boolean = true,
    val isSystemNotificationBarActive: Boolean = true,
    val activeGamePackage: String? = "com.casino.slots.megaways888",
    val activeGameName: String? = "739Slots Megaways Gold",
    val lastWinMultiplier: Double = 18.5,
    val hotColdMeterPercent: Int = 85,
    val isRtpBoostActive: Boolean = true,
    val connectedServerUrl: String = "https://739slots.com/game-api/ws",
    val targetProviderName: String = "739Slots Engine (PG / Pragmatic Direct)",
    val interceptedPacketCount: Long = 14209,
    val isSslInterceptionActive: Boolean = true,
    val lastRawRtpPacket: String = "{\"game\":\"739Slots_Fortune_Tiger\",\"server_rtp\":97.8,\"payout_multiplier\":120.5,\"rng_seed\":\"0x739SLOT_HOT\"}"
)
