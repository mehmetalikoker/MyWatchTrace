package com.example.mywatchtrace.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mywatchtrace.data.entity.AccuracyLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccuracyDao {
    @Query("SELECT * FROM accuracy_logs WHERE watchId = :watchId ORDER BY date DESC")
    fun getAccuracyLogsForWatch(watchId: Long): Flow<List<AccuracyLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccuracyLog(log: AccuracyLogEntity): Long

    @Delete
    suspend fun deleteAccuracyLog(log: AccuracyLogEntity)
}
