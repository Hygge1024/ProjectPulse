package com.hygge.projectpulse.di

import android.content.Context
import androidx.room.Room
import com.hygge.projectpulse.data.local.database.PulseFitDatabase
import com.hygge.projectpulse.data.preferences.UserPreferences
import com.hygge.projectpulse.data.repository.ExerciseImporter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PulseFitDatabase {
        return Room.databaseBuilder(
            context,
            PulseFitDatabase::class.java,
            "pulsefit.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideWorkoutDao(db: PulseFitDatabase) = db.workoutDao()

    @Provides
    @Singleton
    fun provideExerciseDao(db: PulseFitDatabase) = db.exerciseDao()

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    @Singleton
    fun provideExerciseImporter(@ApplicationContext context: Context): ExerciseImporter {
        return ExerciseImporter(context)
    }
}
