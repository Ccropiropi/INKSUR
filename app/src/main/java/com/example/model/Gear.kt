package com.example.model

enum class GearType(
    val id: String,
    val displayName: String,
    val statTag: String,
    val description: String
) {
    HEAVY_VELLUM(
        id = "heavy_vellum",
        displayName = "Heavy Vellum",
        statTag = "+Armor",
        description = "Compressed parchment layers. Grants +3 Armor, reducing incoming damage from paper enemies."
    ),
    ERGONOMIC_GRIP(
        id = "ergonomic_grip",
        displayName = "Ergonomic Grip",
        statTag = "+Attack Speed",
        description = "Ergonomic quill wrap. Increases spell cast rate and reduces auto-fire cooldown by 15%."
    ),
    DENSE_SOOT(
        id = "dense_soot",
        displayName = "Dense Soot",
        statTag = "+Damage",
        description = "High-concentration carbon pigment. Increases all ink projectile damage by 20%."
    ),
    SCRIBES_SANDAL(
        id = "scribes_sandal",
        displayName = "Scribe's Sandal",
        statTag = "+Move Speed",
        description = "Lightweight woven straw sandals. Increases movement speed across the scratchpad by 15%."
    ),
    LODESTONE_INKWELL(
        id = "lodestone_inkwell",
        displayName = "Lodestone Inkwell",
        statTag = "+Pickup Radius",
        description = "Magnetic ink reservoir. Expands orb attraction radius by +40%."
    ),
    SPRING_WATER(
        id = "spring_water",
        displayName = "Spring Water",
        statTag = "+Max Health",
        description = "Pure spring water to dilute ink. Increases max health by +25 HP and heals for 25 HP."
    )
}

data class EquippedGear(
    val type: GearType,
    var stacks: Int = 1
)
