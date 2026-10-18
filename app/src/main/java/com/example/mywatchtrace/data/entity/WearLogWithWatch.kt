package com.example.mywatchtrace.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class WearLogWithWatch(
    @Embedded val wearLog: WearLogEntity,
    @Relation(
        parentColumn = "watchId",
        entityColumn = "id"
    )
    val watch: WatchEntity
)
