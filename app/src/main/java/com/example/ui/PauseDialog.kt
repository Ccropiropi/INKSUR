package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.game.GameUIState
import com.example.game.GameViewModel

@Composable
fun PauseDialog(
    viewModel: GameViewModel,
    uiState: GameUIState,
    onResume: () -> Unit,
    onAbandon: () -> Unit
) {
    var soundEnabled by remember { mutableStateOf(viewModel.soundManager.isSoundEnabled) }

    Dialog(onDismissRequest = onResume) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .shadow(16.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(2.dp, Color.Black)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "GAME PAUSED",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color.Black,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // LEVEL INFORMATION (Only shown when paused)
                val xpRatio = (viewModel.player.xp.toFloat() / viewModel.player.xpNeeded).coerceIn(0f, 1f)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pause_level_info_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.Black)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "LEVEL ${viewModel.player.level}",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                            Text(
                                text = "${viewModel.player.xp} / ${viewModel.player.xpNeeded} XP  (${(xpRatio * 100).toInt()}%)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF555555)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE0E0E0))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(xpRatio)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // HERO STATS CARD
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pause_stats_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCCCCCC))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "HERO ATTRIBUTES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF777777),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Health:", fontSize = 12.sp, color = Color(0xFF555555))
                            Text(text = "${viewModel.player.hp.toInt()} / ${viewModel.player.maxHp.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Damage Multiplier:", fontSize = 12.sp, color = Color(0xFF555555))
                            Text(text = "${String.format("%.1f", viewModel.player.damageMultiplier)}x", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Attack Speed:", fontSize = 12.sp, color = Color(0xFF555555))
                            Text(text = "${String.format("%.1f", viewModel.player.attackSpeedMultiplier)}x", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Move Speed:", fontSize = 12.sp, color = Color(0xFF555555))
                            Text(text = "${viewModel.player.moveSpeed.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Armor:", fontSize = 12.sp, color = Color(0xFF555555))
                            Text(text = "${viewModel.player.armor.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Pickup Radius:", fontSize = 12.sp, color = Color(0xFF555555))
                            Text(text = "${viewModel.player.pickupRadius.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Enemies Banished:", fontSize = 12.sp, color = Color(0xFF555555))
                            Text(text = "${uiState.kills}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Character & Class Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "CHARACTER & CLASS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF666666),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${uiState.character.name}  —  ${uiState.playerClass.name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        Text(
                            text = uiState.character.description,
                            fontSize = 11.sp,
                            color = Color(0xFF444444)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Starter Spell: ${uiState.playerClass.starterSpell.name}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Equipped Gear
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "EQUIPPED GEAR (STAT STICKS)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF666666),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (uiState.equippedGear.isEmpty()) {
                            Text(
                                text = "None equipped yet. Collect orbs to level up!",
                                fontSize = 11.sp,
                                color = Color(0xFF888888)
                            )
                        } else {
                            uiState.equippedGear.forEach { gear ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = gear.type.displayName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "${gear.type.statTag} (Lv. ${gear.stacks})",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Audio toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Sound Effects", fontSize = 13.sp, color = Color.Black)
                    }
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = {
                            soundEnabled = it
                            viewModel.soundManager.isSoundEnabled = it
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = Color(0xFFCCCCCC))
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Fixed Analog Setting Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fixed_analog_toggle"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Fixed Analog Stick",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = if (viewModel.isFixedAnalog) "Fixed at bottom-center" else "Tap-to-place (auto-hides after 5s)",
                                fontSize = 10.sp,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                    Switch(
                        checked = viewModel.isFixedAnalog,
                        onCheckedChange = { checked ->
                            viewModel.updateFixedAnalog(checked)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = Color(0xFFCCCCCC))
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Screen Shake & Impact Frames Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("screen_shake_toggle"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Screen Shake & Impact Frames",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = if (viewModel.isScreenShakeEnabled) "Camera vibration & freeze-frame impact active" else "Camera vibration & freeze-frame disabled",
                                fontSize = 10.sp,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                    Switch(
                        checked = viewModel.isScreenShakeEnabled,
                        onCheckedChange = { checked ->
                            viewModel.updateScreenShakeEnabled(checked)
                            viewModel.updateImpactFrameEnabled(checked)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = Color(0xFFCCCCCC))
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onResume,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("resume_run_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "RESUME", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onAbandon,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("abandon_run_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
                ) {
                    Icon(imageVector = Icons.Default.Stop, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "QUIT TO MENU", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
