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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mywatchtrace.R
import com.example.mywatchtrace.data.entity.WatchEntity
import com.example.mywatchtrace.ui.WatchViewModel

val PREDEFINED_COLORS = listOf(
    "#2196F3", // Mavi
    "#F44336", // Kırmızı
    "#4CAF50", // Yeşil
    "#FF9800", // Turuncu
    "#9C27B0", // Mor
    "#009688", // Teal
    "#00BCD4", // Turkuaz
    "#3F51B5", // İndigo
    "#E91E63", // Pembe
    "#795548"  // Kahverengi
)

val WATCH_CATEGORIES = listOf("Diver", "Dress", "Spor", "Pilot")
val MOVEMENT_TYPES = listOf("Otomatik", "Quartz", "Kurmalı")

fun parseHexColor(hex: String, defaultColor: Color = Color.Gray): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: Exception) {
        defaultColor
    }
}

@Composable
fun WatchWatermarkBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Luxury watch dial watermark in background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.watch_dial_bg),
                contentDescription = null,
                modifier = Modifier.size(320.dp),
                alpha = 0.07f
            )
        }
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyWatchesScreen(
    viewModel: WatchViewModel
) {
    val watches by viewModel.allWatches.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var watchToDelete by remember { mutableStateOf<WatchEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Saatlerim (${watches.size})",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Saat Ekle")
            }
        }
    ) { innerPadding ->
        WatchWatermarkBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (watches.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Watch,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Henüz kayıtlı saatiniz yok",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Sağ alttaki '+' butonuna basarak ilk saatinizi ekleyin.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(watches, key = { it.id }) { watch ->
                            WatchCard(
                                watch = watch,
                                onDeleteClick = { watchToDelete = watch }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddWatchDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { brand, model, colorHex, category, productionYear, movementType, movementModel ->
                viewModel.addWatch(brand, model, colorHex, category, productionYear, movementType, movementModel)
                showAddDialog = false
            }
        )
    }

    watchToDelete?.let { watch ->
        AlertDialog(
            onDismissRequest = { watchToDelete = null },
            title = { Text("Saati Sil") },
            text = { Text("${watch.brand} ${watch.model} saatini silmek istediğinize emin misiniz?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteWatch(watch)
                        watchToDelete = null
                    }
                ) {
                    Text("Sil", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { watchToDelete = null }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
fun WatchCard(
    watch: WatchEntity,
    onDeleteClick: () -> Unit
) {
    val watchColor = parseHexColor(watch.colorHex)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(watchColor)
                    )
                    Text(
                        text = watch.category.ifBlank { "Diver" },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (watch.productionYear.isNotBlank()) {
                        Text(
                            text = "• ${watch.productionYear}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Sil",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = watch.brand,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = watch.model,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Mekanizma: ${watch.movementType}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (watch.movementModel.isNotBlank()) {
                    Text(
                        text = "Kalibre: ${watch.movementModel}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun AddWatchDialog(
    onDismiss: () -> Unit,
    onConfirm: (brand: String, model: String, colorHex: String, category: String, productionYear: String, movementType: String, movementModel: String) -> Unit
) {
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Diver") }
    var productionYear by remember { mutableStateOf("") }
    var selectedMovementType by remember { mutableStateOf("Otomatik") }
    var movementModel by remember { mutableStateOf("") }
    var selectedColorHex by remember { mutableStateOf(PREDEFINED_COLORS.first()) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Saat Ekle", fontWeight = FontWeight.Bold) },
        text = {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Subtle dial background watermark inside dialog
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.watch_dial_bg),
                        contentDescription = null,
                        modifier = Modifier.size(200.dp),
                        alpha = 0.05f
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = {
                            brand = it
                            if (isError) isError = false
                        },
                        label = { Text("Marka (Örn: Oris)") },
                        singleLine = true,
                        isError = isError && brand.isBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = model,
                        onValueChange = {
                            model = it
                            if (isError) isError = false
                        },
                        label = { Text("Model (Örn: Aquis)") },
                        singleLine = true,
                        isError = isError && model.isBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Category Selection
                    Text(text = "Kategori:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(WATCH_CATEGORIES) { cat ->
                            FilterChip(
                                selected = cat == selectedCategory,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat) }
                            )
                        }
                    }

                    // Production Year
                    OutlinedTextField(
                        value = productionYear,
                        onValueChange = { productionYear = it },
                        label = { Text("Yapım Yılı (Örn: 2023)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Movement Type Selection
                    Text(text = "Mekanizma Tipi:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(MOVEMENT_TYPES) { mov ->
                            FilterChip(
                                selected = mov == selectedMovementType,
                                onClick = { selectedMovementType = mov },
                                label = { Text(mov) }
                            )
                        }
                    }

                    // Movement Model
                    OutlinedTextField(
                        value = movementModel,
                        onValueChange = { movementModel = it },
                        label = { Text("Mekanizma Modeli / Kalibre (Örn: NH35)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Etiket / Takvim Rengi:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(PREDEFINED_COLORS) { hex ->
                            val color = parseHexColor(hex)
                            val isSelected = hex == selectedColorHex

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 0.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColorHex = hex }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (brand.isBlank() || model.isBlank()) {
                        isError = true
                    } else {
                        onConfirm(
                            brand,
                            model,
                            selectedColorHex,
                            selectedCategory,
                            productionYear,
                            selectedMovementType,
                            movementModel
                        )
                    }
                }
            ) {
                Text("Ekle", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}
