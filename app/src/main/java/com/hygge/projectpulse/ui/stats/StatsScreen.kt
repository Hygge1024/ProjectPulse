package com.hygge.projectpulse.ui.stats

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hygge.projectpulse.R
import com.hygge.projectpulse.ui.components.GlassCard
import java.io.File
import java.util.Calendar
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: StatsViewModel = hiltViewModel()) {
    val stats by viewModel.stats.collectAsState()
    val period by viewModel.period.collectAsState()
    val range by viewModel.range.collectAsState()
    val exportMessage by viewModel.exportMessage.collectAsState()

    val context = LocalContext.current
    val exportDir = context.getExternalFilesDir(null) ?: context.filesDir

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_stats), fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                PeriodSelector(period) { viewModel.setPeriod(it) }
            }

            item {
                StatsCards(stats)
            }

            item {
                TypeDistributionCard(stats.byType)
            }

            item {
                ExportCard(
                    start = range.first,
                    end = range.second,
                    onExport = { start, end ->
                        viewModel.exportRange(start, end, exportDir)
                    }
                )
            }

            exportMessage?.let { msg ->
                item {
                    Text(
                        text = msg,
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                }
                viewModel.clearExportMessage()
            }
        }
    }
}

@Composable
private fun PeriodSelector(period: StatsPeriod, onPeriodChange: (StatsPeriod) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        StatsPeriod.entries.forEach { p ->
            val selected = p == period
            Button(
                onClick = { onPeriodChange(p) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(
                    text = if (p == StatsPeriod.WEEK) stringResource(R.string.stats_weekly) else stringResource(R.string.stats_monthly)
                )
            }
            if (p != StatsPeriod.entries.last()) Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
private fun StatsCards(stats: StatsData) {
    Row(modifier = Modifier.fillMaxWidth()) {
        StatCard(
            value = stats.totalCount.toString(),
            label = "Times",
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        StatCard(
            value = formatMinutes(stats.totalDuration),
            label = "Minutes",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    GlassCard(
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TypeDistributionCard(byType: Map<String, Int>) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Text(
                text = "Type Distribution",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (byType.isEmpty()) {
                Text("No data", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                byType.entries.forEach { (type, count) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(type, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            count.toString(),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExportCard(start: Long, end: Long, onExport: (Long, Long) -> Unit) {
    var selectedStart by remember { mutableStateOf(start) }
    var selectedEnd by remember { mutableStateOf(end) }

    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Text(
                text = stringResource(R.string.stats_export),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                DatePickerButton(
                    label = stringResource(R.string.export_start_date),
                    timestamp = selectedStart,
                    onDateSelected = { selectedStart = it }
                )
                Spacer(modifier = Modifier.width(12.dp))
                DatePickerButton(
                    label = stringResource(R.string.export_end_date),
                    timestamp = selectedEnd,
                    onDateSelected = { selectedEnd = it }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { onExport(selectedStart, selectedEnd) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(stringResource(R.string.stats_export))
            }
        }
    }
}

@Composable
private fun DatePickerButton(label: String, timestamp: Long, onDateSelected: (Long) -> Unit) {
    val context = LocalContext.current
    val calendar = remember(timestamp) { Calendar.getInstance().apply { timeInMillis = timestamp } }
    val dateText = remember(timestamp) {
        "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
    }

    TextButton(
        onClick = {
            DatePickerDialog(
                context,
                { _: DatePicker, year: Int, month: Int, day: Int ->
                    val cal = Calendar.getInstance().apply {
                        set(year, month, day, 0, 0, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    onDateSelected(cal.timeInMillis)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    ) {
        Text("$label: $dateText")
    }
}

private fun formatMinutes(ms: Long): String {
    return TimeUnit.MILLISECONDS.toMinutes(ms).toString()
}
