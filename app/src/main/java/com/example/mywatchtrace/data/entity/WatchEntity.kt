package com.example.mywatchtrace.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watches")
data class WatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val brand: String,
    val model: String,
    val colorHex: String,
    val category: String = "Diver",          // Diver, Dress, Spor, Pilot vb.
    val productionYear: String = "",         // Yapım Yılı (Örn: 2023)
    val movementType: String = "Otomatik",   // Otomatik, Quartz, Kurmalı
    val movementModel: String = ""           // Mekanizma Modeli (Örn: NH35, ETA 2824)
)
