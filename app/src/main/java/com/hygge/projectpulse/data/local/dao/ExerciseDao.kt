package com.hygge.projectpulse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hygge.projectpulse.data.local.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: ExerciseEntity)

    @Update
    suspend fun update(exercise: ExerciseEntity)

    @Query("SELECT * FROM exercises ORDER BY nameEn ASC")
    fun getAll(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE category = :category ORDER BY nameEn ASC")
    fun getByCategory(category: String): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getById(id: Long): ExerciseEntity?

    @Query("SELECT * FROM exercises WHERE nameEn LIKE '%' || :query || '%' OR nameZh LIKE '%' || :query || '%' ORDER BY nameEn ASC")
    fun search(query: String): Flow<List<ExerciseEntity>>

    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM exercises WHERE imagePath = '' OR gifPath = ''")
    suspend fun countWithEmptyImagePath(): Int

    @Query("SELECT DISTINCT category FROM exercises ORDER BY category ASC")
    suspend fun getCategories(): List<String>

    @Query("UPDATE exercises SET userNote = :note WHERE id = :id")
    suspend fun updateNote(id: Long, note: String)
}
