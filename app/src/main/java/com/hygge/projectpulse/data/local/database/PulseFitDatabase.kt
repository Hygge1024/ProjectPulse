package com.hygge.projectpulse.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hygge.projectpulse.data.local.dao.ExerciseDao
import com.hygge.projectpulse.data.local.dao.WorkoutDao
import com.hygge.projectpulse.data.local.entity.ExerciseEntity
import com.hygge.projectpulse.data.local.entity.WorkoutEntity

@Database(
    entities = [WorkoutEntity::class, ExerciseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PulseFitDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
}
