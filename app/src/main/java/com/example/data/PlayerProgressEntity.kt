package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_progress")
data class PlayerProgressEntity(
    @PrimaryKey
    val id: Int = 1,
    val inkStones: Int = 0,
    val gold: Int = 0,
    val crystals: Int = 0,
    val totalRuns: Int = 0,
    val totalKills: Int = 0,
    val maxSurvivalSeconds: Int = 0,
    val unlockedTools: String = "QUILL",
    val unlockedCharacters: String = "calligrapher",
    val metaAtkLevel: Int = 0,
    val metaSpeedLevel: Int = 0,
    val metaMagnetLevel: Int = 0,
    val hpLevel: Int = 0,
    val speedLevel: Int = 0,
    val cooldownLevel: Int = 0,
    val areaLevel: Int = 0,
    val viscosityLevel: Int = 0,
    val resonanceLevel: Int = 0
)
