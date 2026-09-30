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
    var isMidBoss: Boolean = false
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
            return spd
        }
}
