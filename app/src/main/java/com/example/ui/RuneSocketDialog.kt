package com.example.ui

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.model.ActiveSpell
import com.example.model.SpellRuneType

val RuneRubyRed = Color(0xFFD32F2F)
val RuneGold = Color(0xFFFFB300)

@Composable
fun RuneSocketDialog(
    runeType: SpellRuneType,
    spells: List<ActiveSpell>,
    onSocketSpell: (ActiveSpell) -> Unit
) {
    Dialog(
        onDismissRequest = { /* Modal choice required */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .shadow(20.dp, RoundedCornerShape(18.dp)),
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(2.5.dp, RuneRubyRed)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ruby Rune Icon with pulsing border
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(RuneRubyRed, RoundedCornerShape(12.dp))
                        .border(2.dp, Color.White, RoundedCornerShape(12.dp))
                        .shadow(6.dp, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "SPELL RUNE ACQUIRED",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = Color.Black,
                    letterSpacing = 1.2.sp
                )

                Text(
                    text = "${runeType.displayName}: ${runeType.description}",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = RuneRubyRed,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                Text(
                    text = "SELECT A SPELL TO SOCKET THIS RUNE INTO:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF666666),
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // List of player's currently equipped spells
                spells.forEachIndexed { index, spell ->
                    val isAlreadySocketed = spell.socketedRunes.contains(runeType)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("socket_spell_$index")
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(enabled = !isAlreadySocketed) {
                                onSocketSpell(spell)
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAlreadySocketed) Color(0xFFF0F0F0) else Color(0xFFFAFAFA)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isAlreadySocketed) Color(0xFFCCCCCC) else Color.Black
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = spell.definition.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = "Rank ${spell.rank} / 7  •  ${spell.definition.castType.name}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF666666)
                                )

                                if (spell.socketedRunes.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Socketed: " + spell.socketedRunes.joinToString { it.displayName },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RuneRubyRed
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isAlreadySocketed) Color(0xFFBBBBBB) else Color.Black,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (isAlreadySocketed) "SOCKETED" else "SOCKET",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    if (index < spells.size - 1) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}
