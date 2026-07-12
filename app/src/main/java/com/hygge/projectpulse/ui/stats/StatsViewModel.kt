package com.hygge.projectpulse.ui.stats

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hygge.projectpulse.data.local.entity.WorkoutEntity
import com.hygge.projectpulse.data.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StatsViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _period = MutableStateFlow(StatsPeriod.WEEK)
    val period: StateFlow<StatsPeriod> = _period

    val range = MutableStateFlow(Pair(startOfWeek(), System.currentTimeMillis()))

    val workouts: StateFlow<List<WorkoutEntity>> = range.flatMapLatest { r ->
        workoutRepository.getWorkoutsBetween(r.first, r.second)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stats: StateFlow<StatsData> = combine(workouts, _period) { list, _ ->
        calculateStats(list)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatsData())

    val exportMessage = MutableStateFlow<String?>(null)

    fun setPeriod(p: StatsPeriod) {
        _period.value = p
        range.value = when (p) {
            StatsPeriod.WEEK -> Pair(startOfWeek(), System.currentTimeMillis())
            StatsPeriod.MONTH -> Pair(startOfMonth(), System.currentTimeMillis())
        }
    }

    fun exportRange(start: Long, end: Long) {
        viewModelScope.launch {
            try {
                val list = workoutRepository.getWorkoutsBetween(start, end + 86_400_000 - 1).first()
                val file = WorkoutExporter.export(list, start, end, context.cacheDir)
                shareFile(file)
                exportMessage.value = "分享弹窗已打开"
            } catch (e: Exception) {
                exportMessage.value = "Export failed: ${e.message}"
            }
        }
    }

    private fun shareFile(file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, "Share workout data")
        chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    private fun calculateStats(list: List<WorkoutEntity>): StatsData {
        val total = list.count()
        val duration = list.sumOf { (it.endTime ?: it.startTime) - it.startTime }
        val byType = list.groupBy { it.type }.mapValues { it.value.count() }
        return StatsData(total, duration, byType)
    }

    private fun startOfWeek(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        }
        return cal.timeInMillis
    }

    private fun startOfMonth(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        return cal.timeInMillis
    }

    fun clearExportMessage() {
        exportMessage.value = null
    }
}

enum class StatsPeriod { WEEK, MONTH }

data class StatsData(
    val totalCount: Int = 0,
    val totalDuration: Long = 0,
    val byType: Map<String, Int> = emptyMap()
)
