package com.example.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

/**
 * Elemental interaction system for calligraphic weapons.
 * Combines distinct elemental affinities to trigger devastating, screen-filling reactions.
 */
enum class CalligraphicElement(
    val id: String,
    val displayName: String,
    val glyph: String,
    val colorHex: Long,
    val description: String
) {
    FROST(
        id = "frost",
        displayName = "Glacial Ink",
        glyph = "寒",
        colorHex = 0xFF00E5FF,
        description = "Chills constructs with sub-zero ink needles, slowing movement and preparing for cryo-shatter."
    ),
    CINNABAR_FLAME(
        id = "flame",
        displayName = "Cinnabar Flame",
        glyph = "炎",
        colorHex = 0xFFFF3D00,
        description = "Ignites paper and ink with volatile crimson cinnabar, causing continuous burn damage."
    ),
    CORROSIVE_ACID(
        id = "acid",
        displayName = "Corrosive Wash",
        glyph = "蚀",
        colorHex = 0xFF00E676,
        description = "Saturates and liquefies paper fibers, destroying construct armor and soaking surfaces in liquid ink."
    ),
    CELESTIAL_ASTRAL(
        id = "astral",
        displayName = "Cosmic Sigil",
        glyph = "宿",
        colorHex = 0xFFFFD54F,
        description = "Inscribes gravitational celestial coordinates, pulling reality toward ink vortex nodes."
    )
}

/**
 * The specific elemental reactions created by combining opposing or complementary elements.
 */
enum class ElementalReactionType(
    val id: String,
    val title: String,
    val subtitle: String,
    val kanjiStamps: String,
    val primaryColorHex: Long,
    val secondaryColorHex: Long,
    val description: String
) {
    FROZEN_INK(
        id = "frozen_ink",
        title = "FROZEN INK",
        subtitle = "Cryo-Solidification Fracture",
        kanjiStamps = "玄冰碎墨",
        primaryColorHex = 0xFF00E5FF,
        secondaryColorHex = 0xFF0A192F,
        description = "Sub-zero frost flash-freezes liquid ink pools and soaked constructs into brittle black ice, immobilizing enemies and triggering a screen-filling crystal shatter with +60% critical vulnerability."
    ),
    BURNING_CALLIGRAPHY(
        id = "burning_calligraphy",
        title = "BURNING CALLIGRAPHY",
        subtitle = "Cinnabar Conflagration Cataclysm",
        kanjiStamps = "焚墨烈焰",
        primaryColorHex = 0xFFFF3D00,
        secondaryColorHex = 0xFFFFAB00,
        description = "Volatile cinnabar spark ignites wet ink pools and trails into a raging wildfire, unleashing sweeping dragon flames across the entire canvas that spread to all nearby foes."
    ),
    COSMIC_SUPERNOVA(
        id = "cosmic_supernova",
        title = "COSMIC SUPERNOVA",
        subtitle = "Gravitational Collapse & Stellar Discharge",
        kanjiStamps = "太虚星爆",
        primaryColorHex = 0xFFFFD54F,
        secondaryColorHex = 0xFF7C4DFF,
        description = "Celestial sigils collapse under volatile cinnabar heat into a gravitational vortex that drags enemies inward before a blinding cosmic supernova blast."
    ),
    PERMAFROST_BLOSSOM(
        id = "permafrost_blossom",
        title = "PERMAFROST BLOSSOM",
        subtitle = "Absolute Zero Space-Time Blossom",
        kanjiStamps = "雪魄绽放",
        primaryColorHex = 0xFF80D8FF,
        secondaryColorHex = 0xFFEDE7F6,
        description = "Glacial needles crystallize celestial ink sigils, freezing time across the canvas with expanding fractal ice blossoms."
    ),
    THERMAL_SHOCK(
        id = "thermal_shock",
        title = "THERMAL SHOCK",
        subtitle = "Yin-Yang Vapor Explosion",
        kanjiStamps = "阴阳冲决",
        primaryColorHex = 0xFFFF1744,
        secondaryColorHex = 0xFF00E5FF,
        description = "Instant collision of extreme heat and extreme cold triggers a rapid kinetic vapor expansion."
    )
}

/**
 * Screen-filling visual effect entity for an active elemental reaction.
 */
data class ScreenElementalReactionVisual(
    val id: Long,
    val reactionType: ElementalReactionType,
    val x: Float,
    val y: Float,
    var timer: Float = 0f,
    val maxDuration: Float = 2.0f,
    val damage: Float = 100f,
    val radius: Float = 1100f,
    val comboMultiplier: Float = 1.8f
) {
    val progress: Float get() = (timer / maxDuration).coerceIn(0f, 1f)
    val flashAlpha: Float get() = ((1f - progress) * 0.85f).coerceIn(0f, 0.85f)
    val expansionRadius: Float get() = radius * (0.15f + 0.85f * kotlin.math.sqrt(progress))
    val isFinished: Boolean get() = timer >= maxDuration
}

/**
 * Crystalline ice shard or flame ember particle spawned by elemental reactions.
 */
data class ElementalParticle(
    val id: Long,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    var size: Float,
    var life: Float,
    val maxLife: Float,
    val isShard: Boolean = true,
    var angle: Float = 0f,
    var rotSpeed: Float = 0f
)
