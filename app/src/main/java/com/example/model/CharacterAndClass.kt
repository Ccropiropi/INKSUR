package com.example.model

data class CharacterDefinition(
    val id: String,
    val name: String,
    val title: String,
    val description: String,
    val projectileSpeedMultiplier: Float = 1.0f,
    val bonusPierce: Int = 0,
    val moveSpeedMultiplier: Float = 1.0f,
    val damageMultiplier: Float = 1.0f,
    val xpMultiplier: Float = 1.0f,
    val pickupRadiusMultiplier: Float = 1.0f,
    val cooldownMultiplier: Float = 1.0f,
    val bonusProjectiles: Int = 0,
    val crystalUnlockCost: Int = 0
) {
    companion object {
        // Character 1: The Calligrapher (+20% Projectile Speed, +1 Pierce) - Starter
        val TheCalligrapher = CharacterDefinition(
            id = "calligrapher",
            name = "The Calligrapher",
            title = "Master of the Nib",
            description = "Applies +20% Projectile Speed and +1 Pierce to all ink projectiles.",
            projectileSpeedMultiplier = 1.20f,
            bonusPierce = 1,
            crystalUnlockCost = 0
        )

        // Character 2: The Scholar (+15% XP Gain, +20% Pickup Radius) - Unlocked with 50 Crystals
        val TheScholar = CharacterDefinition(
            id = "scholar",
            name = "The Scholar",
            title = "Seeker of Ancient Glyphs",
            description = "Gains +15% XP from collected Orbs and +20% base Magnet Radius.",
            xpMultiplier = 1.15f,
            pickupRadiusMultiplier = 1.20f,
            crystalUnlockCost = 50
        )

        // Character 3: The Grandmaster (+1 Projectile, -15% Cooldown) - Unlocked with 150 Crystals
        val TheGrandmaster = CharacterDefinition(
            id = "grandmaster",
            name = "The Grandmaster",
            title = "Sovereign of the Black Sea",
            description = "All projectile spells fire +1 extra projectile with -15% Cooldown.",
            bonusProjectiles = 1,
            cooldownMultiplier = 0.85f,
            damageMultiplier = 1.10f,
            crystalUnlockCost = 150
        )

        val allCharacters = listOf(TheCalligrapher, TheScholar, TheGrandmaster)
    }
}

data class ClassDefinition(
    val id: String,
    val name: String,
    val role: String,
    val description: String,
    val starterSpell: SpellDefinition
) {
    companion object {
        val Scribe = ClassDefinition(
            id = "scribe",
            name = "Scribe",
            role = "Starter Class",
            description = "Wields the sharpened quill. Auto-fires Quill Dart in the direction of movement.",
            starterSpell = SpellDefinition.QuillDart
        )

        val Painter = ClassDefinition(
            id = "painter",
            name = "Painter",
            role = "AOE Fluid Specialist",
            description = "Wields the wide horsehair wash brush. Executes wide physics overlap sweeps leaving damaging ink puddles.",
            starterSpell = SpellDefinition.WashBrush
        )

        val allClasses = listOf(Scribe, Painter)
    }
}

// Trait modules for dynamic inheritance
interface SpellTraitModule {
    val id: String
    val name: String
    val description: String

    fun onProjectileTick(projectile: InkProjectile, dt: Float) {}
    fun onHitEnemy(enemy: Enemy, damageDealt: Float, projectile: InkProjectile?) {}
    fun onPuddleSpawn(puddle: InkPuddle) {}
}

enum class SpellTraitType(
    val id: String,
    val displayName: String,
    val description: String
) {
    // Quill Dart Traits (Level 3 Choice)
    SERRATED_NIB(
        id = "serrated_nib",
        displayName = "Serrated Nib",
        description = "Applies a damage-over-time (bleed) effect to enemies hit for 3.0s."
    ),
    FLEX_NIB(
        id = "flex_nib",
        displayName = "Flex Nib",
        description = "The projectile scales up in physical size by 2% for every frame it travels."
    ),

    // Wash Brush Traits (Level 3 Choice)
    WIDE_BRISTLE(
        id = "wide_bristle",
        displayName = "Wide Bristle",
        description = "Sweep arc expanded by +40%, and spawned ink puddles are +35% wider."
    ),
    DEEP_WELL(
        id = "deep_well",
        displayName = "Deep Well",
        description = "Ink puddles linger for 6.0s (up from 4.0s) and tick damage every 0.35s."
    ),

    // Steel Fountain Traits (Level 3 Choice)
    RAZOR_FLOW(
        id = "razor_flow",
        displayName = "Razor Flow",
        description = "Fires +2 additional needles per volley with +1 bonus Pierce."
    ),
    PRESSURIZED_INK(
        id = "pressurized_ink",
        displayName = "Pressurized Ink",
        description = "Needles explode on final impact into 3 mini ink shrapnel droplets."
    )
}

object SerratedNibTrait : SpellTraitModule {
    override val id = SpellTraitType.SERRATED_NIB.id
    override val name = SpellTraitType.SERRATED_NIB.displayName
    override val description = SpellTraitType.SERRATED_NIB.description

    override fun onHitEnemy(enemy: Enemy, damageDealt: Float, projectile: InkProjectile?) {
        enemy.bleedTimer = 3.0f
        enemy.bleedDamagePerTick = (damageDealt * 0.28f).coerceAtLeast(4f)
    }
}

object FlexNibTrait : SpellTraitModule {
    override val id = SpellTraitType.FLEX_NIB.id
    override val name = SpellTraitType.FLEX_NIB.displayName
    override val description = SpellTraitType.FLEX_NIB.description

    override fun onProjectileTick(projectile: InkProjectile, dt: Float) {
        projectile.strokeWidth *= 1.02f
        projectile.strokeLength *= 1.02f
    }
}

object WideBristleTrait : SpellTraitModule {
    override val id = SpellTraitType.WIDE_BRISTLE.id
    override val name = SpellTraitType.WIDE_BRISTLE.displayName
    override val description = SpellTraitType.WIDE_BRISTLE.description

    override fun onPuddleSpawn(puddle: InkPuddle) {
        puddle.radius *= 1.35f
    }
}

object DeepWellTrait : SpellTraitModule {
    override val id = SpellTraitType.DEEP_WELL.id
    override val name = SpellTraitType.DEEP_WELL.displayName
    override val description = SpellTraitType.DEEP_WELL.description

    override fun onPuddleSpawn(puddle: InkPuddle) {
        puddle.maxLife = 6.0f
        puddle.tickInterval = 0.35f
    }
}

object RazorFlowTrait : SpellTraitModule {
    override val id = SpellTraitType.RAZOR_FLOW.id
    override val name = SpellTraitType.RAZOR_FLOW.displayName
    override val description = SpellTraitType.RAZOR_FLOW.description
}

object PressurizedInkTrait : SpellTraitModule {
    override val id = SpellTraitType.PRESSURIZED_INK.id
    override val name = SpellTraitType.PRESSURIZED_INK.displayName
    override val description = SpellTraitType.PRESSURIZED_INK.description
}

enum class SpellRuneType(
    val id: String,
    val displayName: String,
    val description: String,
    val slowPercent: Float
) {
    VISCOUS_RUNE(
        id = "viscous_rune",
        displayName = "Viscous Rune",
        description = "Applies 40% movement speed reduction to any enemy damaged by this spell.",
        slowPercent = 0.40f
    )
}

enum class SpellCastType {
    DIRECTIONAL_PROJECTILE,
    PHYSICS_OVERLAP_ARC,
    OMNIDIRECTIONAL_BARRAGE
}

data class SpellDefinition(
    val id: String,
    val name: String,
    val description: String,
    val castType: SpellCastType,
    val baseDamage: Float,
    val baseCooldown: Float,
    val baseSpeed: Float = 0f,
    val basePierce: Int = 1,
    val projectileLength: Float = 36f,
    val projectileWidth: Float = 10f,
    val arcRadius: Float = 160f,
    val arcAngleSpanRad: Float = 1.9f,
    val isHarpoon: Boolean = false,
    val isVortex: Boolean = false,
    val isBarrage: Boolean = false,
    val isUltimate: Boolean = false
) {
    companion object {
        // Base Spells
        val QuillDart = SpellDefinition(
            id = "quill_dart",
            name = "Quill Dart",
            description = "Auto-fires a sharp ink projectile in the direction of movement.",
            castType = SpellCastType.DIRECTIONAL_PROJECTILE,
            baseDamage = 25f,
            baseCooldown = 0.85f,
            baseSpeed = 520f,
            basePierce = 1,
            projectileLength = 36f,
            projectileWidth = 10f
        )

        val WashBrush = SpellDefinition(
            id = "wash_brush",
            name = "Wash Brush",
            description = "Executes a wide physics overlap arc in front of the player, leaving persistent ink puddles.",
            castType = SpellCastType.PHYSICS_OVERLAP_ARC,
            baseDamage = 38f,
            baseCooldown = 1.55f,
            arcRadius = 165f,
            arcAngleSpanRad = 1.95f
        )

        val SteelFountain = SpellDefinition(
            id = "steel_fountain",
            name = "Steel Fountain",
            description = "Rapidly fires precise high-velocity ink needles targeting the nearest cluster of enemies.",
            castType = SpellCastType.DIRECTIONAL_PROJECTILE,
            baseDamage = 18f,
            baseCooldown = 0.45f,
            baseSpeed = 640f,
            basePierce = 2,
            projectileLength = 26f,
            projectileWidth = 6f
        )

        // Phase 3 & 4 Evolutions (Spell Syntheses)
        val TheHarpoon = SpellDefinition(
            id = "the_harpoon",
            name = "The Harpoon",
            description = "Massive, high-velocity ink spear that fires less frequently with infinite piercing, pushing enemies backward.",
            castType = SpellCastType.DIRECTIONAL_PROJECTILE,
            baseDamage = 180f,
            baseCooldown = 2.2f,
            baseSpeed = 750f,
            basePierce = 9999,
            projectileLength = 80f,
            projectileWidth = 24f,
            isHarpoon = true
        )

        val InkwellVortex = SpellDefinition(
            id = "inkwell_vortex",
            name = "Inkwell Vortex",
            description = "Spawns a massive gravitational ink storm pulling in surrounding enemies and tearing them with continuous vortex ticks.",
            castType = SpellCastType.PHYSICS_OVERLAP_ARC,
            baseDamage = 120f,
            baseCooldown = 2.0f,
            arcRadius = 240f,
            arcAngleSpanRad = 3.14f * 2f,
            isVortex = true
        )

        val FountainBarrage = SpellDefinition(
            id = "fountain_barrage",
            name = "Fountain Barrage",
            description = "Unleashes an intense 360-degree storm of piercing steel ink needles in all directions.",
            castType = SpellCastType.OMNIDIRECTIONAL_BARRAGE,
            baseDamage = 45f,
            baseCooldown = 0.70f,
            baseSpeed = 680f,
            basePierce = 4,
            projectileLength = 32f,
            projectileWidth = 8f,
            isBarrage = true
        )

        // Phase 4 Level 100 "Magnum Opus" Ultimate Spell
        val TheMastersDecree = SpellDefinition(
            id = "the_masters_decree",
            name = "The Master's Decree",
            description = "Cosmic calligraphy strokes tear the void in 8 directions, obliterating any non-boss entity on contact.",
            castType = SpellCastType.DIRECTIONAL_PROJECTILE,
            baseDamage = 500f,
            baseCooldown = 0.50f,
            baseSpeed = 900f,
            basePierce = 99999,
            projectileLength = 110f,
            projectileWidth = 32f,
            isUltimate = true
        )

        val baseSpells = listOf(QuillDart, WashBrush, SteelFountain)
        val evolvedSpells = listOf(TheHarpoon, InkwellVortex, FountainBarrage)
        val allSpells = listOf(QuillDart, WashBrush, SteelFountain, TheHarpoon, InkwellVortex, FountainBarrage, TheMastersDecree)
    }
}

// Synthesis Recipe definition
data class SpellSynthesisRecipe(
    val id: String,
    val name: String,
    val requiredSpellId: String,
    val requiredGearId: String,
    val evolvedSpell: SpellDefinition,
    val description: String
) {
    companion object {
        // Synthesis 1: The Harpoon = Quill Dart (Max) + Heavy Vellum (Max)
        val HarpoonSynthesis = SpellSynthesisRecipe(
            id = "synthesis_harpoon",
            name = "The Harpoon",
            requiredSpellId = "quill_dart",
            requiredGearId = "heavy_vellum",
            evolvedSpell = SpellDefinition.TheHarpoon,
            description = "Evolves Quill Dart into a massive ink spear with infinite pierce and heavy knockback."
        )

        // Synthesis 2: Inkwell Vortex = Wash Brush (Max) + Lodestone Inkwell (Max)
        val VortexSynthesis = SpellSynthesisRecipe(
            id = "synthesis_vortex",
            name = "Inkwell Vortex",
            requiredSpellId = "wash_brush",
            requiredGearId = "lodestone_inkwell",
            evolvedSpell = SpellDefinition.InkwellVortex,
            description = "Evolves Wash Brush into a giant swirling gravitational ink vortex that pulls and crushes enemies."
        )

        // Synthesis 3: Fountain Barrage = Steel Fountain (Max) + Ergonomic Grip (Max)
        val BarrageSynthesis = SpellSynthesisRecipe(
            id = "synthesis_barrage",
            name = "Fountain Barrage",
            requiredSpellId = "steel_fountain",
            requiredGearId = "ergonomic_grip",
            evolvedSpell = SpellDefinition.FountainBarrage,
            description = "Evolves Steel Fountain into a rapid-fire omnidirectional needle storm."
        )

        val allRecipes = listOf(HarpoonSynthesis, VortexSynthesis, BarrageSynthesis)
    }
}

data class ActiveSpell(
    val definition: SpellDefinition,
    var rank: Int = 1,
    var cooldownTimer: Float = 0f,
    var totalDamageDealt: Long = 0L,
    val traitModules: MutableList<SpellTraitModule> = mutableListOf(),
    val socketedRunes: MutableList<SpellRuneType> = mutableListOf()
) {
    val isMaxLevel: Boolean get() = rank >= 7

    fun hasTrait(traitId: String): Boolean = traitModules.any { it.id == traitId }

    fun hasViscousRune(): Boolean = socketedRunes.contains(SpellRuneType.VISCOUS_RUNE)

    fun getEffectiveCooldown(attackSpeedMultiplier: Float): Float {
        val baseCd = definition.baseCooldown * (1f - (rank - 1) * 0.05f)
        return (baseCd / attackSpeedMultiplier).coerceAtLeast(0.12f)
    }

    fun getEffectiveDamage(damageMultiplier: Float): Float {
        return definition.baseDamage * (1f + (rank - 1) * 0.25f) * damageMultiplier
    }
}
