package com.example.mywatchtrace.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.mywatchtrace.data.entity.WatchEntity
import com.example.mywatchtrace.data.entity.WearLogEntity
import com.example.mywatchtrace.data.entity.WearLogWithWatch
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchDao {

    @Query("SELECT * FROM watches ORDER BY brand ASC, model ASC")
    fun getAllWatches(): Flow<List<WatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatch(watch: WatchEntity): Long

    @Delete
    suspend fun deleteWatch(watch: WatchEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWearLog(wearLog: WearLogEntity): Long

    @Query("DELETE FROM wear_logs WHERE watchId = :watchId AND date = :date")
    suspend fun deleteWearLog(watchId: Long, date: String)

    @Transaction
    @Query("SELECT * FROM wear_logs WHERE date = :date")
    fun getWearLogsForDate(date: String): Flow<List<WearLogWithWatch>>

    @Transaction
    @Query("SELECT * FROM wear_logs WHERE date LIKE :yearMonth || '%'")
    fun getWearLogsForMonth(yearMonth: String): Flow<List<WearLogWithWatch>>

    @Query("SELECT COUNT(*) FROM wear_logs WHERE watchId = :watchId")
    fun getWearCountForWatch(watchId: Long): Flow<Int>
}
