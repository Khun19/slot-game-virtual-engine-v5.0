package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sandbox.RtpLiveData
import com.example.sandbox.RtpStatus
import com.example.sandbox.RtpTrend
import com.example.ui.theme.SlotCyanAccent
import com.example.ui.theme.SlotEmeraldGreen
import com.example.ui.theme.SlotGoldPrimary

@Composable
fun LiveRtpNotificationOverlay(
    rtpData: RtpLiveData,
    onToggleFloatingOverlay: (Boolean) -> Unit,
    onToggleRtpBoost: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!rtpData.floatingOverlayEnabled) return

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val statusColor = when (rtpData.status) {
        RtpStatus.ULTRA_PAYOUT -> Color(0xFFFF0055)
        RtpStatus.HOT -> SlotEmeraldGreen
        RtpStatus.WARM -> SlotGoldPrimary
        RtpStatus.NORMAL -> SlotCyanAccent
        RtpStatus.COLD -> Color(0xFF64B5F6)
    }

    val statusTag = when (rtpData.status) {
        RtpStatus.ULTRA_PAYOUT -> "⚡ ULTRA"
        RtpStatus.HOT -> "🔥 HOT"
        RtpStatus.WARM -> "☀️ WARM"
        RtpStatus.NORMAL -> "⚖️ NORMAL"
        RtpStatus.COLD -> "❄️ COLD"
    }

    // Network Speed Meter Style Floating Pill Widget
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 2.dp, start = 8.dp, end = 8.dp)
            .shadow(8.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xF00D1527),
                        Color(0xF00A0E1A)
                    )
                )
            )
            .border(1.2.dp, statusColor.copy(alpha = 0.85f), CircleShape)
            .testTag("floating_network_speed_rtp_meter")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Live Signal / Speed Meter Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Live Status Pulse Dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                        .alpha(alphaAnim)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "RTP:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "${String.format("%.1f", rtpData.currentRtp)}%",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = statusColor,
                    modifier = Modifier.testTag("network_speed_rtp_value")
                )
            }

            // Middle: Latency & Trend (Network Speed Meter Style)
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Network Ping Latency Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(SlotCyanAccent.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${(rtpData.currentRtp * 0.18).toInt() + 12}ms",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlotCyanAccent,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Trend Badge
                val deltaText = if (rtpData.changeDelta >= 0) "↑+${String.format("%.1f", rtpData.changeDelta)}%" else "↓${String.format("%.1f", rtpData.changeDelta)}%"
                val trendColor = if (rtpData.changeDelta >= 0) SlotEmeraldGreen else MaterialTheme.colorScheme.error

                Text(
                    text = deltaText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = trendColor
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Status Tag (HOT / COLD)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.25f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = statusTag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            // Right: Close Button
            IconButton(
                onClick = { onToggleFloatingOverlay(false) },
                modifier = Modifier.size(22.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Hide Floating Speed Meter",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun RtpSparklineCanvas(
    history: List<Double>,
    lineColor: Color
) {
    if (history.size < 2) return

    Canvas(modifier = Modifier.fillMaxWidth().height(40.dp)) {
        val width = size.width
        val height = size.height

        val minRtp = (history.minOrNull() ?: 90.0) - 0.5
        val maxRtp = (history.maxOrNull() ?: 100.0) + 0.5
        val range = (maxRtp - minRtp).coerceAtLeast(1.0)

        val points = history.mapIndexed { index, value ->
            val x = (index.toFloat() / (history.size - 1)) * width
            val normalizedY = ((value - minRtp) / range).toFloat()
            val y = height - (normalizedY * height)
            Offset(x, y)
        }

        val path = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx())
        )

        // Draw glow fill gradient
        val fillPath = Path().apply {
            addPath(path)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.3f), Color.Transparent)
            )
        )

        // Draw last point dot
        val lastPoint = points.last()
        drawCircle(
            color = Color.White,
            radius = 4.dp.toPx(),
            center = lastPoint
        )
        drawCircle(
            color = lineColor,
            radius = 2.dp.toPx(),
            center = lastPoint
        )
    }
}

@Composable
fun LiveRtpMainDashboardSection(
    rtpData: RtpLiveData,
    onToggleFloatingOverlay: (Boolean) -> Unit,
    onToggleSystemNotification: (Boolean) -> Unit,
    onToggleRtpBoost: () -> Unit,
    onUpdateServerUrl: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var customUrlInput by remember { mutableStateOf(rtpData.connectedServerUrl) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_rtp_dashboard_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SlotEmeraldGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = SlotEmeraldGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${rtpData.activeGameName ?: "Active Slot Game"} Real-Time RTP Tracer",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Dynamic SSL/WebSocket Interceptor for ${rtpData.activeGamePackage ?: "Current Game"}",
                            fontSize = 11.sp,
                            color = SlotCyanAccent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main stats grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = "LIVE RTP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlotCyanAccent)
                        Text(
                            text = "${String.format("%.1f", rtpData.currentRtp)}%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = SlotEmeraldGreen
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = "PEAK RTP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlotGoldPrimary)
                        Text(
                            text = "${String.format("%.1f", rtpData.highestRtp)}%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = SlotGoldPrimary
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = "PACKETS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "#${rtpData.interceptedPacketCount}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Server Presets (739Slots, PG Soft, Pragmatic, Spadegaming)
            Text(
                text = "Target Slot Provider Server:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val presets = listOf(
                    Triple("739Slots", "https://739slots.com/", "739Slots Engine"),
                    Triple("PG Soft", "https://pgsoft-games.com/ws", "PG Soft Live"),
                    Triple("Pragmatic", "https://pragmaticplay.net/ws", "Pragmatic Play")
                )

                presets.forEach { (label, url, providerName) ->
                    val isSelected = rtpData.connectedServerUrl.contains(label.lowercase()) || rtpData.targetProviderName.contains(label)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            customUrlInput = url
                            onUpdateServerUrl(url, providerName)
                        },
                        label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(12.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SlotEmeraldGreen.copy(alpha = 0.2f),
                            selectedLabelColor = SlotEmeraldGreen
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Direct Server URL Connection Box
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = customUrlInput,
                    onValueChange = { customUrlInput = it },
                    label = { Text("Game Site / WebSocket API URL", fontSize = 10.sp) },
                    placeholder = { Text("https://739slots.com/", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SlotCyanAccent,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        onUpdateServerUrl(customUrlInput, "Custom Slot Site Engine")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SlotCyanAccent, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(52.dp).padding(top = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Connect", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Display Intercepted Raw RTP Payload Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0A0E17))
                    .border(1.dp, SlotCyanAccent.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "JNI SSL_read() DECODED PACKET:",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlotCyanAccent
                        )
                        Text(
                            text = "CONNECTED: ${rtpData.connectedServerUrl}",
                            fontSize = 9.sp,
                            color = SlotEmeraldGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = rtpData.lastRawRtpPacket,
                        fontSize = 10.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = Color(0xFFFFD54F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Toggles Row
            Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = SlotGoldPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Top Floating Notification Bar Overlay", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Show live RTP bar on top of screen while playing games", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Switch(
                        checked = rtpData.floatingOverlayEnabled,
                        onCheckedChange = onToggleFloatingOverlay,
                        colors = SwitchDefaults.colors(checkedThumbColor = SlotEmeraldGreen, checkedTrackColor = SlotEmeraldGreen.copy(alpha = 0.3f))
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = SlotCyanAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "RTP Optimization Interceptor", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Anti-detect hook for high-payout RNG packets", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Switch(
                        checked = rtpData.isRtpBoostActive,
                        onCheckedChange = { onToggleRtpBoost() },
                        colors = SwitchDefaults.colors(checkedThumbColor = SlotGoldPrimary, checkedTrackColor = SlotGoldPrimary.copy(alpha = 0.3f))
                    )
                }
            }
        }
    }
}
