package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "run_records")
data class RunRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val score: Int,
    val survivalTimeSeconds: Int,
    val kills: Int,
    val damageDealt: Long,
    val primaryColor: String,
    val toolName: String,
    val levelReached: Int,
    val isVictory: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
