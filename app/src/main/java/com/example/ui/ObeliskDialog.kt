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
import androidx.compose.material.icons.filled.Diamond
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ArtifactDefinition
import com.example.model.ArtifactTier

@Composable
fun ObeliskDialog(
    artifacts: List<ArtifactDefinition>,
    onSelectArtifact: (ArtifactDefinition) -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .shadow(24.dp, RoundedCornerShape(18.dp)),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF121212),
            border = androidx.compose.foundation.BorderStroke(2.5.dp, Color(0xFF888888))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Obelisk Monolith icon
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.White, RoundedCornerShape(10.dp))
                        .border(2.dp, Color.Black, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "THE ANCIENT OBELISK",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    color = Color.White,
                    letterSpacing = 1.2.sp
                )

                Text(
                    text = "High-Risk Pact: Choose exactly one Master or Cursed Artifact",
                    fontSize = 11.sp,
                    color = Color(0xFFAAAAAA)
                )

                Spacer(modifier = Modifier.height(16.dp))

                artifacts.take(3).forEachIndexed { index, artifact ->
                    val isCursed = artifact.tier == ArtifactTier.CURSED
                    val accentColor = if (isCursed) Color(0xFFFF5252) else Color(0xFFFFD700)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("obelisk_artifact_$index")
                            .shadow(3.dp, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectArtifact(artifact) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColor)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = artifact.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                                Box(
                                    modifier = Modifier
                                        .background(accentColor, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = artifact.tier.name,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = artifact.description,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = accentColor
                            )

                            if (artifact.archetypeTag.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Archetype: ${artifact.archetypeTag}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF888888)
                                )
                            }
                        }
                    }

                    if (index < artifacts.size - 1) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}
