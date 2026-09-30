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
    var isMasteryCured: Boolean = false
) {
    companion object {
        const val ARCHETYPE_HEAVY_WEIGHT = "Heavy Weight"

        // Implementation Test - The Iron Vats (Cursed): +300% Ink Damage, -60% Movement Speed. Archetype: Heavy Weight
        val TheIronVats = ArtifactDefinition(
            id = "iron_vats",
            name = "The Iron Vats",
            tier = ArtifactTier.CURSED,
            archetypeTag = ARCHETYPE_HEAVY_WEIGHT,
            description = "+300% Ink Damage, -60% Movement Speed.",
            damageModifier = 3.0f,
            moveSpeedModifier = -0.60f
        )

        // Other Heavy Weight Archetype Artifacts for Broken Stone & Obelisk
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

        // General Cursed & Master Artifacts for Obelisk
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

        // Lower-tier artifacts for Broken Stone shop
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

        val StoneList = listOf(LeadNib, AnvilSeal, ColossusParchment, QuickSilverInk, FeatherWeightSeal)
        val ObeliskList = listOf(TheIronVats, BleachParasite, SootCrown, ColossusParchment)
    }
}
