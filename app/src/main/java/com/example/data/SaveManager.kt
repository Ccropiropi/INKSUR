package com.example.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Phase 4 & 5 Post-Run Economy, Meta-Progression & Mid-Run Bookmark Architecture: SaveManager
 *
 * Persistently serializes:
 * - Persistent Meta currencies (Gold, Crystals, InkStones) and permanent upgrades
 * - Temporary Bookmark Run State (`bookmark_run.json`) for the Minute 30:00 Inkwell Checkpoint.
 *   Loading this bookmark deletes it to prevent save-scumming.
 * - Map Tier unlocks (Tier 1: Bamboo Scratchpad, Tier 2: The Forbidden Archive)
 * - 2x Multiplier payout on Minute 61:00 Eraser Victory!
 */
data class SaveData(
    val gold: Int = 0,
    val crystals: Int = 0,
    val inkStones: Int = 0,
    val metaAtkLevel: Int = 0,
    val metaSpeedLevel: Int = 0,
    val metaMagnetLevel: Int = 0,
    val unlockedCharacters: String = "calligrapher",
    val unlockedMapTier: Int = 1,
    val totalRuns: Int = 0,
    val totalKills: Int = 0,
    val maxSurvivalSeconds: Int = 0,
    val fixedAnalog: Boolean = true,
    val screenShakeEnabled: Boolean = true,
    val hapticFeedbackEnabled: Boolean = true,
    val impactFrameEnabled: Boolean = true,
    val unlockedCodexEnemies: String = "BASIC_CONSTRUCT"
)

data class BookmarkRunState(
    val characterId: String,
    val classId: String,
    val timeSurvivedSeconds: Float,
    val score: Int,
    val kills: Int,
    val damageDealt: Long,
    val playerHp: Float,
    val playerMaxHp: Float,
    val playerLevel: Int,
    val playerXp: Int,
    val playerXpNeeded: Int,
    val activeSpellsData: String,
    val equippedGearData: String,
    val equippedArtifactsData: String,
    val completedSynthesesData: String,
    val slot1Unlocked: Boolean,
    val slot2Unlocked: Boolean,
    val slot3Unlocked: Boolean,
    val heavyWeightCount: Int,
    val isHeavyWeightMastered: Boolean,
    val corruptedCount: Int,
    val isCorruptedMastered: Boolean,
    val geometryCount: Int,
    val isGeometryMastered: Boolean
)

class SaveManager(private val context: Context) {

    private val saveFileName = "ink_survivor_save.json"
    private val bookmarkFileName = "bookmark_run.json"

    private val saveFile: File
        get() = File(context.filesDir, saveFileName)

    private val bookmarkFile: File
        get() = File(context.filesDir, bookmarkFileName)

    /**
     * Reads persistent save data from local JSON file.
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
                unlockedMapTier = json.optInt("unlockedMapTier", 1),
                totalRuns = json.optInt("totalRuns", 0),
                totalKills = json.optInt("totalKills", 0),
                maxSurvivalSeconds = json.optInt("maxSurvivalSeconds", 0),
                fixedAnalog = json.optBoolean("fixedAnalog", true),
                screenShakeEnabled = json.optBoolean("screenShakeEnabled", true),
                hapticFeedbackEnabled = json.optBoolean("hapticFeedbackEnabled", true),
                impactFrameEnabled = json.optBoolean("impactFrameEnabled", true),
                unlockedCodexEnemies = json.optString("unlockedCodexEnemies", "BASIC_CONSTRUCT")
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
                put("unlockedMapTier", data.unlockedMapTier)
                put("totalRuns", data.totalRuns)
                put("totalKills", data.totalKills)
                put("maxSurvivalSeconds", data.maxSurvivalSeconds)
                put("fixedAnalog", data.fixedAnalog)
                put("screenShakeEnabled", data.screenShakeEnabled)
                put("hapticFeedbackEnabled", data.hapticFeedbackEnabled)
                put("impactFrameEnabled", data.impactFrameEnabled)
                put("unlockedCodexEnemies", data.unlockedCodexEnemies)
            }
            saveFile.writeText(json.toString(2), Charsets.UTF_8)
        } catch (e: Exception) {
            // Graceful fallback
        }
    }

    @Synchronized
    fun unlockCodexEnemy(enemyTypeName: String) {
        val current = loadSaveData()
        val set = current.unlockedCodexEnemies.split(",").filter { it.isNotBlank() }.toMutableSet()
        if (set.add(enemyTypeName)) {
            writeSaveData(current.copy(unlockedCodexEnemies = set.joinToString(",")))
        }
    }

    @Synchronized
    fun setFixedAnalog(fixed: Boolean) {
        val current = loadSaveData()
        writeSaveData(current.copy(fixedAnalog = fixed))
    }

    @Synchronized
    fun setScreenShake(enabled: Boolean) {
        val current = loadSaveData()
        writeSaveData(current.copy(screenShakeEnabled = enabled))
    }

    @Synchronized
    fun setHapticFeedback(enabled: Boolean) {
        val current = loadSaveData()
        writeSaveData(current.copy(hapticFeedbackEnabled = enabled))
    }

    @Synchronized
    fun setImpactFrame(enabled: Boolean) {
        val current = loadSaveData()
        writeSaveData(current.copy(impactFrameEnabled = enabled))
    }

    /**
     * Phase 4 & 5 Post-Run Calculation:
     * - Gold Earned = 1 Kill = 1 Gold (2x on Minute 61 Victory)
     * - Crystals Earned = 1 Minute = 10 Crystals (2x on Minute 61 Victory)
     * - Unlocks Map Tier 2 upon Minute 61 Eraser Victory
     */
    @Synchronized
    fun recordRunPayout(
        kills: Int,
        survivalSeconds: Int,
        isMinute61Victory: Boolean = false
    ): Pair<Int, Int> {
        var goldEarned = kills
        val minutesSurvived = survivalSeconds / 60
        var crystalsEarned = minutesSurvived * 10

        if (isMinute61Victory) {
            goldEarned *= 2
            crystalsEarned *= 2
        }

        val current = loadSaveData()
        val nextTier = if (isMinute61Victory) maxOf(current.unlockedMapTier, 2) else current.unlockedMapTier
        val updated = current.copy(
            gold = current.gold + goldEarned,
            crystals = current.crystals + crystalsEarned,
            unlockedMapTier = nextTier,
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

    // ==========================================
    // PHASE 5 MID-RUN CHECKPOINT (THE INKWELL BOOKMARK)
    // ==========================================

    @Synchronized
    fun hasBookmark(): Boolean {
        return bookmarkFile.exists() && bookmarkFile.length() > 0
    }

    @Synchronized
    fun saveBookmark(state: BookmarkRunState): Boolean {
        return try {
            val json = JSONObject().apply {
                put("characterId", state.characterId)
                put("classId", state.classId)
                put("timeSurvivedSeconds", state.timeSurvivedSeconds.toDouble())
                put("score", state.score)
                put("kills", state.kills)
                put("damageDealt", state.damageDealt)
                put("playerHp", state.playerHp.toDouble())
                put("playerMaxHp", state.playerMaxHp.toDouble())
                put("playerLevel", state.playerLevel)
                put("playerXp", state.playerXp)
                put("playerXpNeeded", state.playerXpNeeded)
                put("activeSpellsData", state.activeSpellsData)
                put("equippedGearData", state.equippedGearData)
                put("equippedArtifactsData", state.equippedArtifactsData)
                put("completedSynthesesData", state.completedSynthesesData)
                put("slot1Unlocked", state.slot1Unlocked)
                put("slot2Unlocked", state.slot2Unlocked)
                put("slot3Unlocked", state.slot3Unlocked)
                put("heavyWeightCount", state.heavyWeightCount)
                put("isHeavyWeightMastered", state.isHeavyWeightMastered)
                put("corruptedCount", state.corruptedCount)
                put("isCorruptedMastered", state.isCorruptedMastered)
                put("geometryCount", state.geometryCount)
                put("isGeometryMastered", state.isGeometryMastered)
            }
            bookmarkFile.writeText(json.toString(2), Charsets.UTF_8)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Loads the bookmarked run and immediately deletes the file to prevent save-scumming.
     */
    @Synchronized
    fun loadAndConsumeBookmark(): BookmarkRunState? {
        if (!hasBookmark()) return null
        return try {
            val content = bookmarkFile.readText(Charsets.UTF_8)
            val json = JSONObject(content)
            val state = BookmarkRunState(
                characterId = json.optString("characterId", "calligrapher"),
                classId = json.optString("classId", "scribe"),
                timeSurvivedSeconds = json.optDouble("timeSurvivedSeconds", 1800.0).toFloat(),
                score = json.optInt("score", 0),
                kills = json.optInt("kills", 0),
                damageDealt = json.optLong("damageDealt", 0L),
                playerHp = json.optDouble("playerHp", 100.0).toFloat(),
                playerMaxHp = json.optDouble("playerMaxHp", 100.0).toFloat(),
                playerLevel = json.optInt("playerLevel", 1),
                playerXp = json.optInt("playerXp", 0),
                playerXpNeeded = json.optInt("playerXpNeeded", 10),
                activeSpellsData = json.optString("activeSpellsData", ""),
                equippedGearData = json.optString("equippedGearData", ""),
                equippedArtifactsData = json.optString("equippedArtifactsData", ""),
                completedSynthesesData = json.optString("completedSynthesesData", ""),
                slot1Unlocked = json.optBoolean("slot1Unlocked", false),
                slot2Unlocked = json.optBoolean("slot2Unlocked", false),
                slot3Unlocked = json.optBoolean("slot3Unlocked", false),
                heavyWeightCount = json.optInt("heavyWeightCount", 0),
                isHeavyWeightMastered = json.optBoolean("isHeavyWeightMastered", false),
                corruptedCount = json.optInt("corruptedCount", 0),
                isCorruptedMastered = json.optBoolean("isCorruptedMastered", false),
                geometryCount = json.optInt("geometryCount", 0),
                isGeometryMastered = json.optBoolean("isGeometryMastered", false)
            )
            // Delete file upon consumption (anti-save-scumming)
            bookmarkFile.delete()
            state
        } catch (e: Exception) {
            bookmarkFile.delete()
            null
        }
    }

    @Synchronized
    fun deleteBookmark() {
        if (bookmarkFile.exists()) {
            bookmarkFile.delete()
        }
    }
}
