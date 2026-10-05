package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameViewModel
import com.example.model.CalligraphicElement
import com.example.model.CodexDatabase
import com.example.model.CodexEntry
import com.example.model.EnemyType
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Sumi-e Codex Screen (墨画妖鉴):
 * Displays unlocked lore entries, stats, and high-contrast sumi-e concept art
 * for defeated paper horror enemies.
 */
@Composable
fun SumiECodexScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val isDevMode = viewModel.isDevTestingMode
    val unlockedSet = remember(viewModel.saveManager.loadSaveData(), isDevMode) {
        if (isDevMode) {
            EnemyType.entries.map { it.name }.toSet()
        } else {
            val data = viewModel.saveManager.loadSaveData()
            data.unlockedCodexEnemies.split(",").filter { it.isNotBlank() }.toSet()
        }
    }

    val allEntries = remember { CodexDatabase.allEntries }
    var selectedEntry by remember { mutableStateOf(allEntries.first()) }

    var animTime by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(16L)
            animTime += 0.016f
        }
    }

    val isUnlocked = isDevMode || unlockedSet.contains(selectedEntry.type.name)

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("sumi_e_codex_screen"),
        color = Color(0xFF07070A)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            // Header Bar
            CodexHeader(
                unlockedCount = allEntries.count { isDevMode || unlockedSet.contains(it.type.name) },
                totalCount = allEntries.size,
                onBack = onBack
            )

            // Horizontal Enemy Selector Carousel
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(allEntries) { entry ->
                    val unlocked = isDevMode || unlockedSet.contains(entry.type.name)
                    val isSelected = entry.type == selectedEntry.type

                    CodexCarouselItem(
                        entry = entry,
                        isUnlocked = unlocked,
                        isSelected = isSelected,
                        onClick = { selectedEntry = entry }
                    )
                }
            }

            // Main Detail Pane
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // High-Contrast Concept Art Canvas Card
                ConceptArtCard(
                    entry = selectedEntry,
                    isUnlocked = isUnlocked,
                    animTime = animTime
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Lore & Tactical Intelligence Card
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    CodexDetailCard(
                        entry = selectedEntry,
                        isUnlocked = isUnlocked
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun CodexHeader(
    unlockedCount: Int,
    totalCount: Int,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF161622))
                    .testTag("codex_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Vermillion Seal Stamp
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(Color(0xFFD32F2F), RoundedCornerShape(4.dp))
                    .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "妖鉴",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "SUMI-E CODEX",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Archive of Defeated Paper Horrors",
                    color = Color(0xFFAAAAAE),
                    fontSize = 10.sp
                )
            }
        }

        // Progress Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF161622))
                .border(1.dp, Color(0xFFFFD54F).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = "$unlockedCount / $totalCount DISCOVERED",
                color = Color(0xFFFFD54F),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CodexCarouselItem(
    entry: CodexEntry,
    isUnlocked: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFFFFD54F) else Color(0xFF232332)
    val bgColor = if (isSelected) Color(0xFF1B1B28) else Color(0xFF101017)

    Card(
        modifier = Modifier
            .width(100.dp)
            .height(72.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag("codex_item_${entry.type.name}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(if (isSelected) 1.8.dp else 1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mini Kanji Stamp
                Text(
                    text = if (isUnlocked) entry.kanjiStamp else "??",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) Color(0xFFFF5252) else Color(0xFF616172),
                    fontFamily = FontFamily.Serif
                )

                if (!isUnlocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color(0xFF616172),
                        modifier = Modifier.size(12.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color(0xFFFFD54F), CircleShape)
                    )
                }
            }

            Text(
                text = if (isUnlocked) entry.title else "Unknown",
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isUnlocked) Color.White else Color(0xFF757582),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ConceptArtCard(
    entry: CodexEntry,
    isUnlocked: Boolean,
    animTime: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .shadow(16.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0F16)),
        border = BorderStroke(1.5.dp, Color(0xFF2C2C3D))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Sumi-e Concept Art Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f

                // Calligraphic parchment grid & ring
                drawCircle(
                    color = Color(0xFF1E1E2C).copy(alpha = 0.5f),
                    radius = 90f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.5f)
                )
                drawCircle(
                    color = Color(0xFF29293C).copy(alpha = 0.35f),
                    radius = 115f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1f)
                )

                // Ink bleed splatter flourishes in corners
                drawInkSplatterFlourish(Offset(50f, 40f), 18f)
                drawInkSplatterFlourish(Offset(size.width - 50f, size.height - 40f), 22f)

                if (isUnlocked) {
                    drawEnemyConceptArt(entry.type, cx, cy, animTime)
                } else {
                    drawLockedEnemySilhouette(entry.type, cx, cy, animTime)
                }
            }

            // High-Contrast Kanji Watermark
            if (isUnlocked) {
                Text(
                    text = entry.kanjiStamp,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFD32F2F).copy(alpha = 0.22f),
                    fontFamily = FontFamily.Serif,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                )
            }

            // Threat Tier Badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xE6161622))
                    .border(1.dp, Color(0xFFFFD54F).copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isUnlocked) Icons.Default.Warning else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (isUnlocked) Color(0xFFFFD54F) else Color(0xFF757582),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isUnlocked) entry.threatTier else "UNDISCOVERED",
                        color = if (isUnlocked) Color(0xFFFFD54F) else Color(0xFF757582),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CodexDetailCard(
    entry: CodexEntry,
    isUnlocked: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF11111A)),
        border = BorderStroke(1.dp, Color(0xFF222230))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title & Subtitle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isUnlocked) entry.title else "Classified Construct",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = if (isUnlocked) entry.subtitle else "Encounter during survival runs to decrypt entry",
                        fontSize = 11.sp,
                        color = Color(0xFFFFD54F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stats Matrix (HP, Speed, Damage, Radius)
            if (isUnlocked) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatPill("HP", "${entry.type.baseHp.toInt()}", Modifier.weight(1f))
                    StatPill("SPEED", "${entry.type.speed.toInt()}", Modifier.weight(1f))
                    StatPill("DAMAGE", "${entry.type.damage.toInt()}", Modifier.weight(1f))
                    StatPill("RADIUS", "${entry.type.radius.toInt()}px", Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Lore Section
                Text(
                    text = "LORE INSCRIPTION",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD54F),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = entry.loreText,
                    fontSize = 12.sp,
                    color = Color(0xFFCFD8DC),
                    lineHeight = 18.sp,
                    fontFamily = FontFamily.Serif
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Tactical Behavior & Weaknesses
                Text(
                    text = "TACTICAL INTEL & WEAKNESS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD54F),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = entry.behaviorDetails,
                    fontSize = 11.sp,
                    color = Color(0xFFAAAAAE),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Weakness Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF181824))
                        .border(1.dp, Color(entry.recommendedElement.colorHex).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color(entry.recommendedElement.colorHex), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = entry.recommendedElement.glyph,
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "PRIMARY WEAKNESS: ${entry.primaryWeakness}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(entry.recommendedElement.colorHex)
                        )
                        Text(
                            text = entry.counterStrategy,
                            fontSize = 10.sp,
                            color = Color(0xFFCFD8DC)
                        )
                    }
                }
            } else {
                // Locked Guidance
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF161622))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFF888894),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Slay this paper horror in battle to unlock full lore entries, tactical weaknesses, and sumi-e concept art.",
                            fontSize = 11.sp,
                            color = Color(0xFF888894),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatPill(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF181824))
            .border(0.8.dp, Color(0xFF2C2C3D), RoundedCornerShape(6.dp))
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF888894))
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
        }
    }
}

// ---------------- CANVAS CONCEPT ART DRAWING HELPERS ----------------

private fun DrawScope.drawInkSplatterFlourish(center: Offset, baseRadius: Float) {
    drawCircle(color = Color(0xFF181824), radius = baseRadius, center = center)
    for (i in 0 until 5) {
        val a = i * (PI.toFloat() * 2f / 5f)
        val d = baseRadius * 1.4f
        drawCircle(color = Color(0xFF181824), radius = baseRadius * 0.35f, center = Offset(center.x + cos(a) * d, center.y + sin(a) * d))
    }
}

private fun DrawScope.drawEnemyConceptArt(
    type: EnemyType,
    cx: Float,
    cy: Float,
    time: Float
) {
    val bob = sin(time * 3f) * 4f
    val ey = cy + bob
    val ex = cx

    when (type) {
        EnemyType.BASIC_CONSTRUCT -> {
            val r = 48f
            // 4-faceted diamond origami styling
            val pTopLeft = Path().apply {
                moveTo(ex, ey - r * 1.15f)
                lineTo(ex - r, ey)
                lineTo(ex, ey + r * 0.2f)
                close()
            }
            val pBottomRight = Path().apply {
                moveTo(ex, ey + r * 0.2f)
                lineTo(ex + r, ey)
                lineTo(ex, ey + r * 1.15f)
                close()
            }
            val pTopRight = Path().apply {
                moveTo(ex, ey - r * 1.15f)
                lineTo(ex + r, ey)
                lineTo(ex, ey + r * 0.2f)
                close()
            }
            val pBottomLeft = Path().apply {
                moveTo(ex, ey + r * 0.2f)
                lineTo(ex - r, ey)
                lineTo(ex, ey + r * 1.15f)
                close()
            }
            drawPath(pTopLeft, Color(0xFFF0F0F0))
            drawPath(pTopRight, Color(0xFFE0E0E0))
            drawPath(pBottomLeft, Color(0xFFD6D6D6))
            drawPath(pBottomRight, Color(0xFFC4C4C4))

            // Stark high-contrast outlines
            val fullDiamond = Path().apply {
                moveTo(ex, ey - r * 1.15f)
                lineTo(ex + r, ey)
                lineTo(ex, ey + r * 1.15f)
                lineTo(ex - r, ey)
                close()
            }
            drawPath(fullDiamond, Color(0xFF0F0F14), style = Stroke(width = 3.5f))
            drawLine(Color(0xFF0F0F14), Offset(ex - r, ey), Offset(ex + r, ey), strokeWidth = 2.5f)
            drawLine(Color(0xFF0F0F14), Offset(ex, ey - r * 1.15f), Offset(ex, ey + r * 1.15f), strokeWidth = 2.5f)

            // Neon eye slit
            drawCircle(Color(0xFFFF007F), radius = 6f, center = Offset(ex, ey - 6f))
            drawCircle(Color.White, radius = 2.5f, center = Offset(ex - 1.5f, ey - 7.5f))
        }

        EnemyType.FOLDED_STALKER -> {
            val r = 54f
            val wingLeft = Path().apply {
                moveTo(ex, ey - r * 1.3f)
                lineTo(ex - r * 1.4f, ey + r * 0.9f)
                lineTo(ex, ey + r * 0.35f)
                close()
            }
            val wingRight = Path().apply {
                moveTo(ex, ey - r * 1.3f)
                lineTo(ex + r * 1.4f, ey + r * 0.9f)
                lineTo(ex, ey + r * 0.35f)
                close()
            }
            drawPath(wingLeft, Color(0xFFDEDBD2))
            drawPath(wingRight, Color(0xFFF5F5F0))

            val fullStalker = Path().apply {
                moveTo(ex, ey - r * 1.3f)
                lineTo(ex + r * 1.4f, ey + r * 0.9f)
                lineTo(ex, ey + r * 0.35f)
                lineTo(ex - r * 1.4f, ey + r * 0.9f)
                close()
            }
            drawPath(fullStalker, Color(0xFF0F0F14), style = Stroke(width = 4f))
            drawLine(Color(0xFF0F0F14), Offset(ex, ey - r * 1.3f), Offset(ex, ey + r * 0.35f), strokeWidth = 3f)
            drawCircle(Color(0xFFFF007F), radius = 6f, center = Offset(ex, ey - r * 0.3f))
        }

        EnemyType.PAPER_BRUTE -> {
            val r = 64f
            val brutePath = Path().apply {
                moveTo(ex, ey - r)
                lineTo(ex + r * 0.95f, ey - r * 0.5f)
                lineTo(ex + r * 0.95f, ey + r * 0.6f)
                lineTo(ex, ey + r * 1.05f)
                lineTo(ex - r * 0.95f, ey + r * 0.6f)
                lineTo(ex - r * 0.95f, ey - r * 0.5f)
                close()
            }
            drawPath(brutePath, Color(0xFFECEFF1))
            drawPath(brutePath, Color(0xFF0F0F14), style = Stroke(width = 4.5f))

            // Armor plates
            drawLine(Color(0xFF0F0F14), Offset(ex - r * 0.6f, ey - r * 0.4f), Offset(ex, ey + r * 0.4f), strokeWidth = 2.5f)
            drawLine(Color(0xFF0F0F14), Offset(ex + r * 0.6f, ey - r * 0.4f), Offset(ex, ey + r * 0.4f), strokeWidth = 2.5f)
            drawCircle(Color(0xFFFF3D00), radius = 8f, center = Offset(ex, ey - 4f))
        }

        EnemyType.THE_TITAN -> {
            val r = 78f
            val path = Path().apply {
                moveTo(ex, ey - r)
                lineTo(ex + r * 0.8f, ey - r * 0.6f)
                lineTo(ex + r, ey)
                lineTo(ex + r * 0.8f, ey + r * 0.7f)
                lineTo(ex, ey + r)
                lineTo(ex - r * 0.8f, ey + r * 0.7f)
                lineTo(ex - r, ey)
                lineTo(ex - r * 0.8f, ey - r * 0.6f)
                close()
            }
            drawPath(path, Color(0xFFF5F5F5))
            drawPath(path, Color(0xFFD32F2F), style = Stroke(width = 5f))

            // Orbiting runic ring
            for (k in 0..7) {
                val a = time * 2f + (k * PI / 4).toFloat()
                val rx = ex + cos(a) * (r * 1.15f)
                val ry = ey + sin(a) * (r * 1.15f)
                drawCircle(color = Color(0xFFD32F2F), radius = 5.5f, center = Offset(rx, ry))
            }
            drawCircle(Color(0xFF00E5FF), radius = 10f, center = Offset(ex, ey))
        }

        EnemyType.MID_BOSS_COLOSSUS -> {
            val r = 70f
            val path = Path().apply {
                moveTo(ex, ey - r * 1.2f)
                lineTo(ex + r * 0.9f, ey - r * 0.5f)
                lineTo(ex + r * 0.75f, ey + r * 0.9f)
                lineTo(ex, ey + r * 1.25f)
                lineTo(ex - r * 0.75f, ey + r * 0.9f)
                lineTo(ex - r * 0.9f, ey - r * 0.5f)
                close()
            }
            drawPath(path, Color(0xFFE8EAF6))
            drawPath(path, Color(0xFF283593), style = Stroke(width = 4f))
            drawCircle(Color(0xFF00E676), radius = 14f, center = Offset(ex, ey - 6f))
        }

        EnemyType.THE_BLOTTER -> {
            val r = 58f
            val path = Path().apply {
                moveTo(ex - r, ey - r * 0.75f)
                lineTo(ex + r, ey - r * 0.75f)
                lineTo(ex + r * 1.15f, ey + r * 0.75f)
                lineTo(ex - r * 1.15f, ey + r * 0.75f)
                close()
            }
            drawPath(path, Color(0xFFE0F2F1))
            drawPath(path, Color(0xFF00796B), style = Stroke(width = 4f))
            drawCircle(Color(0xFF004D40), radius = 7f, center = Offset(ex - r * 0.35f, ey - r * 0.2f))
            drawCircle(Color(0xFF004D40), radius = 8f, center = Offset(ex + r * 0.3f, ey))
        }

        EnemyType.ORIGAMI_SHIELD -> {
            val r = 56f
            val path = Path().apply {
                moveTo(ex, ey - r * 1.2f)
                lineTo(ex + r * 1.1f, ey - r * 0.3f)
                lineTo(ex + r * 0.7f, ey + r * 1.1f)
                lineTo(ex, ey + r * 1.3f)
                lineTo(ex - r * 0.7f, ey + r * 1.1f)
                lineTo(ex - r * 1.1f, ey - r * 0.3f)
                close()
            }
            drawPath(path, Color(0xFFECEFF1))
            drawPath(path, Color(0xFF37474F), style = Stroke(width = 4f))
            drawCircle(Color(0xFF1E88E5), radius = 7f, center = Offset(ex, ey))
        }

        EnemyType.STILT_WALKER -> {
            val r = 52f
            drawLine(Color(0xFF5D4037), Offset(ex - r * 0.4f, ey), Offset(ex - r * 0.6f, ey + r * 1.5f), strokeWidth = 4f)
            drawLine(Color(0xFF5D4037), Offset(ex + r * 0.4f, ey), Offset(ex + r * 0.6f, ey + r * 1.5f), strokeWidth = 4f)
            val path = Path().apply {
                moveTo(ex, ey - r * 1.2f)
                lineTo(ex + r * 0.8f, ey - r * 0.2f)
                lineTo(ex, ey + r * 0.4f)
                lineTo(ex - r * 0.8f, ey - r * 0.2f)
                close()
            }
            drawPath(path, Color(0xFFFFF9C4))
            drawPath(path, Color(0xFFF57F17), style = Stroke(width = 3.5f))
            drawCircle(Color(0xFFFF007F), radius = 5.5f, center = Offset(ex, ey - r * 0.3f))
        }

        EnemyType.INK_SWARMER -> {
            val r = 50f
            val wingFlap = sin(time * 12f) * 6f
            val dartPath = Path().apply {
                moveTo(ex, ey - r * 1.3f)
                lineTo(ex + r * 1.2f + wingFlap, ey + r * 0.8f)
                lineTo(ex + r * 0.3f, ey + r * 0.4f)
                lineTo(ex, ey + r * 1.1f)
                lineTo(ex - r * 0.3f, ey + r * 0.4f)
                lineTo(ex - r * 1.2f - wingFlap, ey + r * 0.8f)
                close()
            }
            drawPath(dartPath, Color(0xFFFFF9C4))
            drawPath(dartPath, Color(0xFFF57F17), style = Stroke(width = 4f))
            drawLine(Color(0xFFE65100), Offset(ex, ey - r * 1.2f), Offset(ex, ey + r * 0.9f), strokeWidth = 2.5f)
            drawCircle(Color(0xFFFF1744), radius = 6f, center = Offset(ex, ey - r * 0.3f))
        }

        EnemyType.INK_WEAVER -> {
            val r = 54f
            val legColor = Color(0xFF37474F)
            for (side in listOf(-1f, 1f)) {
                for (i in 0..3) {
                    val angleOffset = -0.5f + i * 0.35f
                    val legBob = sin(time * 6f + i * 1.2f) * 4f
                    val kneeX = ex + side * (r * 0.95f)
                    val kneeY = ey + angleOffset * r * 1.2f - r * 0.2f + legBob
                    val footX = ex + side * (r * 1.55f)
                    val footY = ey + angleOffset * r * 1.6f + r * 0.4f + legBob
                    drawLine(legColor, Offset(ex + side * r * 0.35f, ey + (i - 1.5f) * r * 0.2f), Offset(kneeX, kneeY), strokeWidth = 3f)
                    drawLine(legColor, Offset(kneeX, kneeY), Offset(footX, footY), strokeWidth = 2.2f)
                }
            }

            val abdomen = Path().apply {
                moveTo(ex, ey - r * 0.9f)
                lineTo(ex + r * 0.7f, ey - r * 0.1f)
                lineTo(ex + r * 0.5f, ey + r * 0.85f)
                lineTo(ex, ey + r * 1.05f)
                lineTo(ex - r * 0.5f, ey + r * 0.85f)
                lineTo(ex - r * 0.7f, ey - r * 0.1f)
                close()
            }
            drawPath(abdomen, Color(0xFFECEFF1))
            drawPath(abdomen, Color(0xFF263238), style = Stroke(width = 4f))
            drawCircle(Color(0xFF8E24AA), radius = 6.5f, center = Offset(ex, ey - r * 0.2f))
            drawCircle(Color(0xFF8E24AA), radius = 5f, center = Offset(ex, ey + r * 0.3f))
        }
    }
}

private fun DrawScope.drawLockedEnemySilhouette(
    type: EnemyType,
    cx: Float,
    cy: Float,
    time: Float
) {
    val r = 52f
    val pulse = sin(time * 4f) * 3f
    // Dark shadowy ink silhouette
    drawCircle(
        color = Color(0xFF000000).copy(alpha = 0.85f),
        radius = r + pulse,
        center = Offset(cx, cy)
    )
    drawCircle(
        color = Color(0xFF2C2C3D),
        radius = r + pulse,
        center = Offset(cx, cy),
        style = Stroke(width = 2f)
    )

    // Glowing mystery eye slit in center
    drawCircle(
        color = Color(0xFF757582).copy(alpha = 0.7f),
        radius = 5f,
        center = Offset(cx, cy - 4f)
    )
}
