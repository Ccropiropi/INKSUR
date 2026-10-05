package com.example.ui

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PlayerProgressEntity
import com.example.model.ClassDefinition
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Authentic Magic Survival style Class Selection Screen.
 * Features the pitch black mystical background, currency header,
 * mastery level counter with golden grunge brush underline, colored perk bonuses,
 * a 5-column grid of 24 occult class glyphs, and a large bottom "Selected" / "Select" action.
 */
@Composable
fun MagicSurvivalClassSelectionScreen(
    selectedClass: ClassDefinition,
    progress: PlayerProgressEntity,
    onSelectClass: (ClassDefinition) -> Unit,
    onClose: () -> Unit,
    onBuyFragment: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var inspectedClass by remember { mutableStateOf(selectedClass) }
    val isInspectedUnlocked = progress.isClassUnlocked(inspectedClass.id)
    val inspectedLevel = if (isInspectedUnlocked) progress.getClassLevel(inspectedClass.id) else 0
    val inspectedFragments = progress.getClassFragments(inspectedClass.id)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("magic_survival_class_selection_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. TOP BAR: Currency (Gold & Crystals) on Left, Close button on Right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Currencies
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Gold Coin
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFD54F))
                                .border(1.5.dp, Color(0xFFFFA000), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${progress.gold}",
                            color = Color(0xFFFFD54F),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    }

                    // Crystals
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Diamond,
                            contentDescription = "Crystals",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${progress.crystals}",
                            color = Color(0xFF00E5FF),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                    }
                }

                // Close Button
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("close_class_selection")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 2. SELECTED CLASS HEADER & DETAILS
            Text(
                text = inspectedClass.name,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily.Serif,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Mastery level e.g. "Level 1 / 12" or "Locked (Fragments: 0/1)"
            if (isInspectedUnlocked) {
                Text(
                    text = "$inspectedLevel / ${inspectedClass.maxMastery}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFE0E0E0),
                    fontFamily = FontFamily.Serif
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color(0xFFFF7043),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "LOCKED (Fragments: $inspectedFragments/1)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF7043),
                        fontFamily = FontFamily.Serif
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Grunge brush underline
            Canvas(modifier = Modifier.size(width = 130.dp, height = 5.dp)) {
                val brushPath = Path().apply {
                    moveTo(0f, size.height * 0.5f)
                    quadraticBezierTo(size.width * 0.25f, size.height * 0.8f, size.width * 0.5f, size.height * 0.4f)
                    quadraticBezierTo(size.width * 0.75f, size.height * 0.2f, size.width, size.height * 0.6f)
                }
                drawPath(
                    path = brushPath,
                    color = if (isInspectedUnlocked) Color(0xFFFFE082) else Color(0xFFFF7043),
                    style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Class Perks / Passives List
            val spellName = inspectedClass.starterSpell.name
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                // Perk 1 (Cyan)
                Text(
                    text = "$spellName Lv +1",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isInspectedUnlocked) Color(0xFF4DD0E1) else Color(0xFF757575),
                    textAlign = TextAlign.Center
                )

                // Perk 2 (Dynamic colored annotated text)
                val annotatedPerk2 = buildAnnotatedString {
                    append("Every time the character gains ")
                    withStyle(SpanStyle(color = if (isInspectedUnlocked) Color(0xFFFFD54F) else Color(0xFF9E9E9E), fontWeight = FontWeight.Bold)) {
                        append("5")
                    }
                    append(" levels, $spellName Damage ")
                    withStyle(SpanStyle(color = if (isInspectedUnlocked) Color(0xFF81C784) else Color(0xFF9E9E9E), fontWeight = FontWeight.Bold)) {
                        append("3%")
                    }
                    append(" is ")
                    withStyle(SpanStyle(color = if (isInspectedUnlocked) Color(0xFFCE93D8) else Color(0xFF9E9E9E), fontWeight = FontWeight.Bold)) {
                        append("added")
                    }
                }
                Text(
                    text = annotatedPerk2,
                    fontSize = 11.sp,
                    color = if (isInspectedUnlocked) Color(0xFFE0E0E0) else Color(0xFF757575),
                    textAlign = TextAlign.Center
                )

                // Perk 3 (Cyan)
                Text(
                    text = "$spellName Lv +1",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isInspectedUnlocked && inspectedLevel >= 3) Color(0xFF4DD0E1) else Color(0xFF757575),
                    textAlign = TextAlign.Center
                )

                // Perk 4 (Cyan)
                Text(
                    text = "Decrease $spellName Cooldown by 20%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isInspectedUnlocked && inspectedLevel >= 5) Color(0xFF4DD0E1) else Color(0xFF757575),
                    textAlign = TextAlign.Center
                )

                // Perk 5 (Dimmed white)
                Text(
                    text = "Increase the number of $spellName by 1",
                    fontSize = 11.sp,
                    color = if (isInspectedUnlocked && inspectedLevel >= 8) Color(0xFFB0BEC5) else Color(0xFF616161),
                    textAlign = TextAlign.Center
                )

                // Perk 6 (All classes bonus, grey)
                Text(
                    text = "Increase $spellName Damage by 20% (All Classes)",
                    fontSize = 11.sp,
                    color = if (isInspectedUnlocked && inspectedLevel >= 12) Color(0xFFFFD54F) else Color(0xFF555555),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. THE 5-COLUMN GRID OF 24 OCCULT CLASS GLYPHS
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ClassDefinition.allClasses) { classDef ->
                        val isInspected = inspectedClass.id == classDef.id
                        val isEquipped = selectedClass.id == classDef.id
                        val isUnlocked = progress.isClassUnlocked(classDef.id)

                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isInspected) Color(0x33FFD54F) else if (!isUnlocked) Color(0x22111116) else Color.Transparent
                                )
                                .border(
                                    width = if (isInspected) 1.5.dp else if (isEquipped) 1.5.dp else 0.5.dp,
                                    color = if (isInspected) Color(0xFFFFD54F) else if (isEquipped) Color(0xFF81C784) else if (isUnlocked) Color(0x33FFFFFF) else Color(0x22FFFFFF),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    inspectedClass = classDef
                                }
                                .testTag("class_glyph_${classDef.id}"),
                            contentAlignment = Alignment.Center
                        ) {
                            ClassGlyphCanvas(
                                glyphType = classDef.glyphType,
                                isSelected = isInspected,
                                defaultColor = if (isUnlocked) Color(classDef.glyphColorHex) else Color(0xFF55555A),
                                modifier = Modifier.size(46.dp)
                            )

                            if (!isUnlocked) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(3.dp)
                                        .size(14.dp)
                                        .background(Color.Black.copy(alpha = 0.7f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = Color(0xFFFFAB91),
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4. BOTTOM ACTION BUTTON ("Selected", "Select", or "Unlock Fragment")
            val isCurrentClassSelected = selectedClass.id == inspectedClass.id
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isCurrentClassSelected) {
                    Text(
                        text = "Selected",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.Serif,
                        letterSpacing = 1.sp,
                        modifier = Modifier.testTag("class_status_selected")
                    )
                } else if (isInspectedUnlocked) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1A1A1A))
                            .border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(12.dp))
                            .clickable {
                                onSelectClass(inspectedClass.copy(masteryLevel = inspectedLevel))
                            }
                            .padding(horizontal = 42.dp, vertical = 10.dp)
                            .testTag("class_select_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Select",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFD54F),
                            fontFamily = FontFamily.Serif,
                            letterSpacing = 1.sp
                        )
                    }
                } else {
                    // Locked class: offer to unlock via fragment purchase if afford
                    val canAffordGold = progress.gold >= 100
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (canAffordGold) Color(0xFF261D12) else Color(0xFF1A1A1A))
                            .border(1.5.dp, if (canAffordGold) Color(0xFFFFB300) else Color(0xFF757575), RoundedCornerShape(12.dp))
                            .clickable(enabled = canAffordGold) {
                                onBuyFragment?.invoke(inspectedClass.id)
                            }
                            .padding(horizontal = 24.dp, vertical = 10.dp)
                            .testTag("unlock_class_fragment_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "UNLOCK FRAGMENT (100 Gold)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (canAffordGold) Color(0xFFFFD54F) else Color.Gray,
                                fontFamily = FontFamily.Serif,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// CANVAS VECTOR RENDERING FOR ALL 24 OCCULT CLASS GLYPHS
// ----------------------------------------------------

@Composable
fun ClassGlyphCanvas(
    glyphType: String,
    isSelected: Boolean,
    defaultColor: Color,
    modifier: Modifier = Modifier
) {
    val renderColor = if (isSelected) Color(0xFFFFF59D) else defaultColor

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // Soft outer luminous bloom when selected
        if (isSelected) {
            drawCircle(
                color = Color(0xFFFFE082).copy(alpha = 0.28f),
                radius = w * 0.48f,
                center = Offset(cx, cy)
            )
        }

        when (glyphType) {
            "SPIRE_TOWER" -> drawSpireTower(cx, cy, w, h, renderColor)
            "CONSTELLATION" -> drawConstellation(cx, cy, w, h, renderColor)
            "SNOWFLAKE" -> drawSnowflake(cx, cy, w, h, renderColor)
            "TOME_GATE" -> drawTomeGate(cx, cy, w, h, renderColor)
            "WHEEL_SPIKES" -> drawWheelSpikes(cx, cy, w, h, renderColor)
            "VOODOO_DOLL" -> drawVoodooDoll(cx, cy, w, h, renderColor)
            "HEXAGRAM_SEAL" -> drawHexagramSeal(cx, cy, w, h, renderColor)
            "CROSS_DIAMOND" -> drawCrossDiamond(cx, cy, w, h, renderColor)
            "SUN_ROSE" -> drawSunRose(cx, cy, w, h, renderColor)
            "TREE_RUNE" -> drawTreeRune(cx, cy, w, h, renderColor)
            "EXPLOSIVE_BURST" -> drawExplosiveBurst(cx, cy, w, h, renderColor)
            "LIGHTNING_HAND" -> drawLightningHand(cx, cy, w, h, renderColor)
            "ALCHEMIC_SPIRES" -> drawAlchemicSpires(cx, cy, w, h, renderColor)
            "BOOK_STACK" -> drawBookStack(cx, cy, w, h, renderColor)
            "CAULDRON" -> drawCauldron(cx, cy, w, h, renderColor)
            "CRACKED_ORB" -> drawCrackedOrb(cx, cy, w, h, renderColor)
            "SWIRL_VORTEX" -> drawSwirlVortex(cx, cy, w, h, renderColor)
            "SERPENT_STAFF" -> drawSerpentStaff(cx, cy, w, h, renderColor)
            "CHALICE" -> drawChalice(cx, cy, w, h, renderColor)
            "HOODED_MAGE" -> drawHoodedMage(cx, cy, w, h, renderColor)
            "SHADOW_FACE" -> drawShadowFace(cx, cy, w, h, renderColor)
            "WIZARD_HAT" -> drawWizardHat(cx, cy, w, h, renderColor)
            "SOLAR_CORONA" -> drawSolarCorona(cx, cy, w, h, renderColor)
            "SKULL_VISAGE" -> drawSkullVisage(cx, cy, w, h, renderColor)
            else -> drawSpireTower(cx, cy, w, h, renderColor)
        }
    }
}

// 1. Spire Tower (Wizard / Scribe)
private fun DrawScope.drawSpireTower(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    val path = Path().apply {
        moveTo(cx, cy - h * 0.44f) // Spire tip
        lineTo(cx - w * 0.08f, cy - h * 0.18f)
        lineTo(cx - w * 0.18f, cy + h * 0.08f)
        lineTo(cx - w * 0.28f, cy + h * 0.38f)
        lineTo(cx + w * 0.28f, cy + h * 0.38f)
        lineTo(cx + w * 0.18f, cy + h * 0.08f)
        lineTo(cx + w * 0.08f, cy - h * 0.18f)
        close()
    }
    drawPath(path, color)
    // Tower base cross-lines
    drawLine(color, Offset(cx - w * 0.35f, cy + h * 0.42f), Offset(cx + w * 0.35f, cy + h * 0.42f), strokeWidth = 2.5f)
}

// 2. Constellation (Astronomer)
private fun DrawScope.drawConstellation(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    val points = listOf(
        Offset(cx - w * 0.30f, cy - h * 0.22f),
        Offset(cx - w * 0.12f, cy - h * 0.38f),
        Offset(cx + w * 0.24f, cy - h * 0.28f),
        Offset(cx + w * 0.34f, cy - h * 0.05f),
        Offset(cx + w * 0.16f, cy + h * 0.18f),
        Offset(cx - w * 0.08f, cy + h * 0.04f),
        Offset(cx - w * 0.26f, cy + h * 0.28f),
        Offset(cx + w * 0.02f, cy + h * 0.36f)
    )
    for (i in 0 until points.size - 1) {
        drawLine(color, points[i], points[i + 1], strokeWidth = 1.5f)
    }
    for (p in points) {
        drawCircle(color, radius = 3.5f, center = p)
    }
}

// 3. Snowflake (Cryomancer / Electromancer)
private fun DrawScope.drawSnowflake(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    val r = w * 0.38f
    for (i in 0 until 6) {
        val angle = (i * PI / 3).toFloat()
        val ex = cx + cos(angle) * r
        val ey = cy + sin(angle) * r
        drawLine(color, Offset(cx, cy), Offset(ex, ey), strokeWidth = 2.2f)

        // Chevron branches
        val bx = cx + cos(angle) * (r * 0.6f)
        val by = cy + sin(angle) * (r * 0.6f)
        val branchR = r * 0.25f
        val a1 = angle + 0.6f
        val a2 = angle - 0.6f
        drawLine(color, Offset(bx, by), Offset(bx + cos(a1) * branchR, by + sin(a1) * branchR), strokeWidth = 1.8f)
        drawLine(color, Offset(bx, by), Offset(bx + cos(a2) * branchR, by + sin(a2) * branchR), strokeWidth = 1.8f)
    }
    drawCircle(color, radius = 4f, center = Offset(cx, cy))
}

// 4. Tome Gate (Archivist)
private fun DrawScope.drawTomeGate(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    val lines = 7
    val startX = cx - w * 0.32f
    val endX = cx + w * 0.32f
    val step = (endX - startX) / (lines - 1)
    for (i in 0 until lines) {
        val x = startX + i * step
        val length = if (i == 0 || i == lines - 1) h * 0.75f else h * 0.6f
        drawLine(color, Offset(x, cy - length / 2f), Offset(x, cy + length / 2f), strokeWidth = 2.4f)
    }
    drawLine(color, Offset(startX - 2f, cy - h * 0.15f), Offset(endX + 2f, cy - h * 0.15f), strokeWidth = 2.5f)
    drawLine(color, Offset(startX - 2f, cy + h * 0.15f), Offset(endX + 2f, cy + h * 0.15f), strokeWidth = 2.5f)
}

// 5. Wheel Spikes (Illuminator)
private fun DrawScope.drawWheelSpikes(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    drawCircle(color, radius = w * 0.26f, center = Offset(cx, cy), style = Stroke(width = 2.2f))
    drawCircle(color, radius = w * 0.12f, center = Offset(cx, cy), style = Stroke(width = 2f))
    val count = 8
    for (i in 0 until count) {
        val angle = (i * 2 * PI / count).toFloat()
        val s = w * 0.26f
        val e = w * 0.42f
        drawLine(
            color,
            Offset(cx + cos(angle) * s, cy + sin(angle) * s),
            Offset(cx + cos(angle) * e, cy + sin(angle) * e),
            strokeWidth = 2.4f
        )
    }
}

// 6. Voodoo Doll (Witch)
private fun DrawScope.drawVoodooDoll(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    // Head
    drawCircle(color, radius = w * 0.14f, center = Offset(cx - w * 0.05f, cy - h * 0.22f))
    // Body & limbs path
    val path = Path().apply {
        moveTo(cx - w * 0.05f, cy - h * 0.08f)
        lineTo(cx - w * 0.05f, cy + h * 0.25f)
        lineTo(cx - w * 0.18f, cy + h * 0.38f)
        moveTo(cx - w * 0.05f, cy + h * 0.25f)
        lineTo(cx + w * 0.12f, cy + h * 0.38f)
        moveTo(cx - w * 0.22f, cy + h * 0.05f)
        lineTo(cx + w * 0.15f, cy + h * 0.05f)
    }
    drawPath(path, color, style = Stroke(width = 3f, cap = StrokeCap.Round))
    // Piercing pins
    drawLine(color, Offset(cx - w * 0.28f, cy - h * 0.30f), Offset(cx - w * 0.05f, cy - h * 0.20f), strokeWidth = 1.8f)
    drawCircle(color, radius = 2.5f, center = Offset(cx - w * 0.28f, cy - h * 0.30f))
}

// 7. Hexagram Seal (Magician)
private fun DrawScope.drawHexagramSeal(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    drawCircle(color, radius = w * 0.38f, center = Offset(cx, cy), style = Stroke(width = 2f))
    val r = w * 0.34f
    // Upright triangle
    val p1 = Path().apply {
        moveTo(cx, cy - r)
        lineTo(cx + cos(PI.toFloat() / 6) * r, cy + sin(PI.toFloat() / 6) * r)
        lineTo(cx - cos(PI.toFloat() / 6) * r, cy + sin(PI.toFloat() / 6) * r)
        close()
    }
    // Inverted triangle
    val p2 = Path().apply {
        moveTo(cx, cy + r)
        lineTo(cx + cos(PI.toFloat() / 6) * r, cy - sin(PI.toFloat() / 6) * r)
        lineTo(cx - cos(PI.toFloat() / 6) * r, cy - sin(PI.toFloat() / 6) * r)
        close()
    }
    drawPath(p1, color, style = Stroke(width = 1.8f))
    drawPath(p2, color, style = Stroke(width = 1.8f))
}

// 8. Cross Diamond (Bishop)
private fun DrawScope.drawCrossDiamond(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    val dPath = Path().apply {
        moveTo(cx, cy - h * 0.38f)
        lineTo(cx + w * 0.34f, cy)
        lineTo(cx, cy + h * 0.38f)
        lineTo(cx - w * 0.34f, cy)
        close()
    }
    drawPath(dPath, color, style = Stroke(width = 2.2f))
    // Cross inside
    drawLine(color, Offset(cx, cy - h * 0.34f), Offset(cx, cy + h * 0.34f), strokeWidth = 2.5f)
    drawLine(color, Offset(cx - w * 0.30f, cy), Offset(cx + w * 0.30f, cy), strokeWidth = 2.5f)
    drawCircle(color, radius = 5f, center = Offset(cx, cy))
}

// 9. Sun Rose (Sorcerer)
private fun DrawScope.drawSunRose(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    drawCircle(color, radius = w * 0.35f, center = Offset(cx, cy), style = Stroke(width = 2f))
    drawCircle(color, radius = w * 0.18f, center = Offset(cx, cy), style = Stroke(width = 1.5f))
    for (i in 0 until 12) {
        val angle = (i * 2 * PI / 12).toFloat()
        val pStart = Offset(cx + cos(angle) * (w * 0.18f), cy + sin(angle) * (w * 0.18f))
        val pEnd = Offset(cx + cos(angle) * (w * 0.35f), cy + sin(angle) * (w * 0.35f))
        drawLine(color, pStart, pEnd, strokeWidth = 1.5f)
    }
}

// 10. Tree Rune (Druid)
private fun DrawScope.drawTreeRune(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    val trunk = Path().apply {
        moveTo(cx, cy + h * 0.38f)
        quadraticBezierTo(cx - w * 0.15f, cy, cx, cy - h * 0.35f)
    }
    drawPath(trunk, color, style = Stroke(width = 3.5f, cap = StrokeCap.Round))
    // Branches
    drawLine(color, Offset(cx - w * 0.05f, cy), Offset(cx + w * 0.25f, cy - h * 0.20f), strokeWidth = 2f)
    drawLine(color, Offset(cx - w * 0.08f, cy + h * 0.15f), Offset(cx + w * 0.22f, cy), strokeWidth = 2f)
}

// 11. Explosive Burst (Pyromancer)
private fun DrawScope.drawExplosiveBurst(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    val points = 10
    val path = Path()
    for (i in 0 until points * 2) {
        val angle = (i * PI / points).toFloat()
        val r = if (i % 2 == 0) w * 0.38f else w * 0.15f
        val x = cx + cos(angle) * r
        val y = cy + sin(angle) * r
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color)
}

// 12. Lightning Hand (Shaman)
private fun DrawScope.drawLightningHand(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    // Upper hand cloud
    drawCircle(color, radius = w * 0.18f, center = Offset(cx, cy - h * 0.18f))
    // Zigzag lightning
    val bolt = Path().apply {
        moveTo(cx, cy - h * 0.05f)
        lineTo(cx - w * 0.12f, cy + h * 0.10f)
        lineTo(cx + w * 0.05f, cy + h * 0.10f)
        lineTo(cx - w * 0.15f, cy + h * 0.38f)
    }
    drawPath(bolt, color, style = Stroke(width = 3f, cap = StrokeCap.Round))
}

// 13. Alchemic Spires (Alchemist)
private fun DrawScope.drawAlchemicSpires(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    // Base platform
    drawLine(color, Offset(cx - w * 0.35f, cy + h * 0.36f), Offset(cx + w * 0.35f, cy + h * 0.36f), strokeWidth = 3f)
    // 3 cylinders
    val xs = listOf(cx - w * 0.20f, cx, cx + w * 0.20f)
    val heights = listOf(h * 0.40f, h * 0.65f, h * 0.45f)
    for (i in 0..2) {
        val rx = xs[i]
        val rh = heights[i]
        drawLine(color, Offset(rx, cy + h * 0.36f), Offset(rx, cy + h * 0.36f - rh), strokeWidth = 4f)
        drawCircle(color, radius = 3.5f, center = Offset(rx, cy + h * 0.36f - rh - 4f))
    }
}

// 14. Book Stack (Scholar)
private fun DrawScope.drawBookStack(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    // Stack of horizontal books
    for (i in 0..3) {
        val by = cy + h * 0.32f - i * (h * 0.14f)
        drawLine(color, Offset(cx - w * 0.22f, by), Offset(cx + w * 0.28f, by), strokeWidth = 5f, cap = StrokeCap.Square)
    }
    // Upright quill
    val quill = Path().apply {
        moveTo(cx - w * 0.28f, cy + h * 0.32f)
        lineTo(cx - w * 0.22f, cy - h * 0.35f)
    }
    drawPath(quill, color, style = Stroke(width = 2.5f, cap = StrokeCap.Round))
}

// 15. Cauldron (Occultist)
private fun DrawScope.drawCauldron(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    // Round cauldron body
    drawArc(
        color = color,
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(cx - w * 0.30f, cy - h * 0.12f),
        size = Size(w * 0.60f, h * 0.45f)
    )
    // Rim
    drawLine(color, Offset(cx - w * 0.32f, cy - h * 0.12f), Offset(cx + w * 0.32f, cy - h * 0.12f), strokeWidth = 4f)
    // Bubbles rising
    drawCircle(color, radius = 3f, center = Offset(cx - w * 0.12f, cy - h * 0.25f))
    drawCircle(color, radius = 4f, center = Offset(cx + w * 0.08f, cy - h * 0.32f))
}

// 16. Cracked Orb (Engraver / Geomancer)
private fun DrawScope.drawCrackedOrb(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    drawCircle(color, radius = w * 0.34f, center = Offset(cx, cy), style = Stroke(width = 2.2f))
    val crack = Path().apply {
        moveTo(cx - w * 0.32f, cy - h * 0.10f)
        lineTo(cx - w * 0.08f, cy)
        lineTo(cx + w * 0.05f, cy - h * 0.20f)
        lineTo(cx + w * 0.32f, cy - h * 0.05f)
        moveTo(cx - w * 0.08f, cy)
        lineTo(cx, cy + h * 0.22f)
        lineTo(cx + w * 0.18f, cy + h * 0.30f)
    }
    drawPath(crack, color, style = Stroke(width = 2f, cap = StrokeCap.Round))
}

// 17. Swirl Vortex (Painter)
private fun DrawScope.drawSwirlVortex(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    val swirl = Path().apply {
        moveTo(cx, cy)
        var curR = 2f
        for (i in 0..30) {
            val angle = i * 0.35f
            val x = cx + cos(angle) * curR
            val y = cy + sin(angle) * curR
            if (i == 0) moveTo(x, y) else lineTo(x, y)
            curR += (w * 0.32f) / 30f
        }
    }
    drawPath(swirl, color, style = Stroke(width = 2.5f, cap = StrokeCap.Round))
}

// 18. Serpent Staff (Summoner)
private fun DrawScope.drawSerpentStaff(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    // Staff rod
    drawLine(color, Offset(cx, cy - h * 0.38f), Offset(cx, cy + h * 0.38f), strokeWidth = 3f)
    // Coiling serpent head
    val serpent = Path().apply {
        moveTo(cx, cy - h * 0.10f)
        quadraticBezierTo(cx + w * 0.25f, cy - h * 0.25f, cx, cy - h * 0.38f)
        quadraticBezierTo(cx - w * 0.22f, cy - h * 0.28f, cx, cy - h * 0.20f)
    }
    drawPath(serpent, color, style = Stroke(width = 2.5f, cap = StrokeCap.Round))
}

// 19. Chalice (Apostle)
private fun DrawScope.drawChalice(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    val cup = Path().apply {
        moveTo(cx - w * 0.25f, cy - h * 0.25f)
        lineTo(cx + w * 0.25f, cy - h * 0.25f)
        quadraticBezierTo(cx + w * 0.20f, cy + h * 0.05f, cx, cy + h * 0.12f)
        quadraticBezierTo(cx - w * 0.20f, cy + h * 0.05f, cx - w * 0.25f, cy - h * 0.25f)
        close()
    }
    drawPath(cup, color)
    // Stem and base
    drawLine(color, Offset(cx, cy + h * 0.12f), Offset(cx, cy + h * 0.32f), strokeWidth = 3f)
    drawLine(color, Offset(cx - w * 0.20f, cy + h * 0.32f), Offset(cx + w * 0.20f, cy + h * 0.32f), strokeWidth = 3.5f)
}

// 20. Hooded Mage (Warlock)
private fun DrawScope.drawHoodedMage(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    // Hooded silhouette
    val hood = Path().apply {
        moveTo(cx, cy - h * 0.36f)
        lineTo(cx + w * 0.15f, cy - h * 0.12f)
        lineTo(cx + w * 0.22f, cy + h * 0.36f)
        lineTo(cx - w * 0.22f, cy + h * 0.36f)
        lineTo(cx - w * 0.15f, cy - h * 0.12f)
        close()
    }
    drawPath(hood, color)
    // Staff in hand
    drawLine(color, Offset(cx + w * 0.24f, cy - h * 0.40f), Offset(cx + w * 0.24f, cy + h * 0.38f), strokeWidth = 2.5f)
}

// 21. Shadow Face (Dark Mage)
private fun DrawScope.drawShadowFace(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    val hoodOutline = Path().apply {
        moveTo(cx, cy - h * 0.38f)
        quadraticBezierTo(cx + w * 0.28f, cy - h * 0.10f, cx + w * 0.22f, cy + h * 0.35f)
        lineTo(cx + w * 0.12f, cy + h * 0.30f)
        lineTo(cx, cy + h * 0.38f)
        lineTo(cx - w * 0.12f, cy + h * 0.30f)
        lineTo(cx - w * 0.22f, cy + h * 0.35f)
        quadraticBezierTo(cx - w * 0.28f, cy - h * 0.10f, cx, cy - h * 0.38f)
    }
    drawPath(hoodOutline, color, style = Stroke(width = 3f))
    // Glowing sharp eye slits
    drawLine(color, Offset(cx - w * 0.12f, cy - h * 0.05f), Offset(cx - w * 0.04f, cy - h * 0.02f), strokeWidth = 2.5f)
    drawLine(color, Offset(cx + w * 0.12f, cy - h * 0.05f), Offset(cx + w * 0.04f, cy - h * 0.02f), strokeWidth = 2.5f)
}

// 22. Wizard Hat (Battle Mage)
private fun DrawScope.drawWizardHat(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    val hat = Path().apply {
        moveTo(cx, cy - h * 0.38f)
        lineTo(cx + w * 0.22f, cy + h * 0.18f)
        lineTo(cx - w * 0.22f, cy + h * 0.18f)
        close()
    }
    drawPath(hat, color)
    // Wide brim
    drawLine(color, Offset(cx - w * 0.38f, cy + h * 0.22f), Offset(cx + w * 0.38f, cy + h * 0.22f), strokeWidth = 3f)
}

// 23. Solar Corona (Elementalist)
private fun DrawScope.drawSolarCorona(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    drawCircle(color, radius = w * 0.16f, center = Offset(cx, cy))
    val rays = 12
    for (i in 0 until rays) {
        val angle = (i * 2 * PI / rays).toFloat()
        val s = w * 0.20f
        val e = if (i % 2 == 0) w * 0.38f else w * 0.28f
        drawLine(
            color,
            Offset(cx + cos(angle) * s, cy + sin(angle) * s),
            Offset(cx + cos(angle + 0.2f) * e, cy + sin(angle + 0.2f) * e),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
    }
}

// 24. Skull Visage (Necromancer)
private fun DrawScope.drawSkullVisage(cx: Float, cy: Float, w: Float, h: Float, color: Color) {
    // Upper skull dome
    drawArc(
        color = color,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(cx - w * 0.22f, cy - h * 0.32f),
        size = Size(w * 0.44f, h * 0.36f)
    )
    // Jaw and dripping cheek lines
    drawLine(color, Offset(cx - w * 0.16f, cy - h * 0.14f), Offset(cx - w * 0.16f, cy + h * 0.34f), strokeWidth = 3f)
    drawLine(color, Offset(cx + w * 0.16f, cy - h * 0.14f), Offset(cx + w * 0.16f, cy + h * 0.34f), strokeWidth = 3f)
    drawLine(color, Offset(cx, cy - h * 0.10f), Offset(cx, cy + h * 0.28f), strokeWidth = 2.5f)
    // Eye sockets (cutout black)
    drawCircle(Color.Black, radius = 4.5f, center = Offset(cx - w * 0.08f, cy - h * 0.18f))
    drawCircle(Color.Black, radius = 4.5f, center = Offset(cx + w * 0.08f, cy - h * 0.18f))
}
