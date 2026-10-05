package com.example.model

sealed class LevelUpChoice {
    abstract val title: String
    abstract val badge: String
    abstract val description: String
    abstract val subtitle: String

    data class TraitChoice(
        val spellId: String,
        val spellName: String,
        val targetRank: Int,
        val traitType: SpellTraitType
    ) : LevelUpChoice() {
        override val title: String get() = traitType.displayName
        override val badge: String get() = "$spellName Lv.$targetRank Trait"
        override val subtitle: String get() = "Mechanical Spell Trait"
        override val description: String get() = traitType.description
    }

    data class SpellLevelChoice(
        val spellId: String,
        val spellName: String,
        val targetRank: Int,
        val statBonusDesc: String
    ) : LevelUpChoice() {
        override val title: String get() = "Upgrade $spellName"
        override val badge: String get() = "Rank $targetRank / 7"
        override val subtitle: String get() = "Spell Level Up"
        override val description: String get() = statBonusDesc
    }

    data class NewSpellChoice(
        val definition: SpellDefinition
    ) : LevelUpChoice() {
        override val title: String get() = "Learn: ${definition.name}"
        override val badge: String get() = "New Spell"
        override val subtitle: String get() = definition.castType.name
        override val description: String get() = definition.description
    }

    data class GearChoice(
        val gearType: GearType,
        val currentStacks: Int,
        val nextStacks: Int = currentStacks + 1
    ) : LevelUpChoice() {
        override val title: String get() = gearType.displayName
        override val badge: String get() = gearType.statTag
        override val subtitle: String get() = if (currentStacks > 0) "Current: Lv.$currentStacks  →  Lv.$nextStacks" else "New Item (Lv.1)"
        override val description: String get() = gearType.description
    }

    data class PassiveStatChoice(
        val statType: PassiveStatType,
        override val title: String,
        override val badge: String,
        override val subtitle: String,
        override val description: String,
        val bonusValue: Float
    ) : LevelUpChoice()
}

enum class PassiveStatType {
    DRAGON_BLOOD_HP,
    SWIFT_BRUSH_SPEED,
    CARBON_DENSITY_DMG,
    EXPANDED_WELL_MAGNET,
    TEMPERED_NIB_ARMOR,
    KEEN_BRISTLE_CRIT
}
