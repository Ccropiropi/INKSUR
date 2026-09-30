package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerProgressDao {
    @Query("SELECT * FROM player_progress WHERE id = 1")
    fun getProgress(): Flow<PlayerProgressEntity?>

    @Query("SELECT * FROM player_progress WHERE id = 1")
    suspend fun getProgressOnce(): PlayerProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: PlayerProgressEntity)
}
