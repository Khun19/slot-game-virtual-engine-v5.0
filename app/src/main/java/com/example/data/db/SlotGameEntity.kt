package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "slot_games")
data class SlotGameEntity(
    @PrimaryKey val packageName: String,
    val appName: String,
    val versionName: String = "1.0.0",
    val apkSizeBytes: Long = 48500000L,
    val isRunningInSandbox: Boolean = false,
    val targetFps: Int = 120,
    val category: String = "Slot Machine",
    val localApkPath: String? = null,
    val downloadSourceUrl: String? = null,
    val isDownloaded: Boolean = true,
    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val lastLaunchedTimestamp: Long = System.currentTimeMillis(),
    val installedAtTimestamp: Long = System.currentTimeMillis()
)
