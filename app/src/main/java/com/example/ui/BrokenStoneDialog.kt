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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterVintage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ArtifactDefinition

@Composable
fun BrokenStoneDialog(
    artifacts: List<ArtifactDefinition>,
    orbCost: Int,
    currentOrbs: Int,
    heavyWeightCount: Int,
    isMastered: Boolean,
    onSelectArtifact: (ArtifactDefinition) -> Unit,
    onClose: () -> Unit
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .shadow(20.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF555555))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF555555), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterVintage,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "THE BROKEN STONE",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = Color.Black
                            )
                            Text(
                                text = "Cost: $orbCost Orbs  (You have: $currentOrbs)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentOrbs >= orbCost) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                            )
                        }
                    }

                    IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mastery tracking progress banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isMastered) Color(0xFFE8F5E9) else Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .border(1.dp, if (isMastered) Color(0xFF4CAF50) else Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isMastered)
                            "★ HEAVY WEIGHT MASTERY: ACTIVE (+60% Speed Inverted!)"
                        else
                            "Archetype Tracking: Heavy Weight ($heavyWeightCount/4 for Mastery Cure)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isMastered) Color(0xFF2E7D32) else Color(0xFF444444)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                artifacts.forEachIndexed { index, artifact ->
                    val canAfford = currentOrbs >= orbCost
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("broken_stone_item_$index")
                            .shadow(2.dp, RoundedCornerShape(10.dp))
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(enabled = canAfford) {
                                onSelectArtifact(artifact)
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF333333))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = artifact.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(Color.Black, RoundedCornerShape(3.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = artifact.tier.name,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                                Text(
                                    text = artifact.description,
                                    fontSize = 11.sp,
                                    color = Color(0xFF444444)
                                )
                                if (artifact.archetypeTag.isNotEmpty()) {
                                    Text(
                                        text = "Archetype: ${artifact.archetypeTag}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1976D2)
                                    )
                                }
                            }

                            Button(
                                onClick = { onSelectArtifact(artifact) },
                                enabled = canAfford,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(text = "$orbCost", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (index < artifacts.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
