package com.example.mywatchtrace.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "wear_logs",
    foreignKeys = [
        ForeignKey(
            entity = WatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["watchId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("watchId"), Index("date")]
)
data class WearLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val watchId: Long,
    val date: String // ISO string YYYY-MM-DD
)
