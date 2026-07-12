package com.hygge.projectpulse.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hygge.projectpulse.R
import com.hygge.projectpulse.ui.components.GlassCard
import com.hygge.projectpulse.ui.components.PulseFitDatePickerField
import java.util.Calendar
import java.util.concurrent.TimeUnit

private val bottomBarHeight = 80.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: StatsViewModel = hiltViewModel()) {
    val stats by viewModel.stats.collectAsState()
    val period by viewModel.period.collectAsState()
    val range by viewModel.range.collectAsState()
    val exportMessage by viewModel.exportMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_stats), fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LaunchedEffect(exportMessage) {
            exportMessage?.let { viewModel.clearExportMessage() }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = bottomBarHeight
            ),
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
                        viewModel.exportRange(start, end)
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
            }
        }
    }
}

@Composable
private fun PeriodSelector(period: StatsPeriod, onPeriodChange: (StatsPeriod) -> Unit) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        blurRadius = 20.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            StatsPeriod.entries.forEach { p ->
                val selected = p == period
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            else Color.Transparent
                        )
                        .clickable { onPeriodChange(p) }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = if (p == StatsPeriod.WEEK) stringResource(R.string.stats_weekly) else stringResource(R.string.stats_monthly),
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsCards(stats: StatsData) {
    Row(modifier = Modifier.fillMaxWidth()) {
        StatCard(
            value = stats.totalCount.toString(),
            label = stringResource(R.string.times),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        StatCard(
            value = formatMinutes(stats.totalDuration),
            label = stringResource(R.string.duration),
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
                text = stringResource(R.string.type_distribution),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (byType.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_data),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
    var selectedStart by remember(start) { mutableStateOf(start) }
    var selectedEnd by remember(end) { mutableStateOf(end) }

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
                PulseFitDatePickerField(
                    label = stringResource(R.string.export_start_date),
                    timestamp = selectedStart,
                    onDateSelected = { selectedStart = stripTime(it) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                PulseFitDatePickerField(
                    label = stringResource(R.string.export_end_date),
                    timestamp = selectedEnd,
                    onDateSelected = { selectedEnd = stripTime(it) },
                    modifier = Modifier.weight(1f)
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

private fun formatMinutes(ms: Long): String {
    return TimeUnit.MILLISECONDS.toMinutes(ms).toString()
}

private fun stripTime(timestamp: Long): Long {
    val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
    return Calendar.getInstance().apply {
        set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        set(Calendar.MONTH, calendar.get(Calendar.MONTH))
        set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
