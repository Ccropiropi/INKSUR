package com.example.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class InkProjectile(
    val id: Long,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var angleRad: Float,
    var damage: Float,
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
    val hasViscousRune: Boolean = false,
    // Phase 5 Sacred Geometry Bounce Mechanics
    var bounceRemaining: Int = 0,
    var bounceDamageMultiplier: Float = 1.0f,
    var isSacredGeometry: Boolean = false,
    var isGeometryCured: Boolean = false
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

    val activeCount: Int
        get() = pool.count { it.active }

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

// Orbital Runes revolving around player
data class OrbitalRuneEntity(
    val id: Long,
    var orbitAngle: Float = 0f,
    var orbitRadius: Float = 95f,
    var damage: Float = 28f,
    var hitCooldownTimer: Float = 0f,
    var hasViscous: Boolean = false
)

// Cinnabar Seal detonation glyph
data class CinnabarSealEntity(
    val id: Long,
    val x: Float,
    val y: Float,
    val radius: Float = 110f,
    val damage: Float = 80f,
    var timer: Float = 0f,
    val fuseTime: Float = 0.85f,
    var detonated: Boolean = false,
    var blastTimer: Float = 0f,
    val blastDuration: Float = 0.35f,
    var hasViscous: Boolean = false
)

// ==========================================
// PHASE 5 ENTITIES
// ==========================================

// 1. The Mid-Run Checkpoint (The Inkwell) at Minute 30:00
data class InkwellStructure(
    val id: Long,
    var x: Float = 0f,
    var y: Float = 0f,
    val radius: Float = 42f,
    var pulseTimer: Float = 0f
)

// 2. The Blotter Burst AOE Visual
data class BlotterBurstVisual(
    val id: Long,
    val x: Float,
    val y: Float,
    val radius: Float = 180f,
    var timer: Float = 0f,
    val maxDuration: Float = 0.5f
)

// 3. The Minute 60 Climax: The Eraser Boss & Telegraphed Geometric Strikes
data class TelegraphedStrike(
    val id: Long,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val lineWidth: Float = 26f,
    var timer: Float = 0f,
    val telegraphDuration: Float = 1.2f,
    val strikeDuration: Float = 0.4f,
    var hasDealtDamage: Boolean = false,
    var isFinished: Boolean = false
) {
    val isStriking: Boolean get() = timer >= telegraphDuration && !isFinished
}

data class TheEraserBoss(
    var active: Boolean = false,
    var timer: Float = 0f,
    val maxDuration: Float = 60.0f,
    var currentSafeRadius: Float = 600f,
    val initialSafeRadius: Float = 600f,
    val minSafeRadius: Float = 170f,
    var strikeTimer: Float = 0f,
    val telegraphedStrikes: MutableList<TelegraphedStrike> = mutableListOf()
) {
    fun reset() {
        active = false
        timer = 0f
        currentSafeRadius = initialSafeRadius
        strikeTimer = 0f
        telegraphedStrikes.clear()
    }
}
