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
        baseHp = 450f,
        speed = 55f,
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
    var pigmentReactionCooldown: Float = 0f,  // Prevents double-reaction per frame
    // ─── Elemental Interaction System ──────────────────────────────────
    var frostTimer: Float = 0f,              // Chilled (slowed by 40%, prime for Frozen Ink)
    var flameTimer: Float = 0f,              // Burning with volatile cinnabar flames
    var burningTickTimer: Float = 0f,
    var burningDamagePerTick: Float = 0f,
    var acidTimer: Float = 0f,               // Soaked in liquid ink / corrosive wash
    var astralTimer: Float = 0f,             // Marked with celestial gravity sigil
    var frozenSolidTimer: Float = 0f,        // Completely frozen in black ice (immobile, +60% shatter crit)
    var elementalReactionCooldown: Float = 0f // Internal reaction threshold cooldown
) {
    val position: Offset get() = Offset(x, y)
    val isDead: Boolean get() = hp <= 0f
    val isBoss: Boolean get() = type == EnemyType.THE_TITAN || type == EnemyType.MID_BOSS_COLOSSUS || isMidBoss

    val isFrozen: Boolean get() = frozenSolidTimer > 0f

    val effectiveSpeed: Float
        get() {
            if (frozenSolidTimer > 0f) return 0f // Frozen solid in black ice!
            var spd = if (isElite) type.speed * 1.5f else type.speed
            if (slowTimer > 0f) {
                spd *= (1f - slowRatio).coerceAtLeast(0.1f)
            }
            if (frostTimer > 0f) {
                spd *= 0.60f // Glacial chill slow (-40%)
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

/**
 * Sumi-e Codex entry containing rich lore, high-contrast concept art parameters,
 * tactical weaknesses, and combat strategies for each defeated paper horror.
 */
data class CodexEntry(
    val type: EnemyType,
    val title: String,
    val subtitle: String,
    val kanjiStamp: String,
    val threatTier: String,
    val loreText: String,
    val behaviorDetails: String,
    val primaryWeakness: String,
    val recommendedElement: CalligraphicElement,
    val counterStrategy: String
)

object CodexDatabase {
    val allEntries = listOf(
        CodexEntry(
            type = EnemyType.BASIC_CONSTRUCT,
            title = "Paper Construct",
            subtitle = "Creased Pawn of the Void",
            kanjiStamp = "折兵",
            threatTier = "Minor Swarm",
            loreText = "Born from discarded drafting sheets and crumpled manuscript margins, these mindless origami drones march forward in relentless waves to smother the artist's canvas.",
            behaviorDetails = "Direct pursuit of the player character. Individual units have low durability but form suffocating migratory swarms as time elapses.",
            primaryWeakness = "Corrosive Wash & Puddle DoT",
            recommendedElement = CalligraphicElement.CORROSIVE_ACID,
            counterStrategy = "Lay persistent ink puddles with the Wash Brush or Calligrapher's Wake to dissolve entire clusters without wasting projectile pierce."
        ),
        CodexEntry(
            type = EnemyType.FOLDED_STALKER,
            title = "Folded Stalker",
            subtitle = "Predatory Origami Crane",
            kanjiStamp = "剪鹤",
            threatTier = "Agile Flanker",
            loreText = "Folded with precision-creased wing blades, the Stalker glides silently across the ruled paper surface, flanking and ambushing scribes while they are distracted by frontline hordes.",
            behaviorDetails = "High-velocity curvilinear approach. Frequently attempts to cut off escape corridors and corner the player against swarm walls.",
            primaryWeakness = "Glacial Frost Chill & Piercing Needles",
            recommendedElement = CalligraphicElement.FROST,
            counterStrategy = "Use sub-zero ink needles from Quill Dart or Steel Fountain to chill and slow their swift approach before trigger-freezing them."
        ),
        CodexEntry(
            type = EnemyType.PAPER_BRUTE,
            title = "Paper Brute",
            subtitle = "Layered Cardboard Juggernaut",
            kanjiStamp = "甲魔",
            threatTier = "Heavy Vanguard",
            loreText = "Reinforced with thick industrial binding glue and multiple plies of corrugated stock, the Brute absorbs direct frontal impacts with terrifying resilience.",
            behaviorDetails = "Slow but relentless advance. Acts as a moving paper shield for swarms behind it, absorbing projectiles and body-blocking pathing.",
            primaryWeakness = "Cinnabar Flame & Burning Conflagration",
            recommendedElement = CalligraphicElement.CINNABAR_FLAME,
            counterStrategy = "Ignite wet ink pools beneath the Brute with volatile Cinnabar Seals or Phoenix strikes. The conflagration rapidly disintegrates heavy paper fibers."
        ),
        CodexEntry(
            type = EnemyType.THE_TITAN,
            title = "The Titan",
            subtitle = "Monolithic Paper Colossus",
            kanjiStamp = "泰坦",
            threatTier = "Catastrophic Boss",
            loreText = "A massive abomination stitched together from hundreds of ancient forbidden scrolls. Revolving runic glyphs orbit its perimeter, crushing everything in its trajectory.",
            behaviorDetails = "Spawns at minute 15:00. Emits dense runic orbiting rings, massive health pool, and heavy contact damage that overwhelms careless scribes.",
            primaryWeakness = "Thermal Shock & Evolved Syntheses",
            recommendedElement = CalligraphicElement.CELESTIAL_ASTRAL,
            counterStrategy = "Alternate between extreme frost and cinnabar flame to trigger Thermal Shock vapor explosions while maintaining circular kiting distance."
        ),
        CodexEntry(
            type = EnemyType.MID_BOSS_COLOSSUS,
            title = "Scrollkeeper Colossus",
            subtitle = "Guardian of the 25:00 Threshold",
            kanjiStamp = "巨神",
            threatTier = "Archive Gatekeeper",
            loreText = "The ancient guardian of the Blank Scroll. Unfurling sacred papyrus bindings across the void, it tests whether the scribe is worthy of divine mastery.",
            behaviorDetails = "Guaranteed encounter at minute 25:00. Defeating this mid-boss is the only way to obtain the legendary Blank Scroll and unlock Tier 2-3 ink orbs.",
            primaryWeakness = "Frozen Ink Cryo-Shatter (+60% Crit)",
            recommendedElement = CalligraphicElement.FROST,
            counterStrategy = "Prime with wet corrosive ink, then freeze solid in black ice. Unleash high-tier crits on its brittle frozen form to crack its defenses."
        ),
        CodexEntry(
            type = EnemyType.THE_BLOTTER,
            title = "The Blotter",
            subtitle = "Porous Cellulose Sponge",
            kanjiStamp = "汲墨",
            threatTier = "Tactical Hazard",
            loreText = "A parasitic porous sponge construct that feeds on the player's ink. It actively pathfinds to wet puddles, drinking them dry and growing ever more dangerous.",
            behaviorDetails = "Does not chase the player directly; instead hunts down dense ink puddle clusters. Upon absorbing sufficient ink, it detonates in a corrosive acid burst.",
            primaryWeakness = "High-Velocity Direct Projectiles",
            recommendedElement = CalligraphicElement.FROST,
            counterStrategy = "Eliminate swiftly with high-velocity steel needles or quill darts before it can consume your defensive ink puddle network."
        ),
        CodexEntry(
            type = EnemyType.ORIGAMI_SHIELD,
            title = "Origami Shield",
            subtitle = "Reflective Folded Aegis",
            kanjiStamp = "纸盾",
            threatTier = "Adaptive Counter-Pick",
            loreText = "Designed by the void to counter projectile-heavy scribes. Its mirror-like paper creases deflect all direct piercing ink darts and needles harmlessly.",
            behaviorDetails = "Immune to directional piercing projectiles. Spawns when the player focuses heavily on single-target projectile DPS.",
            primaryWeakness = "Ground Ink Puddles & Wash Sweeps",
            recommendedElement = CalligraphicElement.CORROSIVE_ACID,
            counterStrategy = "Do not waste darts. Guide them into dense ink puddles or use the sweeping Wash Brush arc to dissolve their paper base instantly."
        ),
        CodexEntry(
            type = EnemyType.STILT_WALKER,
            title = "Stilt-Walker",
            subtitle = "Long-Legged Heron Construct",
            kanjiStamp = "鹭步",
            threatTier = "Adaptive Counter-Pick",
            loreText = "Elevated on elongated timber and paper stilt poles, this tall heron construct struts high above ground-level ink, stepping over puddles without taking damage.",
            behaviorDetails = "Immune to AOE ground puddles. Strides directly through hazards toward the player at brisk pace.",
            primaryWeakness = "Direct Projectiles & Needle Storms",
            recommendedElement = CalligraphicElement.CELESTIAL_ASTRAL,
            counterStrategy = "Target their tall bodies with direct quill darts, Harpoon spears, or rotating celestial runes to snap their stilt legs."
        )
    )

    fun getEntry(type: EnemyType): CodexEntry? {
        return allEntries.find { it.type == type }
    }
}
