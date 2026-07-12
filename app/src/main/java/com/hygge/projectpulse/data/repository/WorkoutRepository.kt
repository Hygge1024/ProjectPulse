package com.hygge.projectpulse.data.repository

import com.hygge.projectpulse.data.local.dao.WorkoutDao
import com.hygge.projectpulse.data.local.entity.WorkoutEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class WorkoutRepository @Inject constructor(
    private val workoutDao: WorkoutDao
) {
    fun getAllWorkouts(): Flow<List<WorkoutEntity>> = workoutDao.getAll()

    fun getWorkoutsBetween(from: Long, to: Long): Flow<List<WorkoutEntity>> =
        workoutDao.getBetween(from, to)

    suspend fun getActiveWorkout(): WorkoutEntity? = workoutDao.getActive()

    suspend fun startWorkout(type: String, note: String = ""): Long {
        val active = getActiveWorkout()
        if (active != null) {
            updateWorkout(active.copy(endTime = System.currentTimeMillis()))
        }
        val workout = WorkoutEntity(
            startTime = System.currentTimeMillis(),
            type = type,
            note = note
        )
        return workoutDao.insert(workout)
    }

    suspend fun stopWorkout(workout: WorkoutEntity): WorkoutEntity {
        val updated = workout.copy(endTime = System.currentTimeMillis())
        workoutDao.update(updated)
        return updated
    }

    suspend fun updateWorkout(workout: WorkoutEntity) = workoutDao.update(workout)

    suspend fun deleteWorkout(workout: WorkoutEntity) = workoutDao.delete(workout)

    suspend fun deleteAll() = workoutDao.deleteAll()

    suspend fun getById(id: Long): WorkoutEntity? = workoutDao.getById(id)

    suspend fun countBetween(from: Long, to: Long): Int = workoutDao.countBetween(from, to)

    suspend fun totalDurationBetween(from: Long, to: Long): Long =
        workoutDao.totalDurationBetween(from, to)
}
