package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.game.GameViewModel
import com.example.model.CharacterDefinition
import com.example.model.ClassDefinition

@Composable
fun MainMenuScreen(
    viewModel: GameViewModel,
    selectedClass: ClassDefinition,
    onSelectClass: (ClassDefinition) -> Unit,
    onStartGame: () -> Unit
) {
    var soundEnabled by remember { mutableStateOf(viewModel.soundManager.isSoundEnabled) }
    val progress by viewModel.playerProgress.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    val unlockedCharSet = remember(progress.unlockedCharacters) {
        progress.unlockedCharacters.split(",").map { it.trim() }.toSet()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with Currencies & Audio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gold & Crystal Badges
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFF8E1))
                            .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Gold",
                                tint = Color(0xFFF57F17),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${progress.gold}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE1F5FE))
                            .border(1.dp, Color(0xFF81D4FA), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = "Crystals",
                                tint = Color(0xFF0288D1),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${progress.crystals}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }

                IconButton(
                    onClick = {
                        soundEnabled = !soundEnabled
                        viewModel.soundManager.isSoundEnabled = soundEnabled
                    }
                ) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                        contentDescription = "Sound Toggle",
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Hero Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(14.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.parchment_banner_1790726113869),
                    contentDescription = "Sumi-e Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "INK SURVIVOR",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = Color.White,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Phase 5: The Mid-Run Checkpoint, The Blotter & The Erasure",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE0E0E0)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Navigation Tabs (Deploy vs Atelier Shop)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFFF5F5F5),
                contentColor = Color.Black,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("DEPLOY RUN", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("ATELIER SHOP", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (selectedTab == 0) {
                // Character Selection
                Text(
                    text = "SELECT CHARACTER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF666666),
                    letterSpacing = 1.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                CharacterDefinition.allCharacters.forEach { charDef ->
                    val isUnlocked = unlockedCharSet.contains(charDef.id)
                    val isSelected = uiState.character.id == charDef.id

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .testTag("char_select_${charDef.id}")
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(enabled = isUnlocked) {
                                viewModel.selectCharacter(charDef)
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFFFF8E1) else if (isUnlocked) Color(0xFFFAFAFA) else Color(0xFFEEEEEE)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(0xFFF57F17) else Color(0xFFE0E0E0)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color(0xFFF57F17) else if (isUnlocked) Color.Black else Color.Gray),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isUnlocked) Icons.Default.AutoAwesome else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = charDef.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isUnlocked) Color.Black else Color.Gray
                                    )
                                    if (isSelected) {
                                        Text(
                                            text = "SELECTED",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFFF57F17)
                                        )
                                    } else if (!isUnlocked) {
                                        Text(
                                            text = "Locked (${charDef.crystalUnlockCost} Crystals in Shop)",
                                            fontSize = 9.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                                Text(
                                    text = charDef.description,
                                    fontSize = 10.sp,
                                    color = if (isUnlocked) Color(0xFF444444) else Color.Gray,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Class Selection: Scribe vs Painter
                Text(
                    text = "SELECT STARTER CLASS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF666666),
                    letterSpacing = 1.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                ClassDefinition.allClasses.forEach { classDef ->
                    val isSelected = selectedClass.id == classDef.id
                    val icon = if (classDef.id == "painter") Icons.Default.Brush else Icons.Default.Create

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .testTag("select_class_${classDef.id}")
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelectClass(classDef) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFFFF9F9) else Color(0xFFFAFAFA)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(0xFFD32F2F) else Color(0xFFE0E0E0)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color(0xFFD32F2F) else Color.Black),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = classDef.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.Black
                                    )
                                    if (isSelected) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
                                    }
                                }
                                Text(
                                    text = "Spell: ${classDef.starterSpell.name} • ${classDef.role}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) Color(0xFFD32F2F) else Color(0xFF555555)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Map Tier Selection (Tier 1: Void Scratchpad, Tier 2: The Forbidden Archive)
                Text(
                    text = "SELECT MAP TIER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF666666),
                    letterSpacing = 1.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tier1Selected = uiState.selectedMapTier == 1
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { viewModel.selectMapTier(1) }
                            .testTag("map_tier_1"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (tier1Selected) Color(0xFFFFF9F9) else Color(0xFFFAFAFA)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (tier1Selected) 2.dp else 1.dp,
                            color = if (tier1Selected) Color(0xFFD32F2F) else Color(0xFFE0E0E0)
                        )
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Map, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tier 1", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                            }
                            Text("The Scratchpad", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF444444))
                            Text("Standard void arena", fontSize = 9.sp, color = Color(0xFF777777))
                        }
                    }

                    val tier2Unlocked = progress.unlockedMapTier >= 2 || uiState.unlockedMapTier >= 2
                    val tier2Selected = uiState.selectedMapTier == 2
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(enabled = tier2Unlocked) { if (tier2Unlocked) viewModel.selectMapTier(2) }
                            .testTag("map_tier_2"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (tier2Selected) Color(0xFFFFF8E1) else if (tier2Unlocked) Color(0xFFFAFAFA) else Color(0xFFEEEEEE)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (tier2Selected) 2.dp else 1.dp,
                            color = if (tier2Selected) Color(0xFFF57F17) else Color(0xFFE0E0E0)
                        )
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (tier2Unlocked) Icons.Default.AutoAwesome else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (tier2Unlocked) Color(0xFFF57F17) else Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tier 2", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (tier2Unlocked) Color.Black else Color.Gray)
                            }
                            Text("Forbidden Archive", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (tier2Unlocked) Color(0xFF444444) else Color.Gray)
                            Text(if (tier2Unlocked) "+50% Payout Bonus" else "Clear Min 61 Climax", fontSize = 9.sp, color = if (tier2Unlocked) Color(0xFFE65100) else Color.Gray)
                        }
                    }
                }

                // Bookmark Card (if active)
                if (uiState.hasBookmarkRun || viewModel.saveManager.hasBookmark()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .testTag("bookmark_card"),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF00E676))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "Bookmark",
                                    tint = Color(0xFF00897B),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "BOOKMARKED RUN DETECTED",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = Color(0xFF004D40)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Saved at Minute 30:00 Inkwell Sanctuary. Note: Resuming will delete this bookmark to maintain roguelike integrity.",
                                fontSize = 11.sp,
                                color = Color(0xFF00695C)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { viewModel.resumeBookmarkedRun() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("resume_bookmark_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "RESUME BOOKMARKED RUN",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Start Game Button
                Button(
                    onClick = onStartGame,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(4.dp, RoundedCornerShape(10.dp))
                        .testTag("start_game_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DEPLOY INTO THE SCRATCHPAD",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            } else {
                // ATELIER META-SHOP (Phase 4 Meta-Progression Architecture)
                Text(
                    text = "PERMANENT STAT INCREASES (GOLD)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF666666),
                    letterSpacing = 1.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 1. Ink Might (ATK)
                MetaStatUpgradeRow(
                    name = "Ink Might",
                    bonusTag = "+5% Ink Damage / Level",
                    level = progress.metaAtkLevel,
                    maxLevel = 5,
                    gold = progress.gold,
                    onUpgrade = { cost -> viewModel.upgradeMetaStat("atk", cost) }
                )

                // 2. Wind Stride (Speed)
                MetaStatUpgradeRow(
                    name = "Wind Stride",
                    bonusTag = "+4% Move Speed / Level",
                    level = progress.metaSpeedLevel,
                    maxLevel = 5,
                    gold = progress.gold,
                    onUpgrade = { cost -> viewModel.upgradeMetaStat("speed", cost) }
                )

                // 3. Lodestone Force (Magnet)
                MetaStatUpgradeRow(
                    name = "Lodestone Force",
                    bonusTag = "+20% Magnet Range / Level",
                    level = progress.metaMagnetLevel,
                    maxLevel = 5,
                    gold = progress.gold,
                    onUpgrade = { cost -> viewModel.upgradeMetaStat("magnet", cost) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "UNLOCK CHARACTERS (CRYSTALS)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF666666),
                    letterSpacing = 1.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                CharacterDefinition.allCharacters.filter { it.crystalUnlockCost > 0 }.forEach { charDef ->
                    val isUnlocked = unlockedCharSet.contains(charDef.id)
                    val canAfford = progress.crystals >= charDef.crystalUnlockCost

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = charDef.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = charDef.description, fontSize = 10.sp, color = Color(0xFF666666), lineHeight = 13.sp)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            if (isUnlocked) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFE8F5E9))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = "UNLOCKED", color = Color(0xFF2E7D32), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.unlockCharacter(charDef.id, charDef.crystalUnlockCost) },
                                    enabled = canAfford,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("unlock_char_${charDef.id}")
                                ) {
                                    Icon(imageVector = Icons.Default.Diamond, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "${charDef.crystalUnlockCost}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetaStatUpgradeRow(
    name: String,
    bonusTag: String,
    level: Int,
    maxLevel: Int,
    gold: Int,
    onUpgrade: (Int) -> Unit
) {
    val isMax = level >= maxLevel
    val cost = (level + 1) * 100
    val canAfford = gold >= cost && !isMax

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
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
                    Text(text = name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Lv.$level / $maxLevel", fontSize = 10.sp, color = Color(0xFFF57F17), fontWeight = FontWeight.Bold)
                }
                Text(text = bonusTag, fontSize = 10.sp, color = Color(0xFF666666))
            }

            if (isMax) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE0E0E0))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "MAX", color = Color(0xFF666666), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = { onUpgrade(cost) },
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF141312)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "$cost", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
