package com.example.game

import com.example.model.ActiveSpell
import com.example.model.EquippedGear
import com.example.model.SpellSynthesisRecipe

/**
 * Phase 4 Asynchronous Synthesis Manager
 *
 * - Milestone Tracker: Unlocks Slot1 at Level 35, Slot2 at Level 60, and Slot3 at Level 95.
 * - Continuous Listener: Evaluates open synthesis slots whenever the player levels up or updates inventory.
 *   If an open slot exists and prerequisites are met, immediately yields the evolution recipe.
 * - Magnum Opus Validator: At Level 100, checks strict conditions for The Master's Decree.
 */
class SynthesisManager {
    var slot1Unlocked: Boolean = false
        private set
    var slot2Unlocked: Boolean = false
        private set
    var slot3Unlocked: Boolean = false
        private set

    val totalUnlockedSlots: Int
        get() = (if (slot1Unlocked) 1 else 0) + (if (slot2Unlocked) 1 else 0) + (if (slot3Unlocked) 1 else 0)

    fun reset() {
        slot1Unlocked = false
        slot2Unlocked = false
        slot3Unlocked = false
    }

    /**
     * Checks level milestones:
     * - Level 35: Slot 1
     * - Level 60: Slot 2
     * - Level 95: Slot 3
     * Returns true if a new slot was unlocked.
     */
    fun onLevelReached(level: Int): Boolean {
        var newlyUnlocked = false
        if (level >= 35 && !slot1Unlocked) {
            slot1Unlocked = true
            newlyUnlocked = true
        }
        if (level >= 60 && !slot2Unlocked) {
            slot2Unlocked = true
            newlyUnlocked = true
        }
        if (level >= 95 && !slot3Unlocked) {
            slot3Unlocked = true
            newlyUnlocked = true
        }
        return newlyUnlocked
    }

    /**
     * Continuous asynchronous listener check:
     * If an open slot exists, checks whether the player has a valid Max Level Spell (Lv 7) +
     * Max Level Gear (5 Stacks) combination that hasn't been completed yet.
     */
    fun findAvailableSynthesis(
        spells: List<ActiveSpell>,
        gear: List<EquippedGear>,
        completedSyntheses: Set<String>
    ): SpellSynthesisRecipe? {
        if (completedSyntheses.size >= totalUnlockedSlots) {
            return null
        }

        for (recipe in SpellSynthesisRecipe.allRecipes) {
            if (completedSyntheses.contains(recipe.id)) continue

            val hasMaxSpell = spells.any { it.definition.id == recipe.requiredSpellId && it.rank >= 7 }
            val hasMaxGear = gear.any { it.type.id == recipe.requiredGearId && it.stacks >= 5 }

            if (hasMaxSpell && hasMaxGear) {
                return recipe
            }
        }
        return null
    }

    /**
     * Phase 4 Level 100 Magnum Opus strict validation:
     * Requires:
     * 1. Level >= 100
     * 2. Character: The Calligrapher ("calligrapher")
     * 3. Class: Scribe ("scribe")
     * 4. Exactly 3 completed evolutions: The Harpoon, Inkwell Vortex, Fountain Barrage
     */
    fun validateMagnumOpusUltimate(
        level: Int,
        characterId: String,
        classId: String,
        completedSyntheses: Set<String>
    ): Boolean {
        if (level < 100) return false
        val isCalligrapher = characterId.equals("calligrapher", ignoreCase = true)
        val isScribe = classId.equals("scribe", ignoreCase = true)
        val hasAllThreeEvolutions = completedSyntheses.containsAll(
            listOf("synthesis_harpoon", "synthesis_vortex", "synthesis_barrage")
        )
        return isCalligrapher && isScribe && hasAllThreeEvolutions
    }
}
