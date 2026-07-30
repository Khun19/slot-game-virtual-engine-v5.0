package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sandbox.SandboxEngine
import com.example.sandbox.SandboxNativeBridge
import com.example.ui.components.AntiDetectPanel
import com.example.ui.components.ApkManagerSection
import com.example.ui.components.EngineMetricsHeader
import com.example.ui.components.EngineSettingsModal
import com.example.ui.components.FridaConsoleSection
import com.example.ui.components.LiveRtpMainDashboardSection
import com.example.ui.components.LiveRtpNotificationOverlay
import com.example.ui.components.LogConsoleSection
import com.example.ui.theme.SlotCyanAccent
import com.example.ui.theme.SlotEngineTheme
import com.example.ui.theme.SlotGoldPrimary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SlotEngineTheme {
                SlotEngineMainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlotEngineMainApp() {
    val context = LocalContext.current
    val engine = remember { SandboxEngine.getInstance(context) }

    val logs by engine.logs.collectAsState()
    val installedApks by engine.installedApks.collectAsState()
    val spoofProfile by engine.spoofProfile.collectAsState()
    val gpuConfig by engine.gpuConfig.collectAsState()
    val rtpData by engine.rtpData.collectAsState()
    val fridaState by engine.fridaState.collectAsState()

    var showSettingsModal by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SlotGoldPrimary.copy(alpha = 0.2f))
                                    .border(1.dp, SlotGoldPrimary, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Casino,
                                    contentDescription = null,
                                    tint = SlotGoldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                // TITLE EXPLICITLY REQUESTED BY PROMPT: "Slot Game Emulator Engine"
                                Text(
                                    text = "Slot Game Emulator Engine",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.testTag("app_title")
                                )
                                Text(
                                    text = "High-Performance Container Sandbox",
                                    fontSize = 10.sp,
                                    color = SlotCyanAccent
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            engine.interceptSysCall("libart.so", "RuntimeHookCheck")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Trigger Sycall Intercept",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { showSettingsModal = true }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Engine Settings",
                                tint = SlotGoldPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // 1. Engine Hardware Metrics Header
                EngineMetricsHeader(
                    fpsTarget = gpuConfig.refreshRateHz,
                    isGpuPassThrough = gpuConfig.renderBypassActive,
                    isNativeLoaded = SandboxNativeBridge.isNativeLoaded,
                    activeHooksCount = spoofProfile.activeHooksCount
                )

                // 2. Live RTP Monitor & Notification Bar Section
                LiveRtpMainDashboardSection(
                    rtpData = rtpData,
                    onToggleFloatingOverlay = { engine.toggleFloatingOverlay(it) },
                    onToggleSystemNotification = { engine.toggleSystemNotificationBar(it) },
                    onToggleRtpBoost = { engine.toggleRtpBoost() },
                    onUpdateServerUrl = { url, provider -> engine.updateServerRtpTracerUrl(url, provider) }
                )

                // 3. APK Manager Section with "Download / Stage APK" Button, Direct Downloader & APK list
                ApkManagerSection(
                    apks = installedApks,
                    onLoadApkClick = { },
                    onCustomApkLoaded = { appName, pkgName, sizeMb ->
                        engine.loadCustomApk(appName, pkgName, sizeMb)
                    },
                    onDirectDownloadApk = { url, appName, pkgName, onProgress ->
                        var successResult = false
                        engine.downloadAndInstallApk(
                            downloadUrl = url,
                            appName = appName,
                            packageName = pkgName,
                            onProgress = onProgress,
                            onResult = { ok, _ ->
                                successResult = ok
                            }
                        )
                        successResult
                    },
                    onPickLocalFolderApk = { filePath, appName, pkgName ->
                        engine.loadApkFromLocalFolder(filePath, appName, pkgName)
                    },
                    onDeleteApk = { pkgName ->
                        engine.deleteApk(pkgName)
                    },
                    onLaunchApk = { apk ->
                        engine.launchApkInSandbox(apk)
                    },
                    onStopApk = { pkgName ->
                        engine.stopApkInSandbox(pkgName)
                    },
                    onToggleFavorite = { pkgName, fav ->
                        engine.toggleFavorite(pkgName, fav)
                    }
                )

                // 4. Frida Hooking Engine Section
                FridaConsoleSection(
                    state = fridaState,
                    installedGames = installedApks,
                    onToggleScript = { scriptId -> engine.toggleFridaScript(scriptId) },
                    onInjectCustomScript = { name, code -> engine.injectCustomFridaScript(name, code) },
                    onSetSpeedMultiplier = { multiplier -> engine.setGameSpeedMultiplier(multiplier) },
                    onAutoGenerateGameScript = { appName, pkgName -> engine.generateAndInjectGameFridaScript(appName, pkgName) }
                )

                // 5. Anti-Detection & Device Spoofing Panel
                AntiDetectPanel(
                    profile = spoofProfile,
                    onUpdateProfile = { newProfile ->
                        engine.updateSpoofProfile(newProfile)
                    }
                )

                // 6. Sandbox Execution Console / Log Viewer
                LogConsoleSection(
                    logs = logs,
                    onClearLogs = { engine.clearLogs() }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Top Floating Notification Bar Overlay HUD
        LiveRtpNotificationOverlay(
            rtpData = rtpData,
            onToggleFloatingOverlay = { engine.toggleFloatingOverlay(it) },
            onToggleRtpBoost = { engine.toggleRtpBoost() },
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

    if (showSettingsModal) {
        EngineSettingsModal(
            config = gpuConfig,
            onDismiss = { showSettingsModal = false },
            onSaveConfig = { fps, passThrough ->
                engine.updateGpuConfig(fps, passThrough)
            }
        )
    }
}
