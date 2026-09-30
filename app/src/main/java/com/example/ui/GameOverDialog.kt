package com.example.ui

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.game.GameUIState

@Composable
fun GameOverDialog(
    uiState: GameUIState,
    onRestart: () -> Unit,
    onHome: () -> Unit
) {
    val minutes = (uiState.timeSurvivedSeconds / 60).toInt()
    val seconds = (uiState.timeSurvivedSeconds % 60).toInt()
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)
    val goldEarned = if (uiState.goldEarnedThisRun > 0) uiState.goldEarnedThisRun else if (uiState.isMinute61Victory) uiState.kills * 2 else uiState.kills
    val crystalsEarned = if (uiState.crystalsEarnedThisRun > 0) uiState.crystalsEarnedThisRun else if (uiState.isMinute61Victory) (minutes * 10) * 2 else minutes * 10

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .shadow(20.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(2.5.dp, Color.Black)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (uiState.isMinute61Victory) "THE ERASURE SHATTERED!" else if (uiState.isVictory) "CHRONICLE SEALED" else "INK DEPLETED",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = if (uiState.isMinute61Victory) Color(0xFFD32F2F) else Color.Black,
                    letterSpacing = 1.2.sp
                )

                Text(
                    text = if (uiState.isMinute61Victory) "Absolute Victory! You survived Minute 61 and dissolved The Eraser Monolith!"
                    else if (uiState.isVictory) "You conquered the 60-minute parchment swarm!"
                    else "The paper construct legion overwhelmed your quill.",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                // Phase 5 Climax Victory Multiplier Banner
                if (uiState.isMinute61Victory) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFFF8E1))
                            .border(1.5.dp, Color(0xFFFFB300), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFFF57F17),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "2X CLIMAX MULTIPLIER APPLIED",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = Color(0xFFE65100)
                                )
                                Text(
                                    text = "Map Tier 2: The Forbidden Archive Unlocked!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = Color(0xFFBF360C)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Currency Payout Card (Phase 4 & 5 Post-Run Economy)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF141312))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gold Payout
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "+$goldEarned Gold",
                                color = Color(0xFFFFD54F),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "1/Enemy Erased",
                                color = Color(0xFFB0BEC5),
                                fontSize = 9.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(Color(0xFF423F3D))
                    )

                    // Crystals Payout
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Diamond,
                            contentDescription = null,
                            tint = Color(0xFF80D8FF),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "+$crystalsEarned Crystals",
                                color = Color(0xFF80D8FF),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "10/Min Survived",
                                color = Color(0xFFB0BEC5),
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stats breakdown
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        StatRow("Survival Time", timeFormatted)
                        StatRow("Enemies Erased", "${uiState.kills}")
                        StatRow("Score", "${uiState.score}")
                        StatRow("Damage Dealt", "${uiState.damageDealt}")
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onHome,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("game_over_home_button"),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.Black)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ATELIER",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = onRestart,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("game_over_restart_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "RETRY",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF666666),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}
