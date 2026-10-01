package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
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
import com.example.data.PlayerProgressEntity
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

    var showClassSelectionScreen by remember { mutableStateOf(false) }
    var showCharacterSelectionDialog by remember { mutableStateOf(false) }

    // Dedicated Class Selection Window / Screen
    if (showClassSelectionScreen || selectedTab == 2) {
        BackHandler {
            showClassSelectionScreen = false
            if (selectedTab == 2) selectedTab = 0
        }
        MagicSurvivalClassSelectionScreen(
            selectedClass = selectedClass,
            progress = progress,
            onSelectClass = { classDef ->
                onSelectClass(classDef)
                showClassSelectionScreen = false
                if (selectedTab == 2) selectedTab = 0
            },
            onClose = {
                showClassSelectionScreen = false
                if (selectedTab == 2) selectedTab = 0
            },
            onBuyFragment = { classId ->
                viewModel.buyClassFragment(classId)
            }
        )
        return
    }

    // Dedicated Character Selection Window / Modal
    if (showCharacterSelectionDialog) {
        BackHandler { showCharacterSelectionDialog = false }
        CharacterSelectionDialog(
            characters = CharacterDefinition.allCharacters,
            selectedCharacter = uiState.character,
            progress = progress,
            onSelectCharacter = { charDef ->
                viewModel.selectCharacter(charDef)
                showCharacterSelectionDialog = false
            },
            onUnlockCharacter = { charDef ->
                viewModel.unlockCharacter(charDef.id, charDef.crystalUnlockCost)
            },
            onClose = { showCharacterSelectionDialog = false }
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
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
                    .height(125.dp)
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
                        .background(Color.Black.copy(alpha = 0.52f))
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

            // Navigation Tabs (Deploy, Atelier Shop, Class Grimoire)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFFEEEEEE),
                contentColor = Color.Black,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("DEPLOY", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("ATELIER SHOP", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        showClassSelectionScreen = true
                    },
                    text = { Text("GRIMOIRE (24)", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (selectedTab == 0) {
                // ==========================================
                // CLEAN UNCLUTTERED MAIN DEPLOY SCREEN
                // ==========================================

                // 1. SELECTED CHARACTER SUMMARY CARD (Tapping opens dedicated Character Window)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACTIVE CALLIGRAPHER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF666666),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "CHANGE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0288D1),
                        modifier = Modifier
                            .clickable { showCharacterSelectionDialog = true }
                            .testTag("open_character_selector_button")
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showCharacterSelectionDialog = true }
                        .testTag("selected_character_summary_card"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, Color(0xFF0288D1))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0288D1)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
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
                                    text = uiState.character.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = uiState.character.title,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0288D1)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = uiState.character.description,
                                fontSize = 10.sp,
                                color = Color(0xFF555555),
                                lineHeight = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Change Character",
                            tint = Color(0xFF0288D1),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. SELECTED STARTER CLASS SUMMARY CARD (Tapping opens dedicated Class Selection Window)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STARTER CLASS & SPELL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF666666),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "ALL 24 GRIMOIRES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier
                            .clickable { showClassSelectionScreen = true }
                            .testTag("open_class_grimoire_button")
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                val classMasteryLvl = progress.getClassLevel(selectedClass.id)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showClassSelectionScreen = true }
                        .testTag("selected_class_card"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0E0E12)),
                    border = BorderStroke(1.5.dp, Color(0xFFFFD54F))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1E1E26))
                                .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            ClassGlyphCanvas(
                                glyphType = selectedClass.glyphType,
                                isSelected = true,
                                defaultColor = Color(selectedClass.glyphColorHex),
                                modifier = Modifier.size(38.dp)
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
                                    text = selectedClass.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.White,
                                    fontFamily = FontFamily.Serif
                                )
                                Text(
                                    text = "Lv.$classMasteryLvl / ${selectedClass.maxMastery}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Spell: ${selectedClass.starterSpell.name} • ${selectedClass.role}",
                                fontSize = 10.sp,
                                color = Color(0xFFCFD8DC)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open Class Selection",
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. MAP TIER SELECTION
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
                        border = BorderStroke(
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

                    val tier2Unlocked = uiState.unlockedMapTier >= 2
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
                        border = BorderStroke(
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

                // 4. Bookmark Card (if active)
                if (uiState.hasBookmarkRun || viewModel.saveManager.hasBookmark()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .testTag("bookmark_card"),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = BorderStroke(2.dp, Color(0xFF00E676))
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

                Spacer(modifier = Modifier.height(18.dp))

                // 5. START GAME BUTTON
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
                // ==========================================
                // ATELIER META-SHOP & CLASS FRAGMENTS
                // ==========================================
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

                Spacer(modifier = Modifier.height(16.dp))

                // ==========================================
                // CLASS FRAGMENTS & GRIMOIRES (GOLD / CRYSTALS)
                // ==========================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CLASS FRAGMENTS & UNLOCKS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF666666),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "100 GOLD / FRAGMENT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF57F17)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                ClassDefinition.allClasses.forEach { classDef ->
                    val isUnlocked = progress.isClassUnlocked(classDef.id)
                    val level = progress.getClassLevel(classDef.id)
                    val fragments = progress.getClassFragments(classDef.id)
                    val canAffordGold = progress.gold >= 100

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isUnlocked) Color(0xFFE0E0E0) else Color(0xFFFFCC80)
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
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isUnlocked) Color(0xFF1E1E26) else Color(0xFF2C2C34)),
                                contentAlignment = Alignment.Center
                            ) {
                                ClassGlyphCanvas(
                                    glyphType = classDef.glyphType,
                                    isSelected = isUnlocked,
                                    defaultColor = if (isUnlocked) Color(classDef.glyphColorHex) else Color(0xFF9E9E9E),
                                    modifier = Modifier.size(30.dp)
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
                                        text = classDef.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.Black
                                    )
                                    if (isUnlocked) {
                                        Text(
                                            text = "Lv.$level / ${classDef.maxMastery}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2E7D32)
                                        )
                                    } else {
                                        Text(
                                            text = "LOCKED ($fragments/1 Frag)",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFE65100)
                                        )
                                    }
                                }
                                Text(
                                    text = "Spell: ${classDef.starterSpell.name} • ${classDef.role}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF666666)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = { viewModel.buyClassFragment(classDef.id, costGold = 100) },
                                enabled = canAffordGold && level < classDef.maxMastery,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (!isUnlocked) Color(0xFFE65100) else Color(0xFF141312)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("buy_fragment_${classDef.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD54F),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (!isUnlocked) "Unlock 100" else "Level 100",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // UNLOCK CALLIGRAPHERS
                Text(
                    text = "UNLOCK CALLIGRAPHERS (CRYSTALS)",
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
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
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

/**
 * Dedicated Character Selection Modal Window
 */
@Composable
fun CharacterSelectionDialog(
    characters: List<CharacterDefinition>,
    selectedCharacter: CharacterDefinition,
    progress: PlayerProgressEntity,
    onSelectCharacter: (CharacterDefinition) -> Unit,
    onUnlockCharacter: (CharacterDefinition) -> Unit,
    onClose: () -> Unit
) {
    val unlockedCharSet = remember(progress.unlockedCharacters) {
        progress.unlockedCharacters.split(",").map { it.trim() }.toSet()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF00A0A0F))
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(16.dp)
            .testTag("character_selection_dialog")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CALLIGRAPHER ROSTER",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontFamily = FontFamily.Serif,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Choose your sovereign ink wielder",
                        fontSize = 11.sp,
                        color = Color(0xFFB0BEC5)
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("close_character_dialog")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            characters.forEach { charDef ->
                val isUnlocked = unlockedCharSet.contains(charDef.id)
                val isSelected = selectedCharacter.id == charDef.id
                val canAfford = progress.crystals >= charDef.crystalUnlockCost

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(enabled = isUnlocked) {
                            onSelectCharacter(charDef)
                        }
                        .testTag("char_select_${charDef.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF231F17) else if (isUnlocked) Color(0xFF16161D) else Color(0xFF0F0F14)
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) Color(0xFFFFD54F) else if (isUnlocked) Color(0xFF33333F) else Color(0xFF22222A)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color(0xFFFFD54F) else if (isUnlocked) Color(0xFF2A2A38) else Color(0xFF1E1E26)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isUnlocked) Icons.Default.AutoAwesome else Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.Black else if (isUnlocked) Color(0xFFFFD54F) else Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = charDef.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (isUnlocked) Color.White else Color(0xFF888888),
                                        fontFamily = FontFamily.Serif
                                    )
                                    Text(
                                        text = charDef.title,
                                        fontSize = 11.sp,
                                        color = if (isSelected) Color(0xFFFFD54F) else Color(0xFF9E9E9E)
                                    )
                                }
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF332A15))
                                        .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "EQUIPPED",
                                        color = Color(0xFFFFD54F),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            } else if (!isUnlocked) {
                                Button(
                                    onClick = { onUnlockCharacter(charDef) },
                                    enabled = canAfford,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("dialog_unlock_char_${charDef.id}")
                                ) {
                                    Icon(Icons.Default.Diamond, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${charDef.crystalUnlockCost}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = { onSelectCharacter(charDef) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A38)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("SELECT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = charDef.description,
                            fontSize = 11.sp,
                            color = if (isUnlocked) Color(0xFFCCCCCC) else Color(0xFF666666),
                            lineHeight = 15.sp
                        )
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
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
