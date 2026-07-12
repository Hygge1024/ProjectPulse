package com.hygge.projectpulse.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.hygge.projectpulse.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert
    suspend fun insert(workout: WorkoutEntity): Long

    @Update
    suspend fun update(workout: WorkoutEntity)

    @Delete
    suspend fun delete(workout: WorkoutEntity)

    @Query("DELETE FROM workouts")
    suspend fun deleteAll()

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getById(id: Long): WorkoutEntity?

    @Query("SELECT * FROM workouts ORDER BY startTime DESC")
    fun getAll(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE startTime >= :from AND startTime <= :to ORDER BY startTime DESC")
    fun getBetween(from: Long, to: Long): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE endTime IS NULL ORDER BY startTime DESC LIMIT 1")
    suspend fun getActive(): WorkoutEntity?

    @Query("SELECT COUNT(*) FROM workouts WHERE startTime >= :from AND startTime <= :to")
    suspend fun countBetween(from: Long, to: Long): Int

    @Query("SELECT COALESCE(SUM(endTime - startTime), 0) FROM workouts WHERE startTime >= :from AND startTime <= :to AND endTime IS NOT NULL")
    suspend fun totalDurationBetween(from: Long, to: Long): Long
}
