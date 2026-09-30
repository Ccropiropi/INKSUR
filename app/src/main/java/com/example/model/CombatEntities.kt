package com.example.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class InkProjectile(
    val id: Long,
    var x: Float,
    var y: Float,
    val vx: Float,
    val vy: Float,
    val angleRad: Float,
    val damage: Float,
    var pierceCount: Int,
    var life: Float = 1.8f,
    var strokeWidth: Float = 12f,
    var strokeLength: Float = 36f,
    val isFlexNib: Boolean = false,
    val isSerratedNib: Boolean = false,
    val isHarpoon: Boolean = false,
    val isBarrage: Boolean = false,
    val isUltimate: Boolean = false,
    val sourceSpellId: String = "quill_dart",
    val hasViscousRune: Boolean = false
) {
    val hitEnemyIds: MutableSet<Long> = mutableSetOf()
}

data class InkPuddle(
    var active: Boolean = false,
    var id: Long = 0L,
    var x: Float = 0f,
    var y: Float = 0f,
    var radius: Float = 0f,
    var damage: Float = 0f,
    var life: Float = 0f,
    var maxLife: Float = 4.0f,
    var tickInterval: Float = 0.5f,
    var tickTimer: Float = 0f,
    var sourceSpellId: String = "wash_brush",
    var hasViscousRune: Boolean = false
) {
    val position: Offset get() = Offset(x, y)
}

// Preallocated high-performance pool for ink puddles
class InkPuddlePool(private val capacity: Int = 200) {
    val pool: Array<InkPuddle> = Array(capacity) { InkPuddle() }
    private var idSequence: Long = 0L

    fun obtain(
        x: Float,
        y: Float,
        radius: Float,
        damage: Float,
        maxLife: Float = 4.0f,
        tickInterval: Float = 0.5f,
        sourceSpellId: String = "wash_brush",
        hasViscousRune: Boolean = false
    ): InkPuddle {
        var target: InkPuddle? = null
        for (i in 0 until capacity) {
            if (!pool[i].active) {
                target = pool[i]
                break
            }
        }
        if (target == null) {
            var oldestIdx = 0
            var maxAge = -1f
            for (i in 0 until capacity) {
                if (pool[i].life > maxAge) {
                    maxAge = pool[i].life
                    oldestIdx = i
                }
            }
            target = pool[oldestIdx]
        }

        target.active = true
        target.id = ++idSequence
        target.x = x
        target.y = y
        target.radius = radius
        target.damage = damage
        target.life = 0f
        target.maxLife = maxLife
        target.tickInterval = tickInterval
        target.tickTimer = 0f
        target.sourceSpellId = sourceSpellId
        target.hasViscousRune = hasViscousRune

        return target
    }

    fun clear() {
        for (i in 0 until capacity) {
            pool[i].active = false
        }
    }
}

// Red Rune: Opt-In Elite Spawner
data class RedRuneEntity(
    val id: Long,
    var x: Float,
    var y: Float,
    val radius: Float = 22f,
    var pulseTimer: Float = 0f
)

// Obelisk: Spawns upon defeating an Elite pack from Red Rune
data class ObeliskEntity(
    val id: Long,
    var x: Float,
    var y: Float,
    val radius: Float = 26f,
    var pulseTimer: Float = 0f
)

// Broken Stone: In-run shop offering lower-tier artifacts for Orbs
data class BrokenStoneEntity(
    val id: Long,
    var x: Float,
    var y: Float,
    val radius: Float = 24f,
    val orbCost: Int = 15,
    var pulseTimer: Float = 0f
)

// Spell Rune dropped by the last Elite
data class SpellRuneDrop(
    val id: Long,
    var x: Float,
    var y: Float,
    val runeType: SpellRuneType = SpellRuneType.VISCOUS_RUNE,
    val radius: Float = 20f,
    var pulseTimer: Float = 0f
)

// Phase 4 Guaranteed Drop from 25:00 Mid-Boss: The Blank Scroll
data class BlankScrollDrop(
    val id: Long,
    var x: Float,
    var y: Float,
    val radius: Float = 24f,
    var pulseTimer: Float = 0f
)

// Phase 4 Inkwell Vortex: Gravitational swirling storm
data class InkwellVortexEntity(
    val id: Long,
    var x: Float,
    var y: Float,
    var radius: Float = 240f,
    val damage: Float = 120f,
    var life: Float = 5.0f,
    var angle: Float = 0f,
    var tickTimer: Float = 0f,
    val tickInterval: Float = 0.35f,
    val hasViscousRune: Boolean = false
)

// Phase 4 Level 100 "Magnum Opus" Calligraphy Stroke Execution
data class MagnumOpusVisual(
    var active: Boolean = false,
    var timer: Float = 0f,
    val maxDuration: Float = 2.4f,
    var flashAlpha: Float = 1.0f
)

// Temporary visual for Wash Brush sweep arc
data class WashBrushArcVisual(
    val id: Long,
    val x: Float,
    val y: Float,
    val angleRad: Float,
    val arcSpanRad: Float,
    val radius: Float,
    var life: Float = 0.22f
)

// Regular Orb and Performance-Optimized Condensed Orb (drops 50 value in a single physics entity)
data class Orb(
    val id: Long,
    var x: Float,
    var y: Float,
    val value: Int = 1,
    var vx: Float = 0f,
    var vy: Float = 0f,
    val isCondensed: Boolean = false,
    var pulseTimer: Float = 0f
)

data class DamageNumber(
    val id: Long,
    var x: Float,
    var y: Float,
    val text: String,
    val color: Color,
    val isCritical: Boolean = false,
    var life: Float = 0.8f,
    var vy: Float = -40f
)
