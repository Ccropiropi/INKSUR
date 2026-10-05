package com.example.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import com.example.model.InkSplashParticle
import kotlinx.coroutines.delay
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
import com.example.model.CalligraphicElement
import com.example.model.DamageNumber
import com.example.model.ElementalParticle
import com.example.model.ElementalReactionType
import com.example.model.Enemy
import com.example.model.EnemyType
import com.example.model.FlowingSerpentEntity
import com.example.model.InkBrushSplashParticle
import com.example.model.InkPuddle
import com.example.model.InkProjectile
import com.example.model.InkwellStructure
import com.example.model.InkwellVortexEntity
import com.example.model.MagnumOpusVisual
import com.example.model.ObeliskEntity
import com.example.model.Orb
import com.example.model.PlayerMotionTrailNode
import com.example.model.RedRuneEntity
import com.example.model.ScreenElementalReactionVisual
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
        val shakeMag = if (viewModel.isScreenShakeEnabled) uiState.screenShakeTimer * 12f else 0f
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

            // 7.4 Draw Fluid Simulation Ink Splash Particles (Canvas & Shaders)
            for (splash in viewModel.inkSplashParticles) {
                drawFluidInkSplash(splash, camX, camY)
            }

            // 7.5 Draw Flowing Ink Serpents (Dynamic Summoned Creatures)
            for (serpent in viewModel.flowingSerpents) {
                drawFlowingSerpent(serpent, camX, camY)
            }

            // 7.6 Draw Elemental Particles (Ice Shards, Fire Embers, Cosmic Motes)
            for (part in viewModel.elementalParticles) {
                drawElementalParticle(part, camX, camY)
            }

            // 7.7 Draw Screen Elemental Reactions (Frozen Ink, Burning Calligraphy, etc.)
            for (reaction in viewModel.elementalReactionVisuals) {
                drawScreenElementalReaction(reaction, camX, camY)
            }

            // 8. Draw Player Attacks (Quill Darts & Evolved Spells)
            for (proj in viewModel.inkProjectiles) {
                drawQuillDart(proj, camX, camY)
            }

            // 8.5 Draw Dynamic Player Ink Motion Trails (Fluid Brushstroke Ribbon)
            for (trail in viewModel.playerMotionTrails) {
                drawPlayerMotionTrail(trail, camX, camY)
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

            // 9.05 Subtle Elemental Status Effect HUD above Player Character (Active Elemental Stacks)
            drawPlayerElementalStatusHud(
                x = viewModel.player.x + camX,
                y = viewModel.player.y + camY,
                elementalStacks = viewModel.playerElementalStacks,
                time = uiState.timeSurvivedSeconds
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

            // 10.5 Draw High-Contrast Ink-Brush Splash Particles (Elemental Reactions like Frozen Ink)
            for (splash in viewModel.inkBrushSplashParticles) {
                drawInkBrushSplashParticle(splash, camX, camY)
            }

            // 11. Draw Level 100 Magnum Opus Ultimate Calligraphy Stroke Effect
            if (viewModel.magnumOpusVisual.active) {
                drawMagnumOpusEffect(viewModel.magnumOpusVisual)
            }

            // 11.5 Draw High-Contrast Impact Frame / Shake Feedback Overlay
            if (viewModel.impactFrameTimer > 0f && viewModel.isImpactFrameEnabled) {
                drawImpactFrameOverlay(
                    reaction = viewModel.activeImpactReaction,
                    timer = viewModel.impactFrameTimer
                )
            }
        }

        // Virtual Analog Stick Controls
        val isFixed = viewModel.isFixedAnalog
        var dynamicStickCenter by remember { mutableStateOf<Offset?>(null) }
        var dynamicDragOffset by remember { mutableStateOf(Offset.Zero) }
        var isInteracting by remember { mutableStateOf(false) }
        var lastInteractionTime by remember { mutableLongStateOf(0L) }
        var dynamicStickVisible by remember { mutableStateOf(false) }

        val animatedAlpha by animateFloatAsState(
            targetValue = if (isFixed) 1f else if (dynamicStickVisible) 1f else 0f,
            animationSpec = tween(durationMillis = 350),
            label = "analog_alpha"
        )

        // 5-second auto-hide timer for tap mode
        LaunchedEffect(isInteracting, lastInteractionTime, isFixed) {
            if (!isFixed && !isInteracting && dynamicStickVisible) {
                delay(5000L)
                dynamicStickVisible = false
            }
        }

        // Tap-to-steer gesture detector across screen when in dynamic tap mode
        if (!isFixed) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(isFixed) {
                        detectDragGestures(
                            onDragStart = { startOffset ->
                                dynamicStickCenter = startOffset
                                dynamicDragOffset = Offset.Zero
                                isInteracting = true
                                dynamicStickVisible = true
                                lastInteractionTime = System.currentTimeMillis()
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val newOffset = dynamicDragOffset + dragAmount
                                val maxPx = 65f * density
                                val dist = newOffset.getDistance()
                                val clamped = if (dist > maxPx) newOffset * (maxPx / dist) else newOffset
                                dynamicDragOffset = clamped
                                viewModel.joystickVector = clamped / maxPx
                                isInteracting = true
                                lastInteractionTime = System.currentTimeMillis()
                            },
                            onDragEnd = {
                                dynamicDragOffset = Offset.Zero
                                viewModel.joystickVector = Offset.Zero
                                isInteracting = false
                                lastInteractionTime = System.currentTimeMillis()
                            },
                            onDragCancel = {
                                dynamicDragOffset = Offset.Zero
                                viewModel.joystickVector = Offset.Zero
                                isInteracting = false
                                lastInteractionTime = System.currentTimeMillis()
                            }
                        )
                    }
            )
        }

        // Virtual Analog Stick (Fixed Bottom-Center vs Dynamic Tap-to-Place)
        if (isFixed) {
            VirtualAnalogStick(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = 24.dp),
                onMove = { offset ->
                    viewModel.joystickVector = offset
                }
            )
        } else {
            // Dynamic Tap Mode: hidden at 0% opacity by default; appears at tap position; auto-hides after 5s
            if (dynamicStickCenter != null && animatedAlpha > 0.005f) {
                val density = LocalDensity.current
                val stickRadiusPx = with(density) { 75.dp.toPx() }
                val center = dynamicStickCenter!!
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (center.x - stickRadiusPx).roundToInt(),
                                (center.y - stickRadiusPx).roundToInt()
                            )
                        }
                        .alpha(animatedAlpha)
                ) {
                    OccultCompassDialAndKnob(
                        dragOffset = dynamicDragOffset
                    )
                }
            }
        }

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

        // Screen-Filling Calligraphic Elemental Reaction Banner
        val banner = viewModel.activeElementalBanner
        if (banner != null) {
            ElementalReactionBanner(
                reaction = banner,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(top = 64.dp)
            )
        }
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

    if (puddle.isFrozenInk) {
        // Frozen Solid Black Ice Puddle with Glacial Fractures
        drawCircle(
            color = Color(0xFF001F3F).copy(alpha = 0.55f * lifeRatio),
            radius = r,
            center = Offset(px, py)
        )
        drawCircle(
            color = Color(0xFF00E5FF).copy(alpha = 0.40f * lifeRatio),
            radius = r * 0.9f,
            center = Offset(px, py)
        )
        drawCircle(
            color = Color(0xFF80D8FF).copy(alpha = 0.8f * lifeRatio),
            radius = r,
            center = Offset(px, py),
            style = Stroke(width = 2.5f)
        )
        // Ice fracture lines
        drawLine(
            color = Color.White.copy(alpha = 0.7f * lifeRatio),
            start = Offset(px - r * 0.7f, py - r * 0.4f),
            end = Offset(px + r * 0.6f, py + r * 0.5f),
            strokeWidth = 2f
        )
        drawLine(
            color = Color(0xFF00E5FF).copy(alpha = 0.7f * lifeRatio),
            start = Offset(px + r * 0.4f, py - r * 0.6f),
            end = Offset(px - r * 0.3f, py + r * 0.7f),
            strokeWidth = 1.8f
        )
    } else if (puddle.isBurningCalligraphy) {
        // Raging Cinnabar Conflagration Ink Puddle
        drawCircle(
            color = Color(0xFF3E0000).copy(alpha = 0.65f * lifeRatio),
            radius = r,
            center = Offset(px, py)
        )
        drawCircle(
            color = Color(0xFFFF3D00).copy(alpha = 0.75f * lifeRatio),
            radius = r * 0.85f,
            center = Offset(px, py)
        )
        drawCircle(
            color = Color(0xFFFFAB00).copy(alpha = 0.85f * lifeRatio),
            radius = r * 0.45f,
            center = Offset(px, py)
        )
        drawCircle(
            color = Color(0xFFFFD600).copy(alpha = 0.9f * lifeRatio),
            radius = r * 0.2f,
            center = Offset(px, py)
        )
        drawCircle(
            color = Color(0xFFFF3D00).copy(alpha = lifeRatio),
            radius = r,
            center = Offset(px, py),
            style = Stroke(width = 3f)
        )
    } else {
        // Standard Sumi-e Ink Puddle
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
            color = Color(0xFFFFD54F).copy(alpha = 0.45f),
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
        when (orb.tier) {
            3 -> {
                // Tier 3: Radiant Celestial Jade / Azure Ink Orb (Unlocked 25m+)
                val pulse = sin(orb.pulseTimer * 8f) * 2.5f
                drawCircle(
                    color = Color(0xFF00E5FF).copy(alpha = 0.35f),
                    radius = 13f + pulse,
                    center = Offset(ox, oy)
                )
                drawCircle(
                    color = Color(0xFF00B0FF),
                    radius = 8.5f,
                    center = Offset(ox, oy)
                )
                drawCircle(
                    color = Color(0xFFFFD54F),
                    radius = 5f,
                    center = Offset(ox, oy)
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.2f,
                    center = Offset(ox - 1.5f, oy - 1.5f)
                )
            }
            2 -> {
                // Tier 2: Refined Cinnabar Ink Orb (Unlocked 25m+ or higher-tier paper horrors)
                val pulse = sin(orb.pulseTimer * 6f) * 1.8f
                drawCircle(
                    color = Color(0xFFFF3D00).copy(alpha = 0.3f),
                    radius = 10f + pulse,
                    center = Offset(ox, oy)
                )
                drawCircle(
                    color = Color(0xFFD50000),
                    radius = 7.5f,
                    center = Offset(ox, oy)
                )
                drawCircle(
                    color = StarkBlackInk,
                    radius = 4f,
                    center = Offset(ox, oy)
                )
                drawCircle(
                    color = Color(0xFFFF8A80),
                    radius = 1.8f,
                    center = Offset(ox - 1.2f, oy - 1.2f)
                )
            }
            else -> {
                // Tier 1: Small classic pitch-black sumi-e droplet (lowest tier enemies)
                drawCircle(
                    color = StarkBlackInk.copy(alpha = 0.22f),
                    radius = 8.5f,
                    center = Offset(ox, oy)
                )
                drawCircle(
                    color = StarkBlackInk,
                    radius = 6f,
                    center = Offset(ox, oy)
                )
                drawCircle(
                    color = Color.White,
                    radius = 1.8f,
                    center = Offset(ox - 1.5f, oy - 1.5f)
                )
            }
        }
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
            // Faceted origami diamond with 4 folded planes & subtle shadow
            val facet1 = Path().apply { moveTo(ex, ey - r); lineTo(ex, ey); lineTo(ex - r, ey); close() }
            val facet2 = Path().apply { moveTo(ex, ey - r); lineTo(ex + r, ey); lineTo(ex, ey); close() }
            val facet3 = Path().apply { moveTo(ex, ey); lineTo(ex + r, ey); lineTo(ex, ey + r); close() }
            val facet4 = Path().apply { moveTo(ex - r, ey); lineTo(ex, ey); lineTo(ex, ey + r); close() }

            val shadowShade = if (enemy.isElite) Color(0xFF6B0909) else Color(0xFFE5E5E5)
            val litShade = if (enemy.isElite) EliteCrimsonFill else PaperConstructFill

            drawPath(facet1, litShade)
            drawPath(facet2, shadowShade)
            drawPath(facet3, litShade)
            drawPath(facet4, shadowShade)

            // Outer sumi-e boundary
            val fullDiamond = Path().apply {
                moveTo(ex, ey - r); lineTo(ex + r, ey); lineTo(ex, ey + r); lineTo(ex - r, ey); close()
            }
            drawPath(fullDiamond, outlineColor, style = Stroke(width = outlineWidth))

            // Internal paper crease lines
            drawLine(outlineColor.copy(alpha = 0.6f), Offset(ex, ey - r), Offset(ex, ey + r), strokeWidth = 1.8f)
            drawLine(outlineColor.copy(alpha = 0.6f), Offset(ex - r, ey), Offset(ex + r, ey), strokeWidth = 1.8f)

            // Demonic core / occult paper eye
            drawCircle(color = StarkBlackInk, radius = if (enemy.isElite) 6.5f else 4.5f, center = Offset(ex, ey))
            drawCircle(color = eyeColor, radius = if (enemy.isElite) 4.5f else 3f, center = Offset(ex, ey))
            drawCircle(color = Color.White, radius = 1.2f, center = Offset(ex - 1f, ey - 1f))
        }

        EnemyType.FOLDED_STALKER -> {
            // Origami raptor/crane: sweeping wing planes with crease shading
            val leftWing = Path().apply {
                moveTo(ex, ey - r * 1.35f)
                lineTo(ex, ey + r * 0.4f)
                lineTo(ex - r * 1.25f, ey + r * 0.95f)
                close()
            }
            val rightWing = Path().apply {
                moveTo(ex, ey - r * 1.35f)
                lineTo(ex + r * 1.25f, ey + r * 0.95f)
                lineTo(ex, ey + r * 0.4f)
                close()
            }
            val stalkerShadow = if (enemy.isElite) Color(0xFF720D0D) else Color(0xFFDEDBD2)
            drawPath(leftWing, stalkerShadow)
            drawPath(rightWing, fillColor)

            val fullStalker = Path().apply {
                moveTo(ex, ey - r * 1.35f)
                lineTo(ex + r * 1.25f, ey + r * 0.95f)
                lineTo(ex, ey + r * 0.4f)
                lineTo(ex - r * 1.25f, ey + r * 0.95f)
                close()
            }
            drawPath(fullStalker, outlineColor, style = Stroke(width = outlineWidth))

            // Spine crease and wing ribs
            drawLine(outlineColor, Offset(ex, ey - r * 1.35f), Offset(ex, ey + r * 0.4f), strokeWidth = 2.2f)
            drawLine(outlineColor.copy(alpha = 0.5f), Offset(ex, ey - r * 0.6f), Offset(ex - r * 0.8f, ey + r * 0.5f), strokeWidth = 1.5f)
            drawLine(outlineColor.copy(alpha = 0.5f), Offset(ex, ey - r * 0.6f), Offset(ex + r * 0.8f, ey + r * 0.5f), strokeWidth = 1.5f)

            // Sharp predatory paper eye
            drawCircle(color = StarkBlackInk, radius = if (enemy.isElite) 6.5f else 4.5f, center = Offset(ex, ey - r * 0.35f))
            drawCircle(color = eyeColor, radius = if (enemy.isElite) 4.5f else 3f, center = Offset(ex, ey - r * 0.35f))
        }

        EnemyType.PAPER_BRUTE -> {
            // Heavy armored origami golem with layered hexagonal shoulder plates
            val brutePath = Path().apply {
                moveTo(ex, ey - r * 1.05f)
                lineTo(ex + r * 0.95f, ey - r * 0.55f)
                lineTo(ex + r * 0.95f, ey + r * 0.55f)
                lineTo(ex, ey + r * 1.05f)
                lineTo(ex - r * 0.95f, ey + r * 0.55f)
                lineTo(ex - r * 0.95f, ey - r * 0.55f)
                close()
            }
            drawPath(brutePath, fillColor)
            drawPath(brutePath, outlineColor, style = Stroke(width = outlineWidth + 1.2f))

            // Armored chest fold plate
            val chestFold = Path().apply {
                moveTo(ex - r * 0.6f, ey - r * 0.4f)
                lineTo(ex + r * 0.6f, ey - r * 0.4f)
                lineTo(ex, ey + r * 0.4f)
                close()
            }
            drawPath(chestFold, if (enemy.isElite) Color(0xFF5A0000) else Color(0xFFD6D6D6))
            drawPath(chestFold, outlineColor, style = Stroke(width = 1.8f))

            // Glowing brute eye slit
            drawCircle(color = StarkBlackInk, radius = 8.5f, center = Offset(ex, ey))
            drawCircle(color = eyeColor, radius = 5.5f, center = Offset(ex, ey))
            drawCircle(color = Color.White, radius = 2f, center = Offset(ex - 1.5f, ey - 1.5f))
        }

        EnemyType.THE_TITAN -> {
            val path = Path().apply {
                moveTo(ex, ey - r)
                lineTo(ex + r * 0.75f, ey - r * 0.65f)
                lineTo(ex + r, ey)
                lineTo(ex + r * 0.85f, ey + r * 0.75f)
                lineTo(ex, ey + r)
                lineTo(ex - r * 0.85f, ey + r * 0.75f)
                lineTo(ex - r, ey)
                lineTo(ex - r * 0.75f, ey - r * 0.65f)
                close()
            }
            drawPath(path = path, color = fillColor)
            drawPath(path = path, color = outlineColor, style = Stroke(width = outlineWidth + 2.5f))

            // Rotating runic ring around Titan
            val ringAngle = time * 2.5f
            for (k in 0..7) {
                val a = ringAngle + (k * PI / 4).toFloat()
                val rx = ex + cos(a) * (r * 0.85f)
                val ry = ey + sin(a) * (r * 0.85f)
                drawCircle(color = EliteCrimsonOutline, radius = 4f, center = Offset(rx, ry))
            }

            drawLine(outlineColor, Offset(ex, ey - r), Offset(ex, ey + r), strokeWidth = 2.5f)
            drawLine(outlineColor, Offset(ex - r, ey), Offset(ex + r, ey), strokeWidth = 2.5f)

            // Boss Core & Eye
            drawCircle(color = outlineColor, radius = 16f, center = Offset(ex, ey))
            drawCircle(color = eyeColor, radius = 11f, center = Offset(ex, ey))
            drawCircle(color = Color.White, radius = 4f, center = Offset(ex - 2.5f, ey - 2.5f))

            // Boss Health Bar above Titan
            val barW = 120f
            val barH = 11f
            val barY = ey - r - 24f
            drawRect(
                color = Color(0xAA000000),
                topLeft = Offset(ex - barW / 2f, barY),
                size = androidx.compose.ui.geometry.Size(barW, barH)
            )
            val hpRatio = (enemy.hp / enemy.maxHp).coerceIn(0f, 1f)
            drawRect(
                color = Color(0xFFD32F2F),
                topLeft = Offset(ex - barW / 2f, barY),
                size = androidx.compose.ui.geometry.Size(barW * hpRatio, barH)
            )
            drawRect(
                color = Color.White,
                topLeft = Offset(ex - barW / 2f, barY),
                size = androidx.compose.ui.geometry.Size(barW, barH),
                style = Stroke(width = 1.5f)
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
            drawCircle(color = Color(0xFF00E676), radius = 12f, center = Offset(ex, ey - 4f))
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

    // Frozen Solid in Black Ice (Immobilized & +60% shatter vulnerability)
    if (enemy.frozenSolidTimer > 0f) {
        val iceAlpha = (enemy.frozenSolidTimer / 3.0f).coerceIn(0.4f, 0.95f)
        drawCircle(
            color = Color(0xFF001F3F).copy(alpha = iceAlpha * 0.5f),
            radius = r * 1.35f,
            center = Offset(ex, ey)
        )
        drawCircle(
            color = Color(0xFF00E5FF).copy(alpha = iceAlpha * 0.9f),
            radius = r * 1.35f,
            center = Offset(ex, ey),
            style = Stroke(width = 3.5f)
        )
        // Cryo crystalline fracture lines
        drawLine(Color.White.copy(alpha = iceAlpha), Offset(ex - r, ey - r * 0.5f), Offset(ex + r * 0.8f, ey + r * 0.6f), strokeWidth = 2f)
        drawLine(Color(0xFF80D8FF).copy(alpha = iceAlpha), Offset(ex + r * 0.6f, ey - r * 0.8f), Offset(ex - r * 0.5f, ey + r * 0.7f), strokeWidth = 2f)
    }

    // Cinnabar Flame burning aura
    if (enemy.flameTimer > 0f) {
        val flameAlpha = (enemy.flameTimer / 4.0f).coerceIn(0.35f, 0.9f)
        drawCircle(
            color = Color(0xFFFF3D00).copy(alpha = flameAlpha * 0.45f),
            radius = r * 1.35f,
            center = Offset(ex, ey)
        )
        drawCircle(
            color = Color(0xFFFFAB00).copy(alpha = flameAlpha),
            radius = r * 1.25f,
            center = Offset(ex, ey - 3f),
            style = Stroke(width = 2.5f)
        )
    }

    // Glacial Frost chill ring
    if (enemy.frostTimer > 0f && enemy.frozenSolidTimer <= 0f) {
        drawCircle(
            color = Color(0xFF80D8FF).copy(alpha = 0.6f),
            radius = r * 1.2f,
            center = Offset(ex, ey),
            style = Stroke(width = 2f)
        )
    }

    // Corrosive Acid wash soak
    if (enemy.acidTimer > 0f) {
        drawCircle(
            color = Color(0xFF00E676).copy(alpha = 0.55f),
            radius = r * 1.2f,
            center = Offset(ex, ey),
            style = Stroke(width = 2f)
        )
    }

    // Cosmic Astral sigil
    if (enemy.astralTimer > 0f) {
        val starA = time * 4f
        val sx = ex + cos(starA) * (r * 1.35f)
        val sy = ey + sin(starA) * (r * 1.35f)
        drawCircle(color = Color(0xFFFFD54F), radius = 4f, center = Offset(sx, sy))
        drawCircle(
            color = Color(0xFFFFD54F).copy(alpha = 0.45f),
            radius = r * 1.35f,
            center = Offset(ex, ey),
            style = Stroke(width = 1.5f)
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

// Draw elemental particles (ice crystal shards, fiery embers, cosmic motes)
private fun DrawScope.drawElementalParticle(part: ElementalParticle, camX: Float, camY: Float) {
    val px = part.x + camX
    val py = part.y + camY
    val lifeRatio = (part.life / part.maxLife).coerceIn(0f, 1f)

    if (part.isShard) {
        rotate(degrees = part.angle, pivot = Offset(px, py)) {
            val halfS = (part.size * lifeRatio) * 0.5f
            val shardPath = Path().apply {
                moveTo(px, py - halfS * 1.5f)
                lineTo(px + halfS * 0.7f, py)
                lineTo(px, py + halfS * 1.5f)
                lineTo(px - halfS * 0.7f, py)
                close()
            }
            drawPath(path = shardPath, color = part.color.copy(alpha = lifeRatio))
            drawPath(path = shardPath, color = Color.White.copy(alpha = lifeRatio * 0.8f), style = Stroke(width = 1.2f))
        }
    } else {
        val radius = part.size * lifeRatio
        drawCircle(
            color = part.color.copy(alpha = lifeRatio * 0.75f),
            radius = radius,
            center = Offset(px, py)
        )
        drawCircle(
            color = Color.White.copy(alpha = lifeRatio * 0.9f),
            radius = radius * 0.35f,
            center = Offset(px, py)
        )
    }
}

// Draw Screen-Filling Elemental Reactions (Frozen Ink, Burning Calligraphy, etc.)
private fun DrawScope.drawScreenElementalReaction(
    visual: ScreenElementalReactionVisual,
    camX: Float,
    camY: Float
) {
    val cx = visual.x + camX
    val cy = visual.y + camY
    val progress = visual.progress
    val currentR = visual.expansionRadius
    val alpha = (1f - progress).coerceIn(0f, 1f)

    when (visual.reactionType) {
        ElementalReactionType.FROZEN_INK -> {
            if (visual.flashAlpha > 0.05f) {
                drawRect(color = Color(0xFF00E5FF).copy(alpha = visual.flashAlpha * 0.35f))
            }
            drawCircle(
                color = Color(0xFF00E5FF).copy(alpha = alpha * 0.85f),
                radius = currentR,
                center = Offset(cx, cy),
                style = Stroke(width = 4.5f * (1f - progress * 0.5f))
            )
            drawCircle(
                color = Color(0xFF001F3F).copy(alpha = alpha * 0.25f),
                radius = currentR * 0.85f,
                center = Offset(cx, cy)
            )
            for (i in 0 until 8) {
                val a = (i * PI / 4).toFloat() + progress * 0.2f
                val endX = cx + cos(a) * currentR
                val endY = cy + sin(a) * currentR
                drawLine(
                    color = Color(0xFF00E5FF).copy(alpha = alpha),
                    start = Offset(cx, cy),
                    end = Offset(endX, endY),
                    strokeWidth = 2.5f
                )
                val midX = cx + cos(a) * (currentR * 0.6f)
                val midY = cy + sin(a) * (currentR * 0.6f)
                val branchA = a + 0.35f
                drawLine(
                    color = Color.White.copy(alpha = alpha * 0.8f),
                    start = Offset(midX, midY),
                    end = Offset(midX + cos(branchA) * (currentR * 0.35f), midY + sin(branchA) * (currentR * 0.35f)),
                    strokeWidth = 1.8f
                )
            }
            val sealSize = 54f * (1f + progress * 0.3f)
            drawRect(
                color = Color(0xFF00E5FF).copy(alpha = alpha * 0.7f),
                topLeft = Offset(cx - sealSize / 2f, cy - sealSize / 2f),
                size = androidx.compose.ui.geometry.Size(sealSize, sealSize),
                style = Stroke(width = 2.5f)
            )
        }

        ElementalReactionType.BURNING_CALLIGRAPHY -> {
            if (visual.flashAlpha > 0.05f) {
                drawRect(color = Color(0xFFFF3D00).copy(alpha = visual.flashAlpha * 0.40f))
            }
            drawCircle(
                color = Color(0xFFFF3D00).copy(alpha = alpha * 0.9f),
                radius = currentR,
                center = Offset(cx, cy),
                style = Stroke(width = 6f * (1f - progress * 0.5f))
            )
            drawCircle(
                color = Color(0xFFFFAB00).copy(alpha = alpha * 0.7f),
                radius = currentR * 0.75f,
                center = Offset(cx, cy),
                style = Stroke(width = 4f)
            )
            drawCircle(
                color = Color(0xFFD50000).copy(alpha = alpha * 0.25f),
                radius = currentR * 0.6f,
                center = Offset(cx, cy)
            )
            for (i in 0 until 6) {
                val a = (i * PI / 3).toFloat() + progress * 0.8f
                val flamePath = Path().apply {
                    moveTo(cx, cy)
                    quadraticTo(
                        cx + cos(a + 0.4f) * (currentR * 0.65f),
                        cy + sin(a + 0.4f) * (currentR * 0.65f),
                        cx + cos(a) * currentR,
                        cy + sin(a) * currentR
                    )
                }
                drawPath(path = flamePath, color = Color(0xFFFFD600).copy(alpha = alpha), style = Stroke(width = 3.5f, cap = StrokeCap.Round))
            }
            val sealSize = 60f
            drawRect(
                color = Color(0xFFD50000).copy(alpha = alpha * 0.85f),
                topLeft = Offset(cx - sealSize / 2f, cy - sealSize / 2f),
                size = androidx.compose.ui.geometry.Size(sealSize, sealSize)
            )
            drawRect(
                color = Color(0xFFFFD600).copy(alpha = alpha),
                topLeft = Offset(cx - sealSize / 2f, cy - sealSize / 2f),
                size = androidx.compose.ui.geometry.Size(sealSize, sealSize),
                style = Stroke(width = 2.5f)
            )
        }

        ElementalReactionType.COSMIC_SUPERNOVA -> {
            if (visual.flashAlpha > 0.05f) {
                drawRect(color = Color(0xFFFFD54F).copy(alpha = visual.flashAlpha * 0.45f))
            }
            drawCircle(
                color = Color(0xFFFFD54F).copy(alpha = alpha * 0.9f),
                radius = currentR,
                center = Offset(cx, cy),
                style = Stroke(width = 5f)
            )
            drawCircle(
                color = Color(0xFF7C4DFF).copy(alpha = alpha * 0.75f),
                radius = currentR * 0.5f,
                center = Offset(cx, cy),
                style = Stroke(width = 3.5f)
            )
            for (i in 0 until 12) {
                val a = (i * PI / 6).toFloat()
                val len = if (i % 2 == 0) currentR else currentR * 0.7f
                drawLine(
                    color = Color(0xFFFFEA00).copy(alpha = alpha),
                    start = Offset(cx, cy),
                    end = Offset(cx + cos(a) * len, cy + sin(a) * len),
                    strokeWidth = if (i % 2 == 0) 3.5f else 1.8f,
                    cap = StrokeCap.Round
                )
            }
        }

        ElementalReactionType.PERMAFROST_BLOSSOM -> {
            if (visual.flashAlpha > 0.05f) {
                drawRect(color = Color(0xFF80D8FF).copy(alpha = visual.flashAlpha * 0.35f))
            }
            for (i in 0 until 6) {
                val a = (i * PI / 3).toFloat() + progress * 0.15f
                val tipX = cx + cos(a) * currentR
                val tipY = cy + sin(a) * currentR
                drawLine(
                    color = Color(0xFF80D8FF).copy(alpha = alpha),
                    start = Offset(cx, cy),
                    end = Offset(tipX, tipY),
                    strokeWidth = 3f
                )
                for (b in 1..3) {
                    val branchDist = currentR * (b * 0.28f)
                    val bx = cx + cos(a) * branchDist
                    val by = cy + sin(a) * branchDist
                    val branchLen = currentR * 0.18f
                    drawLine(
                        color = Color.White.copy(alpha = alpha * 0.85f),
                        start = Offset(bx, by),
                        end = Offset(bx + cos(a + 0.6f) * branchLen, by + sin(a + 0.6f) * branchLen),
                        strokeWidth = 1.8f
                    )
                    drawLine(
                        color = Color.White.copy(alpha = alpha * 0.85f),
                        start = Offset(bx, by),
                        end = Offset(bx + cos(a - 0.6f) * branchLen, by + sin(a - 0.6f) * branchLen),
                        strokeWidth = 1.8f
                    )
                }
            }
            drawCircle(
                color = Color(0xFF80D8FF).copy(alpha = alpha * 0.6f),
                radius = currentR * 0.65f,
                center = Offset(cx, cy),
                style = Stroke(width = 2f)
            )
        }

        ElementalReactionType.THERMAL_SHOCK -> {
            val leftPath = Path().apply {
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(cx - currentR, cy - currentR, cx + currentR, cy + currentR),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = true
                )
            }
            drawPath(path = leftPath, color = Color(0xFFFF1744).copy(alpha = alpha * 0.9f), style = Stroke(width = 5f))

            val rightPath = Path().apply {
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(cx - currentR, cy - currentR, cx + currentR, cy + currentR),
                    startAngleDegrees = 270f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = true
                )
            }
            drawPath(path = rightPath, color = Color(0xFF00E5FF).copy(alpha = alpha * 0.9f), style = Stroke(width = 5f))

            drawCircle(
                color = Color.White.copy(alpha = alpha * 0.5f),
                radius = currentR * 0.5f,
                center = Offset(cx, cy),
                style = Stroke(width = 8f)
            )
        }
    }
}

@Composable
private fun ElementalReactionBanner(
    reaction: ElementalReactionType,
    modifier: Modifier = Modifier
) {
    val primaryColor = Color(reaction.primaryColorHex)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xE60A0A0E))
            .border(1.5.dp, primaryColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(12.dp, RoundedCornerShape(8.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFD32F2F), RoundedCornerShape(4.dp))
                    .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = reaction.kanjiStamps.take(2),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
            }

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = reaction.title,
                        color = primaryColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Box(
                        modifier = Modifier
                            .background(primaryColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .border(0.5.dp, primaryColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "COMBO REACTION",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Text(
                    text = reaction.subtitle,
                    color = Color(0xFFECEFF1),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
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

    // 1. Dynamic Calligraphic Ground Shadow with breathing ink aura
    val shadowPulse = 26f + sin(time * 6f) * 2f
    drawCircle(
        color = StarkBlackInk.copy(alpha = 0.16f * alpha),
        radius = shadowPulse,
        center = Offset(x, y + 6f)
    )

    // Outer faint ink mist aura around hero
    val auraPulse = 34f + sin(time * 4f) * 3f
    drawCircle(
        color = (if (isPainter) Color(0xFF00E5FF) else Color(0xFFFFD700)).copy(alpha = 0.08f * alpha),
        radius = auraPulse,
        center = Offset(x, y)
    )

    // 2. Health ring indicator around hero feet with brush-styled bristle tips
    val ringColor = if (hpRatio < 0.30f) {
        val blinkAlpha = 0.45f + 0.55f * kotlin.math.abs(sin(time * 10f))
        Color(0xFFD32F2F).copy(alpha = blinkAlpha)
    } else {
        Color(0xFF2E7D32).copy(alpha = 0.85f)
    }
    drawArc(
        color = ringColor,
        startAngle = -90f,
        sweepAngle = hpRatio * 360f,
        useCenter = false,
        topLeft = Offset(x - 24f, y - 24f),
        size = androidx.compose.ui.geometry.Size(48f, 48f),
        style = Stroke(width = 3f, cap = StrokeCap.Round)
    )

    // 3. Dynamic calligraphic ink ribbons & robe tails trailing behind hero
    val ribbonAngle = facingAngle + PI.toFloat()
    for (i in -2..2) {
        val waveOffset = sin(time * 12f + i * 0.9f) * (6f + kotlin.math.abs(i) * 2f)
        val rAngle = ribbonAngle + (i * 0.18f)
        val rDist = 34f - kotlin.math.abs(i) * 4f
        val rEnd = Offset(
            x + cos(rAngle) * rDist - sin(rAngle) * waveOffset,
            y + sin(rAngle) * rDist + cos(rAngle) * waveOffset
        )
        // Shaded ribbon with calligraphic taper
        drawLine(
            color = StarkBlackInk.copy(alpha = (0.75f - kotlin.math.abs(i) * 0.18f) * alpha),
            start = Offset(x, y),
            end = rEnd,
            strokeWidth = (6.5f - kotlin.math.abs(i) * 1.2f),
            cap = StrokeCap.Round
        )
    }

    // 4. Hero body with dynamic breathing bob & layered robes
    val bobY = sin(time * 8f) * 2f
    val cy = y + bobY

    // Outer dark robe mantle
    drawCircle(
        color = StarkBlackInk.copy(alpha = alpha),
        radius = 20f,
        center = Offset(x, cy)
    )
    // Inner tunic / parchment collar
    drawCircle(
        color = (if (isPainter) Color(0xFF1E3A3A) else Color(0xFF2A231C)).copy(alpha = alpha),
        radius = 15f,
        center = Offset(x, cy - 3f)
    )
    // Golden or Azure seal clasp on chest
    val claspColor = if (isPainter) Color(0xFF00E5FF) else Color(0xFFFFD700)
    drawCircle(
        color = claspColor.copy(alpha = alpha),
        radius = 4f,
        center = Offset(x, cy - 1f)
    )

    // 5. Dynamic Weapon (Quill / Brush) with organic sway and wet ink tip
    val swayAngle = facingAngle + sin(time * 7f) * 0.14f
    if (isPainter) {
        // Painter's heavy calligraphy wash brush
        val brushTip = Offset(x + cos(swayAngle) * 32f, cy + sin(swayAngle) * 32f)
        // Bamboo handle
        drawLine(
            color = Color(0xFF5D4037).copy(alpha = alpha),
            start = Offset(x, cy),
            end = brushTip,
            strokeWidth = 6.5f,
            cap = StrokeCap.Round
        )
        // Black horsehair bristle bundle
        val bristleTip = Offset(x + cos(swayAngle) * 38f, cy + sin(swayAngle) * 38f)
        drawLine(
            color = StarkBlackInk.copy(alpha = alpha),
            start = brushTip,
            end = bristleTip,
            strokeWidth = 9f,
            cap = StrokeCap.Round
        )
        // Vibrant cyan wet ink droplet that pulses
        val dropPulse = 4.2f + sin(time * 14f) * 1.2f
        drawCircle(
            color = Color(0xFF00E5FF),
            radius = dropPulse,
            center = bristleTip
        )
        drawCircle(
            color = Color.White,
            radius = 1.8f,
            center = Offset(bristleTip.x - 1f, bristleTip.y - 1f)
        )
    } else {
        // Calligrapher's sharp sumi-e quill
        val quillTip = Offset(x + cos(swayAngle) * 30f, cy + sin(swayAngle) * 30f)
        // Quill spine
        drawLine(
            color = Color(0xFFECEFF1).copy(alpha = alpha),
            start = Offset(x, cy),
            end = quillTip,
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )
        // Gold nib with ink reservoir
        val nibEnd = Offset(x + cos(swayAngle) * 34f, cy + sin(swayAngle) * 34f)
        drawLine(
            color = Color(0xFFFFD700).copy(alpha = alpha),
            start = quillTip,
            end = nibEnd,
            strokeWidth = 3f,
            cap = StrokeCap.Square
        )
        val dropPulse = 3.2f + sin(time * 14f) * 1f
        drawCircle(
            color = StarkBlackInk,
            radius = dropPulse,
            center = nibEnd
        )
    }
}

// ---------------- PLAYER MOTION TRAIL RENDERER ----------------

private fun DrawScope.drawPlayerMotionTrail(trail: PlayerMotionTrailNode, camX: Float, camY: Float) {
    val tx = trail.x + camX
    val ty = trail.y + camY
    val progress = (trail.life / trail.maxLife).coerceIn(0f, 1f)
    if (progress <= 0.01f) return

    val currentWidth = trail.width * progress
    val alpha = (progress * 0.72f).coerceIn(0f, 0.72f)

    // Calligraphic brush stroke oriented along movement angle
    val perpAngle = trail.angle + (PI / 2).toFloat()
    val dx = cos(perpAngle) * (currentWidth * 0.5f)
    val dy = sin(perpAngle) * (currentWidth * 0.5f)

    // Soft outer ink bleed
    drawLine(
        color = StarkBlackInk.copy(alpha = alpha * 0.35f),
        start = Offset(tx - dx * 1.35f, ty - dy * 1.35f),
        end = Offset(tx + dx * 1.35f, ty + dy * 1.35f),
        strokeWidth = 7f * progress,
        cap = StrokeCap.Round
    )

    // Dense black saturated core stroke
    drawLine(
        color = StarkBlackInk.copy(alpha = alpha),
        start = Offset(tx - dx, ty - dy),
        end = Offset(tx + dx, ty + dy),
        strokeWidth = 4f * progress,
        cap = StrokeCap.Round
    )

    // Faint trailing capillary droplet
    if (progress > 0.35f) {
        val dropDist = (1f - progress) * 14f
        drawCircle(
            color = StarkBlackInk.copy(alpha = alpha * 0.55f),
            radius = 2.8f * progress,
            center = Offset(tx - cos(trail.angle) * dropDist, ty - sin(trail.angle) * dropDist)
        )
    }
}

// ---------------- FLOWING INK SERPENT RENDERER ----------------

private fun DrawScope.drawFlowingSerpent(serpent: FlowingSerpentEntity, camX: Float, camY: Float) {
    val sx = serpent.x + camX
    val sy = serpent.y + camY
    val lifeRatio = (serpent.life / serpent.maxLife).coerceIn(0f, 1f)
    val alpha = (lifeRatio * 0.95f).coerceIn(0f, 1f)

    // 1. Draw flowing body segments from tail to head
    val segCount = serpent.segments.size
    for (i in (segCount - 1) downTo 0) {
        val seg = serpent.segments[i]
        val segX = seg.x + camX
        val segY = seg.y + camY
        val t = 1f - (i.toFloat() / segCount.coerceAtLeast(1))
        val segRadius = (serpent.radius * (0.35f + 0.65f * t) * lifeRatio).coerceAtLeast(3f)

        // Toxic dark ink ripple
        drawCircle(
            color = Color(0xFF0D1B1E).copy(alpha = alpha * 0.55f * t),
            radius = segRadius * 1.35f,
            center = Offset(segX, segY)
        )
        // Solid black ink body segment
        drawCircle(
            color = StarkBlackInk.copy(alpha = alpha * (0.5f + 0.5f * t)),
            radius = segRadius,
            center = Offset(segX, segY)
        )
        // Emerald/jade fluid spine scale
        if (i % 2 == 0) {
            drawCircle(
                color = Color(0xFF00E676).copy(alpha = alpha * 0.75f * t),
                radius = segRadius * 0.35f,
                center = Offset(segX, segY)
            )
        }
    }

    // 2. Draw Serpent Head
    val headRadius = serpent.radius * lifeRatio
    // Outer mist aura
    drawCircle(
        color = Color(0xFF004D40).copy(alpha = alpha * 0.4f),
        radius = headRadius * 1.45f,
        center = Offset(sx, sy)
    )
    // Dark core head
    drawCircle(
        color = StarkBlackInk.copy(alpha = alpha),
        radius = headRadius,
        center = Offset(sx, sy)
    )

    // Dragon eye
    val eyeAngle = serpent.currentAngle + 0.3f
    val eyeDist = headRadius * 0.55f
    val eyePos = Offset(sx + cos(eyeAngle) * eyeDist, sy + sin(eyeAngle) * eyeDist)
    drawCircle(
        color = Color(0xFF00E676).copy(alpha = alpha),
        radius = 4f * lifeRatio,
        center = eyePos
    )
    drawCircle(
        color = Color.White.copy(alpha = alpha),
        radius = 1.8f * lifeRatio,
        center = eyePos
    )

    // Flowing ink whiskers / barbels
    val whiskerWiggle = sin(serpent.waveTimer * 14f) * 6f
    val wAngle1 = serpent.currentAngle + 2.2f
    val wAngle2 = serpent.currentAngle - 2.2f
    drawLine(
        color = StarkBlackInk.copy(alpha = alpha * 0.8f),
        start = Offset(sx, sy),
        end = Offset(
            sx + cos(wAngle1) * (headRadius * 1.8f) - sin(wAngle1) * whiskerWiggle,
            sy + sin(wAngle1) * (headRadius * 1.8f) + cos(wAngle1) * whiskerWiggle
        ),
        strokeWidth = 2.5f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = StarkBlackInk.copy(alpha = alpha * 0.8f),
        start = Offset(sx, sy),
        end = Offset(
            sx + cos(wAngle2) * (headRadius * 1.8f) + sin(wAngle2) * whiskerWiggle,
            sy + sin(wAngle2) * (headRadius * 1.8f) - cos(wAngle2) * whiskerWiggle
        ),
        strokeWidth = 2.5f,
        cap = StrokeCap.Round
    )
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

// ---------------- FLUID SIMULATION INK SPLASH PARTICLES (CANVAS & SHADERS) ----------------

private fun DrawScope.drawFluidInkSplash(
    p: InkSplashParticle,
    camX: Float,
    camY: Float
) {
    val sx = p.x + camX
    val sy = p.y + camY
    // Frustum culling
    if (sx < -60f || sx > size.width + 60f || sy < -60f || sy > size.height + 60f) return

    val lifeAlpha = (p.life / p.maxLife).coerceIn(0f, 1f)
    val speed = kotlin.math.hypot(p.vx, p.vy)
    val angle = kotlin.math.atan2(p.vy, p.vx)

    val primaryColor = p.color.copy(alpha = lifeAlpha)
    val secondaryColor = p.secondaryColor.copy(alpha = lifeAlpha * 0.75f)
    val rimColor = if (p.isCrit) Color(0xFFFF5252).copy(alpha = lifeAlpha * 0.45f) else Color(0xFF1E1E28).copy(alpha = lifeAlpha * 0.25f)

    // Viscous Fluid Shader: Multi-stop Radial Gradient mimicking dense sumi-e ink droplet diffusion
    val fluidShaderBrush = Brush.radialGradient(
        colors = listOf(
            primaryColor,
            secondaryColor,
            rimColor,
            Color.Transparent
        ),
        center = Offset(sx, sy),
        radius = (p.radius * 1.55f).coerceAtLeast(4f)
    )

    // When particle is traveling rapidly, simulate aerodynamic fluid droplet elongation & trailing tendril
    if (speed > 20f) {
        val stretch = (1f + (speed / 130f)).coerceAtMost(3.2f)
        val cosA = cos(angle)
        val sinA = sin(angle)

        val path = Path().apply {
            val headX = sx + cosA * (p.radius * stretch * 0.6f)
            val headY = sy + sinA * (p.radius * stretch * 0.6f)
            val tailX = sx - cosA * (p.radius * stretch * 1.1f)
            val tailY = sy - sinA * (p.radius * stretch * 1.1f)
            val perpX = -sinA * p.radius
            val perpY = cosA * p.radius

            moveTo(headX, headY)
            quadraticBezierTo(sx + perpX, sy + perpY, tailX, tailY)
            quadraticBezierTo(sx - perpX, sy - perpY, headX, headY)
            close()
        }
        drawPath(path = path, brush = fluidShaderBrush)

        // Trailing capillary micro-tendril
        val tendrilLen = p.tendrilLength * lifeAlpha
        if (tendrilLen > 1f) {
            val tendrilTailX = sx - cosA * (p.radius * stretch + tendrilLen)
            val tendrilTailY = sy - sinA * (p.radius * stretch + tendrilLen)
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(primaryColor, Color.Transparent),
                    start = Offset(sx, sy),
                    end = Offset(tendrilTailX, tendrilTailY)
                ),
                start = Offset(sx, sy),
                end = Offset(tendrilTailX, tendrilTailY),
                strokeWidth = (p.radius * 0.45f).coerceAtLeast(1.2f),
                cap = StrokeCap.Round
            )
        }
    } else {
        // Viscous ink puddle droplet on paper
        drawCircle(
            brush = fluidShaderBrush,
            radius = (p.radius * 1.35f).coerceAtLeast(3f),
            center = Offset(sx, sy)
        )
        // High-density carbon ink core
        drawCircle(
            color = primaryColor,
            radius = (p.radius * 0.68f).coerceAtLeast(1.5f),
            center = Offset(sx, sy)
        )
    }

    // Micro splatter satellite
    if (p.isCrit) {
        drawCircle(
            color = Color(0x55FF1744),
            radius = (p.radius * 0.45f).coerceAtLeast(1.5f),
            center = Offset(sx + p.vx * 0.035f, sy + p.vy * 0.035f)
        )
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
        OccultCompassDialAndKnob(dragOffset = dragOffset)
    }
}

@Composable
fun OccultCompassDialAndKnob(
    dragOffset: Offset
) {
    Box(
        modifier = Modifier
            .size(150.dp)
            .testTag("virtual_analog_stick"),
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

// Subtle Elemental Status Effect HUD above the Player Character displaying active stacks
private fun DrawScope.drawPlayerElementalStatusHud(
    x: Float,
    y: Float,
    elementalStacks: Map<CalligraphicElement, Int>,
    time: Float
) {
    val activeElements = elementalStacks.filter { it.value > 0 }.entries.toList()
    if (activeElements.isEmpty()) return

    val iconWidth = 32f
    val totalWidth = activeElements.size * iconWidth
    val startX = x - (totalWidth / 2f)
    val floatOffset = kotlin.math.sin(time * 5f) * 2f
    val hudY = y - 56f + floatOffset

    // 1. Subtle brush-painted parchment pill backing
    val bgPaddingX = 8f
    val bgHeight = 22f
    drawRoundRect(
        color = Color(0xDD0D0D14),
        topLeft = Offset(startX - bgPaddingX, hudY - bgHeight / 2f),
        size = androidx.compose.ui.geometry.Size(totalWidth + (bgPaddingX * 2), bgHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(11f, 11f)
    )
    drawRoundRect(
        color = Color(0x66FFD54F),
        topLeft = Offset(startX - bgPaddingX, hudY - bgHeight / 2f),
        size = androidx.compose.ui.geometry.Size(totalWidth + (bgPaddingX * 2), bgHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(11f, 11f),
        style = Stroke(width = 1f)
    )

    // 2. Minimalist brush-stroke icons & stack dots
    activeElements.forEachIndexed { idx, (element, stacks) ->
        val iconCenterX = startX + (idx * iconWidth) + (iconWidth / 2f)
        val iconCenterY = hudY
        val elColor = when (element) {
            CalligraphicElement.FROST -> Color(0xFF00E5FF)
            CalligraphicElement.CINNABAR_FLAME -> Color(0xFFFF3D00)
            CalligraphicElement.CORROSIVE_ACID -> Color(0xFF00E676)
            CalligraphicElement.CELESTIAL_ASTRAL -> Color(0xFFFFD54F)
        }

        // Draw Minimalist Brushstroke Icon
        when (element) {
            CalligraphicElement.FROST -> {
                // Frost: 6-pointed brush snowflake needle
                for (a in 0 until 3) {
                    val angle = a * (kotlin.math.PI / 3).toFloat()
                    val dx = kotlin.math.cos(angle) * 5.5f
                    val dy = kotlin.math.sin(angle) * 5.5f
                    drawLine(
                        color = elColor,
                        start = Offset(iconCenterX - dx, iconCenterY - dy),
                        end = Offset(iconCenterX + dx, iconCenterY + dy),
                        strokeWidth = 1.8f,
                        cap = StrokeCap.Round
                    )
                }
            }
            CalligraphicElement.CINNABAR_FLAME -> {
                // Flame: Curling calligraphy lick
                val p = Path().apply {
                    moveTo(iconCenterX, iconCenterY - 6f)
                    quadraticBezierTo(iconCenterX + 4f, iconCenterY - 1f, iconCenterX + 2f, iconCenterY + 4f)
                    quadraticBezierTo(iconCenterX - 2f, iconCenterY + 6f, iconCenterX - 4f, iconCenterY + 1f)
                    close()
                }
                drawPath(path = p, color = elColor)
            }
            CalligraphicElement.CORROSIVE_ACID -> {
                // Acid: Teardrop droplet & splatter
                drawCircle(color = elColor, radius = 3.5f, center = Offset(iconCenterX, iconCenterY + 1f))
                drawCircle(color = elColor, radius = 1.5f, center = Offset(iconCenterX, iconCenterY - 4f))
            }
            CalligraphicElement.CELESTIAL_ASTRAL -> {
                // Astral: Delicate orbiting star ring with core glyph
                drawCircle(color = elColor, radius = 4.5f, center = Offset(iconCenterX, iconCenterY), style = Stroke(width = 1.4f))
                drawCircle(color = Color.White, radius = 1.6f, center = Offset(iconCenterX, iconCenterY))
            }
        }

        // Stack Tally Dots (1 to 5 tiny calligraphic dots)
        val clampedStacks = stacks.coerceIn(1, 5)
        val dotRadius = 1.2f
        val dotSpacing = 3f
        val startDotX = iconCenterX - ((clampedStacks - 1) * dotSpacing / 2f)
        for (s in 0 until clampedStacks) {
            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                radius = dotRadius,
                center = Offset(startDotX + s * dotSpacing, iconCenterY + 7f)
            )
        }
    }
}

// High-Contrast Ink-Brush Splash Animation (Triggered by reactions like Frozen Ink)
private fun DrawScope.drawInkBrushSplashParticle(splash: InkBrushSplashParticle, camX: Float, camY: Float) {
    val px = splash.x + camX
    val py = splash.y + camY
    val lifeRatio = (splash.life / splash.maxLife).coerceIn(0f, 1f)
    if (lifeRatio <= 0f) return

    val angle = kotlin.math.atan2(splash.vy, splash.vx)
    val length = (splash.radius * 2.6f) * (0.4f + lifeRatio * 0.6f)
    val width = splash.radius * lifeRatio

    // Outer elemental radiant aura
    drawLine(
        color = splash.elementalGlowColor.copy(alpha = 0.55f * lifeRatio),
        start = Offset(px - kotlin.math.cos(angle) * (length * 0.4f), py - kotlin.math.sin(angle) * (length * 0.4f)),
        end = Offset(px + kotlin.math.cos(angle) * length, py + kotlin.math.sin(angle) * length),
        strokeWidth = width * 1.8f,
        cap = StrokeCap.Round
    )

    // Stark stark-black / stark-contrast sumi-e core brush stroke
    drawLine(
        color = splash.starkColor.copy(alpha = 0.95f * lifeRatio),
        start = Offset(px - kotlin.math.cos(angle) * (length * 0.2f), py - kotlin.math.sin(angle) * (length * 0.2f)),
        end = Offset(px + kotlin.math.cos(angle) * (length * 0.85f), py + kotlin.math.sin(angle) * (length * 0.85f)),
        strokeWidth = width,
        cap = StrokeCap.Round
    )

    // Sharp white ink speckle accent at bristle tip
    drawCircle(
        color = Color.White.copy(alpha = 0.8f * lifeRatio),
        radius = (width * 0.25f).coerceAtLeast(1f),
        center = Offset(px + kotlin.math.cos(angle) * length, py + kotlin.math.sin(angle) * length)
    )
}

// High-Contrast Impact Frame / Shake Feedback Overlay
private fun DrawScope.drawImpactFrameOverlay(
    reaction: ElementalReactionType?,
    timer: Float
) {
    val progress = (timer / 0.08f).coerceIn(0f, 1f)
    if (progress <= 0f) return

    val flashAlpha = (progress * 0.65f)
    val (primaryColor, accentColor) = when (reaction) {
        ElementalReactionType.FROZEN_INK -> Color(0xFF001220) to Color(0xFF00E5FF)
        ElementalReactionType.BURNING_CALLIGRAPHY -> Color(0xFF280202) to Color(0xFFFF3D00)
        ElementalReactionType.COSMIC_SUPERNOVA -> Color(0xFF140828) to Color(0xFFFFD54F)
        ElementalReactionType.PERMAFROST_BLOSSOM -> Color(0xFF001A2C) to Color(0xFF80D8FF)
        ElementalReactionType.THERMAL_SHOCK -> Color(0xFF2C0210) to Color(0xFFFF1744)
        null -> Color(0xFF000000) to Color.White
    }

    // High-contrast negative tint flash
    drawRect(
        color = primaryColor.copy(alpha = flashAlpha)
    )

    // Diagonal calligraphic speed slash lines for dramatic impact
    val w = size.width
    val h = size.height
    drawLine(
        color = accentColor.copy(alpha = flashAlpha * 0.8f),
        start = Offset(0f, h * 0.25f),
        end = Offset(w, h * 0.75f),
        strokeWidth = 3f * progress,
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color.White.copy(alpha = flashAlpha * 0.9f),
        start = Offset(w * 0.1f, 0f),
        end = Offset(w * 0.9f, h),
        strokeWidth = 2f * progress,
        cap = StrokeCap.Round
    )
}


