package com.example.mywatchtrace.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mywatchtrace.data.dao.AccuracyDao
import com.example.mywatchtrace.data.dao.WatchDao
import com.example.mywatchtrace.data.entity.AccuracyLogEntity
import com.example.mywatchtrace.data.entity.WatchEntity
import com.example.mywatchtrace.data.entity.WearLogEntity

@Database(entities = [WatchEntity::class, WearLogEntity::class, AccuracyLogEntity::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun watchDao(): WatchDao
    abstract fun accuracyDao(): AccuracyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "watch_trace_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
