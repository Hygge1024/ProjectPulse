package com.hygge.projectpulse.data.repository

import com.hygge.projectpulse.data.local.dao.ExerciseDao
import com.hygge.projectpulse.data.local.entity.ExerciseEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class ExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao
) {
    fun getAllExercises(): Flow<List<ExerciseEntity>> = exerciseDao.getAll()

    fun getExercisesByCategory(category: String): Flow<List<ExerciseEntity>> =
        exerciseDao.getByCategory(category)

    fun searchExercises(query: String): Flow<List<ExerciseEntity>> =
        exerciseDao.search(query.trim())

    suspend fun getExerciseById(id: Long): ExerciseEntity? = exerciseDao.getById(id)

    suspend fun insertAll(exercises: List<ExerciseEntity>) = exerciseDao.insertAll(exercises)

    suspend fun updateExercise(exercise: ExerciseEntity) = exerciseDao.update(exercise)

    suspend fun updateNote(id: Long, note: String) = exerciseDao.updateNote(id, note)

    suspend fun getCategories(): List<String> = exerciseDao.getCategories()

    suspend fun count(): Int = exerciseDao.count()
}
