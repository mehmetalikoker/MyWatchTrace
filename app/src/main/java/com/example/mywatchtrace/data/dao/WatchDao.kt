package com.example.mywatchtrace.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.mywatchtrace.data.entity.WatchEntity
import com.example.mywatchtrace.data.entity.WearLogEntity
import com.example.mywatchtrace.data.entity.WearLogWithWatch
import kotlinx.coroutines.flow.Flow

data class WatchWearCount(
    val watchId: Long,
    val brand: String,
    val model: String,
    val colorHex: String,
    val photoUri: String,
    val wearCount: Int
)

@Dao
interface WatchDao {

    @Query("SELECT * FROM watches ORDER BY brand ASC, model ASC")
    fun getAllWatches(): Flow<List<WatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatch(watch: WatchEntity): Long

    @Update
    suspend fun updateWatch(watch: WatchEntity)

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

    @Query("""
        SELECT w.id as watchId, w.brand as brand, w.model as model, w.colorHex as colorHex, w.photoUri as photoUri, COUNT(l.id) as wearCount
        FROM watches w
        LEFT JOIN wear_logs l ON w.id = l.watchId
        GROUP BY w.id
        ORDER BY wearCount DESC
        LIMIT 10
    """)
    fun getTopWornWatches(): Flow<List<WatchWearCount>>

    @Query("""
        SELECT w.id as watchId, w.brand as brand, w.model as model, w.colorHex as colorHex, w.photoUri as photoUri, COUNT(l.id) as wearCount
        FROM watches w
        JOIN wear_logs l ON w.id = l.watchId
        WHERE l.date LIKE :yearMonth || '%'
        GROUP BY w.id
        ORDER BY wearCount DESC
        LIMIT 5
    """)
    fun getTopWornWatchesForMonth(yearMonth: String): Flow<List<WatchWearCount>>
}
