package com.example.mywatchtrace.data

import com.example.mywatchtrace.data.dao.WatchDao
import com.example.mywatchtrace.data.dao.WatchWearCount
import com.example.mywatchtrace.data.entity.WatchEntity
import com.example.mywatchtrace.data.entity.WearLogEntity
import com.example.mywatchtrace.data.entity.WearLogWithWatch
import kotlinx.coroutines.flow.Flow

class WatchRepository(private val watchDao: WatchDao) {

    val allWatches: Flow<List<WatchEntity>> = watchDao.getAllWatches()
    val topWornWatches: Flow<List<WatchWearCount>> = watchDao.getTopWornWatches()

    suspend fun addWatch(watch: WatchEntity): Long {
        return watchDao.insertWatch(watch)
    }

    suspend fun updateWatch(watch: WatchEntity) {
        watchDao.updateWatch(watch)
    }

    suspend fun deleteWatch(watch: WatchEntity) {
        watchDao.deleteWatch(watch)
    }

    suspend fun addWearLog(watchId: Long, date: String) {
        watchDao.insertWearLog(WearLogEntity(watchId = watchId, date = date))
    }

    suspend fun removeWearLog(watchId: Long, date: String) {
        watchDao.deleteWearLog(watchId, date)
    }

    fun getWearLogsForDate(date: String): Flow<List<WearLogWithWatch>> {
        return watchDao.getWearLogsForDate(date)
    }

    fun getWearLogsForMonth(yearMonth: String): Flow<List<WearLogWithWatch>> {
        return watchDao.getWearLogsForMonth(yearMonth)
    }

    fun getWearCountForWatch(watchId: Long): Flow<Int> {
        return watchDao.getWearCountForWatch(watchId)
    }
}
