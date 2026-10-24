package com.example.mywatchtrace.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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
val CURRENCIES = listOf("TL", "USD", "EUR", "CHF")
val STATUS_OPTIONS = listOf("Aktif", "Satıldı")

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
            onConfirm = { brand, model, colorHex, category, productionYear, movementType, movementModel, photoUri, hasBox, hasPapers, price, currency, status, soldPrice, soldCurrency, soldDate, soldTo ->
                viewModel.addWatch(
                    brand = brand,
                    model = model,
                    colorHex = colorHex,
                    category = category,
                    productionYear = productionYear,
                    movementType = movementType,
                    movementModel = movementModel,
                    photoUri = photoUri,
                    hasBox = hasBox,
                    hasPapers = hasPapers,
                    price = price,
                    currency = currency,
                    status = status,
                    soldPrice = soldPrice,
                    soldCurrency = soldCurrency,
                    soldDate = soldDate,
                    soldTo = soldTo
                )
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
    val isSold = watch.status == "Satıldı"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSold)
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Watch photo thumbnail or color badge
            if (watch.photoUri.isNotBlank()) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(64.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    AsyncImage(
                        model = watch.photoUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(watchColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(watchColor)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = watch.category.ifBlank { "Diver" },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (watch.productionYear.isNotBlank()) {
                            Text(
                                text = "• ${watch.productionYear}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (isSold) {
                            Text(
                                text = "• SATILDI",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
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
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = watch.brand,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )

                Text(
                    text = watch.model,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Mekanizma: ${watch.movementType}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (watch.price.isNotBlank()) {
                        Text(
                            text = "Fiyat: ${watch.price} ${watch.currency}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Box & Papers badges
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Kutu: ${if (watch.hasBox) "Var" else "Yok"}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Belge: ${if (watch.hasPapers) "Var" else "Yok"}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isSold && watch.soldPrice.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Satış: ${watch.soldPrice} ${watch.soldCurrency} (${watch.soldDate}) -> ${watch.soldTo}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun AddWatchDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        brand: String,
        model: String,
        colorHex: String,
        category: String,
        productionYear: String,
        movementType: String,
        movementModel: String,
        photoUri: String,
        hasBox: Boolean,
        hasPapers: Boolean,
        price: String,
        currency: String,
        status: String,
        soldPrice: String,
        soldCurrency: String,
        soldDate: String,
        soldTo: String
    ) -> Unit
) {
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Diver") }
    var productionYear by remember { mutableStateOf("") }
    var selectedMovementType by remember { mutableStateOf("Otomatik") }
    var movementModel by remember { mutableStateOf("") }
    var selectedColorHex by remember { mutableStateOf(PREDEFINED_COLORS.first()) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // New Fields
    var hasBox by remember { mutableStateOf(false) }
    var hasPapers by remember { mutableStateOf(false) }
    var price by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf("TL") }
    var selectedStatus by remember { mutableStateOf("Aktif") }

    // Sold Fields
    var soldPrice by remember { mutableStateOf("") }
    var selectedSoldCurrency by remember { mutableStateOf("TL") }
    var soldDate by remember { mutableStateOf("") }
    var soldTo by remember { mutableStateOf("") }

    var isError by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            photoUri = uri
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Saat Ekle", fontWeight = FontWeight.Bold) },
        text = {
            Box(modifier = Modifier.fillMaxWidth()) {
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
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Photo Picker at the very top
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoUri != null) {
                            AsyncImage(
                                model = photoUri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = "Fotoğraf Ekle",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Fotoğraf Ekle",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

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
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                        Text(text = "Kategori:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
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
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                        Text(text = "Mekanizma Tipi:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
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
                    }

                    // Movement Model
                    OutlinedTextField(
                        value = movementModel,
                        onValueChange = { movementModel = it },
                        label = { Text("Mekanizma Modeli / Kalibre (Örn: NH35)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Kutu var mı? (Evet / Hayır)
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                        Text(text = "Kutu var mı?", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = hasBox,
                                onClick = { hasBox = true },
                                label = { Text("Evet") }
                            )
                            FilterChip(
                                selected = !hasBox,
                                onClick = { hasBox = false },
                                label = { Text("Hayır") }
                            )
                        }
                    }

                    // Belge var mı? (Evet / Hayır)
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                        Text(text = "Belge var mı?", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = hasPapers,
                                onClick = { hasPapers = true },
                                label = { Text("Evet") }
                            )
                            FilterChip(
                                selected = !hasPapers,
                                onClick = { hasPapers = false },
                                label = { Text("Hayır") }
                            )
                        }
                    }

                    // Saat Fiyatı & Para Birimi
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = { Text("Saat Fiyatı") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                        Text(text = "Para Birimi:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(CURRENCIES) { curr ->
                                FilterChip(
                                    selected = curr == selectedCurrency,
                                    onClick = { selectedCurrency = curr },
                                    label = { Text(curr) }
                                )
                            }
                        }
                    }

                    // Durumu (Aktif, Satıldı)
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                        Text(text = "Durumu:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(STATUS_OPTIONS) { status ->
                                FilterChip(
                                    selected = status == selectedStatus,
                                    onClick = { selectedStatus = status },
                                    label = { Text(status) }
                                )
                            }
                        }
                    }

                    // Eğer Durumu Satıldı ise ek 4 soru
                    if (selectedStatus == "Satıldı") {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Satış Detayları",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        OutlinedTextField(
                            value = soldPrice,
                            onValueChange = { soldPrice = it },
                            label = { Text("Kaça satıldı?") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                            Text(text = "Satış Para Birimi:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(CURRENCIES) { curr ->
                                    FilterChip(
                                        selected = curr == selectedSoldCurrency,
                                        onClick = { selectedSoldCurrency = curr },
                                        label = { Text(curr) }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = soldDate,
                            onValueChange = { soldDate = it },
                            label = { Text("Hangi tarihte satıldı? (Örn: 2026-07-02)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = soldTo,
                            onValueChange = { soldTo = it },
                            label = { Text("Kime satıldı?") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "Etiket / Takvim Rengi:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
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
                            movementModel,
                            photoUri?.toString() ?: "",
                            hasBox,
                            hasPapers,
                            price,
                            selectedCurrency,
                            selectedStatus,
                            soldPrice,
                            selectedSoldCurrency,
                            soldDate,
                            soldTo
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
