package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepository(
    private val runDao: RunDao,
    private val playerProgressDao: PlayerProgressDao
) {
    val allRuns: Flow<List<RunRecordEntity>> = runDao.getAllRuns()
    val topScores: Flow<List<RunRecordEntity>> = runDao.getTopScores()
    val progress: Flow<PlayerProgressEntity> = playerProgressDao.getProgress().map {
        it ?: PlayerProgressEntity()
    }

    suspend fun getProgressOnce(): PlayerProgressEntity {
        return playerProgressDao.getProgressOnce() ?: PlayerProgressEntity()
    }

    suspend fun saveRun(run: RunRecordEntity, goldEarned: Int, crystalsEarned: Int, inkStonesGained: Int = 0) {
        runDao.insertRun(run)
        val current = getProgressOnce()
        val updated = current.copy(
            gold = current.gold + goldEarned,
            crystals = current.crystals + crystalsEarned,
            inkStones = current.inkStones + inkStonesGained,
            totalRuns = current.totalRuns + 1,
            totalKills = current.totalKills + run.kills,
            maxSurvivalSeconds = maxOf(current.maxSurvivalSeconds, run.survivalTimeSeconds)
        )
        playerProgressDao.saveProgress(updated)
    }

    suspend fun upgradeMetaStat(statName: String, costGold: Int): Boolean {
        val current = getProgressOnce()
        if (current.gold < costGold) return false

        val updated = when (statName) {
            "atk" -> current.copy(gold = current.gold - costGold, metaAtkLevel = current.metaAtkLevel + 1)
            "speed" -> current.copy(gold = current.gold - costGold, metaSpeedLevel = current.metaSpeedLevel + 1)
            "magnet" -> current.copy(gold = current.gold - costGold, metaMagnetLevel = current.metaMagnetLevel + 1)
            else -> current
        }
        playerProgressDao.saveProgress(updated)
        return true
    }

    suspend fun unlockCharacterWithCrystals(characterId: String, costCrystals: Int): Boolean {
        val current = getProgressOnce()
        if (current.crystals < costCrystals) return false
        val characters = current.unlockedCharacters.split(",").map { it.trim() }.toMutableSet()
        if (characters.contains(characterId)) return false
        characters.add(characterId)
        val updated = current.copy(
            crystals = current.crystals - costCrystals,
            unlockedCharacters = characters.joinToString(",")
        )
        playerProgressDao.saveProgress(updated)
        return true
    }

    suspend fun upgradeTalent(talentName: String, cost: Int): Boolean {
        val current = getProgressOnce()
        if (current.inkStones < cost) return false

        val updated = when (talentName) {
            "hp" -> current.copy(inkStones = current.inkStones - cost, hpLevel = current.hpLevel + 1)
            "speed" -> current.copy(inkStones = current.inkStones - cost, speedLevel = current.speedLevel + 1)
            "cooldown" -> current.copy(inkStones = current.inkStones - cost, cooldownLevel = current.cooldownLevel + 1)
            "area" -> current.copy(inkStones = current.inkStones - cost, areaLevel = current.areaLevel + 1)
            "viscosity" -> current.copy(inkStones = current.inkStones - cost, viscosityLevel = current.viscosityLevel + 1)
            "resonance" -> current.copy(inkStones = current.inkStones - cost, resonanceLevel = current.resonanceLevel + 1)
            else -> current
        }
        playerProgressDao.saveProgress(updated)
        return true
    }

    suspend fun unlockTool(toolName: String, cost: Int): Boolean {
        val current = getProgressOnce()
        if (current.inkStones < cost) return false
        val tools = current.unlockedTools.split(",").toMutableSet()
        if (tools.contains(toolName)) return false
        tools.add(toolName)
        val updated = current.copy(
            inkStones = current.inkStones - cost,
            unlockedTools = tools.joinToString(",")
        )
        playerProgressDao.saveProgress(updated)
        return true
    }

    suspend fun clearHistory() {
        runDao.clearHistory()
    }
}
