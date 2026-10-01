package com.example.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.IntOffset
import com.example.model.CinnabarSealEntity
import com.example.model.OrbitalRuneEntity
import kotlin.math.roundToInt
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
import com.example.model.BlotterBurstVisual
import com.example.model.BrokenStoneEntity
import com.example.model.DamageNumber
import com.example.model.Enemy
import com.example.model.EnemyType
import com.example.model.InkPuddle
import com.example.model.InkProjectile
import com.example.model.InkwellStructure
import com.example.model.InkwellVortexEntity
import com.example.model.MagnumOpusVisual
import com.example.model.ObeliskEntity
import com.example.model.Orb
import com.example.model.RedRuneEntity
import com.example.model.SpellRuneDrop
import com.example.model.TelegraphedStrike
import com.example.model.TheEraserBoss
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

        // Camera: 2D top-down perspective, locked to player character + screen shake
        val shakeMag = uiState.screenShakeTimer * 12f
        val shakeX = if (shakeMag > 0f) sin(uiState.timeSurvivedSeconds * 50f) * shakeMag else 0f
        val shakeY = if (shakeMag > 0f) cos(uiState.timeSurvivedSeconds * 45f) * shakeMag else 0f

        val camX = screenCenterX - viewModel.player.x + shakeX
        val camY = screenCenterY - viewModel.player.y + shakeY

        // Custom Game Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("battle_game_canvas")
        ) {
            // 1. Draw The Scratchpad grid
            drawScratchpadBackground(camX, camY)

            // 1.1 Draw The Eraser Arena Boundary & Void (Phase 5 Climax)
            if (viewModel.theEraserBoss.active) {
                drawEraserArena(viewModel.theEraserBoss, camX, camY)
            }

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

            // 4.3 Draw The Sacred Inkwell Structure (Phase 5 Minute 30:00 Checkpoint)
            for (inkwell in viewModel.inkwellStructures) {
                drawInkwell(inkwell, camX, camY)
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

            // 7. Draw Enemies, Elites & The Blotter
            for (enemy in viewModel.enemies) {
                drawEnemy(enemy, camX, camY, uiState.timeSurvivedSeconds)
            }

            // 7.1 Draw Blotter Burst AOE Visuals
            for (burst in viewModel.blotterBurstVisuals) {
                drawBlotterBurst(burst, camX, camY)
            }

            // 7.2 Draw Cinnabar Seals (Detonation Glyphs)
            for (seal in viewModel.cinnabarSeals) {
                drawCinnabarSeal(seal, camX, camY)
            }

            // 7.3 Draw Orbital Runes (Celestial Orbit)
            for (rune in viewModel.orbitalRunes) {
                drawOrbitalRune(rune, camX, camY, viewModel.player.x, viewModel.player.y)
            }

            // 8. Draw Player Attacks (Quill Darts & Evolved Spells)
            for (proj in viewModel.inkProjectiles) {
                drawQuillDart(proj, camX, camY)
            }

            // 9. Draw Player Character (Scribe / Painter)
            val hpRatio = (viewModel.player.hp / viewModel.player.maxHp).coerceIn(0f, 1f)
            drawPlayerCharacter(
                x = viewModel.player.x + camX,
                y = viewModel.player.y + camY,
                facingAngle = viewModel.player.lastMoveDirection,
                isInvincible = viewModel.player.isInvincible,
                isPainter = uiState.playerClass.id == "painter",
                time = uiState.timeSurvivedSeconds,
                hpRatio = hpRatio
            )

            // 9.1 Draw Telegraphed Geometric Eraser Strikes (Phase 5 Climax)
            if (viewModel.theEraserBoss.active) {
                for (strike in viewModel.theEraserBoss.telegraphedStrikes) {
                    drawTelegraphedStrike(strike, camX, camY)
                }
            }

            // 10. Draw Damage Numbers
            for (dn in viewModel.damageNumbers) {
                drawDamageNumber(dn, camX, camY)
            }

            // 11. Draw Level 100 Magnum Opus Ultimate Calligraphy Stroke Effect
            if (viewModel.magnumOpusVisual.active) {
                drawMagnumOpusEffect(viewModel.magnumOpusVisual)
            }
        }

        // Virtual Analog Stick (Movement ONLY, centered at middle bottom)
        VirtualAnalogStick(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 24.dp),
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

private fun DrawScope.drawEnemy(enemy: Enemy, camX: Float, camY: Float, time: Float = 0f) {
    val ex = enemy.x + camX
    val ey = enemy.y + camY
    val flap = sin(time * 9f + enemy.id * 1.5f) * 1.8f
    val r = (if (enemy.isElite) enemy.type.radius * 1.35f else enemy.type.radius) + flap

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

        EnemyType.THE_BLOTTER -> {
            // Porous cellulose sponge texture with sumi-e shading
            val path = Path().apply {
                moveTo(ex - r, ey - r * 0.8f)
                lineTo(ex + r, ey - r * 0.8f)
                lineTo(ex + r * 1.15f, ey + r * 0.8f)
                lineTo(ex - r * 1.15f, ey + r * 0.8f)
                close()
            }
            // Sponge fill
            drawPath(path = path, color = Color(0xFFE0F2F1))
            drawPath(path = path, color = Color(0xFF00796B), style = Stroke(width = outlineWidth + 1.5f))

            // Cellulose sponge pore pockets
            drawCircle(color = Color(0xFF004D40), radius = 5f, center = Offset(ex - r * 0.4f, ey - r * 0.2f))
            drawCircle(color = Color(0xFF004D40), radius = 4f, center = Offset(ex + r * 0.3f, ey - r * 0.1f))
            drawCircle(color = Color(0xFF004D40), radius = 6f, center = Offset(ex, ey + r * 0.3f))
            drawCircle(color = Color(0xFF80CBC4), radius = 3f, center = Offset(ex - r * 0.2f, ey + r * 0.4f))

            // Absorbing ink liquid ripples around sponge
            val rippleR = r * 1.25f + sin(enemy.flashTimer * 10f) * 3f
            drawCircle(color = Color(0xFF009688).copy(alpha = 0.35f), radius = rippleR, center = Offset(ex, ey), style = Stroke(width = 2.5f))

            // Health bar
            val barW = 75f
            val barH = 6f
            val barY = ey - r - 16f
            drawRect(color = Color(0x88000000), topLeft = Offset(ex - barW / 2f, barY), size = androidx.compose.ui.geometry.Size(barW, barH))
            val hpRatio = (enemy.hp / enemy.maxHp).coerceIn(0f, 1f)
            drawRect(color = Color(0xFF00BFA5), topLeft = Offset(ex - barW / 2f, barY), size = androidx.compose.ui.geometry.Size(barW * hpRatio, barH))
        }

        EnemyType.ORIGAMI_SHIELD -> {
            // Folded kite shield reflecting direct projectiles
            val path = Path().apply {
                moveTo(ex, ey - r * 1.2f)
                lineTo(ex + r * 1.1f, ey - r * 0.3f)
                lineTo(ex + r * 0.7f, ey + r * 1.1f)
                lineTo(ex, ey + r * 1.3f)
                lineTo(ex - r * 0.7f, ey + r * 1.1f)
                lineTo(ex - r * 1.1f, ey - r * 0.3f)
                close()
            }
            drawPath(path = path, color = Color(0xFFECEFF1))
            drawPath(path = path, color = Color(0xFF37474F), style = Stroke(width = outlineWidth + 1f))
            // Cross crease
            drawLine(Color(0xFF78909C), Offset(ex, ey - r * 1.1f), Offset(ex, ey + r * 1.2f), strokeWidth = 2f)
            drawLine(Color(0xFF78909C), Offset(ex - r * 0.9f, ey), Offset(ex + r * 0.9f, ey), strokeWidth = 2f)
            drawCircle(color = Color(0xFF1E88E5), radius = 5f, center = Offset(ex, ey))
        }

        EnemyType.STILT_WALKER -> {
            // Tall stilt legs stepping over puddles
            val legColor = Color(0xFF4E342E)
            drawLine(legColor, Offset(ex - r * 0.4f, ey), Offset(ex - r * 0.6f, ey + r * 1.5f), strokeWidth = 3f)
            drawLine(legColor, Offset(ex + r * 0.4f, ey), Offset(ex + r * 0.6f, ey + r * 1.5f), strokeWidth = 3f)
            // Paper crane / bird body atop
            val path = Path().apply {
                moveTo(ex, ey - r * 1.2f)
                lineTo(ex + r * 0.8f, ey - r * 0.2f)
                lineTo(ex, ey + r * 0.4f)
                lineTo(ex - r * 0.8f, ey - r * 0.2f)
                close()
            }
            drawPath(path = path, color = Color(0xFFFFF9C4))
            drawPath(path = path, color = Color(0xFFF57F17), style = Stroke(width = outlineWidth))
            drawCircle(color = eyeColor, radius = 4f, center = Offset(ex, ey - r * 0.3f))
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
    isPainter: Boolean,
    time: Float,
    hpRatio: Float
) {
    val alpha = if (isInvincible) (0.4f + 0.4f * kotlin.math.abs(sin(time * 20f))) else 1f

    // 1. Dynamic ground shadow
    val shadowPulse = 24f + sin(time * 6f) * 1.5f
    drawCircle(
        color = StarkBlackInk.copy(alpha = 0.14f * alpha),
        radius = shadowPulse,
        center = Offset(x, y + 5f)
    )

    // 2. Health ring indicator around hero feet
    val ringColor = if (hpRatio < 0.30f) {
        val blinkAlpha = 0.4f + 0.6f * kotlin.math.abs(sin(time * 10f))
        Color(0xFFD32F2F).copy(alpha = blinkAlpha)
    } else {
        Color(0xFF2E7D32).copy(alpha = 0.75f)
    }
    drawArc(
        color = ringColor,
        startAngle = -90f,
        sweepAngle = hpRatio * 360f,
        useCenter = false,
        topLeft = Offset(x - 22f, y - 22f),
        size = androidx.compose.ui.geometry.Size(44f, 44f),
        style = Stroke(width = 2.5f, cap = StrokeCap.Round)
    )

    // 3. Dynamic calligraphic ink ribbons trailing behind hero
    val ribbonAngle = facingAngle + PI.toFloat()
    for (i in -1..1) {
        val waveOffset = sin(time * 12f + i * 1.2f) * 5f
        val rAngle = ribbonAngle + (i * 0.16f)
        val rEnd = Offset(
            x + cos(rAngle) * 32f - sin(rAngle) * waveOffset,
            y + sin(rAngle) * 32f + cos(rAngle) * waveOffset
        )
        drawLine(
            color = StarkBlackInk.copy(alpha = (0.7f - kotlin.math.abs(i) * 0.25f) * alpha),
            start = Offset(x, y),
            end = rEnd,
            strokeWidth = if (i == 0) 5.5f else 3.5f,
            cap = StrokeCap.Round
        )
    }

    // 4. Hero body with dynamic breathing bob
    val bobY = sin(time * 8f) * 1.8f
    val cy = y + bobY
    drawCircle(
        color = StarkBlackInk.copy(alpha = alpha),
        radius = 18f,
        center = Offset(x, cy)
    )
    drawCircle(
        color = Color(0xFF262626).copy(alpha = alpha),
        radius = 13f,
        center = Offset(x, cy - 3f)
    )

    // 5. Dynamic Weapon (Quill / Brush) with organic sway and wet ink tip
    val swayAngle = facingAngle + sin(time * 7f) * 0.12f
    if (isPainter) {
        val brushTip = Offset(x + cos(swayAngle) * 28f, cy + sin(swayAngle) * 28f)
        drawLine(
            color = StarkBlackInk.copy(alpha = alpha),
            start = Offset(x, cy),
            end = brushTip,
            strokeWidth = 7f,
            cap = StrokeCap.Round
        )
        val dropPulse = 3.5f + sin(time * 14f) * 1f
        drawCircle(
            color = Color(0xFF00E5FF),
            radius = dropPulse,
            center = brushTip
        )
    } else {
        val quillTip = Offset(x + cos(swayAngle) * 26f, cy + sin(swayAngle) * 26f)
        drawLine(
            color = StarkBlackInk.copy(alpha = alpha),
            start = Offset(x, cy),
            end = quillTip,
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )
        val dropPulse = 2.5f + sin(time * 14f) * 0.8f
        drawCircle(
            color = Color(0xFFFFD700),
            radius = dropPulse,
            center = quillTip
        )
    }
}

private fun DrawScope.drawDamageNumber(dn: DamageNumber, camX: Float, camY: Float) {
    val nx = dn.x + camX
    val ny = dn.y + camY
    val lifeProgress = (1f - (dn.life / 0.8f)).coerceIn(0f, 1f)
    val alpha = (dn.life / 0.8f).coerceIn(0f, 1f)

    // Dynamic scale pop with bounce curve
    val scale = if (lifeProgress < 0.25f) {
        0.75f + (lifeProgress / 0.25f) * 0.55f // pop from 0.75x to 1.30x
    } else {
        1.30f - ((lifeProgress - 0.25f) / 0.75f) * 0.30f // settle smoothly to 1.0x
    }

    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.argb(
                (alpha * 255).toInt(),
                (dn.color.red * 255).toInt(),
                (dn.color.green * 255).toInt(),
                (dn.color.blue * 255).toInt()
            )
            textSize = 24f * scale
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
        // Occult Inscribed Compass Base Dial
        Canvas(modifier = Modifier.size(136.dp)) {
            val r = size.width / 2f
            val center = Offset(r, r)

            // 1. Dark semi-translucent ink body
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xD0181822), Color(0xEB0A0A0F)),
                    center = center,
                    radius = r
                ),
                radius = r,
                center = center
            )

            // 2. Outer golden rim
            drawCircle(
                color = Color(0xFFFFD54F).copy(alpha = 0.65f),
                radius = r - 1.5f,
                center = center,
                style = Stroke(width = 2.5f)
            )

            // 3. Inner faint rune track
            drawCircle(
                color = Color(0xFFFFE082).copy(alpha = 0.25f),
                radius = r * 0.68f,
                center = center,
                style = Stroke(width = 1.2f)
            )

            // 4. Cardinal ticks (N, S, E, W)
            val tickLen = 8f
            for (i in 0 until 4) {
                val angle = i * (PI.toFloat() / 2f)
                val pOuter = Offset(
                    center.x + cos(angle) * (r - 4f),
                    center.y + sin(angle) * (r - 4f)
                )
                val pInner = Offset(
                    center.x + cos(angle) * (r - 4f - tickLen),
                    center.y + sin(angle) * (r - 4f - tickLen)
                )
                drawLine(
                    color = Color(0xFFFFD54F).copy(alpha = 0.85f),
                    start = pInner,
                    end = pOuter,
                    strokeWidth = 2.5f
                )
            }

            // 5. 4 Diagonal mini-ticks
            for (i in 0 until 4) {
                val angle = (i * PI.toFloat() / 2f) + (PI.toFloat() / 4f)
                val pOuter = Offset(
                    center.x + cos(angle) * (r - 4f),
                    center.y + sin(angle) * (r - 4f)
                )
                val pInner = Offset(
                    center.x + cos(angle) * (r - 4f - 4.5f),
                    center.y + sin(angle) * (r - 4f - 4.5f)
                )
                drawLine(
                    color = Color(0xFFFFD54F).copy(alpha = 0.40f),
                    start = pInner,
                    end = pOuter,
                    strokeWidth = 1.5f
                )
            }

            // Directional pointer line towards drag
            if (dragOffset.getDistance() > 10f) {
                drawLine(
                    color = Color(0xFFFFD54F).copy(alpha = 0.45f),
                    start = center,
                    end = center + dragOffset * 0.85f,
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
            }
        }

        // Inner Inked Knob
        Box(
            modifier = Modifier
                .offset { IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt()) }
                .size(62.dp)
                .shadow(8.dp, CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF373744), Color(0xFF14141B), Color(0xFF08080C))
                    ),
                    CircleShape
                )
                .border(2.5.dp, Color(0xFFFFD54F), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Emblazoned Center Rune/Nib Emblem
            Canvas(modifier = Modifier.size(24.dp)) {
                val cw = size.width
                val ch = size.height
                val cx = cw / 2f
                val cy = ch / 2f

                // Central Golden Star / Nib Diamond
                val path = Path().apply {
                    moveTo(cx, cy - ch * 0.42f)
                    lineTo(cx + cw * 0.32f, cy)
                    lineTo(cx, cy + ch * 0.42f)
                    lineTo(cx - cw * 0.32f, cy)
                    close()
                }
                drawPath(path, Color(0xFFFFD54F))
                drawCircle(Color(0xFF14141B), radius = 2.5f, center = Offset(cx, cy))
            }
        }
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

    // Dynamic animated XP/Level progress filling
    val animatedXp by animateFloatAsState(
        targetValue = xpRatio,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
        label = "xp_ratio_anim"
    )

    // Top HUD: ONLY Pause, Progress Bar(lvl), and Time
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Pause Button
        Surface(
            onClick = onPauseClick,
            modifier = Modifier
                .size(48.dp)
                .testTag("pause_button"),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.95f),
            shadowElevation = 4.dp,
            border = BorderStroke(1.5.dp, StarkBlackInk)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pause",
                    tint = StarkBlackInk,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // 2. Dynamic Progress Bar (lvl)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(14.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(Color(0xFFEEEEEE))
                .border(1.2.dp, Color(0xFFC0C0C0), RoundedCornerShape(7.dp))
        ) {
            // Animated ink flow
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedXp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF212121),
                                Color(0xFF424242),
                                StarkBlackInk
                            )
                        )
                    )
            )
        }

        // 3. Time Display
        Surface(
            modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.95f),
            border = BorderStroke(1.5.dp, StarkBlackInk)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = StarkBlackInk,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = timeFormatted,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = StarkBlackInk,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

// ---------------- PHASE 5 CANVAS DRAWING HELPERS ----------------

// Phase 5 Mid-Run Checkpoint: The Inkwell structure (Minute 30:00)
private fun DrawScope.drawInkwell(inkwell: InkwellStructure, camX: Float, camY: Float) {
    val ix = inkwell.x + camX
    val iy = inkwell.y + camY
    val r = inkwell.radius
    val pulse = sin(inkwell.pulseTimer * 3.5f) * 4f

    // Outer sanctuary protective aura (soft jade/cyan glow)
    drawCircle(
        color = Color(0xFF00E676).copy(alpha = 0.18f),
        radius = r * 1.8f + pulse,
        center = Offset(ix, iy)
    )
    drawCircle(
        color = Color(0xFF00BFA5).copy(alpha = 0.28f),
        radius = r * 1.35f + pulse * 0.6f,
        center = Offset(ix, iy),
        style = Stroke(width = 3f)
    )

    // Ceramic Inkwell Basin: sumi-e porcelain body with golden lacquer
    val potPath = Path().apply {
        moveTo(ix - r * 0.85f, iy - r * 0.7f)
        lineTo(ix + r * 0.85f, iy - r * 0.7f)
        lineTo(ix + r * 1.05f, iy + r * 0.6f)
        lineTo(ix - r * 1.05f, iy + r * 0.6f)
        close()
    }
    drawPath(path = potPath, color = Color(0xFF1F1F1F))
    drawPath(path = potPath, color = Color(0xFFC5A059), style = Stroke(width = 3.5f))

    // Ink pool inside the well
    drawCircle(
        color = StarkBlackInk,
        radius = r * 0.55f,
        center = Offset(ix, iy - 2f)
    )
    // Deep glossy highlight inside inkwell
    drawCircle(
        color = Color(0xFFE0E0E0).copy(alpha = 0.7f),
        radius = 4f,
        center = Offset(ix - 6f, iy - 8f)
    )

    // Calligraphy character / seal mark on front of well
    drawLine(
        color = Color(0xFFC5A059),
        start = Offset(ix - r * 0.4f, iy + r * 0.15f),
        end = Offset(ix + r * 0.4f, iy + r * 0.15f),
        strokeWidth = 2.5f
    )
    drawLine(
        color = Color(0xFFC5A059),
        start = Offset(ix, iy - r * 0.1f),
        end = Offset(ix, iy + r * 0.45f),
        strokeWidth = 2.5f
    )
}

// Phase 5 The Blotter sponge death AOE shockwave burst
private fun DrawScope.drawBlotterBurst(burst: BlotterBurstVisual, camX: Float, camY: Float) {
    val bx = burst.x + camX
    val by = burst.y + camY
    val progress = (burst.timer / burst.maxDuration).coerceIn(0f, 1f)
    val curRadius = burst.radius * progress
    val alpha = (1f - progress).coerceIn(0f, 1f)

    // Expanding shockwave circle
    drawCircle(
        color = Color(0xFF00BFA5).copy(alpha = 0.25f * alpha),
        radius = curRadius,
        center = Offset(bx, by)
    )
    drawCircle(
        color = Color(0xFF004D40).copy(alpha = 0.8f * alpha),
        radius = curRadius,
        center = Offset(bx, by),
        style = Stroke(width = 6f * (1f - progress * 0.5f))
    )
    drawCircle(
        color = StarkBlackInk.copy(alpha = 0.6f * alpha),
        radius = curRadius * 0.75f,
        center = Offset(bx, by),
        style = Stroke(width = 3.5f)
    )

    // Kinetic splatter ink spikes radiating outward
    val spikeCount = 8
    for (i in 0 until spikeCount) {
        val angle = (i * 2 * PI / spikeCount).toFloat() + progress * 0.5f
        val spikeStart = curRadius * 0.4f
        val spikeEnd = curRadius * 1.05f
        drawLine(
            color = Color(0xFF00796B).copy(alpha = 0.75f * alpha),
            start = Offset(bx + cos(angle) * spikeStart, by + sin(angle) * spikeStart),
            end = Offset(bx + cos(angle) * spikeEnd, by + sin(angle) * spikeEnd),
            strokeWidth = 4f * alpha,
            cap = StrokeCap.Round
        )
    }
}

// Phase 5 The Eraser Arena Boundary & Monolith (Minute 60 Climax)
private fun DrawScope.drawEraserArena(eraser: TheEraserBoss, camX: Float, camY: Float) {
    val cx = 0f + camX
    val cy = 0f + camY
    val safeR = eraser.currentSafeRadius

    // 1. Outside Erasure Void overlay: A thick wash erasing the canvas outside the safe zone
    val voidStroke = 1600f
    drawCircle(
        color = Color(0xFFFAFAFA).copy(alpha = 0.65f),
        radius = safeR + voidStroke / 2f,
        center = Offset(cx, cy),
        style = Stroke(width = voidStroke)
    )

    // 2. Safe zone boundary barrier (Stark crimson & black warning ring)
    val pulse = sin(eraser.timer * 6f) * 3f
    drawCircle(
        color = Color(0xFFD32F2F).copy(alpha = 0.25f),
        radius = safeR + 8f + pulse,
        center = Offset(cx, cy),
        style = Stroke(width = 4f)
    )
    drawCircle(
        color = Color(0xFFD32F2F),
        radius = safeR,
        center = Offset(cx, cy),
        style = Stroke(width = 3f)
    )
    drawCircle(
        color = StarkBlackInk,
        radius = safeR - 3f,
        center = Offset(cx, cy),
        style = Stroke(width = 2f)
    )

    // 3. The Eraser Entity at the center (0, 0)
    // A colossal geometric prism / rubber block floating and rotating
    val bossAngle = eraser.timer * 25f
    rotate(degrees = bossAngle, pivot = Offset(cx, cy)) {
        val bw = 64f
        val bh = 36f
        drawRect(
            color = Color(0xFFF0F0F0),
            topLeft = Offset(cx - bw, cy - bh),
            size = androidx.compose.ui.geometry.Size(bw * 2f, bh * 2f)
        )
        // Blue angled sleeve band
        drawRect(
            color = Color(0xFF1E88E5),
            topLeft = Offset(cx - bw * 0.4f, cy - bh),
            size = androidx.compose.ui.geometry.Size(bw * 0.8f, bh * 2f)
        )
        drawRect(
            color = StarkBlackInk,
            topLeft = Offset(cx - bw, cy - bh),
            size = androidx.compose.ui.geometry.Size(bw * 2f, bh * 2f),
            style = Stroke(width = 3.5f)
        )
        // Menacing red core glyph
        drawCircle(
            color = Color(0xFFD32F2F),
            radius = 10f,
            center = Offset(cx, cy)
        )
    }

    // Health/Survival timer ring around the Eraser
    val progress = (eraser.timer / eraser.maxDuration).coerceIn(0f, 1f)
    drawArc(
        color = Color(0xFFD32F2F),
        startAngle = -90f,
        sweepAngle = progress * 360f,
        useCenter = false,
        topLeft = Offset(cx - 80f, cy - 80f),
        size = androidx.compose.ui.geometry.Size(160f, 160f),
        style = Stroke(width = 4f, cap = StrokeCap.Round)
    )
}

// Phase 5 Telegraphed Geometric Eraser Strikes
private fun DrawScope.drawTelegraphedStrike(strike: TelegraphedStrike, camX: Float, camY: Float) {
    val sx = strike.startX + camX
    val sy = strike.startY + camY
    val ex = strike.endX + camX
    val ey = strike.endY + camY

    if (!strike.isStriking) {
        // Telegraph warning phase (transparent red laser guide with dashed feel)
        val progress = (strike.timer / strike.telegraphDuration).coerceIn(0f, 1f)
        val alpha = 0.25f + progress * 0.45f
        val pulseWidth = strike.lineWidth * (0.6f + 0.4f * sin(strike.timer * 12f))

        // Outer danger corridor
        drawLine(
            color = Color(0xFFD32F2F).copy(alpha = alpha * 0.4f),
            start = Offset(sx, sy),
            end = Offset(ex, ey),
            strokeWidth = pulseWidth,
            cap = StrokeCap.Round
        )
        // Sharp center laser trace
        drawLine(
            color = Color(0xFFD32F2F).copy(alpha = alpha),
            start = Offset(sx, sy),
            end = Offset(ex, ey),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
    } else {
        // Active striking laser beam (incinerating blinding cut)
        val strikeProgress = ((strike.timer - strike.telegraphDuration) / strike.strikeDuration).coerceIn(0f, 1f)
        val beamAlpha = (1f - strikeProgress).coerceIn(0.2f, 1f)

        // Broad white-hot core
        drawLine(
            color = Color(0xFFD32F2F).copy(alpha = beamAlpha),
            start = Offset(sx, sy),
            end = Offset(ex, ey),
            strokeWidth = strike.lineWidth * 1.2f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White.copy(alpha = beamAlpha),
            start = Offset(sx, sy),
            end = Offset(ex, ey),
            strokeWidth = strike.lineWidth * 0.5f,
            cap = StrokeCap.Round
        )
    }
}

// Draw Cinnabar Detonation Seal
private fun DrawScope.drawCinnabarSeal(seal: CinnabarSealEntity, camX: Float, camY: Float) {
    val sx = seal.x + camX
    val sy = seal.y + camY

    if (!seal.detonated) {
        val progress = (seal.timer / seal.fuseTime).coerceIn(0f, 1f)
        val pulse = kotlin.math.sin(seal.timer * 14f) * 4f
        val currentR = seal.radius + pulse
        val cinnabarColor = Color(0xFFD32F2F)

        // Outer cinnabar ritual ring
        drawCircle(
            color = cinnabarColor.copy(alpha = 0.25f + progress * 0.45f),
            radius = currentR,
            center = Offset(sx, sy),
            style = Stroke(width = 3f)
        )
        // Inner spinning sigil ring
        drawCircle(
            color = Color(0xFFFF5722).copy(alpha = 0.35f + progress * 0.5f),
            radius = currentR * 0.65f,
            center = Offset(sx, sy),
            style = Stroke(width = 2f)
        )
        // Core glowing character/seal
        drawCircle(
            color = cinnabarColor.copy(alpha = 0.7f),
            radius = 12f + pulse * 0.5f,
            center = Offset(sx, sy)
        )
    } else {
        // Active explosion shockwave
        val blastProgress = (seal.blastTimer / seal.blastDuration).coerceIn(0f, 1f)
        val shockRadius = seal.radius * (0.4f + blastProgress * 0.8f)
        val alpha = (1f - blastProgress).coerceIn(0f, 1f)

        // Explosive crimson shockwave
        drawCircle(
            color = Color(0xFFD32F2F).copy(alpha = 0.75f * alpha),
            radius = shockRadius,
            center = Offset(sx, sy),
            style = Stroke(width = 8f * alpha)
        )
        // Searing orange core flash
        drawCircle(
            color = Color(0xFFFFAB91).copy(alpha = 0.5f * alpha),
            radius = shockRadius * 0.5f,
            center = Offset(sx, sy)
        )
    }
}

// Draw Orbital Cosmic Rune
private fun DrawScope.drawOrbitalRune(rune: OrbitalRuneEntity, camX: Float, camY: Float, playerX: Float, playerY: Float) {
    val rx = playerX + kotlin.math.cos(rune.orbitAngle) * rune.orbitRadius + camX
    val ry = playerY + kotlin.math.sin(rune.orbitAngle) * rune.orbitRadius + camY

    // Outer cosmic aura
    drawCircle(
        color = Color(0xFF1E88E5).copy(alpha = 0.35f),
        radius = 16f,
        center = Offset(rx, ry)
    )
    // Celestial Glyph core
    drawCircle(
        color = StarkBlackInk,
        radius = 11f,
        center = Offset(rx, ry)
    )
    drawCircle(
        color = Color(0xFF80D8FF),
        radius = 6f,
        center = Offset(rx, ry)
    )
    drawCircle(
        color = Color.White,
        radius = 2.5f,
        center = Offset(rx - 1f, ry - 1f)
    )
}


