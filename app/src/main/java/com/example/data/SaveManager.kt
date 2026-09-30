package com.example.data

import android.content.Context
import org.json.JSONObject
import java.io.File

/**
 * Phase 4 Post-Run Economy & Meta-Progression Architecture: SaveManager
 *
 * Persistently serializes the in-run currencies and meta-progression upgrades
 * to a local save file (`ink_survivor_save.json`).
 *
 * Post-run Payout Formulas:
 * - Gold Earned = Total Enemies Killed (1 Kill = 1 Gold)
 * - Crystals Earned = Total Minutes Survived (1 Minute = 10 Crystals)
 *
 * Main Menu Shop Hooks:
 * - Spend Gold on permanent stat increases (ATK, Speed, Magnet Range)
 * - Spend Crystals on unlocking new Characters
 */
data class SaveData(
    val gold: Int = 0,
    val crystals: Int = 0,
    val inkStones: Int = 0,
    val metaAtkLevel: Int = 0,
    val metaSpeedLevel: Int = 0,
    val metaMagnetLevel: Int = 0,
    val unlockedCharacters: String = "calligrapher",
    val totalRuns: Int = 0,
    val totalKills: Int = 0,
    val maxSurvivalSeconds: Int = 0
)

class SaveManager(private val context: Context) {

    private val saveFileName = "ink_survivor_save.json"

    private val saveFile: File
        get() = File(context.filesDir, saveFileName)

    /**
     * Reads persistent save data from local JSON file.
     * Falls back to defaults if file does not exist or parse error.
     */
    @Synchronized
    fun loadSaveData(): SaveData {
        return try {
            if (!saveFile.exists()) {
                val initial = SaveData()
                writeSaveData(initial)
                return initial
            }
            val content = saveFile.readText(Charsets.UTF_8)
            val json = JSONObject(content)
            SaveData(
                gold = json.optInt("gold", 0),
                crystals = json.optInt("crystals", 0),
                inkStones = json.optInt("inkStones", 0),
                metaAtkLevel = json.optInt("metaAtkLevel", 0),
                metaSpeedLevel = json.optInt("metaSpeedLevel", 0),
                metaMagnetLevel = json.optInt("metaMagnetLevel", 0),
                unlockedCharacters = json.optString("unlockedCharacters", "calligrapher"),
                totalRuns = json.optInt("totalRuns", 0),
                totalKills = json.optInt("totalKills", 0),
                maxSurvivalSeconds = json.optInt("maxSurvivalSeconds", 0)
            )
        } catch (e: Exception) {
            SaveData()
        }
    }

    /**
     * Serializes save data into local file JSON format.
     */
    @Synchronized
    fun writeSaveData(data: SaveData) {
        try {
            val json = JSONObject().apply {
                put("gold", data.gold)
                put("crystals", data.crystals)
                put("inkStones", data.inkStones)
                put("metaAtkLevel", data.metaAtkLevel)
                put("metaSpeedLevel", data.metaSpeedLevel)
                put("metaMagnetLevel", data.metaMagnetLevel)
                put("unlockedCharacters", data.unlockedCharacters)
                put("totalRuns", data.totalRuns)
                put("totalKills", data.totalKills)
                put("maxSurvivalSeconds", data.maxSurvivalSeconds)
            }
            saveFile.writeText(json.toString(2), Charsets.UTF_8)
        } catch (e: Exception) {
            // Log or ignore gracefully
        }
    }

    /**
     * Phase 4 Post-Run Calculation:
     * - Gold Earned = 1 Kill = 1 Gold
     * - Crystals Earned = 1 Minute = 10 Crystals
     */
    @Synchronized
    fun recordRunPayout(
        kills: Int,
        survivalSeconds: Int
    ): Pair<Int, Int> {
        val goldEarned = kills
        val minutesSurvived = survivalSeconds / 60
        val crystalsEarned = minutesSurvived * 10

        val current = loadSaveData()
        val updated = current.copy(
            gold = current.gold + goldEarned,
            crystals = current.crystals + crystalsEarned,
            totalRuns = current.totalRuns + 1,
            totalKills = current.totalKills + kills,
            maxSurvivalSeconds = maxOf(current.maxSurvivalSeconds, survivalSeconds)
        )
        writeSaveData(updated)
        return Pair(goldEarned, crystalsEarned)
    }

    /**
     * Spend Gold on permanent stat increases (ATK, Speed, Magnet Range)
     */
    @Synchronized
    fun spendGoldOnMetaStat(statName: String, costGold: Int): Boolean {
        val current = loadSaveData()
        if (current.gold < costGold) return false

        val updated = when (statName.lowercase()) {
            "atk" -> current.copy(gold = current.gold - costGold, metaAtkLevel = current.metaAtkLevel + 1)
            "speed" -> current.copy(gold = current.gold - costGold, metaSpeedLevel = current.metaSpeedLevel + 1)
            "magnet" -> current.copy(gold = current.gold - costGold, metaMagnetLevel = current.metaMagnetLevel + 1)
            else -> return false
        }
        writeSaveData(updated)
        return true
    }

    /**
     * Spend Crystals on unlocking new Characters
     */
    @Synchronized
    fun spendCrystalsOnCharacter(characterId: String, costCrystals: Int): Boolean {
        val current = loadSaveData()
        if (current.crystals < costCrystals) return false

        val set = current.unlockedCharacters.split(",").map { it.trim() }.toMutableSet()
        if (set.contains(characterId)) return false
        set.add(characterId)

        val updated = current.copy(
            crystals = current.crystals - costCrystals,
            unlockedCharacters = set.joinToString(",")
        )
        writeSaveData(updated)
        return true
    }
}
