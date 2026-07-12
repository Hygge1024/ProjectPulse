package com.hygge.projectpulse.ui.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hygge.projectpulse.data.local.entity.ExerciseEntity
import com.hygge.projectpulse.data.preferences.UserPreferences
import com.hygge.projectpulse.data.repository.ExerciseImporter
import com.hygge.projectpulse.data.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ExercisesViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val exerciseImporter: ExerciseImporter,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    private val _selectedExerciseId = MutableStateFlow<Long?>(null)
    val selectedExerciseId: StateFlow<Long?> = _selectedExerciseId

    val categories = flow {
        emit(exerciseRepository.getCategories())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val exercises: StateFlow<List<ExerciseEntity>> = _searchQuery.flatMapLatest { query ->
        _selectedCategory.flatMapLatest { category ->
            when {
                query.isNotBlank() -> exerciseRepository.searchExercises(query)
                category != null -> exerciseRepository.getExercisesByCategory(category)
                else -> exerciseRepository.getAllExercises()
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedExercise: StateFlow<ExerciseEntity?> = combine(exercises, _selectedExerciseId) { list, id ->
        id?.let { list.find { it.id == id } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val importProgress = MutableStateFlow("")

    init {
        viewModelScope.launch {
            val count = exerciseRepository.count()
            val missingMedia = exerciseRepository.countWithEmptyImagePath()
            val missingNames = exerciseRepository.countWithEmptyNameZh()
            if (count == 0 || missingMedia > 0 || missingNames > 0 || !userPreferences.exercisesImported.first()) {
                importExercises()
            }
        }
    }

    private suspend fun importExercises() {
        importProgress.value = "Importing..."
        try {
            val existing = exerciseRepository.getAllExercises().first().associateBy { it.externalId }
            val imported = exerciseImporter.import()
            val exercises = imported.map { item ->
                existing[item.externalId]?.let {
                    item.copy(
                        id = it.id,
                        userNote = it.userNote,
                        isFavorite = it.isFavorite
                    )
                } ?: item
            }
            exerciseRepository.insertAll(exercises)
            userPreferences.setExercisesImported(true)
            importProgress.value = "Imported ${exercises.size} exercises"
        } catch (e: Exception) {
            importProgress.value = "Import failed: ${e.message}"
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun selectExercise(id: Long?) {
        _selectedExerciseId.value = id
    }

    fun updateNote(id: Long, note: String) {
        viewModelScope.launch {
            exerciseRepository.updateNote(id, note)
        }
    }
}
