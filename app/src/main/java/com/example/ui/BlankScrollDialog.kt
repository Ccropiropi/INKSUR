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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.GearType
import com.example.model.SpellDefinition

sealed class InjectedItemTarget {
    data class SpellTarget(val spell: SpellDefinition) : InjectedItemTarget()
    data class GearTarget(val gear: GearType) : InjectedItemTarget()
}

@Composable
fun BlankScrollDialog(
    availableSpells: List<SpellDefinition>,
    availableGear: List<GearType>,
    onSelectItem: (InjectedItemTarget) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .shadow(24.dp, RoundedCornerShape(18.dp)),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF141312),
            border = androidx.compose.foundation.BorderStroke(2.5.dp, Color(0xFFE0E0E0))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEEEEEE))
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "RNG MITIGATION: MID-BOSS SPOILS",
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "The Blank Scroll",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontFamily = FontFamily.Serif
                )

                Text(
                    text = "Inscribe any base spell or gear. It will be guaranteed 100% on your very next level-up to force your missing synthesis piece.",
                    fontSize = 11.sp,
                    color = Color(0xFFB0BEC5),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF22201E),
                    contentColor = Color(0xFFC5A059),
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Base Spells", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Gear Catalog", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    if (selectedTab == 0) {
                        items(availableSpells) { spell ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .testTag("blank_scroll_spell_${spell.id}")
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFF423F3D), RoundedCornerShape(12.dp))
                                    .clickable { onSelectItem(InjectedItemTarget.SpellTarget(spell)) },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF37474F)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Brush,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = spell.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = spell.description,
                                            fontSize = 11.sp,
                                            color = Color(0xFFCFD8DC),
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        items(availableGear) { gear ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .testTag("blank_scroll_gear_${gear.id}")
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFF423F3D), RoundedCornerShape(12.dp))
                                    .clickable { onSelectItem(InjectedItemTarget.GearTarget(gear)) },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF2E7D32)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = gear.displayName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color.White
                                            )
                                            Text(
                                                text = gear.statTag,
                                                fontSize = 10.sp,
                                                color = Color(0xFF81C784),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        Text(
                                            text = gear.description,
                                            fontSize = 11.sp,
                                            color = Color(0xFFCFD8DC),
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
