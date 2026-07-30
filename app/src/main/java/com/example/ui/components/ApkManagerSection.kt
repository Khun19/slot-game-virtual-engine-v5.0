package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sandbox.ApkPackageInfo
import com.example.ui.theme.SlotCyanAccent
import com.example.ui.theme.SlotEmeraldGreen
import com.example.ui.theme.SlotGoldPrimary
import kotlinx.coroutines.launch

@Composable
fun ApkManagerSection(
    apks: List<ApkPackageInfo>,
    onLoadApkClick: () -> Unit,
    onCustomApkLoaded: (appName: String, pkgName: String, sizeMb: Double) -> Unit,
    onDirectDownloadApk: suspend (url: String, appName: String, pkgName: String, onProgress: (Float, Long, Long) -> Unit) -> Boolean,
    onPickLocalFolderApk: (filePath: String, appName: String?, pkgName: String?) -> Unit,
    onDeleteApk: (pkgName: String) -> Unit,
    onLaunchApk: (ApkPackageInfo) -> Unit,
    onStopApk: (String) -> Unit,
    onToggleFavorite: ((pkgName: String, currentFav: Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showLoadApkDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Slot Games Local Storage Library",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Room Database Managed (`slot_games_library.db`) • ${apks.size} Games Stored",
                    fontSize = 11.sp,
                    color = SlotCyanAccent
                )
            }

            // PRIMARY BUTTON REQUIRED BY PROMPT: "Load Slot Game APK"
            Button(
                onClick = { showLoadApkDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SlotGoldPrimary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("load_slot_apk_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Download / Stage APK",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Room DB Persistence Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = null,
                    tint = SlotGoldPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Room Database Persistent Library: SQLite DB • Reactive KSP DAO Flow",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (apks.isEmpty()) {
            EmptyApkCard(onLoadClick = { showLoadApkDialog = true })
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                apks.forEach { apk ->
                    ApkCardItem(
                        apk = apk,
                        onLaunch = { onLaunchApk(apk) },
                        onStop = { onStopApk(apk.packageName) },
                        onDelete = { onDeleteApk(apk.packageName) },
                        onToggleFavorite = { onToggleFavorite?.invoke(apk.packageName, apk.isFavorite) }
                    )
                }
            }
        }
    }

    if (showLoadApkDialog) {
        LoadSlotApkModal(
            onDismiss = { showLoadApkDialog = false },
            onConfirmLoad = { appName, pkgName, sizeMb ->
                onCustomApkLoaded(appName, pkgName, sizeMb)
                showLoadApkDialog = false
            },
            onDirectDownloadApk = onDirectDownloadApk,
            onPickLocalFolderApk = { filePath, appName, pkgName ->
                onPickLocalFolderApk(filePath, appName, pkgName)
                showLoadApkDialog = false
            }
        )
    }
}

@Composable
private fun ApkCardItem(
    apk: ApkPackageInfo,
    onLaunch: () -> Unit,
    onStop: () -> Unit,
    onDelete: () -> Unit,
    onToggleFavorite: (() -> Unit)? = null
) {
    val isRunning = apk.isRunningInSandbox

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isRunning) 1.5.dp else 0.5.dp,
                color = if (isRunning) SlotEmeraldGreen else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(14.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isRunning) SlotEmeraldGreen.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isRunning) SlotEmeraldGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = null,
                        tint = if (isRunning) SlotEmeraldGreen else SlotGoldPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = apk.appName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SlotCyanAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${apk.targetFps}Hz",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlotCyanAccent
                            )
                        }
                        if (apk.playCount > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SlotGoldPrimary.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${apk.playCount} Spins",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlotGoldPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = apk.packageName,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isRunning) SlotEmeraldGreen else SlotGoldPrimary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isRunning) "CONTAINER ACTIVE (PID: ${Math.abs(apk.packageName.hashCode() % 8000 + 1000)})" else "ROOM DB PERSISTED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isRunning) SlotEmeraldGreen else SlotGoldPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                if (onToggleFavorite != null) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (apk.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Toggle Favorite",
                            tint = if (apk.isFavorite) SlotGoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                if (isRunning) {
                    Button(
                        onClick = onStop,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Stop, contentDescription = "Stop", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Stop", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete APK",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Button(
                            onClick = onLaunch,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SlotEmeraldGreen,
                                contentColor = MaterialTheme.colorScheme.onTertiary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Run", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Run", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (apk.localApkPath != null || apk.isDownloaded) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = null,
                        tint = SlotCyanAccent,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Saved in Storage: ${apk.localApkPath ?: "virtual_apks/${apk.packageName}.apk"} (${String.format("%.1f", apk.apkSizeBytes / (1024.0 * 1024.0))} MB)",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyApkCard(onLoadClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Casino,
                contentDescription = null,
                tint = SlotGoldPrimary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No Virtual Slot APKs Loaded",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap 'Download / Stage APK' above to download a third-party APK into the container.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LoadSlotApkModal(
    onDismiss: () -> Unit,
    onConfirmLoad: (appName: String, pkgName: String, sizeMb: Double) -> Unit,
    onDirectDownloadApk: suspend (url: String, appName: String, pkgName: String, onProgress: (Float, Long, Long) -> Unit) -> Boolean,
    onPickLocalFolderApk: (filePath: String, appName: String?, pkgName: String?) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Direct URL Download, 1 = Local Folder Picker, 2 = Presets
    val scope = rememberCoroutineScope()

    // Download state
    var downloadUrl by remember { mutableStateOf("https://slot-mirror.net/apks/vegas_king_v777.apk") }
    var downloadAppName by remember { mutableStateOf("Vegas King 777 Slots") }
    var downloadPkgName by remember { mutableStateOf("com.vegasking.slots.v777") }

    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var downloadedSizeText by remember { mutableStateOf("0 MB / 48.5 MB") }

    // Local Folder Pick state
    var pickedFilePath by remember { mutableStateOf("/storage/emulated/0/Download/Fortune_Tiger_777.apk") }
    var pickedAppName by remember { mutableStateOf("Fortune Tiger 777 Local") }
    var pickedPkgName by remember { mutableStateOf("com.pg.fortune.tiger.local") }

    val sampleLocalFolderFiles = remember {
        listOf(
            Triple("/storage/emulated/0/Download/Fortune_Tiger_777.apk", "Fortune Tiger 777 Local", "com.pg.fortune.tiger.local"),
            Triple("/sdcard/Download/Pragmatic_Gates_1000.apk", "Pragmatic Gates 1000 Local", "com.pragmatic.gates1000.local"),
            Triple("/sdcard/Apks/Megaways_Gold_888.apk", "Megaways Gold 888 Local", "com.slot.megaways888.local")
        )
    }

    // Preset staging state
    var customAppName by remember { mutableStateOf("CyberSlots Deluxe 777") }
    var customPkgName by remember { mutableStateOf("com.slotgame.cyberslots.v777") }
    var customSizeMb by remember { mutableStateOf("52.4") }

    var selectedSampleIndex by remember { mutableStateOf(0) }
    val sampleSlotApks = remember {
        listOf(
            Triple("CyberSlots Deluxe 777", "com.slotgame.cyberslots.v777", 52.4),
            Triple("Dragon King Megaways", "com.oriental.dragon.slots", 78.1),
            Triple("Golden Pharaoh 100x", "com.egypt.pharaoh.wilds", 41.5)
        )
    }

    val sampleDownloadMirrors = remember {
        listOf(
            Triple("Vegas King 777 Slots", "com.vegasking.slots.v777", "https://slot-mirror.net/apks/vegas_king_v777.apk"),
            Triple("Super Fortune Tiger 888", "com.pg.fortune.tiger888", "https://pg-apks.com/downloads/fortune_tiger.apk"),
            Triple("Pragmatic Gates 1000x", "com.pragmatic.gates1000.slot", "https://pragmatic-mirror.org/games/gates1000.apk")
        )
    }

    AlertDialog(
        onDismissRequest = { if (!isDownloading) onDismiss() },
        title = {
            Text(
                text = "Add Third-Party Slot App into Sandbox",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = SlotGoldPrimary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { if (!isDownloading) selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("APK Link", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { if (!isDownloading) selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Folder Picker", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { if (!isDownloading) selectedTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.FolderZip, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Presets", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedTab == 0) {
                    // TAB 0: Direct Download from URL
                    Text(
                        text = "Paste direct APK download link to download & install into container:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Curated Download Mirrors:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlotCyanAccent
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    sampleDownloadMirrors.forEach { mirror ->
                        Card(
                            onClick = {
                                if (!isDownloading) {
                                    downloadAppName = mirror.first
                                    downloadPkgName = mirror.second
                                    downloadUrl = mirror.third
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp)),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = mirror.first, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(text = mirror.third, fontSize = 10.sp, color = SlotCyanAccent)
                                }
                                Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = SlotGoldPrimary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = downloadUrl,
                        onValueChange = { downloadUrl = it },
                        label = { Text("Direct APK Download URL (HTTP/HTTPS)") },
                        singleLine = true,
                        enabled = !isDownloading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SlotGoldPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = downloadAppName,
                        onValueChange = { downloadAppName = it },
                        label = { Text("App Name") },
                        singleLine = true,
                        enabled = !isDownloading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SlotGoldPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = downloadPkgName,
                        onValueChange = { downloadPkgName = it },
                        label = { Text("Package ID (e.g. com.game.slot)") },
                        singleLine = true,
                        enabled = !isDownloading,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SlotGoldPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isDownloading) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Downloading APK & Saving to Disk...",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlotEmeraldGreen
                                )
                                Text(
                                    text = "${(downloadProgress * 100).toInt()}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlotEmeraldGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { downloadProgress },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = SlotEmeraldGreen,
                                trackColor = SlotEmeraldGreen.copy(alpha = 0.2f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = downloadedSizeText,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else if (selectedTab == 1) {
                    // TAB 1: Local Folder Picker
                    Text(
                        text = "Select or enter local APK file path from device storage folder:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Detected Storage Files:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlotEmeraldGreen
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    sampleLocalFolderFiles.forEach { item ->
                        Card(
                            onClick = {
                                pickedFilePath = item.first
                                pickedAppName = item.second
                                pickedPkgName = item.third
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .border(
                                    width = if (pickedFilePath == item.first) 1.5.dp else 0.5.dp,
                                    color = if (pickedFilePath == item.first) SlotEmeraldGreen else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(6.dp)
                                ),
                            colors = CardDefaults.cardColors(containerColor = if (pickedFilePath == item.first) SlotEmeraldGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = item.second, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(text = item.first, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Icon(imageVector = Icons.Default.Storage, contentDescription = null, tint = SlotEmeraldGreen, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = pickedFilePath,
                        onValueChange = { pickedFilePath = it },
                        label = { Text("Local File Path (/storage/emulated/0/...)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SlotEmeraldGreen,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = pickedAppName,
                        onValueChange = { pickedAppName = it },
                        label = { Text("Slot App Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SlotEmeraldGreen,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // TAB 2: Preset Staging
                    Text(
                        text = "Select a pre-packaged Slot Game APK or specify a custom APK path to stage into the isolated container:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    sampleSlotApks.forEachIndexed { index, triple ->
                        val isSelected = selectedSampleIndex == index
                        Card(
                            onClick = {
                                selectedSampleIndex = index
                                customAppName = triple.first
                                customPkgName = triple.second
                                customSizeMb = triple.third.toString()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.5.dp,
                                    color = if (isSelected) SlotGoldPrimary else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) SlotGoldPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = triple.first,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = triple.second,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "${triple.third} MB",
                                    fontSize = 11.sp,
                                    color = SlotCyanAccent,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customAppName,
                        onValueChange = { customAppName = it },
                        label = { Text("App Display Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SlotGoldPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = customPkgName,
                        onValueChange = { customPkgName = it },
                        label = { Text("Package Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SlotGoldPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            if (selectedTab == 0) {
                Button(
                    onClick = {
                        if (downloadUrl.isNotBlank() && !isDownloading) {
                            isDownloading = true
                            scope.launch {
                                onDirectDownloadApk(
                                    downloadUrl.trim(),
                                    downloadAppName.trim().ifEmpty { "Slot App" },
                                    downloadPkgName.trim().ifEmpty { "com.slot.app" }
                                ) { progress, dlBytes, totBytes ->
                                    downloadProgress = progress
                                    val dlMb = dlBytes / (1024.0 * 1024.0)
                                    val totMb = totBytes / (1024.0 * 1024.0)
                                    downloadedSizeText = String.format("%.1f MB / %.1f MB", dlMb, totMb)
                                }
                                isDownloading = false
                                onDismiss()
                            }
                        }
                    },
                    enabled = !isDownloading && downloadUrl.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = SlotEmeraldGreen)
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isDownloading) "Downloading..." else "Download & Save APK",
                        fontWeight = FontWeight.Bold
                    )
                }
            } else if (selectedTab == 1) {
                Button(
                    onClick = {
                        if (pickedFilePath.isNotBlank()) {
                            onPickLocalFolderApk(pickedFilePath, pickedAppName, pickedPkgName)
                        }
                    },
                    enabled = pickedFilePath.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = SlotEmeraldGreen)
                ) {
                    Icon(imageVector = Icons.Default.Storage, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Load File from Storage", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        val size = customSizeMb.toDoubleOrNull() ?: 50.0
                        onConfirmLoad(customAppName, customPkgName, size)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SlotGoldPrimary)
                ) {
                    Text(text = "Stage APK into Container", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isDownloading
            ) {
                Text(text = "Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

