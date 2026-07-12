package com.hygge.projectpulse.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workouts",
    indices = [Index(value = ["startTime"])]
)
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long? = null,
    val type: String,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
