package com.example.model

enum class ArtifactTier {
    BASIC,
    RARE,
    LEGENDARY,
    MASTER,
    CURSED
}

data class ArtifactDefinition(
    val id: String,
    val name: String,
    val tier: ArtifactTier,
    val archetypeTag: String = "",
    val description: String,
    val damageModifier: Float = 0f,
    val moveSpeedModifier: Float = 0f,
    val armorModifier: Float = 0f,
    val attackSpeedModifier: Float = 0f,
    val pickupRadiusModifier: Float = 0f,
    val maxHpMultiplier: Float = 0f,
    val poisonDamageMultiplier: Float = 0f,
    val projectileBounces: Int = 0,
    val bounceDamageDelta: Float = 0f,
    var isMasteryCured: Boolean = false,
    var hasVampirism: Boolean = false
) {
    val maxHpModifier: Float get() = maxHpMultiplier
    val bonusBounces: Int get() = projectileBounces
    companion object {
        const val ARCHETYPE_HEAVY_WEIGHT = "Heavy Weight"
        const val ARCHETYPE_CORRUPTED_MEDIUM = "Corrupted Medium"
        const val ARCHETYPE_SACRED_GEOMETRY = "Sacred Geometry"
        const val ARCHETYPE_ELEMENTAL_ALCHEMIST = "Elemental Alchemist"
        const val ARCHETYPE_BLOOD_CALLIGRAPHY = "Blood Calligraphy"
        const val ARCHETYPE_CELESTIAL_VOID = "Celestial Void"

        // ==========================================
        // ARCHETYPE 1: HEAVY WEIGHT (Kinetic / Mass)
        // ==========================================
        val TheIronVats = ArtifactDefinition(
            id = "iron_vats",
            name = "The Iron Vats",
            tier = ArtifactTier.CURSED,
            archetypeTag = ARCHETYPE_HEAVY_WEIGHT,
            description = "+300% Ink Damage, -60% Movement Speed.",
            damageModifier = 3.0f,
            moveSpeedModifier = -0.60f
        )

        val LeadNib = ArtifactDefinition(
            id = "lead_nib",
            name = "Lead Nib",
            tier = ArtifactTier.RARE,
            archetypeTag = ARCHETYPE_HEAVY_WEIGHT,
            description = "+35% Ink Damage, -10% Move Speed.",
            damageModifier = 0.35f,
            moveSpeedModifier = -0.10f
        )

        val AnvilSeal = ArtifactDefinition(
            id = "anvil_seal",
            name = "Anvil Seal",
            tier = ArtifactTier.LEGENDARY,
            archetypeTag = ARCHETYPE_HEAVY_WEIGHT,
            description = "+8 Armor, -15% Move Speed.",
            armorModifier = 8f,
            moveSpeedModifier = -0.15f
        )

        val ColossusParchment = ArtifactDefinition(
            id = "colossus_parchment",
            name = "Colossus Parchment",
            tier = ArtifactTier.MASTER,
            archetypeTag = ARCHETYPE_HEAVY_WEIGHT,
            description = "+50% Ink Damage, +5 Armor, -15% Move Speed.",
            damageModifier = 0.50f,
            armorModifier = 5f,
            moveSpeedModifier = -0.15f
        )

        // ==========================================
        // ARCHETYPE 2: CORRUPTED MEDIUM (Status / DoT)
        // ==========================================
        val ToxicPigment = ArtifactDefinition(
            id = "toxic_pigment",
            name = "Toxic Pigment",
            tier = ArtifactTier.CURSED,
            archetypeTag = ARCHETYPE_CORRUPTED_MEDIUM,
            description = "+200% Poison/Bleed Damage, -50% Max HP.",
            poisonDamageMultiplier = 2.0f,
            maxHpMultiplier = -0.50f
        )

        val SpoiledInk = ArtifactDefinition(
            id = "spoiled_ink",
            name = "Spoiled Ink",
            tier = ArtifactTier.BASIC,
            archetypeTag = ARCHETYPE_CORRUPTED_MEDIUM,
            description = "+25% Poison/Bleed Damage, +10% Ink Damage.",
            damageModifier = 0.10f,
            poisonDamageMultiplier = 0.25f
        )

        val FungalPaper = ArtifactDefinition(
            id = "fungal_paper",
            name = "Fungal Paper",
            tier = ArtifactTier.RARE,
            archetypeTag = ARCHETYPE_CORRUPTED_MEDIUM,
            description = "+35% Status DoT Damage, +3 Armor.",
            armorModifier = 3f,
            poisonDamageMultiplier = 0.35f
        )

        val BlightedQuill = ArtifactDefinition(
            id = "blighted_quill",
            name = "Blighted Quill",
            tier = ArtifactTier.RARE,
            archetypeTag = ARCHETYPE_CORRUPTED_MEDIUM,
            description = "+30% Poison/Bleed Damage, +20% Attack Speed.",
            attackSpeedModifier = 0.20f,
            poisonDamageMultiplier = 0.30f
        )

        val MoldyParchment = ArtifactDefinition(
            id = "moldy_parchment",
            name = "Moldy Parchment",
            tier = ArtifactTier.LEGENDARY,
            archetypeTag = ARCHETYPE_CORRUPTED_MEDIUM,
            description = "+50% Poison/Bleed Damage, +25% Ink Damage.",
            damageModifier = 0.25f,
            poisonDamageMultiplier = 0.50f
        )

        // ==========================================
        // ARCHETYPE 3: SACRED GEOMETRY (Projectiles)
        // ==========================================
        val TheFracturedRuler = ArtifactDefinition(
            id = "fractured_ruler",
            name = "The Fractured Ruler",
            tier = ArtifactTier.CURSED,
            archetypeTag = ARCHETYPE_SACRED_GEOMETRY,
            description = "Projectiles bounce 5 additional times, but lose 30% damage per bounce.",
            projectileBounces = 5,
            bounceDamageDelta = -0.30f
        )

        val BrassCompass = ArtifactDefinition(
            id = "brass_compass",
            name = "Brass Compass",
            tier = ArtifactTier.BASIC,
            archetypeTag = ARCHETYPE_SACRED_GEOMETRY,
            description = "+30 Magnet Radius, +10% Attack Speed.",
            pickupRadiusModifier = 30f,
            attackSpeedModifier = 0.10f
        )

        val GraphPaper = ArtifactDefinition(
            id = "graph_paper",
            name = "Graph Paper",
            tier = ArtifactTier.RARE,
            archetypeTag = ARCHETYPE_SACRED_GEOMETRY,
            description = "+20% Projectile Damage, +2 Armor.",
            damageModifier = 0.20f,
            armorModifier = 2f
        )

        val ProtractorPlate = ArtifactDefinition(
            id = "protractor_plate",
            name = "Protractor Plate",
            tier = ArtifactTier.RARE,
            archetypeTag = ARCHETYPE_SACRED_GEOMETRY,
            description = "+25% Attack Speed, +10% Move Speed.",
            attackSpeedModifier = 0.25f,
            moveSpeedModifier = 0.10f
        )

        val GoldenSpiral = ArtifactDefinition(
            id = "golden_spiral",
            name = "Golden Spiral",
            tier = ArtifactTier.LEGENDARY,
            archetypeTag = ARCHETYPE_SACRED_GEOMETRY,
            description = "+40% Ink Damage, +15% Move Speed.",
            damageModifier = 0.40f,
            moveSpeedModifier = 0.15f
        )

        // General Cursed & Master Artifacts
        val BleachParasite = ArtifactDefinition(
            id = "bleach_parasite",
            name = "Bleach Parasite",
            tier = ArtifactTier.CURSED,
            archetypeTag = "Void Pact",
            description = "+100% Attack Speed, -30 Max HP.",
            attackSpeedModifier = 1.0f
        )

        val SootCrown = ArtifactDefinition(
            id = "soot_crown",
            name = "Soot Crown",
            tier = ArtifactTier.MASTER,
            archetypeTag = "Sovereign",
            description = "+80% Ink Damage, +30% Pickup Radius.",
            damageModifier = 0.80f,
            pickupRadiusModifier = 50f
        )

        val QuickSilverInk = ArtifactDefinition(
            id = "quicksilver_ink",
            name = "Quicksilver Ink",
            tier = ArtifactTier.BASIC,
            archetypeTag = "Velocity",
            description = "+15% Movement Speed.",
            moveSpeedModifier = 0.15f
        )

        val FeatherWeightSeal = ArtifactDefinition(
            id = "featherweight_seal",
            name = "Featherweight Seal",
            tier = ArtifactTier.RARE,
            archetypeTag = "Velocity",
            description = "+20% Attack Speed.",
            attackSpeedModifier = 0.20f
        )

        val CinnabarPhial = ArtifactDefinition(
            id = "cinnabar_phial",
            name = "Cinnabar Phial",
            tier = ArtifactTier.LEGENDARY,
            archetypeTag = ARCHETYPE_ELEMENTAL_ALCHEMIST,
            description = "+50% Elemental Reaction Damage, attacks ignite foes in lingering Cinnabar flames.",
            damageModifier = 0.30f
        )

        val FrostboundCalliper = ArtifactDefinition(
            id = "frostbound_calliper",
            name = "Frostbound Calliper",
            tier = ArtifactTier.RARE,
            archetypeTag = ARCHETYPE_ELEMENTAL_ALCHEMIST,
            description = "Enemies hit by Frozen Ink explode into 6 piercing frost needles. +25% Attack Speed.",
            attackSpeedModifier = 0.25f
        )

        val InkstoneOfEternity = ArtifactDefinition(
            id = "inkstone_of_eternity",
            name = "Inkstone of Eternity",
            tier = ArtifactTier.MASTER,
            archetypeTag = ARCHETYPE_HEAVY_WEIGHT,
            description = "+60 Max HP, +5 Armor, +35% Spell Area of Effect.",
            maxHpMultiplier = 0.60f,
            armorModifier = 5f
        )

        val CelestialAstrolabe = ArtifactDefinition(
            id = "celestial_astrolabe",
            name = "Celestial Astrolabe",
            tier = ArtifactTier.LEGENDARY,
            archetypeTag = ARCHETYPE_CELESTIAL_VOID,
            description = "+45% Astral Damage, +40 Magnet Radius, summons a celestial orbit node.",
            damageModifier = 0.45f,
            pickupRadiusModifier = 40f
        )

        val VampiricParchment = ArtifactDefinition(
            id = "vampiric_parchment",
            name = "Vampiric Parchment",
            tier = ArtifactTier.CURSED,
            archetypeTag = ARCHETYPE_BLOOD_CALLIGRAPHY,
            description = "+75% Bleed/Poison Damage, slain enemies occasionally restore 5 HP. -20% Move Speed.",
            poisonDamageMultiplier = 0.75f,
            moveSpeedModifier = -0.20f,
            hasVampirism = true
        )

        val VoidChisel = ArtifactDefinition(
            id = "void_chisel",
            name = "Void Chisel",
            tier = ArtifactTier.MASTER,
            archetypeTag = ARCHETYPE_CORRUPTED_MEDIUM,
            description = "+60% Critical Hit Damage, +6 Armor, converts 10% taken damage into ink nova.",
            armorModifier = 6f,
            damageModifier = 0.40f
        )

        val StoneList: List<ArtifactDefinition>
            get() = listOf(
                LeadNib, AnvilSeal, ColossusParchment, QuickSilverInk, FeatherWeightSeal,
                SpoiledInk, FungalPaper, BlightedQuill, MoldyParchment,
                BrassCompass, GraphPaper, ProtractorPlate, GoldenSpiral,
                CinnabarPhial, FrostboundCalliper, InkstoneOfEternity, CelestialAstrolabe, VampiricParchment, VoidChisel,
                TheLeakyPen, TheBrokenMetronome, CyanCartridge, MagentaCartridge, YellowCartridge
            )

        val ObeliskList: List<ArtifactDefinition>
            get() = listOf(
                TheIronVats, ToxicPigment, TheFracturedRuler,
                BleachParasite, SootCrown, ColossusParchment,
                CinnabarPhial, FrostboundCalliper, InkstoneOfEternity,
                CelestialAstrolabe, VampiricParchment, VoidChisel,
                GoldenSpiral, TheLeakyPen, TheBrokenMetronome,
                CyanCartridge, MagentaCartridge, YellowCartridge
            )

        // ══════════════════════════════════════════════════════════════════════
        // PHASE 7: RULE-BREAKING CURSED ARTIFACTS (Behavioral Shifts)
        // ══════════════════════════════════════════════════════════════════════

        /** The player can no longer stand still. Forces max-magnitude movement at
         *  all times. The player leaves a permanent, highly-damaging ink trail
         *  wherever they walk. Gameplay becomes Snake. */
        val TheLeakyPen = ArtifactDefinition(
            id = "the_leaky_pen",
            name = "The Leaky Pen",
            tier = ArtifactTier.CURSED,
            archetypeTag = "Rule-Breaker",
            description = "You cannot stand still. Your movement permanently paints a damaging ink trail that harms enemies and yourself if crossed again.",
            moveSpeedModifier = 0.20f,   // slight speed bonus to make forced movement viable
            damageModifier = 0.50f       // trail damage bonus
        )

        /** All equipped spells stop firing on individual cooldowns. Instead, ALL
         *  spells fire simultaneously every 4 seconds in one synchronized burst. */
        val TheBrokenMetronome = ArtifactDefinition(
            id = "the_broken_metronome",
            name = "The Broken Metronome",
            tier = ArtifactTier.CURSED,
            archetypeTag = "Rule-Breaker",
            description = "All spells stop auto-casting. Instead, every 4 seconds ALL spells fire simultaneously in one massive synchronized burst.",
            attackSpeedModifier = 0f    // cooldown system replaced; handled specially in GameViewModel
        )

        // ══════════════════════════════════════════════════════════════════════
        // PHASE 8: CMYK PIGMENT CARTRIDGES (Elemental Alchemy)
        // Dropped by specific Elite enemies; socket onto spells via new UI.
        // ══════════════════════════════════════════════════════════════════════

        /** Cyan Cartridge — Chill/Slow pigment.
         *  Converts spell damage type to Cyan. Hit enemies gain cyanTimer → -35% speed.
         *  Reaction with Yellow (Conductive) → Green Volatile → instant AOE detonation. */
        val CyanCartridge = ArtifactDefinition(
            id = "cyan_cartridge",
            name = "Cyan Cartridge",
            tier = ArtifactTier.RARE,
            archetypeTag = "CMYK Alchemy",
            description = "[Cyan] Your attacks Chill enemies (-35% speed). Combine with Yellow for a Volatile detonation reaction.",
            moveSpeedModifier = 0f,
            damageModifier = 0f
        )

        /** Magenta Cartridge — Corrosive/Armor Shred pigment.
         *  Hit enemies gain magentaTimer → incoming damage +40% for duration.
         *  Reaction with Cyan → Purple Dissolve → DoT melt. */
        val MagentaCartridge = ArtifactDefinition(
            id = "magenta_cartridge",
            name = "Magenta Cartridge",
            tier = ArtifactTier.RARE,
            archetypeTag = "CMYK Alchemy",
            description = "[Magenta] Your attacks Corrode enemies (+40% damage taken). Combine with Cyan for a Dissolve reaction.",
            damageModifier = 0.10f
        )

        /** Yellow Cartridge — Conductive/Chain Lightning pigment.
         *  Hit enemies gain yellowTimer → next hit chains to 3 nearby enemies.
         *  Reaction with Cyan (Chill) → Green Volatile. */
        val YellowCartridge = ArtifactDefinition(
            id = "yellow_cartridge",
            name = "Yellow Cartridge",
            tier = ArtifactTier.RARE,
            archetypeTag = "CMYK Alchemy",
            description = "[Yellow] Your attacks Conduct electricity, chaining to 3 nearby enemies. Combine with Cyan for a Volatile detonation.",
            attackSpeedModifier = 0.10f
        )

        // ══════════════════════════════════════════════════════════════════════
        // PHASE 8: CANVAS DEGRADATION (Pre-Run Negative Modifiers / Heat System)
        // Activatable before a run for bonus Gold/Crystal at end.
        // ══════════════════════════════════════════════════════════════════════

        /** Torn Edges — Map boundary shrinks/expands dynamically; touching edge
         *  deals heavy damage. +40% Gold/Crystal payout. */
        val TornEdges = ArtifactDefinition(
            id = "torn_edges",
            name = "Torn Edges",
            tier = ArtifactTier.CURSED,
            archetypeTag = "Canvas Degradation",
            description = "[Heat] Map boundary pulses dynamically. Touching the edge deals 50 damage/s. Reward: +40% Gold/Crystal payout."
        )

        /** Spilled Bleach — Random un-inkable white void patches appear. Spells
         *  passing through them are instantly erased. +30% payout. */
        val SpilledBleach = ArtifactDefinition(
            id = "spilled_bleach",
            name = "Spilled Bleach",
            tier = ArtifactTier.CURSED,
            archetypeTag = "Canvas Degradation",
            description = "[Heat] Void patches randomly erase projectiles and puddles passing through them. Reward: +30% Gold/Crystal payout."
        )

        /** Drafting Errors — Every 5 minutes a hostile clone of your build spawns. +50% payout. */
        val DraftingErrors = ArtifactDefinition(
            id = "drafting_errors",
            name = "Drafting Errors",
            tier = ArtifactTier.CURSED,
            archetypeTag = "Canvas Degradation",
            description = "[Heat] Every 5 minutes, a hostile Mirror-Clone of your current build spawns. Reward: +50% Gold/Crystal payout."
        )

        // Phase 7 Cursed Artifact lists
        val Phase7CursedList = listOf(TheLeakyPen, TheBrokenMetronome)

        // Phase 8 CMYK Cartridge list
        val CMYKCartridgeList = listOf(CyanCartridge, MagentaCartridge, YellowCartridge)

        // Phase 8 Canvas Degradation (pre-run modifiers) list
        val CanvasDegradationList = listOf(TornEdges, SpilledBleach, DraftingErrors)
    }
}

