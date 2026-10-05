package com.example.ui

import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * Controller to manage screen-wide Sumi-e Ink Bleed transitions across menu navigation and death.
 */
class InkBleedController(val coroutineScope: CoroutineScope) {
    var isActive by mutableStateOf(false)
        private set

    var bleedProgress by mutableFloatStateOf(0f)
        private set

    var originOffset by mutableStateOf<Offset?>(null)
        private set

    private var onHalfwayAction: (() -> Unit)? = null
    private var onFinishedAction: (() -> Unit)? = null

    fun startBleed(
        origin: Offset? = null,
        durationMillis: Int = 750,
        onHalfway: () -> Unit,
        onFinished: () -> Unit = {}
    ) {
        if (isActive) return
        isActive = true
        bleedProgress = 0f
        originOffset = origin
        onHalfwayAction = onHalfway
        onFinishedAction = onFinished

        coroutineScope.launch {
            val anim = Animatable(0f)
            // Phase 1: Bleed In (0f -> 0.5f: ink spreads to fully cover the screen)
            anim.animateTo(
                targetValue = 0.5f,
                animationSpec = tween(
                    durationMillis = durationMillis / 2,
                    easing = FastOutSlowInEasing
                )
            ) {
                bleedProgress = value
            }

            // At peak blackness, execute state switch
            bleedProgress = 0.5f
            onHalfwayAction?.invoke()
            onHalfwayAction = null

            // Phase 2: Bleed Out / Paper Absorption (0.5f -> 1.0f: ink absorbs/washes away)
            anim.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(
                    durationMillis = durationMillis / 2,
                    easing = FastOutSlowInEasing
                )
            ) {
                bleedProgress = value
            }

            isActive = false
            bleedProgress = 0f
            originOffset = null
            onFinishedAction?.invoke()
            onFinishedAction = null
        }
    }
}

@Composable
fun rememberInkBleedController(): InkBleedController {
    val scope = rememberCoroutineScope()
    return remember { InkBleedController(scope) }
}

/**
 * High-Contrast Sumi-e Ink Bleed Transition Shader / Canvas Overlay.
 * Simulates organic capillary diffusion of ink through raw washi paper fibers.
 */
@Composable
fun InkBleedTransitionOverlay(
    controller: InkBleedController,
    modifier: Modifier = Modifier
) {
    if (!controller.isActive && controller.bleedProgress <= 0f) return

    val progress = controller.bleedProgress
    val origin = controller.originOffset

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val center = origin ?: Offset(w / 2f, h / 2f)
        val maxDist = hypot(w, h) * 0.85f

        // Effective coverage: 0..1 during bleed-in (0f..0.5f), 1..0 during bleed-out (0.5f..1.0f)
        val coverage = if (progress <= 0.5f) {
            (progress / 0.5f).coerceIn(0f, 1f)
        } else {
            (1.0f - ((progress - 0.5f) / 0.5f)).coerceIn(0f, 1f)
        }

        if (coverage <= 0.001f) return@Canvas

        // 1. Draw Full Screen Black Wash if coverage is maximum
        if (coverage >= 0.98f) {
            drawRect(color = Color(0xFF07070B))
            return@Canvas
        }

        val baseRadius = maxDist * coverage * 1.35f

        // 2. Draw Organic Capillary Bleed Discs & Tendril Fronts
        drawInkBleedWavefront(
            center = center,
            baseRadius = baseRadius,
            coverage = coverage,
            screenWidth = w,
            screenHeight = h
        )
    }
}

/**
 * Procedural Sumi-e Capillary Wavefront Drawing.
 * Emulates the micro-dendritic spread of liquid carbon ink soaking into Xuan paper fibers.
 */
private fun DrawScope.drawInkBleedWavefront(
    center: Offset,
    baseRadius: Float,
    coverage: Float,
    screenWidth: Float,
    screenHeight: Float
) {
    val starkInk = Color(0xFF08080C)
    val deepCharcoal = Color(0xFF15151D)
    val washTint = Color(0xFF282834)

    // A. Broad Base Core Ink Wash
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                starkInk,
                starkInk,
                deepCharcoal.copy(alpha = 0.96f),
                washTint.copy(alpha = 0.70f),
                Color.Transparent
            ),
            center = center,
            radius = (baseRadius * 1.05f).coerceAtLeast(10f)
        ),
        radius = (baseRadius * 1.05f).coerceAtLeast(10f),
        center = center
    )

    // B. Organic Ray-Marched Bleed Edge with Paper Fiber Tendrils
    val steps = 72
    val bleedPath = Path()
    var isFirst = true

    for (i in 0..steps) {
        val angle = (i.toFloat() / steps.toFloat()) * (2f * PI.toFloat())
        // Multi-frequency harmonic perturbation to simulate fibrous paper resistance
        val harmonic1 = sin(angle * 3f + coverage * 4f) * 0.16f
        val harmonic2 = cos(angle * 7f - coverage * 3f) * 0.12f
        val harmonic3 = sin(angle * 13f) * 0.08f
        val dendriticSpike = if ((i % 5) == 0) sin(angle * 29f) * 0.18f else 0f

        val radiusScale = (1.0f + harmonic1 + harmonic2 + harmonic3 + dendriticSpike)
        val r = (baseRadius * radiusScale).coerceAtLeast(1f)
        val px = center.x + cos(angle) * r
        val py = center.y + sin(angle) * r

        if (isFirst) {
            bleedPath.moveTo(px, py)
            isFirst = false
        } else {
            bleedPath.lineTo(px, py)
        }
    }
    bleedPath.close()

    drawPath(
        path = bleedPath,
        color = starkInk
    )

    // C. Microscopic Tendril Capillaries Splattering Ahead of the Wavefront
    val tendrilCount = (24 * coverage).toInt().coerceIn(8, 36)
    for (t in 0 until tendrilCount) {
        val tAngle = (t.toFloat() / tendrilCount.toFloat()) * 2f * PI.toFloat() + (coverage * 1.5f)
        val startR = baseRadius * 0.85f
        val endR = baseRadius * (1.15f + (t % 4) * 0.08f)

        val sx = center.x + cos(tAngle) * startR
        val sy = center.y + sin(tAngle) * startR
        val ex = center.x + cos(tAngle) * endR
        val ey = center.y + sin(tAngle) * endR

        drawLine(
            color = deepCharcoal.copy(alpha = (0.85f * (1f - coverage * 0.2f))),
            start = Offset(sx, sy),
            end = Offset(ex, ey),
            strokeWidth = (5.5f * (1f - (t % 3) * 0.25f)).coerceAtLeast(1.5f),
            cap = StrokeCap.Round
        )

        // Micro-droplet at tendril tip
        drawCircle(
            color = starkInk,
            radius = (3.5f + (t % 3) * 1.5f),
            center = Offset(ex, ey)
        )
    }

    // D. Bleeding Spatter Accents Across Surrounding Parchment
    val spatterCount = 14
    for (s in 0 until spatterCount) {
        val sAngle = s * (2f * PI.toFloat() / spatterCount) + 0.35f
        val spatterDist = baseRadius * (1.1f + ((s * 7) % 5) * 0.06f)
        val spatterX = center.x + cos(sAngle) * spatterDist
        val spatterY = center.y + sin(sAngle) * spatterDist

        if (spatterX in 0f..screenWidth && spatterY in 0f..screenHeight) {
            drawCircle(
                color = deepCharcoal.copy(alpha = 0.75f),
                radius = 2.5f + ((s * 3) % 4),
                center = Offset(spatterX, spatterY)
            )
        }
    }
}
