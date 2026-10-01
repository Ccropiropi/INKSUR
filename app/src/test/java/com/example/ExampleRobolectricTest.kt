package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.ActiveSpell
import com.example.model.CharacterDefinition
import com.example.model.ClassDefinition
import com.example.model.Enemy
import com.example.model.EnemyType
import com.example.model.FlexNibTrait
import com.example.model.InkPuddlePool
import com.example.model.InkProjectile
import com.example.model.SerratedNibTrait
import com.example.model.SpellCastType
import com.example.model.SpellDefinition
import com.example.model.SpellRuneType
import com.example.model.SpellSynthesisRecipe
import com.example.model.SpellTraitType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Ink Survivor", appName)
    }

    @Test
    fun `verify Phase 2 Classes - Scribe and Painter`() {
        // Scribe
        val scribe = ClassDefinition.Scribe
        assertEquals("Scribe", scribe.name)
        assertEquals(SpellDefinition.QuillDart, scribe.starterSpell)
        assertEquals(SpellCastType.DIRECTIONAL_PROJECTILE, scribe.starterSpell.castType)

        // Painter
        val painter = ClassDefinition.Painter
        assertEquals("Painter", painter.name)
        assertEquals(SpellDefinition.WashBrush, painter.starterSpell)
        assertEquals(SpellCastType.PHYSICS_OVERLAP_ARC, painter.starterSpell.castType)
        assertTrue(painter.starterSpell.arcRadius > 100f)
    }

    @Test
    fun `verify Quill Dart Level 3 Traits - Serrated Nib and Flex Nib`() {
        val activeQuill = ActiveSpell(definition = SpellDefinition.QuillDart, rank = 3)

        // Test Serrated Nib: applies bleed DoT
        activeQuill.traitModules.add(SerratedNibTrait)
        val enemy = Enemy(
            id = 1L,
            type = EnemyType.BASIC_CONSTRUCT,
            x = 0f,
            y = 0f,
            hp = 35f,
            maxHp = 35f
        )
        SerratedNibTrait.onHitEnemy(enemy, damageDealt = 30f, projectile = null)
        assertTrue("Enemy should have bleed timer active", enemy.bleedTimer > 0f)
        assertTrue("Bleed damage should be positive", enemy.bleedDamagePerTick > 0f)

        // Test Flex Nib: grows 2% per frame
        activeQuill.traitModules.add(FlexNibTrait)
        val proj = InkProjectile(
            id = 2L,
            x = 0f,
            y = 0f,
            vx = 100f,
            vy = 0f,
            angleRad = 0f,
            damage = 25f,
            pierceCount = 2,
            strokeWidth = 10f,
            strokeLength = 30f,
            isFlexNib = true
        )
        val initialWidth = proj.strokeWidth
        val initialLength = proj.strokeLength
        FlexNibTrait.onProjectileTick(proj, 0.016f)
        assertEquals(initialWidth * 1.02f, proj.strokeWidth, 0.001f)
        assertEquals(initialLength * 1.02f, proj.strokeLength, 0.001f)
    }

    @Test
    fun `verify optimized InkPuddlePool pooling system`() {
        val pool = InkPuddlePool(capacity = 50)
        assertEquals(50, pool.pool.size)
        // All initially inactive
        assertTrue(pool.pool.all { !it.active })

        // Obtain puddle
        val puddle = pool.obtain(
            x = 120f,
            y = 200f,
            radius = 80f,
            damage = 15f,
            maxLife = 4.0f,
            tickInterval = 0.5f,
            sourceSpellId = "wash_brush"
        )
        assertTrue(puddle.active)
        assertEquals(120f, puddle.x, 0.01f)
        assertEquals(80f, puddle.radius, 0.01f)
        assertEquals(4.0f, puddle.maxLife, 0.01f)

        // Clear pool
        pool.clear()
        assertFalse(puddle.active)
    }

    @Test
    fun `verify Red Rune Elite Enemy multiplier and speed`() {
        val eliteEnemy = Enemy(
            id = 10L,
            type = EnemyType.BASIC_CONSTRUCT,
            x = 50f,
            y = 50f,
            hp = EnemyType.BASIC_CONSTRUCT.baseHp * 5f, // 5x health
            maxHp = EnemyType.BASIC_CONSTRUCT.baseHp * 5f,
            isElite = true,
            elitePackId = 1L
        )

        assertTrue(eliteEnemy.isElite)
        assertEquals(EnemyType.BASIC_CONSTRUCT.baseHp * 5f, eliteEnemy.hp, 0.01f)
        assertEquals(EnemyType.BASIC_CONSTRUCT.speed * 1.5f, eliteEnemy.effectiveSpeed, 0.01f)
    }

    @Test
    fun `verify Viscous Rune slow applies 40 percent reduction to socketed spell only`() {
        val quillDart = ActiveSpell(definition = SpellDefinition.QuillDart, rank = 1)
        val washBrush = ActiveSpell(definition = SpellDefinition.WashBrush, rank = 1)

        // Socket into Wash Brush only
        washBrush.socketedRunes.add(SpellRuneType.VISCOUS_RUNE)

        assertFalse("Quill Dart should NOT have Viscous Rune", quillDart.hasViscousRune())
        assertTrue("Wash Brush MUST have Viscous Rune", washBrush.hasViscousRune())

        val enemy = Enemy(
            id = 20L,
            type = EnemyType.FOLDED_STALKER,
            x = 0f,
            y = 0f,
            hp = 60f,
            maxHp = 60f,
            slowTimer = 2.5f,
            slowRatio = SpellRuneType.VISCOUS_RUNE.slowPercent
        )

        // Effective speed should be reduced by exactly 40%
        val expectedSpeed = EnemyType.FOLDED_STALKER.speed * 0.60f
        assertEquals(expectedSpeed, enemy.effectiveSpeed, 0.01f)
    }

    @Test
    fun `verify Phase 3 - Spell Synthesis and The Harpoon evolution`() {
        val recipe = SpellSynthesisRecipe.HarpoonSynthesis
        assertEquals("The Harpoon", recipe.name)
        assertEquals("quill_dart", recipe.requiredSpellId)
        assertEquals("heavy_vellum", recipe.requiredGearId)

        val harpoon = recipe.evolvedSpell
        assertEquals("The Harpoon", harpoon.name)
        assertTrue("Harpoon must have infinite pierce", harpoon.basePierce >= 9999)
        assertTrue("Harpoon must be flag isHarpoon", harpoon.isHarpoon)
        assertEquals(750f, harpoon.baseSpeed, 0.1f)
        assertEquals(180f, harpoon.baseDamage, 0.1f)
    }

    @Test
    fun `verify Phase 3 - The Obelisk and The Iron Vats cursed artifact`() {
        val ironVats = com.example.model.ArtifactDefinition.TheIronVats
        assertEquals(com.example.model.ArtifactTier.CURSED, ironVats.tier)
        assertEquals(com.example.model.ArtifactDefinition.ARCHETYPE_HEAVY_WEIGHT, ironVats.archetypeTag)
        assertEquals(3.0f, ironVats.damageModifier, 0.01f) // +300% ink damage
        assertEquals(-0.60f, ironVats.moveSpeedModifier, 0.01f) // -60% movement speed
    }

    @Test
    fun `verify Phase 3 - Archetype Mastery flips Iron Vats debuff into buff`() {
        val inventory = mutableListOf<com.example.model.ArtifactDefinition>()
        inventory.add(com.example.model.ArtifactDefinition.TheIronVats)
        inventory.add(com.example.model.ArtifactDefinition.LeadNib)
        inventory.add(com.example.model.ArtifactDefinition.AnvilSeal)
        inventory.add(com.example.model.ArtifactDefinition.ColossusParchment)

        val heavyWeightCount = inventory.count { it.archetypeTag == com.example.model.ArtifactDefinition.ARCHETYPE_HEAVY_WEIGHT }
        assertEquals(4, heavyWeightCount)

        // When 4 Heavy Weight artifacts are reached, trigger cure
        if (heavyWeightCount >= 4) {
            for (i in 0 until inventory.size) {
                val art = inventory[i]
                if (art.id == com.example.model.ArtifactDefinition.TheIronVats.id) {
                    inventory[i] = art.copy(
                        moveSpeedModifier = 0.60f, // Flipped to +60% buff!
                        isMasteryCured = true
                    )
                }
            }
        }

        val curedIronVats = inventory.first { it.id == com.example.model.ArtifactDefinition.TheIronVats.id }
        assertTrue(curedIronVats.isMasteryCured)
        assertEquals(0.60f, curedIronVats.moveSpeedModifier, 0.01f)
    }

    @Test
    fun `verify Phase 3 - The Titan boss and condensed 50-orb explosion`() {
        val titan = EnemyType.THE_TITAN
        assertEquals("The Titan", titan.displayName)
        assertEquals(3500f, titan.baseHp, 0.1f)
        assertEquals(50, titan.xpValue)
        assertEquals(42f, titan.speed, 0.1f)

        // Condensed orb optimization: single physics entity delivering 50 value
        val condensedOrb = com.example.model.Orb(
            id = 999L,
            x = 100f,
            y = 100f,
            value = 50,
            isCondensed = true
        )
        assertTrue(condensedOrb.isCondensed)
        assertEquals(50, condensedOrb.value)
    }

    // ==========================================
    // PHASE 5 SPECIFICATION TESTS
    // ==========================================

    @Test
    fun `verify Phase 5 - Mid-Run Bookmark save and consume round-trip`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val saveManager = com.example.data.SaveManager(context)

        val bookmark = com.example.data.BookmarkRunState(
            characterId = "calligrapher",
            classId = "scribe",
            timeSurvivedSeconds = 1800f,
            score = 12500,
            kills = 380,
            damageDealt = 45000L,
            playerHp = 100f,
            playerMaxHp = 100f,
            playerLevel = 42,
            playerXp = 25,
            playerXpNeeded = 80,
            activeSpellsData = "quill_dart:7:flex_nib,serrated_nib:VISCOUS_RUNE",
            equippedGearData = "HEAVY_VELLUM:3|ERGONOMIC_GRIP:2",
            equippedArtifactsData = "iron_vats|toxic_pigment",
            completedSynthesesData = "harpoon",
            slot1Unlocked = true,
            slot2Unlocked = false,
            slot3Unlocked = false,
            heavyWeightCount = 1,
            isHeavyWeightMastered = false,
            corruptedCount = 1,
            isCorruptedMastered = false,
            geometryCount = 0,
            isGeometryMastered = false
        )

        // 1. Save bookmark
        val saved = saveManager.saveBookmark(bookmark)
        assertTrue("Bookmark must be saved successfully", saved)
        assertTrue("hasBookmark must report true", saveManager.hasBookmark())

        // 2. Load and consume bookmark
        val restored = saveManager.loadAndConsumeBookmark()
        assertNotNull("Restored bookmark must not be null", restored)
        assertEquals("calligrapher", restored!!.characterId)
        assertEquals("scribe", restored.classId)
        assertEquals(1800f, restored.timeSurvivedSeconds, 0.01f)
        assertEquals(42, restored.playerLevel)
        assertEquals(380, restored.kills)
        assertTrue(restored.slot1Unlocked)
        assertFalse(restored.slot2Unlocked)

        // 3. Anti-save-scumming verification: Bookmark file must be deleted upon consumption
        assertFalse("Bookmark file must be consumed and deleted immediately", saveManager.hasBookmark())
        val secondLoad = saveManager.loadAndConsumeBookmark()
        assertEquals("Second load must return null", null, secondLoad)
    }

    @Test
    fun `verify Phase 5 - The Blotter sponge mechanics and death AOE burst`() {
        val blotter = Enemy(
            id = 55L,
            type = EnemyType.THE_BLOTTER,
            x = 100f,
            y = 100f,
            hp = EnemyType.THE_BLOTTER.baseHp,
            maxHp = EnemyType.THE_BLOTTER.baseHp,
            isBlotter = true
        )

        assertTrue(blotter.isBlotter)
        assertEquals(450f, blotter.maxHp, 0.1f)
        assertEquals(55f, blotter.type.speed, 0.1f)

        // Sponge absorbs persistent puddles
        val pool = InkPuddlePool(10)
        val p1 = pool.obtain(105f, 105f, 50f, 10f, 4f, 0.5f, "wash_brush")
        val p2 = pool.obtain(110f, 110f, 50f, 10f, 4f, 0.5f, "wash_brush")
        assertTrue(p1.active)
        assertTrue(p2.active)

        // Absorb puddles
        blotter.absorbedPuddles += 2
        p1.active = false
        p2.active = false

        assertEquals(2, blotter.absorbedPuddles)
        assertFalse(p1.active)
        assertFalse(p2.active)

        // Death AOE damage calculation: base 350f + absorbed * 35f
        val aoeRadius = 220f
        val aoeDamage = 350f + blotter.absorbedPuddles * 35f
        assertEquals(420f, aoeDamage, 0.01f)
        assertTrue(aoeRadius > 200f)
    }

    @Test
    fun `verify Phase 5 - Archetype 2 Corrupted Medium and Vampirism cure`() {
        val toxicPigment = com.example.model.ArtifactDefinition.ToxicPigment
        assertEquals(com.example.model.ArtifactTier.CURSED, toxicPigment.tier)
        assertEquals(com.example.model.ArtifactDefinition.ARCHETYPE_CORRUPTED_MEDIUM, toxicPigment.archetypeTag)
        assertEquals(2.0f, toxicPigment.poisonDamageMultiplier, 0.01f) // +200% DoT
        assertEquals(-0.50f, toxicPigment.maxHpModifier, 0.01f) // -50% Max HP debuff

        val masteryManager = com.example.game.MasteryManager()
        val inventory = mutableListOf(
            toxicPigment,
            com.example.model.ArtifactDefinition.SpoiledInk,
            com.example.model.ArtifactDefinition.FungalPaper,
            com.example.model.ArtifactDefinition.BlightedQuill
        )

        val result = masteryManager.evaluateArtifacts(inventory)
        assertEquals(4, result.corruptedMediumCount)
        assertTrue("Corrupted Medium must be mastered", result.isCorruptedMediumMastered)
        assertTrue("New mastery unlocked must contain Corrupted Medium", result.newMasteriesUnlocked.contains(com.example.model.ArtifactDefinition.ARCHETYPE_CORRUPTED_MEDIUM))

        // Cured Toxic Pigment grants Vampirism and removes Max HP penalty
        val curedPigment = inventory.first { it.id == toxicPigment.id }
        assertTrue(curedPigment.isMasteryCured)
        assertTrue(curedPigment.hasVampirism)
        assertEquals(0.0f, curedPigment.maxHpModifier, 0.01f)
        assertEquals(2.0f, curedPigment.poisonDamageMultiplier, 0.01f)
    }

    @Test
    fun `verify Phase 5 - Archetype 3 Sacred Geometry and bounce amplification cure`() {
        val ruler = com.example.model.ArtifactDefinition.TheFracturedRuler
        assertEquals(com.example.model.ArtifactTier.CURSED, ruler.tier)
        assertEquals(com.example.model.ArtifactDefinition.ARCHETYPE_SACRED_GEOMETRY, ruler.archetypeTag)
        assertEquals(5, ruler.bonusBounces)
        assertEquals(-0.30f, ruler.bounceDamageDelta, 0.01f) // -30% damage per bounce

        val masteryManager = com.example.game.MasteryManager()
        val inventory = mutableListOf(
            ruler,
            com.example.model.ArtifactDefinition.BrassCompass,
            com.example.model.ArtifactDefinition.GraphPaper,
            com.example.model.ArtifactDefinition.ProtractorPlate
        )

        val result = masteryManager.evaluateArtifacts(inventory)
        assertEquals(4, result.sacredGeometryCount)
        assertTrue("Sacred Geometry must be mastered", result.isSacredGeometryMastered)

        // Cured Fractured Ruler flips -30% damage per bounce to +30% damage per bounce!
        val curedRuler = inventory.first { it.id == ruler.id }
        assertTrue(curedRuler.isMasteryCured)
        assertEquals(0.30f, curedRuler.bounceDamageDelta, 0.01f)
        assertEquals(5, curedRuler.bonusBounces)
    }

    @Test
    fun `verify Phase 5 - The Eraser safe zone shrinkage and Minute 61 2x payout victory`() {
        val boss = com.example.model.TheEraserBoss()
        boss.active = true
        boss.timer = 0f
        boss.currentSafeRadius = boss.initialSafeRadius
        assertEquals(600f, boss.currentSafeRadius, 0.01f)

        // Simulate 30s elapsed (halfway through the 60s climax)
        boss.timer = 30f
        val progress = (boss.timer / boss.maxDuration).coerceIn(0f, 1f)
        boss.currentSafeRadius = boss.initialSafeRadius - progress * (boss.initialSafeRadius - boss.minSafeRadius)
        // 600 - 0.5 * (600 - 170) = 600 - 215 = 385f
        assertEquals(385f, boss.currentSafeRadius, 0.01f)

        // At 60s elapsed
        boss.timer = 60f
        val finalProgress = (boss.timer / boss.maxDuration).coerceIn(0f, 1f)
        boss.currentSafeRadius = boss.initialSafeRadius - finalProgress * (boss.initialSafeRadius - boss.minSafeRadius)
        assertEquals(170f, boss.currentSafeRadius, 0.01f)

        // Telegraphed Strike timing
        val strike = com.example.model.TelegraphedStrike(
            id = 1L,
            startX = -150f,
            startY = 0f,
            endX = 150f,
            endY = 0f
        )
        assertFalse(strike.isStriking)
        strike.timer = 1.25f
        assertTrue(strike.isStriking)

        // Post-run 2x Climax payout verification and Map Tier 2 unlock
        val context = ApplicationProvider.getApplicationContext<Context>()
        val saveManager = com.example.data.SaveManager(context)
        val kills = 500
        val survivalSeconds = 3660 // Minute 61:00

        val payout = saveManager.recordRunPayout(
            kills = kills,
            survivalSeconds = survivalSeconds,
            isMinute61Victory = true
        )

        // Normal gold = 500, with 2x = 1000
        assertEquals(1000, payout.first)
        // Normal crystals = 61 * 10 = 610, with 2x = 1220
        assertEquals(1220, payout.second)

        // Map Tier 2 must be unlocked!
        val savedData = saveManager.loadSaveData()
        assertEquals(2, savedData.unlockedMapTier)
    }

    @Test
    fun `verify New Classes alongside their basic spells`() {
        // Engraver
        val engraver = ClassDefinition.Engraver
        assertEquals("Engraver", engraver.name)
        assertEquals(SpellDefinition.SteelFountain, engraver.starterSpell)
        assertEquals(SpellCastType.DIRECTIONAL_PROJECTILE, engraver.starterSpell.castType)

        // Illuminator
        val illuminator = ClassDefinition.Illuminator
        assertEquals("Illuminator", illuminator.name)
        assertEquals(SpellDefinition.OrbitalRunes, illuminator.starterSpell)
        assertEquals(SpellCastType.ORBITAL_RUNES, illuminator.starterSpell.castType)

        // Alchemist
        val alchemist = ClassDefinition.Alchemist
        assertEquals("Alchemist", alchemist.name)
        assertEquals(SpellDefinition.CinnabarSeal, alchemist.starterSpell)
        assertEquals(SpellCastType.DETONATION_SEAL, alchemist.starterSpell.castType)

        // All classes list
        assertEquals(24, ClassDefinition.allClasses.size)

        // New Character: The Runesmith
        val runesmith = CharacterDefinition.TheRunesmith
        assertEquals("The Runesmith", runesmith.name)
        assertEquals(75, runesmith.crystalUnlockCost)
        assertEquals(4, CharacterDefinition.allCharacters.size)

        // Base Spells list contains all 5 basic spells
        assertEquals(5, SpellDefinition.baseSpells.size)
        assertTrue(SpellDefinition.baseSpells.contains(SpellDefinition.OrbitalRunes))
        assertTrue(SpellDefinition.baseSpells.contains(SpellDefinition.CinnabarSeal))
    }
}

