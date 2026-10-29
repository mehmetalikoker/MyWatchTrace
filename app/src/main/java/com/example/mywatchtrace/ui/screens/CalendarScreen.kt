package com.example.mywatchtrace.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mywatchtrace.data.entity.WearLogWithWatch
import com.example.mywatchtrace.ui.WatchViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: WatchViewModel
) {
    val selectedYearMonth by viewModel.selectedYearMonth.collectAsState()
    val wearLogsForMonth by viewModel.wearLogsForSelectedMonth.collectAsState()

    var selectedDayForDialog by remember { mutableStateOf<LocalDate?>(null) }

    val englishLocale = Locale.US
    val monthYearTitle = remember(selectedYearMonth) {
        selectedYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", englishLocale))
    }

    val totalWears = wearLogsForMonth.size
    val uniqueWatches = wearLogsForMonth.map { it.wearLog.watchId }.toSet().size

    val calendarDays = remember(selectedYearMonth) {
        getCalendarDays(selectedYearMonth)
    }

    val logsByDate = remember(wearLogsForMonth) {
        wearLogsForMonth.groupBy { it.wearLog.date }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Takvim",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    TextButton(onClick = {
                        viewModel.setSelectedYearMonth(YearMonth.now())
                    }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Bugün", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        WatchWatermarkBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.setSelectedYearMonth(selectedYearMonth.minusMonths(1)) },
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    RoundedCornerShape(8.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                                contentDescription = "Önceki Ay",
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { viewModel.setSelectedYearMonth(selectedYearMonth.plusMonths(1)) },
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    RoundedCornerShape(8.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = "Sonraki Ay",
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = monthYearTitle,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "$totalWears kayıt · $uniqueWatches farklı saat",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Weekday headers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val weekDays = listOf("Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz")
                    weekDays.forEach { dayName ->
                        Text(
                            text = dayName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                // Calendar Grid (7 columns)
                val rows = calendarDays.chunked(7)
                rows.forEach { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        week.forEach { date ->
                            val isCurrentMonth = date.month == selectedYearMonth.month
                            val dateString = date.toString()
                            val dayLogs = logsByDate[dateString] ?: emptyList()

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(0.72f)
                                    .padding(2.dp)
                            ) {
                                CalendarDayCell(
                                    date = date,
                                    isCurrentMonth = isCurrentMonth,
                                    logs = dayLogs,
                                    onClick = {
                                        selectedDayForDialog = date
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    selectedDayForDialog?.let { date ->
        val dateString = date.toString()
        val wearLogsForDate = remember(wearLogsForMonth, dateString) {
            wearLogsForMonth.filter { it.wearLog.date == dateString }
        }

        val turkishLocale = remember { Locale.Builder().setLanguage("tr").setRegion("TR").build() }
        val formattedTitle = date.format(DateTimeFormatter.ofPattern("d MMMM yyyy, EEEE", turkishLocale))

        AlertDialog(
            onDismissRequest = { selectedDayForDialog = null },
            title = { Text(formattedTitle, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                if (wearLogsForDate.isEmpty()) {
                    Text(
                        text = "Bu güne ait saat takılma kaydı bulunmuyor.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Bu tarihte takılan saat(ler):",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        wearLogsForDate.forEach { log ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "• ${log.watch.brand} ${log.watch.model}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedDayForDialog = null }) {
                    Text("Kapat", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun CalendarDayCell(
    date: LocalDate,
    isCurrentMonth: Boolean,
    logs: List<WearLogWithWatch>,
    onClick: () -> Unit
) {
    val isToday = date == LocalDate.now()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (isToday) 2.dp else 1.dp,
                color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        color = if (logs.isNotEmpty() && isCurrentMonth)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
        else if (isCurrentMonth)
            MaterialTheme.colorScheme.surface
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp)
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                fontSize = 11.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                color = if (isCurrentMonth)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )

            Spacer(modifier = Modifier.height(2.dp))

            if (isCurrentMonth) {
                logs.take(2).forEach { log ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 0.5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                            .padding(horizontal = 2.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "${log.watch.brand} ${log.watch.model}",
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (logs.size > 2) {
                    Text(
                        text = "+${logs.size - 2}",
                        fontSize = 7.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }
        }
    }
}

fun getCalendarDays(yearMonth: YearMonth): List<LocalDate> {
    val firstOfMonth = yearMonth.atDay(1)
    val firstGridGridDay = firstOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    val lastOfMonth = yearMonth.atEndOfMonth()
    val lastGridDay = lastOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

    val days = mutableListOf<LocalDate>()
    var curr = firstGridGridDay
    while (!curr.isAfter(lastGridDay)) {
        days.add(curr)
        curr = curr.plusDays(1)
    }
    return days
}
