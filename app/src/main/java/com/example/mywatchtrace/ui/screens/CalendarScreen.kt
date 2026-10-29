package com.example.mywatchtrace.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Watch
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mywatchtrace.R
import com.example.mywatchtrace.data.entity.WatchEntity
import com.example.mywatchtrace.data.entity.WearLogWithWatch
import com.example.mywatchtrace.ui.WatchViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: WatchViewModel
) {
    val allWatches by viewModel.allWatches.collectAsState()
    
    // Week navigation state (Monday of selected week)
    var currentWeekStart by remember {
        mutableStateOf(LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
    }

    val weekDays = remember(currentWeekStart) {
        (0..6).map { currentWeekStart.plusDays(it.toLong()) }
    }

    // Observe logs for the month(s) covering this week
    val selectedYearMonth by viewModel.selectedYearMonth.collectAsState()
    val wearLogsForMonth by viewModel.wearLogsForSelectedMonth.collectAsState()

    // Ensure selectedYearMonth tracks currentWeekStart
    val weekMonth = java.time.YearMonth.from(currentWeekStart)
    if (selectedYearMonth != weekMonth) {
        viewModel.setSelectedYearMonth(weekMonth)
    }

    val logsByDate = remember(wearLogsForMonth) {
        wearLogsForMonth.groupBy { it.wearLog.date }
    }

    var selectedDayForBottomSheet by remember { mutableStateOf<LocalDate?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val turkishLocale = remember { Locale.Builder().setLanguage("tr").setRegion("TR").build() }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("d MMM", turkishLocale) }
    val weekTitle = remember(currentWeekStart) {
        val weekEnd = currentWeekStart.plusDays(6)
        "${currentWeekStart.format(dateFormatter)} - ${weekEnd.format(dateFormatter)} ${weekEnd.year}"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Haftalık Takvim",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    TextButton(onClick = {
                        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Week Header & Navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { currentWeekStart = currentWeekStart.minusWeeks(1) },
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                RoundedCornerShape(10.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = "Önceki Hafta",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = weekTitle,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    IconButton(
                        onClick = { currentWeekStart = currentWeekStart.plusWeeks(1) },
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                RoundedCornerShape(10.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "Sonraki Hafta",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 7 Spacious Day Cards
                weekDays.forEach { date ->
                    val dateString = date.toString()
                    val dayLogs = logsByDate[dateString] ?: emptyList()
                    val isToday = date == LocalDate.now()
                    val dayName = date.format(DateTimeFormatter.ofPattern("EEEE", turkishLocale))
                    val dayFormatted = date.format(DateTimeFormatter.ofPattern("d MMMM", turkishLocale))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isToday)
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isToday) 4.dp else 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedDayForBottomSheet = date }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = dayName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "• $dayFormatted",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                OutlinedButton(
                                    onClick = { selectedDayForBottomSheet = date },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Saat Ekle / Düzenle", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (dayLogs.isEmpty()) {
                                Text(
                                    text = "Bu gün için saat kaydı yok.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    dayLogs.forEach { log ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                                    RoundedCornerShape(10.dp)
                                                )
                                                .padding(8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                if (log.watch.photoUri.isNotBlank()) {
                                                    Card(
                                                        shape = RoundedCornerShape(8.dp),
                                                        modifier = Modifier.size(36.dp)
                                                    ) {
                                                        AsyncImage(
                                                            model = log.watch.photoUri,
                                                            contentDescription = null,
                                                            contentScale = ContentScale.Crop,
                                                            modifier = Modifier.fillMaxSize()
                                                        )
                                                    }
                                                } else {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(24.dp)
                                                            .clip(CircleShape)
                                                            .background(MaterialTheme.colorScheme.primary)
                                                    )
                                                }

                                                Column {
                                                    Text(
                                                        text = "${log.watch.brand} ${log.watch.model}",
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = log.watch.category,
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }

                                            IconButton(
                                                onClick = { viewModel.toggleWearLog(log.watch.id, dateString, true) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Kaldır",
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
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
                                if (watch.photoUri.isNotBlank()) {
                                    Card(
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        AsyncImage(
                                            model = watch.photoUri,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                }
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
