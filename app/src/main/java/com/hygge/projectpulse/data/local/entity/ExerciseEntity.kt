package com.hygge.projectpulse.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises",
    indices = [
        Index(value = ["category"]),
        Index(value = ["bodyPart"]),
        Index(value = ["target"]),
        Index(value = ["externalId"], unique = true)
    ]
)

data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val externalId: String = "",
    val nameEn: String,
    val nameZh: String? = null,
    val category: String,
    val bodyPart: String,
    val target: String,
    val muscleGroup: String,
    val equipment: String,
    val instructionsEn: String,
    val instructionsZh: String,
    val level: String,
    val mediaId: String = "",
    val imagePath: String = "",
    val gifPath: String = "",
    val isFavorite: Boolean = false,
    val userNote: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
