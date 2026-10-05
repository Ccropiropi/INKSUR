package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddModerator
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ActiveSpell
import com.example.model.EquippedGear
import com.example.model.GearType
import com.example.model.LevelUpChoice
import com.example.model.SpellRuneType

/**
 * High-contrast, minimalist weapon selection UI with sumi-e ink-brush aesthetic elements.
 * Features an inscribed elemental rack displaying equipped weapons and active awakenings,
 * followed by calligraphic advancement options with elemental synergy previews.
 */
@Composable
fun LevelUpDialog(
    choices: List<LevelUpChoice>,
    onSelectChoice: (LevelUpChoice) -> Unit,
    activeSpells: List<ActiveSpell> = emptyList(),
    equippedGear: List<EquippedGear> = emptyList()
) {
    Dialog(
        onDismissRequest = { /* Modal choice required */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp)
                .shadow(28.dp, RoundedCornerShape(18.dp)),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF07070A), // Deepest stark obsidian
            border = BorderStroke(1.5.dp, Color(0xFFFFD54F).copy(alpha = 0.85f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. INK-BRUSH SUMI-E HEADER BANNER
                InkBrushHeader()

                Spacer(modifier = Modifier.height(10.dp))

                // 2. CURRENT EQUIPPED WEAPONS & ELEMENTAL UPGRADES RACK
                if (activeSpells.isNotEmpty()) {
                    CurrentElementalUpgradesRack(activeSpells = activeSpells)
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // 3. WEAPON SELECTION CHOICES (High-contrast, minimalist ink cards)
                Text(
                    text = "SELECT AN INSCRIPTION",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD54F),
                    letterSpacing = 1.8.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                choices.forEachIndexed { index, choice ->
                    MinimalistInkChoiceCard(
                        choice = choice,
                        index = index,
                        activeSpells = activeSpells,
                        onClick = { onSelectChoice(choice) }
                    )
                    if (index < choices.size - 1) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Subtle Calligraphic Footer
                Text(
                    text = "✦ Ancient ink awakens through deliberate mastery ✦",
                    fontSize = 9.sp,
                    color = Color(0xFF6E6E7A),
                    fontFamily = FontFamily.Serif
                )
            }
        }
    }
}

/**
 * Calligraphic Sumi-e Brush Banner with high-contrast golden kanji seal and brushstroke flourish.
 */
@Composable
private fun InkBrushHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // High-Contrast Title Bar with Kanji Stamps
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Left Ink Seal
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF8B0000))
                    .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(3.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "書",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "ARSENAL ADVANCEMENT",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = Color.White,
                letterSpacing = 1.6.sp
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Right Ink Seal
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF8B0000))
                    .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(3.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "鋒",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Inscribe arcane armaments or deepen elemental affinities",
            fontSize = 10.sp,
            color = Color(0xFFAAAAAE)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Ink-brush Divider Line with tapered calligraphy ends
        Canvas(modifier = Modifier.size(width = 220.dp, height = 6.dp)) {
            val w = size.width
            val h = size.height

            // Main brush stroke
            val path = Path().apply {
                moveTo(0f, h * 0.5f)
                quadraticBezierTo(w * 0.25f, h * 0.1f, w * 0.5f, h * 0.6f)
                quadraticBezierTo(w * 0.75f, h * 0.9f, w, h * 0.4f)
            }
            drawPath(
                path = path,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0x00FFD54F),
                        Color(0xFFFFD54F),
                        Color(0xFFFFE082),
                        Color(0xFFFFD54F),
                        Color(0x00FFD54F)
                    )
                ),
                style = Stroke(width = 2.4f, cap = StrokeCap.Round)
            )

            // Center droplet node
            drawCircle(
                color = Color(0xFFFFD54F),
                radius = 2.5f,
                center = Offset(w * 0.5f, h * 0.6f)
            )
        }
    }
}

/**
 * Inscribed Elemental Upgrades Grimoire Rack displaying all currently equipped weapons,
 * elemental affinities, active awakenings, and elemental resonance count.
 */
@Composable
private fun CurrentElementalUpgradesRack(
    activeSpells: List<ActiveSpell>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101017)),
        border = BorderStroke(1.dp, Color(0xFF242436))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header Row with Elemental Resonance Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(Color(0xFFFFD54F), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CURRENT ELEMENTAL UPGRADES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F),
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "${activeSpells.size} INSCRIBED",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9E9EAE)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Elemental Affinity Summary Pills (Resonance count across arsenal)
            ElementalResonancePillBar(activeSpells = activeSpells)

            Spacer(modifier = Modifier.height(8.dp))

            // Horizontal Scrollable Weapon Badges with Ink-brush aesthetics
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                activeSpells.forEach { spell ->
                    MinimalistElementalWeaponCard(spell = spell)
                }
            }
        }
    }
}

/**
 * Elemental resonance tags displaying active elemental counters across the equipped arsenal.
 */
@Composable
private fun ElementalResonancePillBar(activeSpells: List<ActiveSpell>) {
    var flameCount = 0
    var frostCount = 0
    var astralCount = 0
    var acidCount = 0
    var bleedCount = 0

    activeSpells.forEach { spell ->
        val def = spell.definition
        val traits = spell.traitModules
        when {
            def.id == "cinnabar_seal" || traits.any { it.id.contains("volatile") || it.id.contains("chain") } -> flameCount++
            def.id == "steel_fountain" || traits.any { it.id.contains("razor") || it.id.contains("pressurized") } -> frostCount++
            def.id == "orbital_runes" || traits.any { it.id.contains("astral") || it.id.contains("rotation") } -> astralCount++
            def.id == "wash_brush" || traits.any { it.id.contains("bristle") || it.id.contains("corrosive") } -> acidCount++
            traits.any { it.id.contains("serrated") } -> bleedCount++
            else -> {}
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (flameCount > 0) {
            ElementalResonancePill(label = "FLAME", count = flameCount, color = Color(0xFFFF3D00))
        }
        if (frostCount > 0) {
            ElementalResonancePill(label = "FROST", count = frostCount, color = Color(0xFF00E5FF))
        }
        if (astralCount > 0) {
            ElementalResonancePill(label = "ASTRAL", count = astralCount, color = Color(0xFFFFD54F))
        }
        if (acidCount > 0) {
            ElementalResonancePill(label = "ACID", count = acidCount, color = Color(0xFF00E676))
        }
        if (bleedCount > 0) {
            ElementalResonancePill(label = "BLEED", count = bleedCount, color = Color(0xFFE040FB))
        }
        if (flameCount == 0 && frostCount == 0 && astralCount == 0 && acidCount == 0 && bleedCount == 0) {
            ElementalResonancePill(label = "RAW INK", count = activeSpells.size, color = Color(0xFFB0BEC5))
        }
    }
}

@Composable
private fun ElementalResonancePill(label: String, count: Int, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$label ×$count",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

/**
 * Individual weapon card in the grimoire rack, with brush stroke accents and rune badges.
 */
@Composable
private fun MinimalistElementalWeaponCard(spell: ActiveSpell) {
    val def = spell.definition
    val traits = spell.traitModules
    val hasViscous = spell.socketedRunes.contains(SpellRuneType.VISCOUS_RUNE)

    val (elementalTag, elementalColor) = when {
        def.id == "cinnabar_seal" || traits.any { it.id.contains("volatile") || it.id.contains("chain") } -> {
            "Cinnabar Flame" to Color(0xFFFF3D00)
        }
        def.id == "steel_fountain" || traits.any { it.id.contains("razor") || it.id.contains("pressurized") } -> {
            "Frost Pierce" to Color(0xFF00E5FF)
        }
        def.id == "orbital_runes" || traits.any { it.id.contains("astral") || it.id.contains("rotation") } -> {
            "Astral Void" to Color(0xFFFFD54F)
        }
        def.id == "wash_brush" || traits.any { it.id.contains("bristle") || it.id.contains("corrosive") } -> {
            "Tidal Acid" to Color(0xFF00E676)
        }
        traits.any { it.id.contains("serrated") } -> {
            "Abyssal Bleed" to Color(0xFFE040FB)
        }
        else -> {
            "Raw Carbon" to Color(0xFFB0BEC5)
        }
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF161622))
            .border(1.dp, elementalColor.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
            .padding(horizontal = 9.dp, vertical = 7.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            // Title & Level
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // Miniature Ink Droplet Indicator
                Canvas(modifier = Modifier.size(7.dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(elementalColor, elementalColor.copy(alpha = 0.4f)),
                            center = Offset(size.width / 2f, size.height / 2f),
                            radius = size.width / 2f
                        ),
                        radius = size.width / 2f
                    )
                }

                Text(
                    text = def.name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Lv.${spell.rank}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD54F)
                )
            }

            // Elemental Tag Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(elementalColor.copy(alpha = 0.22f))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = elementalTag,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = elementalColor
                    )
                }

                if (hasViscous) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF651FFF).copy(alpha = 0.25f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "Viscous",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB388FF)
                        )
                    }
                }
            }

            // Active traits list
            if (traits.isNotEmpty()) {
                traits.forEach { trait ->
                    Text(
                        text = "• ${trait.name}",
                        fontSize = 8.sp,
                        color = Color(0xFFCFD8DC),
                        maxLines = 1
                    )
                }
            } else {
                Text(
                    text = "Awakening at Lv.3",
                    fontSize = 8.sp,
                    color = Color(0xFF757582)
                )
            }
        }
    }
}

/**
 * Minimalist, high-contrast choice card with ink-brush aesthetic elements,
 * displaying elemental synergies and upgrade dynamics.
 */
@Composable
fun MinimalistInkChoiceCard(
    choice: LevelUpChoice,
    index: Int,
    activeSpells: List<ActiveSpell> = emptyList(),
    onClick: () -> Unit
) {
    val isTrait = choice is LevelUpChoice.TraitChoice
    val isNewSpell = choice is LevelUpChoice.NewSpellChoice
    val isSpellLevel = choice is LevelUpChoice.SpellLevelChoice

    val isPassiveStat = choice is LevelUpChoice.PassiveStatChoice

    val elementalAccent = when {
        isTrait -> Color(0xFFFF3D00)     // Cinnabar Red
        isNewSpell -> Color(0xFFFFD54F)  // Astral Gold
        isSpellLevel -> Color(0xFF00E5FF)// Frost Cyan
        isPassiveStat -> Color(0xFFFF4081)// Ink Lotus Pink
        else -> Color(0xFF00E676)        // Emerald Gear
    }

    val icon: ImageVector = when (choice) {
        is LevelUpChoice.TraitChoice -> Icons.Default.AutoAwesome
        is LevelUpChoice.SpellLevelChoice -> Icons.Default.Upgrade
        is LevelUpChoice.NewSpellChoice -> Icons.Default.Brush
        is LevelUpChoice.PassiveStatChoice -> when (choice.statType) {
            com.example.model.PassiveStatType.DRAGON_BLOOD_HP -> Icons.Default.Favorite
            com.example.model.PassiveStatType.SWIFT_BRUSH_SPEED -> Icons.Default.Navigation
            com.example.model.PassiveStatType.CARBON_DENSITY_DMG -> Icons.Default.ElectricBolt
            com.example.model.PassiveStatType.EXPANDED_WELL_MAGNET -> Icons.Default.Radar
            com.example.model.PassiveStatType.TEMPERED_NIB_ARMOR -> Icons.Default.AddModerator
            com.example.model.PassiveStatType.KEEN_BRISTLE_CRIT -> Icons.Default.AutoAwesome
        }
        is LevelUpChoice.GearChoice -> when (choice.gearType) {
            GearType.HEAVY_VELLUM -> Icons.Default.AddModerator
            GearType.ERGONOMIC_GRIP -> Icons.Default.ElectricBolt
            GearType.DENSE_SOOT -> Icons.Default.FormatPaint
            GearType.SCRIBES_SANDAL -> Icons.Default.Navigation
            GearType.LODESTONE_INKWELL -> Icons.Default.Radar
            GearType.SPRING_WATER -> Icons.Default.Favorite
        }
    }

    // Determine elemental synergy context
    val synergyNote = when {
        isTrait -> "✦ Trait Awakening: Infuses unique elemental mechanics"
        isSpellLevel -> "✦ Elemental Amplification: +25% Power & Faster Cast"
        isNewSpell -> "✦ New Inscription: Expands active elemental arsenal"
        isPassiveStat -> "✦ Essence Infusion: Direct Hero Stat Transcendence"
        else -> "✦ Permanent Passive: Boosts all active weapons"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("choice_option_$index")
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111118)),
        border = BorderStroke(1.5.dp, elementalAccent.copy(alpha = 0.85f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Badge with Canvas Sumi-e Ink Splatter Texture
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1A1A26))
                    .border(1.2.dp, elementalAccent, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Background Ink Dab Splatter on Canvas
                Canvas(modifier = Modifier.size(42.dp)) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                elementalAccent.copy(alpha = 0.28f),
                                elementalAccent.copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            center = Offset(cx, cy),
                            radius = size.width * 0.48f
                        ),
                        radius = size.width * 0.48f,
                        center = Offset(cx, cy)
                    )
                }

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = elementalAccent,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details Column
            Column(modifier = Modifier.weight(1f)) {
                // Title and Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = choice.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White,
                        fontFamily = FontFamily.Serif
                    )

                    // High-contrast Pill Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(elementalAccent.copy(alpha = 0.22f))
                            .border(1.dp, elementalAccent, RoundedCornerShape(4.dp))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = choice.badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = elementalAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Brush Stroke Underline on Canvas
                Canvas(modifier = Modifier.size(width = 90.dp, height = 3.dp)) {
                    val path = Path().apply {
                        moveTo(0f, size.height * 0.5f)
                        lineTo(size.width * 0.8f, size.height * 0.5f)
                        lineTo(size.width, size.height)
                    }
                    drawPath(
                        path = path,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                elementalAccent.copy(alpha = 0.9f),
                                elementalAccent.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        ),
                        style = Stroke(width = 2f, cap = StrokeCap.Round)
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                // Subtitle
                Text(
                    text = choice.subtitle,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = elementalAccent
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Description
                Text(
                    text = choice.description,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    color = Color(0xFFD4D4DE)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Elemental Synergy Notice
                Text(
                    text = synergyNote,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = elementalAccent.copy(alpha = 0.85f)
                )
            }
        }
    }
}
