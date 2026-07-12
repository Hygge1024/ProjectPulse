package com.hygge.projectpulse.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    private object Keys {
        val USER_ID = stringPreferencesKey("user_id")
        val LANGUAGE = stringPreferencesKey("language")
        val EXERCISES_IMPORTED = booleanPreferencesKey("exercises_imported")
    }

    val userId: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.USER_ID] ?: generateUserId()
    }

    val language: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.LANGUAGE] ?: "zh"
    }

    val exercisesImported: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.EXERCISES_IMPORTED] ?: false
    }

    suspend fun requireUserId(): String {
        val prefs = context.dataStore.data.first()
        return prefs[Keys.USER_ID] ?: generateUserId().also { setUserId(it) }
    }

    suspend fun setUserId(id: String) {
        context.dataStore.edit { it[Keys.USER_ID] = id }
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { it[Keys.LANGUAGE] = language }
    }

    suspend fun setExercisesImported(imported: Boolean) {
        context.dataStore.edit { it[Keys.EXERCISES_IMPORTED] = imported }
    }

    private suspend fun generateUserId(): String {
        val id = UUID.randomUUID().toString()
        setUserId(id)
        return id
    }
}
