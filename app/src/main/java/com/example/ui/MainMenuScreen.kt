package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.PlayerProgressEntity
import com.example.game.GameViewModel
import com.example.model.CharacterDefinition
import com.example.model.ClassDefinition

/**
 * Clutter-free, refined Main Menu Screen for Ink Survivor.
 * Integrates dedicated window modals for Calligrapher & Class selection,
 * an uncluttered battle preparation deck, and a streamlined Atelier Shop.
 */
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
    var shopCategory by remember { mutableIntStateOf(0) } // 0: Stats, 1: Class Fragments, 2: Calligraphers

    var showClassSelectionScreen by remember { mutableStateOf(false) }
    var showCharacterSelectionDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showCodexScreen by remember { mutableStateOf(false) }

    // Dedicated Full-Window Sumi-e Codex Screen
    if (showCodexScreen || selectedTab == 3) {
        BackHandler {
            showCodexScreen = false
            if (selectedTab == 3) selectedTab = 0
        }
        SumiECodexScreen(
            viewModel = viewModel,
            onBack = {
                showCodexScreen = false
                if (selectedTab == 3) selectedTab = 0
            }
        )
        return
    }

    // Dedicated Full-Window Class Grimoire Screen
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

    // Dedicated Full-Window Character Selection Modal
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

    // Occult Settings Modal
    if (showSettingsDialog) {
        BackHandler { showSettingsDialog = false }
        OccultSettingsDialog(
            viewModel = viewModel,
            onClose = { showSettingsDialog = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0C12))
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. TOP CURRENCY & SYSTEM BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gold & Crystal Badges
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Gold badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E1A11))
                            .border(1.dp, Color(0xFFFFD54F).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = "Gold",
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "${progress.gold}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFE082),
                                fontFamily = FontFamily.Serif
                            )
                        }
                    }

                    // Crystal badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF101B24))
                            .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = "Crystals",
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "${progress.crystals}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF80D8FF),
                                fontFamily = FontFamily.Serif
                            )
                        }
                    }
                }

                // Sound Toggle
                IconButton(
                    onClick = {
                        soundEnabled = !soundEnabled
                        viewModel.soundManager.isSoundEnabled = soundEnabled
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                        contentDescription = "Sound Toggle",
                        tint = Color(0xFFE0E0E0),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Codex Button
                IconButton(
                    onClick = { showCodexScreen = true },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("main_menu_codex_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Sumi-e Codex",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Settings Toggle
                IconButton(
                    onClick = { showSettingsDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("main_menu_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // APP PROFILE MODE SELECTOR (Standard from 0 vs Dev Testing Unlocked All)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (viewModel.isDevTestingMode) Color(0xFF2E0814) else Color(0xFF14171E))
                    .border(
                        1.2.dp,
                        if (viewModel.isDevTestingMode) Color(0xFFFF4081) else Color(0xFF64B5F6),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { viewModel.toggleDevTestingMode() }
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (viewModel.isDevTestingMode) Icons.Default.BugReport else Icons.Default.PlayArrow,
                        contentDescription = "Mode Icon",
                        tint = if (viewModel.isDevTestingMode) Color(0xFFFF4081) else Color(0xFF64B5F6),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (viewModel.isDevTestingMode) "MODE: DEV TESTING (UNLOCKED ALL)" else "MODE: STANDARD RUN (START FROM 0)",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Black,
                            color = if (viewModel.isDevTestingMode) Color(0xFFFF80AB) else Color(0xFF90CAF9),
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (viewModel.isDevTestingMode) "All characters & spells unlocked, 999k gold. Tap to switch." else "Clean slate from 0. Tap to switch to Dev Testing.",
                            fontSize = 8.5.sp,
                            color = Color(0xFFAAAAAA)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (viewModel.isDevTestingMode) Color(0xFFFF4081) else Color(0xFF1E88E5))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (viewModel.isDevTestingMode) "SWITCH TO 0" else "UNLOCK ALL",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            // 2. HERO BANNER (Atmospheric parchment artwork)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(105.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0x44FFD54F), RoundedCornerShape(12.dp))
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
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xE60C0C12),
                                    Color(0x990C0C12),
                                    Color(0x660C0C12)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(horizontal = 14.dp)
                ) {
                    Text(
                        text = "INK SURVIVOR",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Roguelike Occult Grimoire • The Void Awaits",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFFFD54F),
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. MAIN NAVIGATION TABS
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF15151D),
                contentColor = Color.White,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFF262634), RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "DEPLOY",
                            fontWeight = if (selectedTab == 0) FontWeight.Black else FontWeight.Normal,
                            fontSize = 11.sp,
                            color = if (selectedTab == 0) Color(0xFFFFD54F) else Color(0xFF9E9E9E),
                            letterSpacing = 0.5.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "ATELIER SHOP",
                            fontWeight = if (selectedTab == 1) FontWeight.Black else FontWeight.Normal,
                            fontSize = 11.sp,
                            color = if (selectedTab == 1) Color(0xFFFFD54F) else Color(0xFF9E9E9E),
                            letterSpacing = 0.5.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        showClassSelectionScreen = true
                    },
                    text = {
                        Text(
                            text = "GRIMOIRE",
                            fontWeight = if (selectedTab == 2) FontWeight.Black else FontWeight.Normal,
                            fontSize = 11.sp,
                            color = if (selectedTab == 2) Color(0xFFFFD54F) else Color(0xFF9E9E9E),
                            letterSpacing = 0.5.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = {
                        selectedTab = 3
                        showCodexScreen = true
                    },
                    text = {
                        Text(
                            text = "CODEX",
                            fontWeight = if (selectedTab == 3) FontWeight.Black else FontWeight.Normal,
                            fontSize = 11.sp,
                            color = if (selectedTab == 3) Color(0xFF00E5FF) else Color(0xFF9E9E9E),
                            letterSpacing = 0.5.sp
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4. MAIN CONTENT AREA
            if (selectedTab == 0) {
                // ==========================================
                // CLUTTER-FREE DEPLOY SCREEN
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // EQUIPPED LOADOUT PANEL (Compact, unified 2-slot presentation)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF14141B)),
                        border = BorderStroke(1.dp, Color(0xFF262634))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "BATTLE LOADOUT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9E9E9E),
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Slot 1: Active Calligrapher
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1A1A24))
                                    .border(1.dp, Color(0xFF2E2E3E), RoundedCornerShape(8.dp))
                                    .clickable { showCharacterSelectionDialog = true }
                                    .padding(8.dp)
                                    .testTag("selected_character_summary_card"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0288D1)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = uiState.character.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "• ${uiState.character.title}",
                                            fontSize = 10.sp,
                                            color = Color(0xFF80D8FF)
                                        )
                                    }
                                    Text(
                                        text = uiState.character.description,
                                        fontSize = 10.sp,
                                        color = Color(0xFFB0BEC5),
                                        maxLines = 1
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF2A2A3A))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .testTag("open_character_selector_button")
                                ) {
                                    Text(
                                        text = "CHANGE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF80D8FF)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Slot 2: Active Class Grimoire
                            val classMasteryLvl = progress.getClassLevel(selectedClass.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1A1A24))
                                    .border(1.dp, Color(0xFF332A15), RoundedCornerShape(8.dp))
                                    .clickable { showClassSelectionScreen = true }
                                    .padding(8.dp)
                                    .testTag("selected_class_card"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF242217))
                                        .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ClassGlyphCanvas(
                                        glyphType = selectedClass.glyphType,
                                        isSelected = true,
                                        defaultColor = Color(selectedClass.glyphColorHex),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = selectedClass.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White,
                                            fontFamily = FontFamily.Serif
                                        )
                                        Text(
                                            text = "Lv.$classMasteryLvl / ${selectedClass.maxMastery}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFFD54F)
                                        )
                                    }
                                    Text(
                                        text = "Spell: ${selectedClass.starterSpell.name} • ${selectedClass.role}",
                                        fontSize = 10.sp,
                                        color = Color(0xFFCFD8DC),
                                        maxLines = 1
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF332A15))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .testTag("open_class_grimoire_button")
                                ) {
                                    Text(
                                        text = "GRIMOIRE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFD54F)
                                    )
                                }
                            }
                        }
                    }

                    // MAP ARENA SELECTION (Compact 2-segment selector)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF14141B)),
                        border = BorderStroke(1.dp, Color(0xFF262634))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "ARENA REALM",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9E9E9E),
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val tier1Selected = uiState.selectedMapTier == 1
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (tier1Selected) Color(0xFF2A1515) else Color(0xFF1A1A24))
                                        .border(
                                            width = if (tier1Selected) 1.5.dp else 1.dp,
                                            color = if (tier1Selected) Color(0xFFE53935) else Color(0xFF2E2E3E),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { viewModel.selectMapTier(1) }
                                        .padding(8.dp)
                                        .testTag("map_tier_1")
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Map, contentDescription = null, tint = if (tier1Selected) Color(0xFFEF5350) else Color.Gray, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Tier 1: Scratchpad", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                        Text("Standard void arena", fontSize = 9.sp, color = Color(0xFF9E9E9E))
                                    }
                                }

                                val tier2Unlocked = uiState.unlockedMapTier >= 2
                                val tier2Selected = uiState.selectedMapTier == 2
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (tier2Selected) Color(0xFF332A15) else Color(0xFF1A1A24))
                                        .border(
                                            width = if (tier2Selected) 1.5.dp else 1.dp,
                                            color = if (tier2Selected) Color(0xFFFFD54F) else Color(0xFF2E2E3E),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable(enabled = tier2Unlocked) { if (tier2Unlocked) viewModel.selectMapTier(2) }
                                        .padding(8.dp)
                                        .testTag("map_tier_2")
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = if (tier2Unlocked) Icons.Default.AutoAwesome else Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = if (tier2Unlocked) Color(0xFFFFD54F) else Color.Gray,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Tier 2: Archive", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (tier2Unlocked) Color.White else Color.Gray)
                                        }
                                        Text(if (tier2Unlocked) "+50% Payout Bonus" else "Clear Min 61 Climax", fontSize = 9.sp, color = if (tier2Unlocked) Color(0xFFFFD54F) else Color.Gray)
                                    }
                                }
                            }
                        }
                    }

                    // RESUME BOOKMARK RUN (if present)
                    if (uiState.hasBookmarkRun || viewModel.saveManager.hasBookmark()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .testTag("bookmark_card"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B221B)),
                            border = BorderStroke(1.dp, Color(0xFF00E676))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(Icons.Default.Bookmark, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("BOOKMARKED RUN DETECTED", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("Saved at Minute 30:00 Inkwell Sanctuary", fontSize = 9.sp, color = Color(0xFF80CBC4))
                                    }
                                }
                                Button(
                                    onClick = { viewModel.resumeBookmarkedRun() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B)),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("resume_bookmark_button")
                                ) {
                                    Text("RESUME", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // 5. DEPLOY ACTION BUTTON (High-impact CTA)
                    Button(
                        onClick = onStartGame,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(6.dp, RoundedCornerShape(10.dp))
                            .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(10.dp))
                            .testTag("start_game_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1A11)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DEPLOY INTO THE VOID",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            color = Color(0xFFFFD54F),
                            fontFamily = FontFamily.Serif
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }
            } else {
                // ==========================================
                // ATELIER SHOP (Organized & Uncluttered Hub)
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    // Category Selector Pills
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("STAT UPGRADES", "CLASS FRAGMENTS", "CALLIGRAPHERS").forEachIndexed { idx, label ->
                            val isSel = shopCategory == idx
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFF332A15) else Color(0xFF161620))
                                    .border(1.dp, if (isSel) Color(0xFFFFD54F) else Color(0xFF262634), RoundedCornerShape(8.dp))
                                    .clickable { shopCategory = idx }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) Color(0xFFFFD54F) else Color(0xFF9E9E9E)
                                )
                            }
                        }
                    }

                    when (shopCategory) {
                        0 -> {
                            // Section 1: Permanent Stats
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MetaStatUpgradeCard(
                                    name = "Ink Might",
                                    bonusTag = "+5% Ink Damage per Level",
                                    level = progress.metaAtkLevel,
                                    maxLevel = 5,
                                    gold = progress.gold,
                                    onUpgrade = { cost -> viewModel.upgradeMetaStat("atk", cost) }
                                )

                                MetaStatUpgradeCard(
                                    name = "Wind Stride",
                                    bonusTag = "+4% Move Speed per Level",
                                    level = progress.metaSpeedLevel,
                                    maxLevel = 5,
                                    gold = progress.gold,
                                    onUpgrade = { cost -> viewModel.upgradeMetaStat("speed", cost) }
                                )

                                MetaStatUpgradeCard(
                                    name = "Lodestone Force",
                                    bonusTag = "+20% Magnet Range per Level",
                                    level = progress.metaMagnetLevel,
                                    maxLevel = 5,
                                    gold = progress.gold,
                                    onUpgrade = { cost -> viewModel.upgradeMetaStat("magnet", cost) }
                                )
                            }
                        }
                        1 -> {
                            // Section 2: Class Fragments Grid (Clean, compact 4-column layout)
                            var activeClassDetail by remember { mutableStateOf(selectedClass) }
                            val isUnlocked = progress.isClassUnlocked(activeClassDetail.id)
                            val classLevel = progress.getClassLevel(activeClassDetail.id)
                            val fragments = progress.getClassFragments(activeClassDetail.id)
                            val canAffordGold = progress.gold >= 100

                            Column(modifier = Modifier.fillMaxSize()) {
                                // Inspected Class Action Banner
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp)),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF14141B)),
                                    border = BorderStroke(1.dp, Color(0xFF332A15))
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
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFF222019))
                                                .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            ClassGlyphCanvas(
                                                glyphType = activeClassDetail.glyphType,
                                                isSelected = isUnlocked,
                                                defaultColor = if (isUnlocked) Color(activeClassDetail.glyphColorHex) else Color(0xFF888888),
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(activeClassDetail.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                                if (isUnlocked) {
                                                    Text("Lv.$classLevel/12", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF81C784))
                                                } else {
                                                    Text("LOCKED ($fragments/1 Frag)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFAB91))
                                                }
                                            }
                                            Text("Spell: ${activeClassDetail.starterSpell.name} • ${activeClassDetail.role}", fontSize = 9.sp, color = Color(0xFF9E9E9E), maxLines = 1)
                                        }

                                        Button(
                                            onClick = { viewModel.buyClassFragment(activeClassDetail.id, costGold = 100) },
                                            enabled = canAffordGold && classLevel < activeClassDetail.maxMastery,
                                            colors = ButtonDefaults.buttonColors(containerColor = if (!isUnlocked) Color(0xFFE65100) else Color(0xFF2E7D32)),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                            modifier = Modifier.testTag("buy_fragment_${activeClassDetail.id}")
                                        ) {
                                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (!isUnlocked) "Unlock 100" else "Upgrade 100", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // 4-Column Compact Glyph Grid
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(4),
                                    contentPadding = PaddingValues(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    items(ClassDefinition.allClasses) { classDef ->
                                        val isItemUnlocked = progress.isClassUnlocked(classDef.id)
                                        val isSelected = activeClassDetail.id == classDef.id
                                        val itemLvl = progress.getClassLevel(classDef.id)

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) Color(0xFF332A15) else Color(0xFF14141B))
                                                .border(
                                                    width = if (isSelected) 1.5.dp else 1.dp,
                                                    color = if (isSelected) Color(0xFFFFD54F) else if (isItemUnlocked) Color(0xFF262634) else Color(0xFF1E1E26),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .clickable { activeClassDetail = classDef }
                                                .padding(6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                ClassGlyphCanvas(
                                                    glyphType = classDef.glyphType,
                                                    isSelected = isSelected,
                                                    defaultColor = if (isItemUnlocked) Color(classDef.glyphColorHex) else Color(0xFF666670),
                                                    modifier = Modifier.size(34.dp)
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = classDef.name,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if (isItemUnlocked) Color.White else Color.Gray,
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = if (isItemUnlocked) "Lv.$itemLvl" else "Locked",
                                                    fontSize = 8.sp,
                                                    color = if (isItemUnlocked) Color(0xFFFFD54F) else Color(0xFFFF8A65)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        2 -> {
                            // Section 3: Calligraphers
                            val unlockedCharSet = remember(progress.unlockedCharacters) {
                                progress.unlockedCharacters.split(",").map { it.trim() }.toSet()
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CharacterDefinition.allCharacters.filter { it.crystalUnlockCost > 0 }.forEach { charDef ->
                                    val isCharUnlocked = unlockedCharSet.contains(charDef.id)
                                    val canAfford = progress.crystals >= charDef.crystalUnlockCost

                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp)),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF14141B)),
                                        border = BorderStroke(1.dp, Color(0xFF262634))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(text = charDef.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                                Text(text = charDef.title, fontSize = 10.sp, color = Color(0xFF80D8FF))
                                                Text(text = charDef.description, fontSize = 10.sp, color = Color(0xFF9E9E9E), lineHeight = 13.sp)
                                            }

                                            Spacer(modifier = Modifier.width(8.dp))

                                            if (isCharUnlocked) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(Color(0xFF1B3A24))
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Text(text = "UNLOCKED", color = Color(0xFF81C784), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            } else {
                                                Button(
                                                    onClick = { viewModel.unlockCharacter(charDef.id, charDef.crystalUnlockCost) },
                                                    enabled = canAfford,
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier.testTag("unlock_char_${charDef.id}")
                                                ) {
                                                    Icon(imageVector = Icons.Default.Diamond, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
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
            }
        }
    }
}

/**
 * Clean, visually appealing Meta Stat Upgrade card with pip progress bar
 */
@Composable
private fun MetaStatUpgradeCard(
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
            .clip(RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14141B)),
        border = BorderStroke(1.dp, Color(0xFF262634))
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
                    Text(text = name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Lv.$level / $maxLevel", fontSize = 10.sp, color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = bonusTag, fontSize = 10.sp, color = Color(0xFF9E9E9E))

                Spacer(modifier = Modifier.height(6.dp))

                // Pip meter
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (i in 1..maxLevel) {
                        Box(
                            modifier = Modifier
                                .size(width = 16.dp, height = 5.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (i <= level) Color(0xFFFFD54F) else Color(0xFF2E2E3E))
                        )
                    }
                }
            }

            if (isMax) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF2E2E3E))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "MAX", color = Color(0xFF9E9E9E), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = { onUpgrade(cost) },
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF332A15)),
                    border = BorderStroke(1.dp, if (canAfford) Color(0xFFFFD54F) else Color.Transparent),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "$cost", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFE082))
                }
            }
        }
    }
}

/**
 * Dedicated Character Selection Modal Window (Uncluttered, full-screen dialog)
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
            .background(Color(0xFF0A0A0E))
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
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontFamily = FontFamily.Serif,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Choose your sovereign ink wielder",
                        fontSize = 10.sp,
                        color = Color(0xFF9E9E9E)
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
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            characters.forEach { charDef ->
                val isUnlocked = unlockedCharSet.contains(charDef.id)
                val isSelected = selectedCharacter.id == charDef.id
                val canAfford = progress.crystals >= charDef.crystalUnlockCost

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(enabled = isUnlocked) {
                            onSelectCharacter(charDef)
                        }
                        .testTag("char_select_${charDef.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF221F17) else if (isUnlocked) Color(0xFF14141B) else Color(0xFF0F0F14)
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) Color(0xFFFFD54F) else if (isUnlocked) Color(0xFF262634) else Color(0xFF1A1A22)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color(0xFFFFD54F) else if (isUnlocked) Color(0xFF0288D1) else Color(0xFF1E1E26)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isUnlocked) Icons.Default.AutoAwesome else Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.Black else if (isUnlocked) Color.White else Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = charDef.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isUnlocked) Color.White else Color(0xFF888888),
                                        fontFamily = FontFamily.Serif
                                    )
                                    Text(
                                        text = charDef.title,
                                        fontSize = 10.sp,
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
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "EQUIPPED",
                                        color = Color(0xFFFFD54F),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            } else if (!isUnlocked) {
                                Button(
                                    onClick = { onUnlockCharacter(charDef) },
                                    enabled = canAfford,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("dialog_unlock_char_${charDef.id}")
                                ) {
                                    Icon(Icons.Default.Diamond, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${charDef.crystalUnlockCost}", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = { onSelectCharacter(charDef) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262634)),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("SELECT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = charDef.description,
                            fontSize = 10.sp,
                            color = if (isUnlocked) Color(0xFFB0BEC5) else Color(0xFF666666),
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Occult Settings Dialog for configuring Game Audio, Controls (Fixed vs Tap-to-Place Analog),
 * and review control mechanics.
 */
@Composable
fun OccultSettingsDialog(
    viewModel: GameViewModel,
    onClose: () -> Unit
) {
    var soundEnabled by remember { mutableStateOf(viewModel.soundManager.isSoundEnabled) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .shadow(24.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F0F16),
            border = BorderStroke(1.5.dp, Color(0xFFFFD54F).copy(alpha = 0.8f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "GAME SETTINGS",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF9E9EAE)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sound Setting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                            contentDescription = null,
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Sound Effects",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Audio cues, hits, and level ups",
                                fontSize = 10.sp,
                                color = Color(0xFF888894)
                            )
                        }
                    }
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = {
                            soundEnabled = it
                            viewModel.soundManager.isSoundEnabled = it
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFFD54F),
                            checkedTrackColor = Color(0xFF333344)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Fixed Analog Setting
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("menu_fixed_analog_toggle"),
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
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Fixed Analog Stick",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (viewModel.isFixedAnalog) {
                                    "Locked at bottom-center of battle screen"
                                } else {
                                    "Tap-to-place (auto-hides after 5s of no touch)"
                                },
                                fontSize = 10.sp,
                                color = Color(0xFF888894)
                            )
                        }
                    }
                    Switch(
                        checked = viewModel.isFixedAnalog,
                        onCheckedChange = { checked ->
                            viewModel.updateFixedAnalog(checked)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFFD54F),
                            checkedTrackColor = Color(0xFF333344)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Screen Shake & Impact Frames Setting
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("menu_screen_shake_toggle"),
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
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Screen Shake & Impact Frames",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (viewModel.isScreenShakeEnabled) {
                                    "Dynamic camera vibration & freeze-frame impact active"
                                } else {
                                    "Shake & freeze-frame impact disabled"
                                },
                                fontSize = 10.sp,
                                color = Color(0xFF888894)
                            )
                        }
                    }
                    Switch(
                        checked = viewModel.isScreenShakeEnabled,
                        onCheckedChange = { checked ->
                            viewModel.updateScreenShakeEnabled(checked)
                            viewModel.updateImpactFrameEnabled(checked)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFFFD54F),
                            checkedTrackColor = Color(0xFF333344)
                        )
                    )
                }

                // Dev Testing Mode Setting (Gated behind BuildConfig.DEBUG)
                if (com.example.BuildConfig.DEBUG) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("menu_dev_mode_toggle"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.BugReport,
                                contentDescription = null,
                                tint = if (viewModel.isDevTestingMode) Color(0xFFFF4081) else Color(0xFF90CAF9),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Dev Testing (Unlocked All)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (viewModel.isDevTestingMode) "Active: All characters, spells, max upgrades" else "Inactive: Clean progression starting from 0",
                                    fontSize = 10.sp,
                                    color = Color(0xFF888894)
                                )
                            }
                        }
                        Switch(
                            checked = viewModel.isDevTestingMode,
                            onCheckedChange = { checked ->
                                viewModel.applyDevTestingMode(checked)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFFF4081),
                                checkedTrackColor = Color(0xFF4A1428)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Guide Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161622)),
                    border = BorderStroke(1.dp, Color(0xFF262634))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "ANALOG CONTROLS BEHAVIOR",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD54F),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Checked (Fixed): Compass stick remains stationary in the bottom middle.\n• Unchecked (Tap): Hidden at 0% opacity by default. Tapping anywhere summons the stick to steer, then auto-hides after 5s of inactivity.",
                            fontSize = 10.sp,
                            color = Color(0xFFCFD8DC),
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onClose,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("close_settings_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "DONE",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

