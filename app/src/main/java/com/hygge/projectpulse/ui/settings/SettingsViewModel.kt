package com.hygge.projectpulse.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hygge.projectpulse.data.preferences.UserPreferences
import com.hygge.projectpulse.data.repository.WorkoutRepository
import com.hygge.projectpulse.ui.stats.WorkoutExporter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val workoutRepository: WorkoutRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val userId = userPreferences.userId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val language = userPreferences.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "zh")

    val exportMessage = MutableStateFlow<String?>(null)

    fun exportAllWorkouts(): Intent? {
        return try {
            val start = 0L
            val end = System.currentTimeMillis()
            viewModelScope.launch {
                val list = workoutRepository.getWorkoutsBetween(start, end)
                    .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList()).value
                val file = WorkoutExporter.export(list, start, end, context.cacheDir)
                shareFile(file)
            }
            null
        } catch (e: Exception) {
            exportMessage.value = "Export failed: ${e.message}"
            null
        }
    }

    private fun shareFile(file: File) {
        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, "Share workouts")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
