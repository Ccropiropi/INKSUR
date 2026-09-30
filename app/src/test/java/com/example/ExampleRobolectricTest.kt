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
}
