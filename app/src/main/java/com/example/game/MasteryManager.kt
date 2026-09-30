package com.example.game

import com.example.model.ArtifactDefinition

data class MasteryEvaluationResult(
    val newMasteriesUnlocked: List<String> = emptyList(),
    val heavyWeightCount: Int,
    val isHeavyWeightMastered: Boolean,
    val corruptedMediumCount: Int,
    val isCorruptedMediumMastered: Boolean,
    val sacredGeometryCount: Int,
    val isSacredGeometryMastered: Boolean
)

/**
 * Phase 5 Artifact Archetype Expansion (Mastery Dictionary)
 *
 * Background manager tracking playstyle archetypes:
 * 1. Heavy Weight: 4 items cures The Iron Vats (-60% Speed -> +60% Speed).
 * 2. Corrupted Medium: 4 items cures Toxic Pigment (-50% Max HP -> Vampirism DoT HP regeneration).
 * 3. Sacred Geometry: 4 items cures The Fractured Ruler (-30% bounce damage -> +30% bounce damage).
 */
class MasteryManager {

    var isHeavyWeightMastered: Boolean = false
        private set
    var isCorruptedMediumMastered: Boolean = false
        private set
    var isSacredGeometryMastered: Boolean = false
        private set

    var heavyWeightCount: Int = 0
        private set
    var corruptedMediumCount: Int = 0
        private set
    var sacredGeometryCount: Int = 0
        private set

    fun reset() {
        isHeavyWeightMastered = false
        isCorruptedMediumMastered = false
        isSacredGeometryMastered = false
        heavyWeightCount = 0
        corruptedMediumCount = 0
        sacredGeometryCount = 0
    }

    fun restore(
        heavyCount: Int,
        heavyMastered: Boolean,
        corruptedCount: Int,
        corruptedMastered: Boolean,
        geometryCount: Int,
        geometryMastered: Boolean
    ) {
        heavyWeightCount = heavyCount
        isHeavyWeightMastered = heavyMastered
        corruptedMediumCount = corruptedCount
        isCorruptedMediumMastered = corruptedMastered
        sacredGeometryCount = geometryCount
        isSacredGeometryMastered = geometryMastered
    }

    /**
     * Evaluates current artifacts list, counts archetypes, and cures corresponding cursed items
     * when the 4-item threshold is achieved.
     */
    fun evaluateArtifacts(artifacts: MutableList<ArtifactDefinition>): MasteryEvaluationResult {
        heavyWeightCount = artifacts.count { it.archetypeTag == ArtifactDefinition.ARCHETYPE_HEAVY_WEIGHT }
        corruptedMediumCount = artifacts.count { it.archetypeTag == ArtifactDefinition.ARCHETYPE_CORRUPTED_MEDIUM }
        sacredGeometryCount = artifacts.count { it.archetypeTag == ArtifactDefinition.ARCHETYPE_SACRED_GEOMETRY }

        val newUnlocks = mutableListOf<String>()

        // 1. Heavy Weight Mastery
        if (heavyWeightCount >= 4 && !isHeavyWeightMastered) {
            isHeavyWeightMastered = true
            newUnlocks.add(ArtifactDefinition.ARCHETYPE_HEAVY_WEIGHT)
            for (i in 0 until artifacts.size) {
                if (artifacts[i].id == ArtifactDefinition.TheIronVats.id) {
                    artifacts[i] = artifacts[i].copy(
                        moveSpeedModifier = 0.60f,
                        isMasteryCured = true
                    )
                }
            }
        }

        // 2. Corrupted Medium Mastery (Vampirism Cure)
        if (corruptedMediumCount >= 4 && !isCorruptedMediumMastered) {
            isCorruptedMediumMastered = true
            newUnlocks.add(ArtifactDefinition.ARCHETYPE_CORRUPTED_MEDIUM)
            for (i in 0 until artifacts.size) {
                if (artifacts[i].id == ArtifactDefinition.ToxicPigment.id) {
                    artifacts[i] = artifacts[i].copy(
                        maxHpMultiplier = 0.0f,
                        hasVampirism = true,
                        isMasteryCured = true
                    )
                }
            }
        }

        // 3. Sacred Geometry Mastery (Harmonic Bounce Cure)
        if (sacredGeometryCount >= 4 && !isSacredGeometryMastered) {
            isSacredGeometryMastered = true
            newUnlocks.add(ArtifactDefinition.ARCHETYPE_SACRED_GEOMETRY)
            for (i in 0 until artifacts.size) {
                if (artifacts[i].id == ArtifactDefinition.TheFracturedRuler.id) {
                    artifacts[i] = artifacts[i].copy(
                        bounceDamageDelta = 0.30f,
                        isMasteryCured = true
                    )
                }
            }
        }

        return MasteryEvaluationResult(
            newMasteriesUnlocked = newUnlocks,
            heavyWeightCount = heavyWeightCount,
            isHeavyWeightMastered = isHeavyWeightMastered,
            corruptedMediumCount = corruptedMediumCount,
            isCorruptedMediumMastered = isCorruptedMediumMastered,
            sacredGeometryCount = sacredGeometryCount,
            isSacredGeometryMastered = isSacredGeometryMastered
        )
    }
}
