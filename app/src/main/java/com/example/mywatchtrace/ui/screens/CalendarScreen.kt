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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.mywatchtrace.data.entity.WatchEntity
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
    val allWatches by viewModel.allWatches.collectAsState()

    var selectedDayForBottomSheet by remember { mutableStateOf<LocalDate?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val englishLocale = Locale.US
    val monthYearTitle = remember(selectedYearMonth) {
        selectedYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", englishLocale))
    }

    // Stats
    val totalWears = wearLogsForMonth.size
    val uniqueWatches = wearLogsForMonth.map { it.wearLog.watchId }.toSet().size

    // Calendar Days calculation
    val calendarDays = remember(selectedYearMonth) {
        getCalendarDays(selectedYearMonth)
    }

    // Group logs by ISO date string
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Month Header & Navigation Control
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
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "$totalWears wears · $uniqueWatches watches",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Days of Week Header (MON TUE WED THU FRI SAT SUN)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val weekDays = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
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
                                    selectedDayForBottomSheet = date
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Bir güne tıklayarak ne taktığınızı ekleyin veya düzenleyin.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp)
            )
        }
    }

    selectedDayForBottomSheet?.let { date ->
        val dateString = date.toString()
        val wearLogsForDate = remember(wearLogsForMonth, dateString) {
            wearLogsForMonth.filter { it.wearLog.date == dateString }
        }

        ModalBottomSheet(
            onDismissRequest = { selectedDayForBottomSheet = null },
            sheetState = sheetState
        ) {
            DayDetailBottomSheetContent(
                date = date,
                allWatches = allWatches,
                wearLogsForDate = wearLogsForDate,
                viewModel = viewModel,
                onDismiss = { selectedDayForBottomSheet = null }
            )
        }
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

    // Background tint if watches worn
    val primaryLogColor = logs.firstOrNull()?.let { parseHexColor(it.watch.colorHex) }

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
        color = if (primaryLogColor != null && isCurrentMonth)
            primaryLogColor.copy(alpha = 0.12f)
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
                fontSize = 12.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                color = if (isCurrentMonth)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )

            Spacer(modifier = Modifier.height(2.dp))

            if (isCurrentMonth) {
                logs.take(2).forEach { log ->
                    val watchColor = parseHexColor(log.watch.colorHex)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 1.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(watchColor.copy(alpha = 0.2f))
                            .padding(horizontal = 2.dp, vertical = 1.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(watchColor)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${log.watch.brand} ${log.watch.model}",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (logs.size > 2) {
                    Text(
                        text = "+${logs.size - 2} daha",
                        fontSize = 7.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DayDetailBottomSheetContent(
    date: LocalDate,
    allWatches: List<WatchEntity>,
    wearLogsForDate: List<WearLogWithWatch>,
    viewModel: WatchViewModel,
    onDismiss: () -> Unit
) {
    val dateString = date.toString()
    val wornWatchIds = remember(wearLogsForDate) {
        wearLogsForDate.map { it.wearLog.watchId }.toSet()
    }

    val turkishLocale = remember { Locale.Builder().setLanguage("tr").setRegion("TR").build() }
    val formattedTitle = date.format(DateTimeFormatter.ofPattern("d MMMM yyyy, EEEE", turkishLocale))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = formattedTitle,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Bu tarihte taktığınız saatleri seçin:",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (allWatches.isEmpty()) {
            Text(
                text = "Kayıtlı saatiniz yok. Saatlerim sayfasından saat ekleyebilirsiniz.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allWatches, key = { it.id }) { watch ->
                    val isWorn = wornWatchIds.contains(watch.id)
                    val watchColor = parseHexColor(watch.colorHex)

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isWorn)
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(watchColor)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "${watch.brand} ${watch.model}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            if (isWorn) {
                                Button(
                                    onClick = { viewModel.toggleWearLog(watch.id, dateString, true) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Takıldı")
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { viewModel.toggleWearLog(watch.id, dateString, false) },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Ekle")
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Tamam")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// Helper to calculate days grid starting Monday ending Sunday
fun getCalendarDays(yearMonth: YearMonth): List<LocalDate> {
    val firstOfMonth = yearMonth.atDay(1)
    val firstGridDay = firstOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    val lastOfMonth = yearMonth.atEndOfMonth()
    val lastGridDay = lastOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

    val days = mutableListOf<LocalDate>()
    var curr = firstGridDay
    while (!curr.isAfter(lastGridDay)) {
        days.add(curr)
        curr = curr.plusDays(1)
    }
    return days
}
