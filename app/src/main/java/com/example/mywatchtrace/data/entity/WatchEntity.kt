package com.example.mywatchtrace.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watches")
data class WatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val brand: String,
    val model: String,
    val colorHex: String
)
