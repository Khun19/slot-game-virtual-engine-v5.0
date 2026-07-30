package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CodeOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import com.example.sandbox.ApkPackageInfo
import com.example.sandbox.FridaEngineState
import com.example.sandbox.FridaScriptItem
import com.example.ui.theme.SlotCyanAccent
import com.example.ui.theme.SlotEmeraldGreen
import com.example.ui.theme.SlotGoldPrimary
import com.example.ui.theme.TerminalBackground
import com.example.ui.theme.TerminalCyanText
import com.example.ui.theme.TerminalGoldText

@Composable
fun FridaConsoleSection(
    state: FridaEngineState,
    installedGames: List<ApkPackageInfo> = emptyList(),
    onToggleScript: (scriptId: String) -> Unit,
    onInjectCustomScript: (name: String, code: String) -> Unit,
    onSetSpeedMultiplier: (multiplier: Float) -> Unit,
    onAutoGenerateGameScript: ((appName: String, packageName: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(true) }
    var showCustomJsEditor by remember { mutableStateOf(false) }
    var customScriptName by remember { mutableStateOf("") }
    var customJsCode by remember {
        mutableStateOf(
            "Java.perform(() => {\n" +
            "  const SlotEngine = Java.use('com.slot.game.Engine');\n" +
            "  SlotEngine.getWinMultiplier.implementation = function() {\n" +
            "    console.log('[Frida::Custom] Intercepted win multiplier hook!');\n" +
            "    return 50.0;\n" +
            "  };\n" +
            "});"
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(SlotCyanAccent.copy(alpha = 0.4f))
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SlotCyanAccent.copy(alpha = 0.15f))
                            .border(1.dp, SlotCyanAccent.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = null,
                            tint = SlotCyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Frida Hooking Engine",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.testTag("frida_header")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SlotEmeraldGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "v16.1.4 ACTIVE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlotEmeraldGreen
                                )
                            }
                        }

                        Text(
                            text = "Dynamic Native & Java Symbol Interceptor • Stalker Clock Scaling",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SlotGoldPrimary.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${state.totalActiveHooks} Hooks",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlotGoldPrimary
                        )
                    }
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    // Metric Badges Strip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FridaStatBadge(
                            title = "Active Scripts",
                            value = "${state.activeScriptsCount} Running",
                            color = SlotCyanAccent,
                            modifier = Modifier.weight(1f)
                        )
                        FridaStatBadge(
                            title = "Total PLT/JNI Hooks",
                            value = "${state.totalActiveHooks} Intercepted",
                            color = SlotEmeraldGreen,
                            modifier = Modifier.weight(1f)
                        )
                        FridaStatBadge(
                            title = "Animation Speed",
                            value = "${String.format("%.1f", state.speedMultiplier)}x Turbo",
                            color = SlotGoldPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Speed Hack Slider Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = TerminalBackground),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlotGoldPrimary.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = null,
                                        tint = SlotGoldPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Reel Spin Speed Hack (Frida Stalker Hook)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TerminalGoldText
                                    )
                                }
                                Text(
                                    text = "${String.format("%.1f", state.speedMultiplier)}x Speed",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlotGoldPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Slider(
                                value = state.speedMultiplier,
                                onValueChange = { onSetSpeedMultiplier(it) },
                                valueRange = 0.5f..5.0f,
                                steps = 8,
                                colors = SliderDefaults.colors(
                                    thumbColor = SlotGoldPrimary,
                                    activeTrackColor = SlotGoldPrimary,
                                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier.testTag("frida_speed_slider")
                            )

                            // Speed Presets
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(1.0f to "1.0x Normal", 2.0f to "2.0x Fast", 3.5f to "3.5x Turbo", 5.0f to "5.0x Max").forEach { (speed, label) ->
                                    val isSelected = Math.abs(state.speedMultiplier - speed) < 0.1f
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) SlotGoldPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                            .clickable { onSetSpeedMultiplier(speed) }
                                            .padding(vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Game-Specific Frida JS Script Auto Generator Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlotCyanAccent.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = SlotCyanAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Game-Specific Frida JS Auto Generator",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SlotCyanAccent.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "TARGET: ${state.currentTargetGame}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SlotCyanAccent
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Detects game engine architecture & auto-hooks RNG calculations, SSL sockets, and payout multipliers.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Select Provider Engine Architecture Preset:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val providerPresets = listOf(
                                    "PG Soft" to ("PG Soft Fortune Tiger" to "com.pgsoft.slot.engine"),
                                    "Pragmatic" to ("Gates of Olympus 3D" to "com.pragmatic.olympus"),
                                    "Megaways" to ("Megaways Gold 888" to "com.casino.slots.megaways888"),
                                    "JILI/Spade" to ("JILI Golden Empire" to "com.jili.slot.empire")
                                )

                                providerPresets.forEach { (label, gameInfo) ->
                                    val (appName, pkgName) = gameInfo
                                    val isCurrent = state.currentTargetGame.contains(label, ignoreCase = true) || state.currentTargetGame.contains(appName, ignoreCase = true)

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isCurrent) SlotCyanAccent else MaterialTheme.colorScheme.surface)
                                            .border(1.dp, if (isCurrent) SlotCyanAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                            .clickable {
                                                onAutoGenerateGameScript?.invoke(appName, pkgName)
                                            }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 10.sp,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isCurrent) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    onAutoGenerateGameScript?.invoke(state.currentTargetGame, state.currentTargetPackage)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SlotCyanAccent),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                                    .testTag("auto_generate_frida_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Auto-Generate & Hook JS for [${state.currentTargetGame}]",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.surface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Preset Scripts Title & Custom Inject Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Preset Frida Script Library",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Button(
                            onClick = { showCustomJsEditor = !showCustomJsEditor },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (showCustomJsEditor) SlotCyanAccent else SlotCyanAccent.copy(alpha = 0.2f),
                                contentColor = if (showCustomJsEditor) MaterialTheme.colorScheme.surface else SlotCyanAccent
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(
                                imageVector = if (showCustomJsEditor) Icons.Default.CodeOff else Icons.Default.Code,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showCustomJsEditor) "Hide JS Editor" else "Inject Custom JS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Custom JS Editor Box
                    AnimatedVisibility(visible = showCustomJsEditor) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            colors = CardDefaults.cardColors(containerColor = TerminalBackground),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SlotCyanAccent.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Dynamic Frida JavaScript Injection Console",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TerminalCyanText
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = customScriptName,
                                    onValueChange = { customScriptName = it },
                                    label = { Text("Script Name", fontSize = 11.sp) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SlotCyanAccent,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = customJsCode,
                                    onValueChange = { customJsCode = it },
                                    label = { Text("Frida JS Code (Java.perform / Interceptor.attach)", fontSize = 11.sp) },
                                    minLines = 4,
                                    maxLines = 8,
                                    textStyle = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = TerminalCyanText
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SlotCyanAccent,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        onInjectCustomScript(
                                            customScriptName.ifBlank { "Custom Frida Hook" },
                                            customJsCode
                                        )
                                        showCustomJsEditor = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SlotCyanAccent),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("inject_custom_frida_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Execute & Inject Frida Script",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.surface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset Scripts Cards
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.scripts.forEach { script ->
                            FridaScriptCard(
                                script = script,
                                onToggle = { onToggleScript(script.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FridaScriptCard(
    script: FridaScriptItem,
    onToggle: () -> Unit
) {
    var showCodePreview by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (script.isActive)
                SlotCyanAccent.copy(alpha = 0.08f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (script.isActive) SlotCyanAccent.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = script.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SlotCyanAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = script.category,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SlotCyanAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = script.description,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = script.isActive,
                        onCheckedChange = { onToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SlotCyanAccent,
                            checkedTrackColor = SlotCyanAccent.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.testTag("switch_frida_${script.id}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (script.isActive) SlotEmeraldGreen else MaterialTheme.colorScheme.outline)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (script.isActive) "${script.hookCount} Hooks Attached" else "Disabled",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (script.isActive) SlotEmeraldGreen else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = if (showCodePreview) "Hide Code" else "View JS Code",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlotCyanAccent,
                    modifier = Modifier.clickable { showCodePreview = !showCodePreview }
                )
            }

            AnimatedVisibility(visible = showCodePreview) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(TerminalBackground)
                        .padding(8.dp)
                ) {
                    Text(
                        text = script.code,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TerminalCyanText
                    )
                }
            }
        }
    }
}

@Composable
private fun FridaStatBadge(
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
