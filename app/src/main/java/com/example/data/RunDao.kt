package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {
    @Query("SELECT * FROM run_records ORDER BY timestamp DESC")
    fun getAllRuns(): Flow<List<RunRecordEntity>>

    @Query("SELECT * FROM run_records ORDER BY score DESC LIMIT 10")
    fun getTopScores(): Flow<List<RunRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: RunRecordEntity): Long

    @Query("DELETE FROM run_records")
    suspend fun clearHistory()
}
