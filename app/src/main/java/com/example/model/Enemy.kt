package com.example.model

import androidx.compose.ui.geometry.Offset

enum class EnemyType(
    val displayName: String,
    val baseHp: Float,
    val speed: Float,
    val radius: Float,
    val xpValue: Int,
    val damage: Float
) {
    BASIC_CONSTRUCT(
        displayName = "Paper Construct",
        baseHp = 35f,
        speed = 105f,
        radius = 16f,
        xpValue = 1,
        damage = 10f
    ),
    FOLDED_STALKER(
        displayName = "Folded Stalker",
        baseHp = 60f,
        speed = 125f,
        radius = 18f,
        xpValue = 1,
        damage = 14f
    ),
    PAPER_BRUTE(
        displayName = "Paper Brute",
        baseHp = 180f,
        speed = 65f,
        radius = 28f,
        xpValue = 2,
        damage = 22f
    ),
    THE_TITAN(
        displayName = "The Titan",
        baseHp = 3500f,
        speed = 42f,
        radius = 58f,
        xpValue = 50,
        damage = 35f
    ),
    // Phase 4 Guaranteed Mid-Boss at 25:00
    MID_BOSS_COLOSSUS(
        displayName = "Scrollkeeper Colossus",
        baseHp = 6500f,
        speed = 52f,
        radius = 54f,
        xpValue = 100,
        damage = 40f
    ),
    // Phase 5 Performance-as-a-Mechanic: The Blotter sponge enemy
    THE_BLOTTER(
        displayName = "The Blotter",
        baseHp = 800f,
        speed = 90f,
        radius = 32f,
        xpValue = 15,
        damage = 0f
    ),

    // ─── Phase 7: Adaptive Enemies (Director AI counter-picks) ───────────────
    // Spawned at minute 20 if player focuses Piercing/Projectile DPS.
    // Immune to directional projectiles; dissolve instantly in ink puddles.
    ORIGAMI_SHIELD(
        displayName = "Origami Shield",
        baseHp = 320f,
        speed = 80f,
        radius = 24f,
        xpValue = 8,
        damage = 16f
    ),

    // Spawned at minute 40 if player focuses Puddle/AOE DPS.
    // Step over puddles (immune to AOE); must be hit by direct projectiles.
    STILT_WALKER(
        displayName = "Stilt-Walker",
        baseHp = 240f,
        speed = 115f,
        radius = 18f,
        xpValue = 6,
        damage = 18f
    )
}

data class Enemy(
    val id: Long,
    val type: EnemyType = EnemyType.BASIC_CONSTRUCT,
    var x: Float,
    var y: Float,
    var hp: Float,
    val maxHp: Float,
    var vx: Float = 0f,
    var vy: Float = 0f,
    var attackCooldown: Float = 0f,
    var flashTimer: Float = 0f,
    // Elite system (Red Rune)
    var isElite: Boolean = false,
    var elitePackId: Long? = null,
    // Status effects
    var bleedTimer: Float = 0f,
    var bleedTickTimer: Float = 0f,
    var bleedDamagePerTick: Float = 0f,
    var slowTimer: Float = 0f,
    var slowRatio: Float = 0f,
    var isMidBoss: Boolean = false,
    // Phase 5 Blotter sponge mechanics
    var isBlotter: Boolean = false,
    var absorbedPuddles: Int = 0,
    // ─── Phase 7: Adaptive enemy flags ────────────────────────────────────────
    // ORIGAMI_SHIELD: true = immune to InkProjectile hits; dissolve in puddles
    var isImmuneToPiercing: Boolean = false,
    // STILT_WALKER: true = immune to puddle ticks; can only be hit by projectiles
    var isImmuneToAOE: Boolean = false,
    // Phase 7 Canvas Saturation: sliding physics override
    var saturationVx: Float = 0f,
    var saturationVy: Float = 0f,
    // ─── Phase 8: CMYK Pigment status effects ─────────────────────────────────
    var cyanTimer: Float = 0f,        // Chill/Slow
    var magentaTimer: Float = 0f,     // Corrosive / Armor Shred (reduces effective HP)
    var yellowTimer: Float = 0f,      // Conductive (chains lightning on hit)
    var pigmentReactionCooldown: Float = 0f  // Prevents double-reaction per frame
) {
    val position: Offset get() = Offset(x, y)
    val isDead: Boolean get() = hp <= 0f
    val isBoss: Boolean get() = type == EnemyType.THE_TITAN || type == EnemyType.MID_BOSS_COLOSSUS || isMidBoss

    val effectiveSpeed: Float
        get() {
            var spd = if (isElite) type.speed * 1.5f else type.speed
            if (slowTimer > 0f) {
                spd *= (1f - slowRatio).coerceAtLeast(0.1f)
            }
            // Phase 8 Cyan: additional chill slow (-35%)
            if (cyanTimer > 0f) {
                spd *= 0.65f
            }
            return spd
        }

    /** Phase 8: Active pigment count — triggers secondary CMYK reactions */
    val activePigmentCount: Int
        get() = (if (cyanTimer > 0f) 1 else 0) +
                (if (magentaTimer > 0f) 1 else 0) +
                (if (yellowTimer > 0f) 1 else 0)
}
