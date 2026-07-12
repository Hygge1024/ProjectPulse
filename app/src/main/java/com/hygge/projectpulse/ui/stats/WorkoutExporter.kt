package com.hygge.projectpulse.ui.stats

import com.hygge.projectpulse.data.local.entity.WorkoutEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.dhatim.fastexcel.Workbook
import org.dhatim.fastexcel.Worksheet

object WorkoutExporter {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun export(
        workouts: List<WorkoutEntity>,
        start: Long,
        end: Long,
        outputDir: File
    ): File {
        val file = File(outputDir, "pulsefit_workouts_${formatDateFile(start)}_${formatDateFile(end)}.xlsx")
        file.outputStream().use { output ->
            val wb = Workbook(output, "PulseFit", "1.0")
            val ws: Worksheet = wb.newWorksheet("Workouts")

            ws.value(0, 0, "No.")
            ws.value(0, 1, "Date")
            ws.value(0, 2, "Type")
            ws.value(0, 3, "Start")
            ws.value(0, 4, "End")
            ws.value(0, 5, "Duration (min)")
            ws.value(0, 6, "Note")

            workouts.sortedBy { it.startTime }.forEachIndexed { index, workout ->
                val row = index + 1
                val endTime = workout.endTime ?: workout.startTime
                val durationMin = ((endTime - workout.startTime) / 1000 / 60).toDouble()
                ws.value(row, 0, index + 1)
                ws.value(row, 1, formatDate(workout.startTime))
                ws.value(row, 2, workout.type)
                ws.value(row, 3, formatTime(workout.startTime))
                ws.value(row, 4, formatTime(endTime))
                ws.value(row, 5, durationMin)
                ws.value(row, 6, workout.note)
            }

            wb.finish()
        }
        return file
    }

    private fun formatDateFile(ts: Long): String {
        return SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(ts))
    }

    private fun formatDate(ts: Long): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(ts))
    }

    private fun formatTime(ts: Long): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(ts))
    }
}
