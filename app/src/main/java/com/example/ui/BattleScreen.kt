package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameUIState
import com.example.game.GameViewModel
import com.example.model.BlankScrollDrop
import com.example.model.BrokenStoneEntity
import com.example.model.DamageNumber
import com.example.model.Enemy
import com.example.model.EnemyType
import com.example.model.InkPuddle
import com.example.model.InkProjectile
import com.example.model.InkwellVortexEntity
import com.example.model.MagnumOpusVisual
import com.example.model.ObeliskEntity
import com.example.model.Orb
import com.example.model.RedRuneEntity
import com.example.model.SpellRuneDrop
import com.example.model.WashBrushArcVisual
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// High Contrast Theme Colors for "The Scratchpad"
val ScratchpadVoid = Color(0xFFFFFFFF)       // Pure blank white void
val ScratchpadGrid = Color(0xFFEDEDED)       // Faint scratchpad ruled lines
val StarkBlackInk = Color(0xFF000000)        // Strictly stark black ink for player attacks
val PaperConstructFill = Color(0xFFF9F9F9)   // Contrasting white paper
val PaperConstructOutline = Color(0xFF1A1A1A) // High contrast outline
val NeonPinkCore = Color(0xFFFF007F)         // High-readability enemy eye slit
val NeonCyanEdge = Color(0xFF00E5FF)         // Crisp edge crease
val DamageFlash = Color(0xFFFF3333)

// Red Rune and Elite Colors (High Readability)
val EliteCrimsonFill = Color(0xFFFFEBEE)
val EliteCrimsonOutline = Color(0xFFB71C1C)
val EliteBloodEye = Color(0xFFFF1744)
val RedRuneColor = Color(0xFFD50000)

@Composable
fun BattleScreen(
    viewModel: GameViewModel,
    uiState: GameUIState,
    onPauseClick: () -> Unit
) {
    // 60fps frame loop
    var lastNano by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { nowNano ->
                if (lastNano != 0f) {
                    val dt = (nowNano - lastNano) / 1_000_000_000f
                    viewModel.updateGame(dt)
                }
                lastNano = nowNano.toFloat()
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(ScratchpadVoid)
    ) {
        val screenWidth = constraints.maxWidth.toFloat()
        val screenHeight = constraints.maxHeight.toFloat()
        val screenCenterX = screenWidth / 2f
        val screenCenterY = screenHeight / 2f

        // Camera: 2D top-down perspective, locked to player character
        val camX = screenCenterX - viewModel.player.x
        val camY = screenCenterY - viewModel.player.y

        // Custom Game Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("battle_game_canvas")
        ) {
            // 1. Draw The Scratchpad grid
            drawScratchpadBackground(camX, camY)

            // 2. Draw Ink Puddles (The Puddle System: static 2D decals)
            for (puddle in viewModel.puddlePool.pool) {
                if (puddle.active) {
                    drawInkPuddleDecal(puddle, camX, camY)
                }
            }

            // 2.1 Draw Inkwell Vortexes (Phase 4 Gravitational Suction)
            for (vortex in viewModel.inkwellVortexes) {
                drawInkwellVortex(vortex, camX, camY)
            }

            // 3. Draw Wash Brush Sweep Arc Visuals
            for (visual in viewModel.washBrushVisuals) {
                drawWashBrushSweep(visual, camX, camY)
            }

            // 4. Draw Red Runes (Opt-In Elites Spawner)
            for (rune in viewModel.redRunes) {
                drawRedRune(rune, camX, camY)
            }

            // 4.1 Draw Obelisks
            for (obelisk in viewModel.obelisks) {
                drawObelisk(obelisk, camX, camY)
            }

            // 4.2 Draw Broken Stones
            for (stone in viewModel.brokenStones) {
                drawBrokenStone(stone, camX, camY)
            }

            // 5. Draw Spell Rune Drops (dropped by last Elite)
            for (drop in viewModel.spellRuneDrops) {
                drawSpellRuneDrop(drop, camX, camY)
            }

            // 5.1 Draw Blank Scroll Drops (Phase 4 Mid-Boss 25:00)
            for (scroll in viewModel.blankScrollDrops) {
                drawBlankScrollDrop(scroll, camX, camY)
            }

            // 6. Draw Orbs
            for (orb in viewModel.orbs) {
                drawOrb(orb, camX, camY)
            }

            // 7. Draw Enemies & Elites
            for (enemy in viewModel.enemies) {
                drawEnemy(enemy, camX, camY)
            }

            // 8. Draw Player Attacks (Quill Darts & Evolved Spells)
            for (proj in viewModel.inkProjectiles) {
                drawQuillDart(proj, camX, camY)
            }

            // 9. Draw Player Character (Scribe / Painter)
            drawPlayerCharacter(
                x = viewModel.player.x + camX,
                y = viewModel.player.y + camY,
                facingAngle = viewModel.player.lastMoveDirection,
                isInvincible = viewModel.player.isInvincible,
                isPainter = uiState.playerClass.id == "painter"
            )

            // 10. Draw Damage Numbers
            for (dn in viewModel.damageNumbers) {
                drawDamageNumber(dn, camX, camY)
            }

            // 11. Draw Level 100 Magnum Opus Ultimate Calligraphy Stroke Effect
            if (viewModel.magnumOpusVisual.active) {
                drawMagnumOpusEffect(viewModel.magnumOpusVisual)
            }
        }

        // Virtual Analog Stick (Movement ONLY, no attack button)
        VirtualAnalogStick(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 28.dp, bottom = 42.dp),
            onMove = { offset ->
                viewModel.joystickVector = offset
            }
        )

        // Top HUD Overlay
        BattleHUD(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            uiState = uiState,
            player = viewModel.player,
            onPauseClick = onPauseClick
        )
    }
}

// ---------------- CANVAS DRAWING HELPERS ----------------

private fun DrawScope.drawScratchpadBackground(camX: Float, camY: Float) {
    val spacing = 64f
    val offsetX = (camX % spacing) - spacing
    val offsetY = (camY % spacing) - spacing

    var x = offsetX
    while (x < size.width + spacing) {
        drawLine(
            color = ScratchpadGrid,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = 1f
        )
        x += spacing
    }

    var y = offsetY
    while (y < size.height + spacing) {
        drawLine(
            color = ScratchpadGrid,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1f
        )
        y += spacing
    }
}

// The Puddle System: static 2D ink puddle decals
private fun DrawScope.drawInkPuddleDecal(puddle: InkPuddle, camX: Float, camY: Float) {
    val px = puddle.x + camX
    val py = puddle.y + camY
    val r = puddle.radius
    val lifeRatio = (1f - (puddle.life / puddle.maxLife)).coerceIn(0.15f, 1f)

    drawCircle(
        color = StarkBlackInk.copy(alpha = 0.25f * lifeRatio),
        radius = r,
        center = Offset(px, py)
    )
    drawCircle(
        color = StarkBlackInk.copy(alpha = 0.65f * lifeRatio),
        radius = r * 0.65f,
        center = Offset(px, py)
    )
    drawCircle(
        color = StarkBlackInk.copy(alpha = 0.85f * lifeRatio),
        radius = r * 0.28f,
        center = Offset(px, py)
    )
}

// Wash Brush sweep arc visual
private fun DrawScope.drawWashBrushSweep(visual: WashBrushArcVisual, camX: Float, camY: Float) {
    val vx = visual.x + camX
    val vy = visual.y + camY
    val alpha = (visual.life / 0.22f).coerceIn(0.1f, 0.8f)

    val sweepAngleDegrees = (visual.arcSpanRad * 180f / PI).toFloat()
    val startAngleDegrees = ((visual.angleRad - visual.arcSpanRad / 2f) * 180f / PI).toFloat()

    drawArc(
        color = StarkBlackInk.copy(alpha = alpha * 0.45f),
        startAngle = startAngleDegrees,
        sweepAngle = sweepAngleDegrees,
        useCenter = true,
        topLeft = Offset(vx - visual.radius, vy - visual.radius),
        size = androidx.compose.ui.geometry.Size(visual.radius * 2f, visual.radius * 2f)
    )

    drawArc(
        color = StarkBlackInk.copy(alpha = alpha),
        startAngle = startAngleDegrees,
        sweepAngle = sweepAngleDegrees,
        useCenter = false,
        topLeft = Offset(vx - visual.radius, vy - visual.radius),
        size = androidx.compose.ui.geometry.Size(visual.radius * 2f, visual.radius * 2f),
        style = Stroke(width = 8f, cap = StrokeCap.Round)
    )
}

// Red Rune: Opt-In Elites interactable object
private fun DrawScope.drawRedRune(rune: RedRuneEntity, camX: Float, camY: Float) {
    val rx = rune.x + camX
    val ry = rune.y + camY
    val pulse = sin(rune.pulseTimer * 4f) * 4f

    drawCircle(
        color = RedRuneColor.copy(alpha = 0.25f),
        radius = rune.radius + 10f + pulse,
        center = Offset(rx, ry)
    )
    val path = Path().apply {
        moveTo(rx, ry - rune.radius)
        lineTo(rx + rune.radius, ry)
        lineTo(rx, ry + rune.radius)
        lineTo(rx - rune.radius, ry)
        close()
    }
    drawPath(path = path, color = RedRuneColor)
    drawPath(path = path, color = Color.White, style = Stroke(width = 2.5f))
    drawCircle(color = Color.White, radius = 5f, center = Offset(rx, ry))
}

// Obelisk: Spawns upon defeating an Elite pack from Red Rune
private fun DrawScope.drawObelisk(obelisk: ObeliskEntity, camX: Float, camY: Float) {
    val ox = obelisk.x + camX
    val oy = obelisk.y + camY
    val pulse = sin(obelisk.pulseTimer * 3.5f) * 3f
    val r = obelisk.radius

    drawCircle(
        color = Color(0xFF673AB7).copy(alpha = 0.25f),
        radius = r + 12f + pulse,
        center = Offset(ox, oy)
    )
    val path = Path().apply {
        moveTo(ox, oy - r * 1.5f)
        lineTo(ox + r * 0.7f, oy - r * 0.3f)
        lineTo(ox + r * 0.8f, oy + r * 1.2f)
        lineTo(ox - r * 0.8f, oy + r * 1.2f)
        lineTo(ox - r * 0.7f, oy - r * 0.3f)
        close()
    }
    drawPath(path = path, color = Color(0xFF1E1E24))
    drawPath(path = path, color = Color(0xFF9575CD), style = Stroke(width = 3f))
    drawCircle(color = Color(0xFFD1C4E9), radius = 6f, center = Offset(ox, oy))
}

// Broken Stone: In-run shop offering lower-tier artifacts for Orbs
private fun DrawScope.drawBrokenStone(stone: BrokenStoneEntity, camX: Float, camY: Float) {
    val sx = stone.x + camX
    val sy = stone.y + camY
    val pulse = sin(stone.pulseTimer * 4f) * 2f
    val r = stone.radius

    drawCircle(
        color = Color(0xFF00796B).copy(alpha = 0.22f),
        radius = r + 8f + pulse,
        center = Offset(sx, sy)
    )
    val path = Path().apply {
        moveTo(sx - r * 0.8f, sy - r * 0.7f)
        lineTo(sx + r * 0.4f, sy - r * 0.9f)
        lineTo(sx + r * 0.9f, sy + r * 0.2f)
        lineTo(sx + r * 0.6f, sy + r * 0.9f)
        lineTo(sx - r * 0.7f, sy + r * 0.8f)
        close()
    }
    drawPath(path = path, color = Color(0xFF263238))
    drawPath(path = path, color = Color(0xFF80CBC4), style = Stroke(width = 2.5f))
    drawLine(Color(0xFF80CBC4), Offset(sx - r * 0.3f, sy - r * 0.6f), Offset(sx + 2f, sy + 3f), strokeWidth = 2f)
    drawLine(Color(0xFF80CBC4), Offset(sx + 2f, sy + 3f), Offset(sx + r * 0.5f, sy + r * 0.7f), strokeWidth = 2f)
}

// Spell Rune Drop (Dropped by the 6th Elite)
private fun DrawScope.drawSpellRuneDrop(drop: SpellRuneDrop, camX: Float, camY: Float) {
    val dx = drop.x + camX
    val dy = drop.y + camY
    val pulse = sin(drop.pulseTimer * 5f) * 3f

    drawCircle(
        color = Color(0xFFFFB300).copy(alpha = 0.35f),
        radius = drop.radius + 8f + pulse,
        center = Offset(dx, dy)
    )
    val path = Path().apply {
        moveTo(dx, dy - drop.radius)
        lineTo(dx + drop.radius * 0.9f, dy)
        lineTo(dx, dy + drop.radius)
        lineTo(dx - drop.radius * 0.9f, dy)
        close()
    }
    drawPath(path = path, color = Color(0xFFD32F2F))
    drawPath(path = path, color = Color(0xFFFFD54F), style = Stroke(width = 2f))
    drawCircle(color = Color.White, radius = 4f, center = Offset(dx, dy))
}

private fun DrawScope.drawOrb(orb: Orb, camX: Float, camY: Float) {
    val ox = orb.x + camX
    val oy = orb.y + camY

    if (orb.isCondensed) {
        val pulse = sin(orb.pulseTimer * 6f) * 3f
        drawCircle(
            color = Color(0xFFFFD54F).copy(alpha = 0.4f),
            radius = 18f + pulse,
            center = Offset(ox, oy)
        )
        drawCircle(
            color = Color(0xFFFF8F00),
            radius = 12f,
            center = Offset(ox, oy)
        )
        drawCircle(
            color = StarkBlackInk,
            radius = 8f,
            center = Offset(ox, oy)
        )
        drawCircle(
            color = Color.White,
            radius = 3f,
            center = Offset(ox - 2f, oy - 2f)
        )
    } else {
        drawCircle(
            color = StarkBlackInk,
            radius = 6.5f,
            center = Offset(ox, oy)
        )
        drawCircle(
            color = Color.White,
            radius = 2f,
            center = Offset(ox - 1.5f, oy - 1.5f)
        )
    }
}

private fun DrawScope.drawEnemy(enemy: Enemy, camX: Float, camY: Float) {
    val ex = enemy.x + camX
    val ey = enemy.y + camY
    val r = if (enemy.isElite) enemy.type.radius * 1.35f else enemy.type.radius

    val isFlashing = enemy.flashTimer > 0f
    val fillColor = when {
        isFlashing -> DamageFlash
        enemy.isElite -> EliteCrimsonFill
        else -> PaperConstructFill
    }
    val outlineColor = if (enemy.isElite) EliteCrimsonOutline else PaperConstructOutline
    val eyeColor = if (enemy.isElite) EliteBloodEye else NeonPinkCore
    val outlineWidth = if (enemy.isElite) 4.5f else 3f

    when (enemy.type) {
        EnemyType.BASIC_CONSTRUCT -> {
            val path = Path().apply {
                moveTo(ex, ey - r)
                lineTo(ex + r, ey)
                lineTo(ex, ey + r)
                lineTo(ex - r, ey)
                close()
            }
            drawPath(path = path, color = fillColor)
            drawPath(path = path, color = outlineColor, style = Stroke(width = outlineWidth))
            drawLine(outlineColor, Offset(ex, ey - r), Offset(ex, ey + r), strokeWidth = 1.5f)
            drawCircle(color = eyeColor, radius = if (enemy.isElite) 5.5f else 3.5f, center = Offset(ex, ey))
        }

        EnemyType.FOLDED_STALKER -> {
            val path = Path().apply {
                moveTo(ex, ey - r * 1.3f)
                lineTo(ex + r * 1.2f, ey + r * 0.9f)
                lineTo(ex, ey + r * 0.4f)
                lineTo(ex - r * 1.2f, ey + r * 0.9f)
                close()
            }
            drawPath(path = path, color = fillColor)
            drawPath(path = path, color = outlineColor, style = Stroke(width = outlineWidth))
            drawCircle(color = eyeColor, radius = if (enemy.isElite) 6f else 4f, center = Offset(ex, ey - r * 0.3f))
        }

        EnemyType.PAPER_BRUTE -> {
            val path = Path().apply {
                moveTo(ex, ey - r)
                lineTo(ex + r * 0.9f, ey - r * 0.5f)
                lineTo(ex + r * 0.9f, ey + r * 0.5f)
                lineTo(ex, ey + r)
                lineTo(ex - r * 0.9f, ey + r * 0.5f)
                lineTo(ex - r * 0.9f, ey - r * 0.5f)
                close()
            }
            drawPath(path = path, color = fillColor)
            drawPath(path = path, color = outlineColor, style = Stroke(width = outlineWidth + 1f))
            drawCircle(color = eyeColor, radius = 7f, center = Offset(ex, ey))
        }

        EnemyType.THE_TITAN -> {
            val path = Path().apply {
                moveTo(ex, ey - r)
                lineTo(ex + r * 0.7f, ey - r * 0.6f)
                lineTo(ex + r, ey)
                lineTo(ex + r * 0.8f, ey + r * 0.7f)
                lineTo(ex, ey + r)
                lineTo(ex - r * 0.8f, ey + r * 0.7f)
                lineTo(ex - r, ey)
                lineTo(ex - r * 0.7f, ey - r * 0.6f)
                close()
            }
            drawPath(path = path, color = fillColor)
            drawPath(path = path, color = outlineColor, style = Stroke(width = outlineWidth + 2f))

            drawLine(outlineColor, Offset(ex, ey - r), Offset(ex, ey + r), strokeWidth = 2f)
            drawLine(outlineColor, Offset(ex - r, ey), Offset(ex + r, ey), strokeWidth = 2f)
            drawLine(outlineColor, Offset(ex - r * 0.7f, ey - r * 0.6f), Offset(ex + r * 0.7f, ey + r * 0.7f), strokeWidth = 1.5f)
            drawLine(outlineColor, Offset(ex + r * 0.7f, ey - r * 0.6f), Offset(ex - r * 0.7f, ey + r * 0.7f), strokeWidth = 1.5f)

            // Boss Core & Eye
            drawCircle(color = outlineColor, radius = 14f, center = Offset(ex, ey))
            drawCircle(color = eyeColor, radius = 9f, center = Offset(ex, ey))
            drawCircle(color = Color.White, radius = 3.5f, center = Offset(ex - 2f, ey - 2f))

            // Boss Health Bar above Titan
            val barW = 110f
            val barH = 10f
            val barY = ey - r - 22f
            drawRect(
                color = Color(0x88000000),
                topLeft = Offset(ex - barW / 2f, barY),
                size = androidx.compose.ui.geometry.Size(barW, barH)
            )
            val hpRatio = (enemy.hp / enemy.maxHp).coerceIn(0f, 1f)
            drawRect(
                color = Color(0xFFD32F2F),
                topLeft = Offset(ex - barW / 2f, barY),
                size = androidx.compose.ui.geometry.Size(barW * hpRatio, barH)
            )
        }

        EnemyType.MID_BOSS_COLOSSUS -> {
            val path = Path().apply {
                moveTo(ex, ey - r)
                lineTo(ex + r * 0.85f, ey - r * 0.5f)
                lineTo(ex + r * 0.9f, ey + r * 0.5f)
                lineTo(ex, ey + r * 1.05f)
                lineTo(ex - r * 0.9f, ey + r * 0.5f)
                lineTo(ex - r * 0.85f, ey - r * 0.5f)
                close()
            }
            drawPath(path = path, color = fillColor)
            drawPath(path = path, color = outlineColor, style = Stroke(width = outlineWidth + 2.5f))

            // Scroll bindings & rune lines
            drawLine(outlineColor, Offset(ex - r * 0.7f, ey - r * 0.2f), Offset(ex + r * 0.7f, ey - r * 0.2f), strokeWidth = 2.5f)
            drawLine(outlineColor, Offset(ex - r * 0.6f, ey + r * 0.3f), Offset(ex + r * 0.6f, ey + r * 0.3f), strokeWidth = 2.5f)
            drawCircle(color = Color(0xFF00E676), radius = 11f, center = Offset(ex, ey - 4f))
            drawCircle(color = Color.White, radius = 4f, center = Offset(ex - 2f, ey - 6f))

            // Boss Health Bar
            val barW = 120f
            val barH = 10f
            val barY = ey - r - 24f
            drawRect(color = Color(0x88000000), topLeft = Offset(ex - barW / 2f, barY), size = androidx.compose.ui.geometry.Size(barW, barH))
            val hpRatio = (enemy.hp / enemy.maxHp).coerceIn(0f, 1f)
            drawRect(color = Color(0xFF00E676), topLeft = Offset(ex - barW / 2f, barY), size = androidx.compose.ui.geometry.Size(barW * hpRatio, barH))
            drawRect(color = Color.White, topLeft = Offset(ex - barW / 2f, barY), size = androidx.compose.ui.geometry.Size(barW, barH), style = Stroke(width = 1.5f))
        }
    }

    // Bleed effect visual indicator
    if (enemy.bleedTimer > 0f) {
        drawCircle(
            color = Color(0xFFD32F2F).copy(alpha = 0.6f),
            radius = r * 1.2f,
            center = Offset(ex, ey),
            style = Stroke(width = 2f)
        )
    }

    // Viscous slow effect visual indicator
    if (enemy.slowTimer > 0f) {
        drawCircle(
            color = StarkBlackInk.copy(alpha = 0.5f),
            radius = r * 1.15f,
            center = Offset(ex, ey + 4f),
            style = Stroke(width = 2.5f)
        )
    }
}

private fun DrawScope.drawQuillDart(dart: InkProjectile, camX: Float, camY: Float) {
    val dx = dart.x + camX
    val dy = dart.y + camY
    val angleDegrees = (dart.angleRad * 180f / PI).toFloat()

    rotate(degrees = angleDegrees, pivot = Offset(dx, dy)) {
        when {
            dart.isUltimate -> {
                // Magnum Opus: The Master's Decree - Cosmic Gold-Black Calligraphy Spear
                val spearLen = dart.strokeLength * 2.0f
                val spearWidth = dart.strokeWidth * 2.2f
                val path = Path().apply {
                    moveTo(dx + spearLen * 0.8f, dy)
                    lineTo(dx + spearLen * 0.3f, dy - spearWidth * 0.5f)
                    lineTo(dx - spearLen * 0.6f, dy - spearWidth * 0.2f)
                    lineTo(dx - spearLen * 0.6f, dy + spearWidth * 0.2f)
                    lineTo(dx + spearLen * 0.3f, dy + spearWidth * 0.5f)
                    close()
                }
                drawPath(path = path, color = Color(0xFFFFD700))
                drawPath(path = path, color = StarkBlackInk, style = Stroke(width = 3.5f))
                drawLine(
                    color = Color(0xFFFFD700).copy(alpha = 0.7f),
                    start = Offset(dx - spearLen * 0.6f, dy),
                    end = Offset(dx - spearLen * 1.5f, dy),
                    strokeWidth = 6f,
                    cap = StrokeCap.Round
                )
            }

            dart.isHarpoon -> {
                // Massive, high-velocity ink spear
                val spearLen = dart.strokeLength * 1.6f
                val spearWidth = dart.strokeWidth * 1.8f
                val path = Path().apply {
                    moveTo(dx + spearLen * 0.7f, dy)
                    lineTo(dx + spearLen * 0.2f, dy - spearWidth * 0.5f)
                    lineTo(dx - spearLen * 0.5f, dy - spearWidth * 0.2f)
                    lineTo(dx - spearLen * 0.5f, dy + spearWidth * 0.2f)
                    lineTo(dx + spearLen * 0.2f, dy + spearWidth * 0.5f)
                    close()
                }
                drawPath(path = path, color = StarkBlackInk)
                drawLine(
                    color = StarkBlackInk.copy(alpha = 0.5f),
                    start = Offset(dx - spearLen * 0.5f, dy),
                    end = Offset(dx - spearLen * 1.1f, dy),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
            }

            dart.isBarrage -> {
                // Sleek silver-black ink needle
                val needleLen = dart.strokeLength * 1.1f
                val needleWidth = dart.strokeWidth * 0.8f
                val path = Path().apply {
                    moveTo(dx + needleLen * 0.6f, dy)
                    lineTo(dx - needleLen * 0.4f, dy - needleWidth * 0.5f)
                    lineTo(dx - needleLen * 0.4f, dy + needleWidth * 0.5f)
                    close()
                }
                drawPath(path = path, color = StarkBlackInk)
                drawCircle(color = Color(0xFF00E5FF), radius = 2.5f, center = Offset(dx + needleLen * 0.6f, dy))
            }

            else -> {
                val path = Path().apply {
                    moveTo(dx + dart.strokeLength * 0.6f, dy)
                    lineTo(dx - dart.strokeLength * 0.4f, dy - dart.strokeWidth * 0.5f)
                    lineTo(dx - dart.strokeLength * 0.2f, dy)
                    lineTo(dx - dart.strokeLength * 0.4f, dy + dart.strokeWidth * 0.5f)
                    close()
                }
                drawPath(path = path, color = StarkBlackInk)
            }
        }
    }

    val tailX = dx - cos(dart.angleRad) * (dart.strokeLength * 0.5f)
    val tailY = dy - sin(dart.angleRad) * (dart.strokeLength * 0.5f)
    drawCircle(
        color = if (dart.isUltimate) Color(0xFFFFD700) else StarkBlackInk.copy(alpha = 0.65f),
        radius = if (dart.isUltimate) 7f else if (dart.isHarpoon) 5f else 2.5f,
        center = Offset(tailX, tailY)
    )
}

// Draw Inkwell Vortex (Evolved Wash Brush)
private fun DrawScope.drawInkwellVortex(vortex: InkwellVortexEntity, camX: Float, camY: Float) {
    val vx = vortex.x + camX
    val vy = vortex.y + camY
    val r = vortex.radius

    drawCircle(
        color = StarkBlackInk.copy(alpha = 0.15f),
        radius = r,
        center = Offset(vx, vy)
    )

    // Swirling vortex arms
    for (i in 0 until 4) {
        val armAngle = vortex.angle + (i * PI / 2).toFloat()
        val startX = vx + cos(armAngle) * (r * 0.2f)
        val startY = vy + sin(armAngle) * (r * 0.2f)
        val endX = vx + cos(armAngle + 1.2f) * r
        val endY = vy + sin(armAngle + 1.2f) * r
        drawLine(
            color = StarkBlackInk.copy(alpha = 0.45f),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 5f,
            cap = StrokeCap.Round
        )
    }

    drawCircle(
        color = StarkBlackInk,
        radius = 16f,
        center = Offset(vx, vy)
    )
    drawCircle(
        color = Color(0xFFC5A059),
        radius = 6f,
        center = Offset(vx, vy)
    )
}

// Draw Blank Scroll Drop (Mid-Boss 25:00)
private fun DrawScope.drawBlankScrollDrop(drop: BlankScrollDrop, camX: Float, camY: Float) {
    val sx = drop.x + camX
    val sy = drop.y + camY
    val pulse = sin(drop.pulseTimer * 4.5f) * 3f

    drawCircle(
        color = Color(0xFFEEEEEE).copy(alpha = 0.4f),
        radius = drop.radius + 8f + pulse,
        center = Offset(sx, sy)
    )

    // Unfurled paper scroll icon
    val path = Path().apply {
        moveTo(sx - 12f, sy - 16f)
        lineTo(sx + 12f, sy - 16f)
        lineTo(sx + 14f, sy + 14f)
        lineTo(sx - 10f, sy + 16f)
        close()
    }
    drawPath(path = path, color = Color(0xFFF5F5F5))
    drawPath(path = path, color = StarkBlackInk, style = Stroke(width = 2.5f))
    // Red ribbon seal on scroll
    drawCircle(color = Color(0xFFD32F2F), radius = 4f, center = Offset(sx, sy))
}

// Draw Level 100 Magnum Opus Ultimate Calligraphy Stroke Effect
private fun DrawScope.drawMagnumOpusEffect(visual: MagnumOpusVisual) {
    // Stark white screen flash
    if (visual.flashAlpha > 0.05f) {
        drawRect(color = Color.White.copy(alpha = visual.flashAlpha))
    }

    val progress = (visual.timer / visual.maxDuration).coerceIn(0f, 1f)
    val alpha = (1f - progress).coerceIn(0.2f, 1f)

    // Giant Calligraphy Kanji Brush Stroke across screen ("筆" / Divine Calligraphy)
    val startX = size.width * 0.15f
    val startY = size.height * 0.20f
    val endX = size.width * 0.85f
    val endY = size.height * 0.80f

    // Thick black stroke path with organic calligraphy taper
    val strokePath = Path().apply {
        moveTo(startX, startY)
        quadraticTo(
            size.width * 0.45f, size.height * 0.40f,
            size.width * 0.50f, size.height * 0.50f
        )
        quadraticTo(
            size.width * 0.55f, size.height * 0.60f,
            endX, endY
        )
    }

    drawPath(
        path = strokePath,
        color = StarkBlackInk.copy(alpha = alpha),
        style = Stroke(width = 28f, cap = StrokeCap.Round)
    )

    // Cross-stroke
    val crossStart = Offset(size.width * 0.70f, size.height * 0.25f)
    val crossEnd = Offset(size.width * 0.30f, size.height * 0.75f)
    drawLine(
        color = StarkBlackInk.copy(alpha = alpha),
        start = crossStart,
        end = crossEnd,
        strokeWidth = 20f,
        cap = StrokeCap.Round
    )

    // Crimson seal stamp "墨神"
    val stampCenter = Offset(size.width * 0.5f, size.height * 0.48f)
    drawRect(
        color = Color(0xFFD32F2F).copy(alpha = alpha),
        topLeft = Offset(stampCenter.x - 22f, stampCenter.y - 22f),
        size = androidx.compose.ui.geometry.Size(44f, 44f)
    )
    drawRect(
        color = Color.White.copy(alpha = alpha),
        topLeft = Offset(stampCenter.x - 20f, stampCenter.y - 20f),
        size = androidx.compose.ui.geometry.Size(40f, 40f),
        style = Stroke(width = 2f)
    )
}

private fun DrawScope.drawPlayerCharacter(
    x: Float,
    y: Float,
    facingAngle: Float,
    isInvincible: Boolean,
    isPainter: Boolean
) {
    val alpha = if (isInvincible) 0.5f else 1f

    drawCircle(
        color = StarkBlackInk.copy(alpha = 0.15f * alpha),
        radius = 24f,
        center = Offset(x, y + 4f)
    )

    val ribbonAngle = facingAngle + PI.toFloat()
    val ribbonEnd = Offset(x + cos(ribbonAngle) * 30f, y + sin(ribbonAngle) * 30f)
    drawLine(
        color = StarkBlackInk.copy(alpha = alpha),
        start = Offset(x, y),
        end = ribbonEnd,
        strokeWidth = 6f,
        cap = StrokeCap.Round
    )

    drawCircle(
        color = StarkBlackInk.copy(alpha = alpha),
        radius = 18f,
        center = Offset(x, y)
    )

    drawCircle(
        color = Color(0xFF262626).copy(alpha = alpha),
        radius = 13f,
        center = Offset(x, y - 3f)
    )

    if (isPainter) {
        val brushTip = Offset(x + cos(facingAngle) * 28f, y + sin(facingAngle) * 28f)
        drawLine(
            color = StarkBlackInk.copy(alpha = alpha),
            start = Offset(x, y),
            end = brushTip,
            strokeWidth = 7f,
            cap = StrokeCap.Round
        )
    } else {
        val quillTip = Offset(x + cos(facingAngle) * 26f, y + sin(facingAngle) * 26f)
        drawLine(
            color = StarkBlackInk.copy(alpha = alpha),
            start = Offset(x, y),
            end = quillTip,
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawDamageNumber(dn: DamageNumber, camX: Float, camY: Float) {
    val nx = dn.x + camX
    val ny = dn.y + camY
    val alpha = (dn.life / 0.8f).coerceIn(0f, 1f)

    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.argb(
                (alpha * 255).toInt(),
                (dn.color.red * 255).toInt(),
                (dn.color.green * 255).toInt(),
                (dn.color.blue * 255).toInt()
            )
            textSize = 24f
            isFakeBoldText = true
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            textAlign = android.graphics.Paint.Align.CENTER
        }
        drawText(dn.text, nx, ny, paint)
    }
}

// ---------------- MOVEMENT ONLY INPUT ----------------

@Composable
fun VirtualAnalogStick(
    modifier: Modifier = Modifier,
    onMove: (Offset) -> Unit
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .size(150.dp)
            .testTag("virtual_analog_stick")
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val delta = offset - center
                        val maxPx = 65f * density
                        val dist = delta.getDistance()
                        val clamped = if (dist > maxPx) delta * (maxPx / dist) else delta
                        dragOffset = clamped
                        onMove(clamped / maxPx)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newOffset = dragOffset + dragAmount
                        val maxPx = 65f * density
                        val dist = newOffset.getDistance()
                        val clamped = if (dist > maxPx) newOffset * (maxPx / dist) else newOffset
                        dragOffset = clamped
                        onMove(clamped / maxPx)
                    },
                    onDragEnd = {
                        dragOffset = Offset.Zero
                        onMove(Offset.Zero)
                    },
                    onDragCancel = {
                        dragOffset = Offset.Zero
                        onMove(Offset.Zero)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(130.dp)
                .background(Color(0xFFF0F0F0), CircleShape)
                .border(2.dp, Color(0xFFCCCCCC), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(StarkBlackInk, CircleShape)
                .border(2.dp, Color.White, CircleShape)
                .shadow(4.dp, CircleShape)
        )
    }
}

// ---------------- HUD OVERLAY ----------------

@Composable
fun BattleHUD(
    modifier: Modifier = Modifier,
    uiState: GameUIState,
    player: com.example.game.PlayerState,
    onPauseClick: () -> Unit
) {
    val minutes = (uiState.timeSurvivedSeconds / 60).toInt()
    val seconds = (uiState.timeSurvivedSeconds % 60).toInt()
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)
    val xpRatio = (player.xp.toFloat() / player.xpNeeded).coerceIn(0f, 1f)
    val hpRatio = (player.hp / player.maxHp).coerceIn(0f, 1f)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFFE5E5E5))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(xpRatio)
                        .height(10.dp)
                        .background(StarkBlackInk)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "LVL ${player.level}",
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = StarkBlackInk
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFDDDDDD))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${uiState.character.name}  (${uiState.playerClass.name})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = StarkBlackInk
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "HP ${player.hp.toInt()}/${player.maxHp.toInt()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (hpRatio < 0.3f) DamageFlash else StarkBlackInk
                        )
                        if (player.armor > 0f) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Armor",
                                    tint = Color(0xFF555555),
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "${player.armor.toInt()}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF555555)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    LinearProgressIndicator(
                        progress = { hpRatio },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = StarkBlackInk,
                        trackColor = Color(0xFFE5E5E5)
                    )
                }

                // Survival Time & Kills
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = timeFormatted,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = StarkBlackInk
                    )
                    Text(
                        text = "☠ ${uiState.kills}",
                        fontSize = 11.sp,
                        color = Color(0xFF666666)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(
                    onClick = onPauseClick,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("pause_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Pause",
                        tint = StarkBlackInk
                    )
                }
            }
        }
    }
}
