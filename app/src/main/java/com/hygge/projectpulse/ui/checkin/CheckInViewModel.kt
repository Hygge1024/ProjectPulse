package com.hygge.projectpulse.ui.checkin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hygge.projectpulse.R
import com.hygge.projectpulse.data.local.entity.WorkoutEntity
import com.hygge.projectpulse.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    @ApplicationContext context: Context
) : ViewModel() {

    val typeOptions: List<String> =
        context.resources.getStringArray(R.array.workout_types).toList()

    private val _activeWorkout = MutableStateFlow<WorkoutEntity?>(null)
    val activeWorkout: StateFlow<WorkoutEntity?> = _activeWorkout

    private val _selectedType = MutableStateFlow(typeOptions.firstOrNull() ?: "")
    val selectedType: StateFlow<String> = _selectedType

    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note

    private val _currentTime = MutableStateFlow(System.currentTimeMillis())

    val workoutHistory = workoutRepository.getAllWorkouts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val elapsedTime = combine(_activeWorkout, _currentTime) { active, now ->
        if (active != null) now - active.startTime else 0L
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    init {
        viewModelScope.launch {
            _activeWorkout.value = workoutRepository.getActiveWorkout()
        }
        startTicker()
    }

    private fun startTicker() {
        viewModelScope.launch {
            while (true) {
                _currentTime.value = System.currentTimeMillis()
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    fun setType(type: String) {
        _selectedType.value = type
    }

    fun setNote(note: String) {
        _note.value = note
    }

    fun startWorkout() {
        viewModelScope.launch {
            val id = workoutRepository.startWorkout(_selectedType.value, _note.value)
            _activeWorkout.value = workoutRepository.getById(id)
            _note.value = ""
        }
    }

    fun stopWorkout() {
        viewModelScope.launch {
            _activeWorkout.value?.let { active ->
                workoutRepository.stopWorkout(active)
                _activeWorkout.value = null
            }
        }
    }

    fun deleteWorkout(workout: WorkoutEntity) {
        viewModelScope.launch {
            workoutRepository.deleteWorkout(workout)
        }
    }

    fun deleteAllWorkouts() {
        viewModelScope.launch {
            workoutRepository.deleteAll()
        }
    }
}
