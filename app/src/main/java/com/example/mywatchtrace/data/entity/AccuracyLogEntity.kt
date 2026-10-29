package com.example.mywatchtrace.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accuracy_logs")
data class AccuracyLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val watchId: Long,
    val date: String,            // YYYY-MM-DD
    val deviationSeconds: Float, // Günlük sapma (Saniye/Gün, örn: +4.5 veya -2.0)
    val notes: String = ""       // Not (örn: "Kolda", "Kadranda yukarı")
)
