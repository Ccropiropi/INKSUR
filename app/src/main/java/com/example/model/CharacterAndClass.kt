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
    val aoeMultiplier: Float = 1.0f,
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

        // Character 4: The Runesmith (+25% AOE, +15% Damage) - Unlocked with 75 Crystals
        val TheRunesmith = CharacterDefinition(
            id = "runesmith",
            name = "The Runesmith",
            title = "Architect of the Outer Glyphs",
            description = "+25% Area of Effect, +15% Damage to all sigils and elemental glyphs.",
            damageMultiplier = 1.15f,
            aoeMultiplier = 1.25f,
            crystalUnlockCost = 75
        )

        val allCharacters = listOf(TheCalligrapher, TheScholar, TheGrandmaster, TheRunesmith)
    }
}

data class ClassDefinition(
    val id: String,
    val name: String,
    val role: String,
    val description: String,
    val starterSpell: SpellDefinition,
    val glyphType: String = "SPIRE_TOWER",
    val glyphColorHex: Long = 0xFFE1F5FE,
    val masteryLevel: Int = 1,
    val maxMastery: Int = 12
) {
    companion object {
        // ROW 1
        val Scribe = ClassDefinition(
            id = "scribe",
            name = "Scribe",
            role = "Starter Sorcerer",
            description = "Wields the sharpened quill. Auto-fires Quill Dart in the direction of movement.",
            starterSpell = SpellDefinition.QuillDart,
            glyphType = "SPIRE_TOWER",
            glyphColorHex = 0xFFFFF9C4,
            masteryLevel = 1,
            maxMastery = 12
        )
        val Wizard = Scribe

        val Astronomer = ClassDefinition(
            id = "astronomer",
            name = "Astronomer",
            role = "Starlight Cartographer",
            description = "Channels stellar orbits. Summons revolving celestial runes that shred encroaching foes.",
            starterSpell = SpellDefinition.OrbitalRunes,
            glyphType = "CONSTELLATION",
            glyphColorHex = 0xFFE1F5FE,
            masteryLevel = 1,
            maxMastery = 12
        )

        val Cryomancer = ClassDefinition(
            id = "cryomancer",
            name = "Electromancer",
            role = "Frost & Arcane Needle Piercer",
            description = "Fires high-frequency penetrating crystal needle volleys at high speed.",
            starterSpell = SpellDefinition.SteelFountain,
            glyphType = "SNOWFLAKE",
            glyphColorHex = 0xFFF8BBD0,
            masteryLevel = 1,
            maxMastery = 12
        )

        val Archivist = ClassDefinition(
            id = "archivist",
            name = "Archivist",
            role = "Grimoire Scholar",
            description = "Preserves forbidden scriptures. Launches piercing ink darts with extended range.",
            starterSpell = SpellDefinition.QuillDart,
            glyphType = "TOME_GATE",
            glyphColorHex = 0xFFE1F5FE,
            masteryLevel = 1,
            maxMastery = 12
        )

        val Illuminator = ClassDefinition(
            id = "illuminator",
            name = "Illuminator",
            role = "Celestial Spoke Weaver",
            description = "Summons revolving cosmic ink runes that continuously orbit the hero, shredding any encroaching foes.",
            starterSpell = SpellDefinition.OrbitalRunes,
            glyphType = "WHEEL_SPIKES",
            glyphColorHex = 0xFFE1F5FE,
            masteryLevel = 1,
            maxMastery = 12
        )

        // ROW 2
        val Witch = ClassDefinition(
            id = "witch",
            name = "Hex Inker",
            role = "Hex Effigy Calligrapher",
            description = "Inscribes curse effigies that trigger sudden localized combustions beneath enemy clusters.",
            starterSpell = SpellDefinition.CinnabarSeal,
            glyphType = "VOODOO_DOLL",
            glyphColorHex = 0xFFECEFF1,
            masteryLevel = 1,
            maxMastery = 12
        )

        val Magician = ClassDefinition(
            id = "magician",
            name = "Magician",
            role = "Arcane Solomon Sigilist",
            description = "Transmutes ink darts into piercing polymorphic bolts.",
            starterSpell = SpellDefinition.QuillDart,
            glyphType = "HEXAGRAM_SEAL",
            glyphColorHex = 0xFFE1F5FE,
            masteryLevel = 1,
            maxMastery = 12
        )

        val Bishop = ClassDefinition(
            id = "bishop",
            name = "Bishop",
            role = "Holy Diamond Relic",
            description = "Sanctified diamond barrier pulses with protective orbital wards.",
            starterSpell = SpellDefinition.OrbitalRunes,
            glyphType = "CROSS_DIAMOND",
            glyphColorHex = 0xFFE1F5FE,
            masteryLevel = 1,
            maxMastery = 12
        )

        val Sorcerer = ClassDefinition(
            id = "sorcerer",
            name = "Sorcerer",
            role = "Petal Mandala Evoker",
            description = "Radiates crystalline needle mandalas across the battlefield.",
            starterSpell = SpellDefinition.SteelFountain,
            glyphType = "SUN_ROSE",
            glyphColorHex = 0xFFE1F5FE,
            masteryLevel = 1,
            maxMastery = 12
        )

        val Druid = ClassDefinition(
            id = "druid",
            name = "Grove Calligrapher",
            role = "Verdant Ink Harmonizer",
            description = "Sweeps the ground with organic foliage wash leaving damaging ink pools.",
            starterSpell = SpellDefinition.WashBrush,
            glyphType = "TREE_RUNE",
            glyphColorHex = 0xFFE1F5FE,
            masteryLevel = 1,
            maxMastery = 12
        )

        // ROW 3
        val Pyromancer = ClassDefinition(
            id = "pyromancer",
            name = "Pyromancer",
            role = "Solar Burst Igniter",
            description = "Inscribes blazing cinnabar runes that explode with volcanic ink shockwaves.",
            starterSpell = SpellDefinition.CinnabarSeal,
            glyphType = "EXPLOSIVE_BURST",
            glyphColorHex = 0xFFFFFFFF,
            masteryLevel = 1,
            maxMastery = 12
        )

        val Shaman = ClassDefinition(
            id = "shaman",
            name = "Spirit Medium",
            role = "Spiritual Ink Channeler",
            description = "Channels atmospheric ink lightning into rapid needle strikes.",
            starterSpell = SpellDefinition.SteelFountain,
            glyphType = "LIGHTNING_HAND",
            glyphColorHex = 0xFFE1F5FE,
            masteryLevel = 1,
            maxMastery = 12
        )

        val Alchemist = ClassDefinition(
            id = "alchemist",
            name = "Alchemist",
            role = "Detonation Specialist",
            description = "Inscribes volatile cinnabar sigils onto the parchment that detonate with explosive ink shockwaves.",
            starterSpell = SpellDefinition.CinnabarSeal,
            glyphType = "ALCHEMIC_SPIRES",
            glyphColorHex = 0xFFE1F5FE,
            masteryLevel = 1,
            maxMastery = 12
        )

        val Scholar = ClassDefinition(
            id = "scholar_class",
            name = "Scholar",
            role = "Tome Inscriber",
            description = "Deep research grants rapid projectile acceleration and piercing nibs.",
            starterSpell = SpellDefinition.QuillDart,
            glyphType = "BOOK_STACK",
            glyphColorHex = 0xFFE1F5FE,
            masteryLevel = 1,
            maxMastery = 12
        )

        val Occultist = ClassDefinition(
            id = "occultist",
            name = "Occultist",
            role = "Ink Stride Shadow",
            description = "Channels ancient ink stride. Leaves a continuous trail of burning sumi-e footprints while moving that slows and melts foes.",
            starterSpell = SpellDefinition.CalligraphersWake,
            glyphType = "CAULDRON",
            glyphColorHex = 0xFFE1F5FE,
            masteryLevel = 1,
            maxMastery = 12
        )

        // ROW 4
        val Engraver = ClassDefinition(
            id = "engraver",
            name = "Engraver",
            role = "Rapid Needle Specialist",
            description = "Wields precision steel nibs. Fires rapid-fire needle barrages with high velocity and native pierce.",
            starterSpell = SpellDefinition.SteelFountain,
            glyphType = "CRACKED_ORB",
            glyphColorHex = 0xFFE1F5FE,
            masteryLevel = 1,
            maxMastery = 12
        )

        val Painter = ClassDefinition(
            id = "painter",
            name = "Painter",
            role = "AOE Fluid Specialist",
            description = "Wields the wide horsehair wash brush. Executes wide physics overlap sweeps leaving damaging ink puddles.",
            starterSpell = SpellDefinition.WashBrush,
            glyphType = "SWIRL_VORTEX",
            glyphColorHex = 0xFFE1F5FE,
            masteryLevel = 1,
            maxMastery = 12
        )

        val Summoner = ClassDefinition(
            id = "summoner",
            name = "Summoner",
            role = "Coiled Serpent Hierophant",
            description = "Commands the Abyssal Serpent to glide across the canvas, shredding foes and leaving toxic ink ripples.",
            starterSpell = SpellDefinition.AbyssalSerpent,
            glyphType = "SERPENT_STAFF",
            glyphColorHex = 0xFFE1F5FE,
            masteryLevel = 1,
            maxMastery = 12
        )

        val Apostle = ClassDefinition(
            id = "apostle",
            name = "Apostle",
            role = "Holy Grail Bearer",
            description = "Radiates holy grail blessings with protective revolving glyphs.",
            starterSpell = SpellDefinition.OrbitalRunes,
            glyphType = "CHALICE",
            glyphColorHex = 0xFFF48FB1,
            masteryLevel = 1,
            maxMastery = 12
        )

        val Warlock = ClassDefinition(
            id = "warlock",
            name = "Warlock",
            role = "Gnarled Staff Invoker",
            description = "Places abyssal cinnabar seals that detonate with corrupted ink waves.",
            starterSpell = SpellDefinition.CinnabarSeal,
            glyphType = "HOODED_MAGE",
            glyphColorHex = 0xFFE1F5FE,
            masteryLevel = 1,
            maxMastery = 12
        )

        // ROW 5
        val DarkMage = ClassDefinition(
            id = "dark_mage",
            name = "Dark Mage",
            role = "Shadow Silhouette Wraith",
            description = "Strikes from the shadows with hyper-penetrating black ink darts.",
            starterSpell = SpellDefinition.QuillDart,
            glyphType = "SHADOW_FACE",
            glyphColorHex = 0xFFE1F5FE,
            masteryLevel = 1,
            maxMastery = 12
        )

        val BattleMage = ClassDefinition(
            id = "battle_mage",
            name = "Battle Mage",
            role = "Tidal Vanguard Cap",
            description = "Vanguard warrior that releases wide sweeping crescent tidal waves of compressed black ink.",
            starterSpell = SpellDefinition.TidalBrushWave,
            glyphType = "WIZARD_HAT",
            glyphColorHex = 0xFFE1F5FE,
            masteryLevel = 1,
            maxMastery = 12
        )

        val Elementalist = ClassDefinition(
            id = "elementalist",
            name = "Elementalist",
            role = "Corona Solar Flare",
            description = "Summons an intense corona of orbital elemental runes.",
            starterSpell = SpellDefinition.OrbitalRunes,
            glyphType = "SOLAR_CORONA",
            glyphColorHex = 0xFFE1F5FE,
            masteryLevel = 1,
            maxMastery = 12
        )

        val Necromancer = ClassDefinition(
            id = "necromancer",
            name = "Sepulcher Scribe",
            role = "Tomb Scripture Master",
            description = "Fires bone-needle steel fountains that consume fallen essences and shatter paper hordes.",
            starterSpell = SpellDefinition.SteelFountain,
            glyphType = "SKULL_VISAGE",
            glyphColorHex = 0xFFE1F5FE,
            masteryLevel = 1,
            maxMastery = 12
        )

        val allClasses = listOf(
            Scribe, Astronomer, Cryomancer, Archivist, Illuminator,
            Witch, Magician, Bishop, Sorcerer, Druid,
            Pyromancer, Shaman, Alchemist, Scholar, Occultist,
            Engraver, Painter, Summoner, Apostle, Warlock,
            DarkMage, BattleMage, Elementalist, Necromancer
        )
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
    // Quill Dart Traits (Ranks 3, 5, 7)
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
    SPLIT_SHARD(
        id = "split_shard",
        displayName = "Split Shard",
        description = "Projectiles split into two angled piercing quill darts upon impacting enemies."
    ),
    BOOMERANG_NIB(
        id = "boomerang_nib",
        displayName = "Boomerang Nib",
        description = "Quill darts reverse flight direction at max range, returning to shred foes again."
    ),
    DETONATING_MARK(
        id = "detonating_mark",
        displayName = "Detonating Mark",
        description = "Enemies hit are branded with dark ink; marked foes detonate violently upon death."
    ),
    VORTEX_TIP(
        id = "vortex_tip",
        displayName = "Vortex Tip",
        description = "Carves micro-vacuums along its trajectory, pulling surrounding paper horrors inward."
    ),

    // Wash Brush Traits (Ranks 3, 5, 7)
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
    VISCOUS_TAR(
        id = "viscous_tar",
        displayName = "Viscous Tar",
        description = "Puddles turn to thick black tar, reducing enemy movement speed by 50%."
    ),
    VOLATILE_VAPOR(
        id = "volatile_vapor",
        displayName = "Volatile Vapor",
        description = "Expiring puddles release pressurized caustic vapor plumes that damage surrounding foes."
    ),
    RESERVOIR_MERGE(
        id = "reservoir_merge",
        displayName = "Reservoir Surge",
        description = "Adjacent ink pools merge into an amplified lake that deals doubled damage."
    ),
    CAUSTIC_DELUGE(
        id = "caustic_deluge",
        displayName = "Caustic Deluge",
        description = "Corrosive fluid liquefies paper fibers, completely stripping enemy armor."
    ),

    // Steel Fountain Traits (Ranks 3, 5, 7)
    RAZOR_FLOW(
        id = "razor_flow",
        displayName = "Razor Flow",
        description = "Fires +2 additional needles per volley with +1 bonus Pierce."
    ),
    PRESSURIZED_INK(
        id = "pressurized_ink",
        displayName = "Pressurized Ink",
        description = "Needles explode on final impact into 3 mini ink shrapnel droplets."
    ),
    RICOCHET_NEEDLES(
        id = "ricochet_needles",
        displayName = "Ricochet Needles",
        description = "Needles ricochet between nearby enemies or canvas boundaries up to 2 times."
    ),
    NEEDLE_FAN(
        id = "needle_fan",
        displayName = "Needle Fan",
        description = "Fires needles in a sweeping 60-degree radial fan covering wide enemy clusters."
    ),
    GATLING_BURST(
        id = "gatling_burst",
        displayName = "Gatling Inscription",
        description = "Greatly reduces cooldown and discharges rapid continuous needle volleys."
    ),
    EMBEDDED_DETONATOR(
        id = "embedded_detonator",
        displayName = "Micro Detonators",
        description = "Needles embed into paper horrors and detonate in miniature ink bursts after 0.8s."
    ),

    // Orbital Runes Traits (Ranks 3, 5, 7)
    ASTRAL_EXPANSION(
        id = "astral_expansion",
        displayName = "Astral Expansion",
        description = "+40% Orbit Radius and spawns +1 additional celestial rune."
    ),
    RAPID_ROTATION(
        id = "rapid_rotation",
        displayName = "Rapid Rotation",
        description = "+60% Orbit Rotation Speed and applies 40% Viscous Slow on contact."
    ),
    ELLIPTICAL_PULSE(
        id = "elliptical_pulse",
        displayName = "Elliptical Pulse",
        description = "Orbit rhythmically expands and contracts, sweeping a dynamic zone around the hero."
    ),
    AEGIS_BARRIER(
        id = "aegis_barrier",
        displayName = "Aegis Barrier",
        description = "Orbiting runes absorb contact with paper horrors, knocking them backward."
    ),
    GRAVITATIONAL_RING(
        id = "gravitational_ring",
        displayName = "Gravitational Ring",
        description = "Celestial perimeter pulls orbs and paper enemies directly into the spinning runes."
    ),
    BINARY_CONSTELLATION(
        id = "binary_constellation",
        displayName = "Binary Constellation",
        description = "Summons a second concentric counter-rotating ring of orbital celestial runes."
    ),

    // Cinnabar Seal Traits (Ranks 3, 5, 7)
    CHAIN_REACTION(
        id = "chain_reaction",
        displayName = "Chain Reaction",
        description = "Detonations trigger secondary mini ink explosions on surrounding targets."
    ),
    VOLATILE_CORE(
        id = "volatile_core",
        displayName = "Volatile Core",
        description = "+45% Blast Radius and +55% Detonation Shockwave Damage."
    ),
    SEAL_ROOT(
        id = "seal_root",
        displayName = "Binding Inscription",
        description = "Glyphs root and paralyze paper constructs inside the seal zone until detonation."
    ),
    DUAL_IMPRINT(
        id = "dual_imprint",
        displayName = "Dual Imprint",
        description = "Inscribes two linked cinnabar seals simultaneously with synchronized detonations."
    ),
    INFERNO_RESIDUE(
        id = "inferno_residue",
        displayName = "Inferno Residue",
        description = "Leaves a lingering pool of raging cinnabar flame that burns enemies for 4.0s."
    ),
    GRAVITATIONAL_CRATER(
        id = "gravitational_crater",
        displayName = "Singularity Well",
        description = "Pulls all surrounding enemies into the epicenter right before detonation."
    );

    companion object {
        fun getTraitsForSpell(spellId: String, rank: Int): List<SpellTraitType> {
            return when (spellId) {
                "quill_dart" -> when (rank) {
                    3 -> listOf(SERRATED_NIB, FLEX_NIB)
                    5 -> listOf(SPLIT_SHARD, BOOMERANG_NIB)
                    7 -> listOf(DETONATING_MARK, VORTEX_TIP)
                    else -> emptyList()
                }
                "wash_brush" -> when (rank) {
                    3 -> listOf(WIDE_BRISTLE, DEEP_WELL)
                    5 -> listOf(VISCOUS_TAR, VOLATILE_VAPOR)
                    7 -> listOf(RESERVOIR_MERGE, CAUSTIC_DELUGE)
                    else -> emptyList()
                }
                "steel_fountain" -> when (rank) {
                    3 -> listOf(RAZOR_FLOW, PRESSURIZED_INK)
                    5 -> listOf(RICOCHET_NEEDLES, NEEDLE_FAN)
                    7 -> listOf(GATLING_BURST, EMBEDDED_DETONATOR)
                    else -> emptyList()
                }
                "orbital_runes" -> when (rank) {
                    3 -> listOf(ASTRAL_EXPANSION, RAPID_ROTATION)
                    5 -> listOf(ELLIPTICAL_PULSE, AEGIS_BARRIER)
                    7 -> listOf(GRAVITATIONAL_RING, BINARY_CONSTELLATION)
                    else -> emptyList()
                }
                "cinnabar_seal" -> when (rank) {
                    3 -> listOf(CHAIN_REACTION, VOLATILE_CORE)
                    5 -> listOf(SEAL_ROOT, DUAL_IMPRINT)
                    7 -> listOf(INFERNO_RESIDUE, GRAVITATIONAL_CRATER)
                    else -> emptyList()
                }
                else -> when (rank) {
                    3 -> listOf(SERRATED_NIB, FLEX_NIB)
                    5 -> listOf(SPLIT_SHARD, BOOMERANG_NIB)
                    7 -> listOf(DETONATING_MARK, VORTEX_TIP)
                    else -> emptyList()
                }
            }
        }

        fun getModule(type: SpellTraitType): SpellTraitModule {
            return when (type) {
                SERRATED_NIB -> SerratedNibTrait
                FLEX_NIB -> FlexNibTrait
                SPLIT_SHARD -> SplitShardTrait
                BOOMERANG_NIB -> BoomerangNibTrait
                DETONATING_MARK -> DetonatingMarkTrait
                VORTEX_TIP -> VortexTipTrait
                WIDE_BRISTLE -> WideBristleTrait
                DEEP_WELL -> DeepWellTrait
                VISCOUS_TAR -> ViscousTarTrait
                VOLATILE_VAPOR -> VolatileVaporTrait
                RESERVOIR_MERGE -> ReservoirMergeTrait
                CAUSTIC_DELUGE -> CausticDelugeTrait
                RAZOR_FLOW -> RazorFlowTrait
                PRESSURIZED_INK -> PressurizedInkTrait
                RICOCHET_NEEDLES -> RicochetNeedlesTrait
                NEEDLE_FAN -> NeedleFanTrait
                GATLING_BURST -> GatlingBurstTrait
                EMBEDDED_DETONATOR -> EmbeddedDetonatorTrait
                ASTRAL_EXPANSION -> AstralExpansionTrait
                RAPID_ROTATION -> RapidRotationTrait
                ELLIPTICAL_PULSE -> EllipticalPulseTrait
                AEGIS_BARRIER -> AegisBarrierTrait
                GRAVITATIONAL_RING -> GravitationalRingTrait
                BINARY_CONSTELLATION -> BinaryConstellationTrait
                CHAIN_REACTION -> ChainReactionTrait
                VOLATILE_CORE -> VolatileCoreTrait
                SEAL_ROOT -> SealRootTrait
                DUAL_IMPRINT -> DualImprintTrait
                INFERNO_RESIDUE -> InfernoResidueTrait
                GRAVITATIONAL_CRATER -> GravitationalCraterTrait
            }
        }
    }
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

object SplitShardTrait : SpellTraitModule {
    override val id = SpellTraitType.SPLIT_SHARD.id
    override val name = SpellTraitType.SPLIT_SHARD.displayName
    override val description = SpellTraitType.SPLIT_SHARD.description
}

object BoomerangNibTrait : SpellTraitModule {
    override val id = SpellTraitType.BOOMERANG_NIB.id
    override val name = SpellTraitType.BOOMERANG_NIB.displayName
    override val description = SpellTraitType.BOOMERANG_NIB.description
}

object DetonatingMarkTrait : SpellTraitModule {
    override val id = SpellTraitType.DETONATING_MARK.id
    override val name = SpellTraitType.DETONATING_MARK.displayName
    override val description = SpellTraitType.DETONATING_MARK.description
}

object VortexTipTrait : SpellTraitModule {
    override val id = SpellTraitType.VORTEX_TIP.id
    override val name = SpellTraitType.VORTEX_TIP.displayName
    override val description = SpellTraitType.VORTEX_TIP.description
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

object ViscousTarTrait : SpellTraitModule {
    override val id = SpellTraitType.VISCOUS_TAR.id
    override val name = SpellTraitType.VISCOUS_TAR.displayName
    override val description = SpellTraitType.VISCOUS_TAR.description
}

object VolatileVaporTrait : SpellTraitModule {
    override val id = SpellTraitType.VOLATILE_VAPOR.id
    override val name = SpellTraitType.VOLATILE_VAPOR.displayName
    override val description = SpellTraitType.VOLATILE_VAPOR.description
}

object ReservoirMergeTrait : SpellTraitModule {
    override val id = SpellTraitType.RESERVOIR_MERGE.id
    override val name = SpellTraitType.RESERVOIR_MERGE.displayName
    override val description = SpellTraitType.RESERVOIR_MERGE.description
}

object CausticDelugeTrait : SpellTraitModule {
    override val id = SpellTraitType.CAUSTIC_DELUGE.id
    override val name = SpellTraitType.CAUSTIC_DELUGE.displayName
    override val description = SpellTraitType.CAUSTIC_DELUGE.description
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

object RicochetNeedlesTrait : SpellTraitModule {
    override val id = SpellTraitType.RICOCHET_NEEDLES.id
    override val name = SpellTraitType.RICOCHET_NEEDLES.displayName
    override val description = SpellTraitType.RICOCHET_NEEDLES.description
}

object NeedleFanTrait : SpellTraitModule {
    override val id = SpellTraitType.NEEDLE_FAN.id
    override val name = SpellTraitType.NEEDLE_FAN.displayName
    override val description = SpellTraitType.NEEDLE_FAN.description
}

object GatlingBurstTrait : SpellTraitModule {
    override val id = SpellTraitType.GATLING_BURST.id
    override val name = SpellTraitType.GATLING_BURST.displayName
    override val description = SpellTraitType.GATLING_BURST.description
}

object EmbeddedDetonatorTrait : SpellTraitModule {
    override val id = SpellTraitType.EMBEDDED_DETONATOR.id
    override val name = SpellTraitType.EMBEDDED_DETONATOR.displayName
    override val description = SpellTraitType.EMBEDDED_DETONATOR.description
}

object AstralExpansionTrait : SpellTraitModule {
    override val id = SpellTraitType.ASTRAL_EXPANSION.id
    override val name = SpellTraitType.ASTRAL_EXPANSION.displayName
    override val description = SpellTraitType.ASTRAL_EXPANSION.description
}

object RapidRotationTrait : SpellTraitModule {
    override val id = SpellTraitType.RAPID_ROTATION.id
    override val name = SpellTraitType.RAPID_ROTATION.displayName
    override val description = SpellTraitType.RAPID_ROTATION.description
}

object EllipticalPulseTrait : SpellTraitModule {
    override val id = SpellTraitType.ELLIPTICAL_PULSE.id
    override val name = SpellTraitType.ELLIPTICAL_PULSE.displayName
    override val description = SpellTraitType.ELLIPTICAL_PULSE.description
}

object AegisBarrierTrait : SpellTraitModule {
    override val id = SpellTraitType.AEGIS_BARRIER.id
    override val name = SpellTraitType.AEGIS_BARRIER.displayName
    override val description = SpellTraitType.AEGIS_BARRIER.description
}

object GravitationalRingTrait : SpellTraitModule {
    override val id = SpellTraitType.GRAVITATIONAL_RING.id
    override val name = SpellTraitType.GRAVITATIONAL_RING.displayName
    override val description = SpellTraitType.GRAVITATIONAL_RING.description
}

object BinaryConstellationTrait : SpellTraitModule {
    override val id = SpellTraitType.BINARY_CONSTELLATION.id
    override val name = SpellTraitType.BINARY_CONSTELLATION.displayName
    override val description = SpellTraitType.BINARY_CONSTELLATION.description
}

object ChainReactionTrait : SpellTraitModule {
    override val id = SpellTraitType.CHAIN_REACTION.id
    override val name = SpellTraitType.CHAIN_REACTION.displayName
    override val description = SpellTraitType.CHAIN_REACTION.description
}

object VolatileCoreTrait : SpellTraitModule {
    override val id = SpellTraitType.VOLATILE_CORE.id
    override val name = SpellTraitType.VOLATILE_CORE.displayName
    override val description = SpellTraitType.VOLATILE_CORE.description
}

object SealRootTrait : SpellTraitModule {
    override val id = SpellTraitType.SEAL_ROOT.id
    override val name = SpellTraitType.SEAL_ROOT.displayName
    override val description = SpellTraitType.SEAL_ROOT.description
}

object DualImprintTrait : SpellTraitModule {
    override val id = SpellTraitType.DUAL_IMPRINT.id
    override val name = SpellTraitType.DUAL_IMPRINT.displayName
    override val description = SpellTraitType.DUAL_IMPRINT.description
}

object InfernoResidueTrait : SpellTraitModule {
    override val id = SpellTraitType.INFERNO_RESIDUE.id
    override val name = SpellTraitType.INFERNO_RESIDUE.displayName
    override val description = SpellTraitType.INFERNO_RESIDUE.description
}

object GravitationalCraterTrait : SpellTraitModule {
    override val id = SpellTraitType.GRAVITATIONAL_CRATER.id
    override val name = SpellTraitType.GRAVITATIONAL_CRATER.displayName
    override val description = SpellTraitType.GRAVITATIONAL_CRATER.description
}

enum class SpellRuneType(
    val id: String,
    val displayName: String,
    val description: String,
    val slowPercent: Float = 0f,
    val infusedElement: CalligraphicElement? = null,
    val isEchoChain: Boolean = false,
    val isReaperBurst: Boolean = false
) {
    VISCOUS_RUNE(
        id = "viscous_rune",
        displayName = "Viscous Rune",
        description = "Applies 40% movement speed reduction to any enemy damaged by this spell.",
        slowPercent = 0.40f
    ),
    CINNABAR_INFUSION(
        id = "cinnabar_infusion",
        displayName = "Cinnabar Flame Rune",
        description = "Infuses this spell with volatile Cinnabar Flame, causing burning DoT and feeding flame reactions.",
        infusedElement = CalligraphicElement.CINNABAR_FLAME
    ),
    GLACIAL_INFUSION(
        id = "glacial_infusion",
        displayName = "Glacial Frost Rune",
        description = "Infuses this spell with sub-zero Glacial Ink, freezing enemies on contact and priming cryo-shatter.",
        infusedElement = CalligraphicElement.FROST
    ),
    CORROSIVE_INFUSION(
        id = "corrosive_infusion",
        displayName = "Corrosive Acid Rune",
        description = "Infuses this spell with Corrosive Ink, melting enemy armor and soaking foes in caustic wash.",
        infusedElement = CalligraphicElement.CORROSIVE_ACID
    ),
    ASTRAL_INFUSION(
        id = "astral_infusion",
        displayName = "Astral Resonance Rune",
        description = "Infuses this spell with Cosmic Sigils, pulling reality and creating micro-gravitational nodes on hit.",
        infusedElement = CalligraphicElement.CELESTIAL_ASTRAL
    ),
    ECHO_CHAIN_RUNE(
        id = "echo_chain_rune",
        displayName = "Echo Chain Rune",
        description = "Attacks that strike an enemy split and arc piercing ink filaments to up to 2 nearby foes.",
        isEchoChain = true
    ),
    REAPER_RUNE(
        id = "reaper_rune",
        displayName = "Reaper's Inscription Rune",
        description = "Defeated enemies violently detonate into fluid ink droplets that damage adjacent horrors.",
        isReaperBurst = true
    )
}

enum class SpellCastType {
    DIRECTIONAL_PROJECTILE,
    PHYSICS_OVERLAP_ARC,
    OMNIDIRECTIONAL_BARRAGE,
    // Phase 7: Wave that travels forward and leaves a brief damaging trail
    WAVE_TRAIL,
    ORBITAL_RUNES,
    DETONATION_SEAL,
    FOOTPRINT_TRAIL,
    FLOWING_SERPENT
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
    val areaRadius: Float = 40f,
    val durationSeconds: Float = 3.0f,
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

        // ─── Phase 7: Sacrificial Fusion Result Spells ───────────────────────
        /** The Rending Tide — Quill Dart (Primary) fused with Wash Brush (Catalyst/Sacrificed).
         *  Player loses the defensive AOE puddle system forever.
         *  Instead: fires a massive wave of piercing ink that leaves brief damaging trail segments.
         *  Forces a shift from static defensive positioning → constant forward aggression. */
        val TheRendingTide = SpellDefinition(
            id = "the_rending_tide",
            name = "The Rending Tide",
            description = "A massive surging wave of ink tears through everything in its path, leaving a brief corrosive trail. The Wash Brush is lost forever.",
            castType = SpellCastType.WAVE_TRAIL,
            baseDamage = 95f,
            baseCooldown = 1.40f,
            baseSpeed = 480f,
            basePierce = 99999,       // infinite piercing — hits all enemies in the wave
            projectileLength = 120f,  // wide wave front
            projectileWidth = 28f
        )

        // Class Basic Spells
        val OrbitalRunes = SpellDefinition(
            id = "orbital_runes",
            name = "Orbital Runes",
            description = "Revolving celestial ink sigils orbit continuously around the hero, dealing continuous contact damage.",
            castType = SpellCastType.ORBITAL_RUNES,
            baseDamage = 28f,
            baseCooldown = 2.4f,
            arcRadius = 95f
        )

        val CinnabarSeal = SpellDefinition(
            id = "cinnabar_seal",
            name = "Cinnabar Seal",
            description = "Inscribes volatile crimson cinnabar glyphs beneath enemies that detonate in fiery ink shockwaves.",
            castType = SpellCastType.DETONATION_SEAL,
            baseDamage = 80f,
            baseCooldown = 1.85f,
            arcRadius = 115f
        )

        // New Mechanic Spells: Damaging Footprints, Undulating Serpent, and Tidal Waves
        val CalligraphersWake = SpellDefinition(
            id = "calligraphers_wake",
            name = "Calligrapher's Wake",
            description = "Leaves a continuous trail of searing sumi-e ink footprints while moving that burns and slows encroaching foes.",
            castType = SpellCastType.FOOTPRINT_TRAIL,
            baseDamage = 26f,
            baseCooldown = 0.22f,
            areaRadius = 38f,
            durationSeconds = 4.0f
        )

        val AbyssalSerpent = SpellDefinition(
            id = "abyssal_serpent",
            name = "Abyssal Serpent",
            description = "Summons an undulating fluid ink serpent that glides across the canvas, shredding enemies and leaving toxic ink ripples.",
            castType = SpellCastType.FLOWING_SERPENT,
            baseDamage = 54f,
            baseCooldown = 3.2f,
            baseSpeed = 390f,
            areaRadius = 32f,
            durationSeconds = 4.5f
        )

        val TidalBrushWave = SpellDefinition(
            id = "tidal_brush_wave",
            name = "Tidal Cleave",
            description = "Releases a surging crescent wave of compressed black ink that washes over hordes in a wide arc.",
            castType = SpellCastType.WAVE_TRAIL,
            baseDamage = 46f,
            baseCooldown = 1.6f,
            baseSpeed = 480f,
            areaRadius = 180f,
            durationSeconds = 1.5f
        )

        val ShadowShuriken = SpellDefinition(
            id = "shadow_shuriken",
            name = "Shadow Shuriken",
            description = "Fires spinning folded origami stars that slice through enemies and bounce between nearby paper foes.",
            castType = SpellCastType.DIRECTIONAL_PROJECTILE,
            baseDamage = 32f,
            baseCooldown = 0.95f,
            baseSpeed = 620f,
            basePierce = 3,
            projectileLength = 26f,
            projectileWidth = 26f
        )

        val GlacialSpike = SpellDefinition(
            id = "glacial_spike",
            name = "Glacial Spike",
            description = "Thrusts frozen black-ice needles upward beneath enemies, freezing foes and shattering wet ink.",
            castType = SpellCastType.DETONATION_SEAL,
            baseDamage = 75f,
            baseCooldown = 1.9f,
            arcRadius = 120f
        )

        val CinnabarMeteor = SpellDefinition(
            id = "cinnabar_meteor",
            name = "Cinnabar Meteor",
            description = "Calls down blazing droplets of celestial cinnabar that explode on impact, igniting fiery pools.",
            castType = SpellCastType.DETONATION_SEAL,
            baseDamage = 95f,
            baseCooldown = 2.4f,
            arcRadius = 145f
        )

        val ChronoScribe = SpellDefinition(
            id = "chrono_scribe",
            name = "Chronos Orbit",
            description = "Sweeps an astrological dial of celestial ink around the hero, heavily slowing and grinding all enemies.",
            castType = SpellCastType.ORBITAL_RUNES,
            baseDamage = 38f,
            baseCooldown = 2.8f,
            arcRadius = 135f
        )

        val TheNineHeadedHydra = SpellDefinition(
            id = "nine_headed_hydra",
            name = "The Nine-Headed Hydra",
            description = "Unleashes three giant undulating abyssal ink serpents that carve through the canvas, leaving toxic ink maelstroms.",
            castType = SpellCastType.FLOWING_SERPENT,
            baseDamage = 135f,
            baseCooldown = 2.2f,
            baseSpeed = 460f,
            areaRadius = 55f,
            durationSeconds = 6.0f
        )

        val TsunamiSurge = SpellDefinition(
            id = "tsunami_surge",
            name = "Tsunami Surge",
            description = "Releases a colossal sweeping tidal wall of compressed black ink washing across the whole screen.",
            castType = SpellCastType.WAVE_TRAIL,
            baseDamage = 140f,
            baseCooldown = 1.3f,
            baseSpeed = 540f,
            basePierce = 99999,
            projectileLength = 160f,
            projectileWidth = 45f
        )

        val CelestialChronometer = SpellDefinition(
            id = "celestial_chronometer",
            name = "Celestial Chronometer",
            description = "Sweeps an expansive cosmic astrological dial that severely slows foes by 60% and deals crushing damage.",
            castType = SpellCastType.ORBITAL_RUNES,
            baseDamage = 95f,
            baseCooldown = 1.9f,
            arcRadius = 185f
        )

        val FrozenPermafrost = SpellDefinition(
            id = "frozen_permafrost",
            name = "Frozen Permafrost",
            description = "Erupts an advancing glacier of black-ice spikes beneath enemies, instantly freezing and shattering hordes.",
            castType = SpellCastType.DETONATION_SEAL,
            baseDamage = 175f,
            baseCooldown = 1.6f,
            arcRadius = 160f
        )

        val CelestialVolcano = SpellDefinition(
            id = "celestial_volcano",
            name = "Celestial Volcano",
            description = "Rains a torrential storm of molten cinnabar meteors that ignite permanent fiery pools across the canvas.",
            castType = SpellCastType.DETONATION_SEAL,
            baseDamage = 220f,
            baseCooldown = 1.8f,
            arcRadius = 190f
        )

        val baseSpells = listOf(
            QuillDart, WashBrush, SteelFountain, OrbitalRunes, CinnabarSeal,
            CalligraphersWake, AbyssalSerpent, TidalBrushWave,
            ShadowShuriken, GlacialSpike, CinnabarMeteor, ChronoScribe
        )
        val evolvedSpells = listOf(
            TheHarpoon, InkwellVortex, FountainBarrage,
            TheNineHeadedHydra, TsunamiSurge, CelestialChronometer,
            FrozenPermafrost, CelestialVolcano
        )
        val allSpells = listOf(
            QuillDart, WashBrush, SteelFountain,
            OrbitalRunes, CinnabarSeal,
            CalligraphersWake, AbyssalSerpent, TidalBrushWave,
            ShadowShuriken, GlacialSpike, CinnabarMeteor, ChronoScribe,
            TheHarpoon, InkwellVortex, FountainBarrage,
            TheNineHeadedHydra, TsunamiSurge, CelestialChronometer,
            FrozenPermafrost, CelestialVolcano,
            TheMastersDecree, TheRendingTide
        )
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

        // Synthesis 4: Nine-Headed Hydra = Abyssal Serpent (Max) + Dense Soot (Max)
        val HydraSynthesis = SpellSynthesisRecipe(
            id = "synthesis_hydra",
            name = "Nine-Headed Hydra",
            requiredSpellId = "abyssal_serpent",
            requiredGearId = "dense_soot",
            evolvedSpell = SpellDefinition.TheNineHeadedHydra,
            description = "Evolves Abyssal Serpent into a trio of giant undulating serpentine leviathans."
        )

        // Synthesis 5: Tsunami Surge = Tidal Cleave (Max) + Scribe's Sandal (Max)
        val TsunamiSynthesis = SpellSynthesisRecipe(
            id = "synthesis_tsunami",
            name = "Tsunami Surge",
            requiredSpellId = "tidal_brush_wave",
            requiredGearId = "scribes_sandal",
            evolvedSpell = SpellDefinition.TsunamiSurge,
            description = "Evolves Tidal Cleave into a screen-clearing tidal deluge."
        )

        // Synthesis 6: Celestial Chronometer = Chronos Orbit (Max) + Lodestone Inkwell (Max)
        val ChronometerSynthesis = SpellSynthesisRecipe(
            id = "synthesis_chronometer",
            name = "Celestial Chronometer",
            requiredSpellId = "chrono_scribe",
            requiredGearId = "lodestone_inkwell",
            evolvedSpell = SpellDefinition.CelestialChronometer,
            description = "Evolves Chronos Orbit into an immense cosmic clock of devastating gravitational reach."
        )

        // Synthesis 7: Frozen Permafrost = Glacial Spike (Max) + Heavy Vellum (Max)
        val PermafrostSynthesis = SpellSynthesisRecipe(
            id = "synthesis_permafrost",
            name = "Frozen Permafrost",
            requiredSpellId = "glacial_spike",
            requiredGearId = "heavy_vellum",
            evolvedSpell = SpellDefinition.FrozenPermafrost,
            description = "Evolves Glacial Spike into a cascading wall of frozen black ice."
        )

        // Synthesis 8: Celestial Volcano = Cinnabar Meteor (Max) + Spring Water (Max)
        val VolcanoSynthesis = SpellSynthesisRecipe(
            id = "synthesis_volcano",
            name = "Celestial Volcano",
            requiredSpellId = "cinnabar_meteor",
            requiredGearId = "spring_water",
            evolvedSpell = SpellDefinition.CelestialVolcano,
            description = "Evolves Cinnabar Meteor into apocalyptic volcanic eruptions across the canvas."
        )

        val allRecipes = listOf(
            HarpoonSynthesis, VortexSynthesis, BarrageSynthesis,
            HydraSynthesis, TsunamiSynthesis, ChronometerSynthesis,
            PermafrostSynthesis, VolcanoSynthesis
        )
    }
}

/**
 * Phase 7: Sacrificial Fusion Recipe
 *
 * Unlike synthesis (which preserves both spells), a Fusion:
 *  1. Takes the PRIMARY spell (mutated/upgraded)
 *  2. Permanently DELETES the CATALYST spell from inventory
 *  3. Replaces the primary spell with [fusedSpell]
 *
 * This forces a fundamental playstyle shift — the player cannot undo the sacrifice.
 */
data class FusionRecipe(
    val id: String,
    val name: String,
    val primarySpellId: String,    // Spell that gets mutated (upgraded)
    val catalystSpellId: String,   // Spell that is SACRIFICED and permanently deleted
    val fusedSpell: SpellDefinition,
    val description: String,
    val playstyleWarning: String   // Shown before confirming — warns player of the shift
) {
    companion object {
        /**
         * Implementation Test: "The Rending Tide"
         * Primary:  Quill Dart  (continues as the evolved form)
         * Catalyst: Wash Brush  (permanently DELETED from inventory)
         *
         * Before: static defensive positioning (puddles protect player)
         * After:  constant forward aggression (must keep moving to ride the wave)
         */
        val RendingTideFusion = FusionRecipe(
            id = "fusion_rending_tide",
            name = "The Rending Tide",
            primarySpellId = "quill_dart",
            catalystSpellId = "wash_brush",
            fusedSpell = SpellDefinition.TheRendingTide,
            description = "Your Quill Dart absorbs the fluid nature of the Wash Brush, erupting into a massive surging ink wave that leaves corrosive trails.",
            playstyleWarning = "⚠ The Wash Brush will be PERMANENTLY DELETED. Your defensive puddle system is gone forever. You must now stay mobile."
        )

        val allFusions = listOf(RendingTideFusion)

        /** Find if player's current spells qualify for any known fusion. */
        fun findAvailableFusion(spells: List<ActiveSpell>, completedFusions: Set<String>): FusionRecipe? {
            for (fusion in allFusions) {
                if (completedFusions.contains(fusion.id)) continue
                val hasPrimary = spells.any { it.definition.id == fusion.primarySpellId && it.rank >= 7 }
                val hasCatalyst = spells.any { it.definition.id == fusion.catalystSpellId && it.rank >= 7 }
                if (hasPrimary && hasCatalyst) return fusion
            }
            return null
        }
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

    fun getEffectiveCooldown(attackSpeedMultiplier: Float, cooldownMultiplier: Float = 1.0f): Float {
        val baseCd = definition.baseCooldown * (1f - (rank - 1) * 0.05f)
        return (baseCd / attackSpeedMultiplier * cooldownMultiplier).coerceAtLeast(0.12f)
    }

    fun getEffectiveDamage(damageMultiplier: Float): Float {
        return definition.baseDamage * (1f + (rank - 1) * 0.25f) * damageMultiplier
    }

    fun getEffectiveAreaRadius(areaMultiplier: Float = 1.0f): Float {
        val rankBonus = 1f + (rank - 1) * 0.12f
        val baseR = if (definition.areaRadius > 0f) definition.areaRadius else definition.arcRadius
        return baseR * rankBonus * areaMultiplier
    }

    fun getEffectiveDuration(durationMultiplier: Float = 1.0f): Float {
        val rankBonus = 1f + (rank - 1) * 0.10f
        return definition.durationSeconds * rankBonus * durationMultiplier
    }

    val element: CalligraphicElement
        get() {
            return when {
                definition.id == "cinnabar_seal" || definition.id == "cinnabar_meteor" || hasTrait("volatile_core") || hasTrait("chain_reaction") -> CalligraphicElement.CINNABAR_FLAME
                definition.id == "steel_fountain" || definition.id == "quill_dart" || definition.id == "the_harpoon" || definition.id == "fountain_barrage" || definition.id == "glacial_spike" -> CalligraphicElement.FROST
                definition.id == "wash_brush" || definition.id == "calligraphers_wake" || definition.id == "the_rending_tide" || definition.id == "inkwell_vortex" || definition.id == "shadow_shuriken" || hasTrait("wide_bristle") -> CalligraphicElement.CORROSIVE_ACID
                definition.id == "orbital_runes" || definition.id == "abyssal_serpent" || definition.id == "the_masters_decree" || definition.id == "chrono_scribe" || hasTrait("astral_expansion") -> CalligraphicElement.CELESTIAL_ASTRAL
                else -> CalligraphicElement.CORROSIVE_ACID
            }
        }
}
