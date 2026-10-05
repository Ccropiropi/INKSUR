package com.example.model

import androidx.compose.ui.graphics.Color

/**
 * Brush-Stroke Mastery Types (墨道宗师流派):
 * Triggered by casting elemental spells in specific calligraphy sequences.
 */
enum class BrushStrokeMasteryType(
    val title: String,
    val kanji: String,
    val subtitle: String,
    val description: String,
    val sequence: List<CalligraphicElement>,
    val durationSeconds: Float,
    val attackRadiusMultiplier: Float,
    val inkFluidDensityMultiplier: Float,
    val attackSpeedBonus: Float = 0.15f,
    val primaryColor: Color,
    val secondaryColor: Color
) {
    GLACIAL_IGNITION(
        title = "Glacial Ignition",
        kanji = "冰火墨流",
        subtitle = "Steam Detonation & Rapid Inscription",
        description = "Frost immediately vaporized by Cinnabar Fire creates expanding thermal ink plumes.",
        sequence = listOf(CalligraphicElement.FROST, CalligraphicElement.CINNABAR_FLAME),
        durationSeconds = 7.0f,
        attackRadiusMultiplier = 1.45f,
        inkFluidDensityMultiplier = 2.2f,
        attackSpeedBonus = 0.20f,
        primaryColor = Color(0xFF00E5FF),
        secondaryColor = Color(0xFFFF3D00)
    ),

    VOID_DISSOLUTION(
        title = "Void Dissolution",
        kanji = "蚀星墨痕",
        subtitle = "Gravitational Well & Defense Melting",
        description = "Acid and Astral calligraphy warp reality, dissolving paper horror defenses into black mist.",
        sequence = listOf(CalligraphicElement.CORROSIVE_ACID, CalligraphicElement.CELESTIAL_ASTRAL),
        durationSeconds = 7.0f,
        attackRadiusMultiplier = 1.50f,
        inkFluidDensityMultiplier = 2.4f,
        attackSpeedBonus = 0.15f,
        primaryColor = Color(0xFF00E676),
        secondaryColor = Color(0xFFFFD54F)
    ),

    ASTRAL_FROSTBITE(
        title = "Astral Frostbite",
        kanji = "霜星墨阵",
        subtitle = "Supercooled Orbit & Glacial Stasis",
        description = "Celestial geometries freeze ink in mid-air, creating massive crystalline perimeter sweeps.",
        sequence = listOf(CalligraphicElement.CELESTIAL_ASTRAL, CalligraphicElement.FROST),
        durationSeconds = 7.5f,
        attackRadiusMultiplier = 1.40f,
        inkFluidDensityMultiplier = 2.3f,
        attackSpeedBonus = 0.15f,
        primaryColor = Color(0xFFFFD54F),
        secondaryColor = Color(0xFF80D8FF)
    ),

    ALCHEMICAL_PYRE(
        title = "Alchemical Pyre",
        kanji = "烈酸墨焰",
        subtitle = "Volatile Slag & Chain Splatter",
        description = "Acid ignited with flame explodes outward, generating torrential showers of caustic fluid droplets.",
        sequence = listOf(CalligraphicElement.CORROSIVE_ACID, CalligraphicElement.CINNABAR_FLAME),
        durationSeconds = 7.0f,
        attackRadiusMultiplier = 1.50f,
        inkFluidDensityMultiplier = 2.5f,
        attackSpeedBonus = 0.18f,
        primaryColor = Color(0xFF00E676),
        secondaryColor = Color(0xFFFF1744)
    ),

    GRAND_MASTERSTROKE(
        title = "Grand Masterstroke",
        kanji = "三味墨极",
        subtitle = "Sovereign Trinity of Calligraphy",
        description = "Harmonizes Frost, Flame, and Celestial glyphs in flawless stroke order. Unrivaled battlefield reach.",
        sequence = listOf(CalligraphicElement.FROST, CalligraphicElement.CINNABAR_FLAME, CalligraphicElement.CELESTIAL_ASTRAL),
        durationSeconds = 8.5f,
        attackRadiusMultiplier = 1.70f,
        inkFluidDensityMultiplier = 3.0f,
        attackSpeedBonus = 0.30f,
        primaryColor = Color(0xFFFFD54F),
        secondaryColor = Color(0xFF00E5FF)
    ),

    FORBIDDEN_INSCRIPTION(
        title = "Forbidden Inscription",
        kanji = "玄冥千钧",
        subtitle = "Black Sun Obliteration",
        description = "Ancient sequence of Astral, Acid, and Flame that soaks parchment in apocalyptic ink detonations.",
        sequence = listOf(CalligraphicElement.CELESTIAL_ASTRAL, CalligraphicElement.CORROSIVE_ACID, CalligraphicElement.CINNABAR_FLAME),
        durationSeconds = 8.5f,
        attackRadiusMultiplier = 1.75f,
        inkFluidDensityMultiplier = 3.2f,
        attackSpeedBonus = 0.25f,
        primaryColor = Color(0xFFFF3D00),
        secondaryColor = Color(0xFF7C4DFF)
    );

    companion object {
        /**
         * Ordered from longest sequence (tri-element) to shortest (dual-element)
         * to prioritize harder, higher-reward mastery triggers.
         */
        val allCombos: List<BrushStrokeMasteryType> = entries.sortedByDescending { it.sequence.size }

        fun findMatchingMastery(recentElements: List<CalligraphicElement>): BrushStrokeMasteryType? {
            for (combo in allCombos) {
                if (recentElements.size >= combo.sequence.size) {
                    val sub = recentElements.takeLast(combo.sequence.size)
                    if (sub == combo.sequence) {
                        return combo
                    }
                }
            }
            return null
        }
    }
}

/**
 * Active runtime bonus state for Brush-Stroke Mastery.
 */
data class ActiveMasteryBonus(
    val type: BrushStrokeMasteryType,
    var timer: Float,
    val maxDuration: Float = type.durationSeconds,
    val attackRadiusMultiplier: Float = type.attackRadiusMultiplier,
    val inkFluidDensityMultiplier: Float = type.inkFluidDensityMultiplier,
    val primaryColor: Color = type.primaryColor,
    val secondaryColor: Color = type.secondaryColor
) {
    val progress: Float
        get() = (timer / maxDuration).coerceIn(0f, 1f)
}

/**
 * Element cast entry in the active combo buffer.
 */
data class ComboCastStamp(
    val element: CalligraphicElement,
    val timestamp: Float,
    val spellName: String
)
