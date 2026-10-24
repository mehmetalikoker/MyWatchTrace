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
    val movementModel: String = "",          // Mekanizma Modeli (Örn: NH35, ETA 2824)
    val photoUri: String = "",               // Saat Fotoğrafı URI
    
    // Yeni Eklenen Alanlar
    val hasBox: Boolean = false,             // Kutu var mı? (Evet/Hayır)
    val hasPapers: Boolean = false,          // Belge var mı? (Evet/Hayır)
    val price: String = "",                  // Saat Fiyatı
    val currency: String = "TL",             // Para Birimi (TL, USD, EUR, CHF)
    val status: String = "Aktif",            // Durumu (Aktif, Satıldı)
    
    // Satıldı ise ek 4 alan
    val soldPrice: String = "",              // Kaça satıldı?
    val soldCurrency: String = "TL",         // Satış Para Birimi
    val soldDate: String = "",               // Hangi tarihte satıldı?
    val soldTo: String = ""                  // Kime satıldı?
)
