package com.example.mywatchtrace.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mywatchtrace.data.AppDatabase
import com.example.mywatchtrace.data.WatchRepository
import com.example.mywatchtrace.data.dao.WatchWearCount
import com.example.mywatchtrace.data.entity.AccuracyLogEntity
import com.example.mywatchtrace.data.entity.WatchEntity
import com.example.mywatchtrace.data.entity.WearLogWithWatch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class WatchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WatchRepository

    val allWatches: StateFlow<List<WatchEntity>>
    val topWornWatches: StateFlow<List<WatchWearCount>>
    val topWornWatchesForCurrentMonth: StateFlow<List<WatchWearCount>>
    val leastWornWatches: StateFlow<List<WatchWearCount>>

    private val prefs = application.getSharedPreferences("watch_trace_prefs", android.content.Context.MODE_PRIVATE)
    private val _themeMode = MutableStateFlow(prefs.getString("theme_mode", "System") ?: "System")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode).apply()
    }

    private val _selectedDate = MutableStateFlow(LocalDate.now().toString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedYearMonth = MutableStateFlow(YearMonth.now())
    val selectedYearMonth: StateFlow<YearMonth> = _selectedYearMonth.asStateFlow()

    val wearLogsForSelectedDate: StateFlow<List<WearLogWithWatch>>
    val wearLogsForSelectedMonth: StateFlow<List<WearLogWithWatch>>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = WatchRepository(db.watchDao(), db.accuracyDao())

        allWatches = repository.allWatches.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        topWornWatches = repository.topWornWatches.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        leastWornWatches = repository.leastWornWatches.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        topWornWatchesForCurrentMonth = _selectedYearMonth.flatMapLatest { ym ->
            val ymString = String.format("%04d-%02d", ym.year, ym.monthValue)
            repository.getTopWornWatchesForMonth(ymString)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        wearLogsForSelectedDate = _selectedDate.flatMapLatest { date ->
            repository.getWearLogsForDate(date)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        wearLogsForSelectedMonth = _selectedYearMonth.flatMapLatest { ym ->
            val ymString = String.format("%04d-%02d", ym.year, ym.monthValue)
            repository.getWearLogsForMonth(ymString)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun setSelectedYearMonth(yearMonth: YearMonth) {
        _selectedYearMonth.value = yearMonth
    }

    fun addWatch(
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
        serialNumber: String,
        price: String,
        currency: String,
        status: String,
        soldPrice: String,
        soldCurrency: String,
        soldDate: String,
        soldTo: String
    ) {
        viewModelScope.launch {
            repository.addWatch(
                WatchEntity(
                    brand = brand.trim(),
                    model = model.trim(),
                    colorHex = colorHex,
                    category = category.trim(),
                    productionYear = productionYear.trim(),
                    movementType = movementType.trim(),
                    movementModel = movementModel.trim(),
                    photoUri = photoUri.trim(),
                    hasBox = hasBox,
                    hasPapers = hasPapers,
                    serialNumber = serialNumber.trim(),
                    price = price.trim(),
                    currency = currency,
                    status = status,
                    soldPrice = soldPrice.trim(),
                    soldCurrency = soldCurrency,
                    soldDate = soldDate.trim(),
                    soldTo = soldTo.trim()
                )
            )
        }
    }

    fun importWatch(watch: WatchEntity) {
        viewModelScope.launch {
            repository.addWatch(watch)
        }
    }

    fun updateWatch(
        id: Long,
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
        serialNumber: String,
        price: String,
        currency: String,
        status: String,
        soldPrice: String,
        soldCurrency: String,
        soldDate: String,
        soldTo: String
    ) {
        viewModelScope.launch {
            repository.updateWatch(
                WatchEntity(
                    id = id,
                    brand = brand.trim(),
                    model = model.trim(),
                    colorHex = colorHex,
                    category = category.trim(),
                    productionYear = productionYear.trim(),
                    movementType = movementType.trim(),
                    movementModel = movementModel.trim(),
                    photoUri = photoUri.trim(),
                    hasBox = hasBox,
                    hasPapers = hasPapers,
                    serialNumber = serialNumber.trim(),
                    price = price.trim(),
                    currency = currency,
                    status = status,
                    soldPrice = soldPrice.trim(),
                    soldCurrency = soldCurrency,
                    soldDate = soldDate.trim(),
                    soldTo = soldTo.trim()
                )
            )
        }
    }

    fun deleteWatch(watch: WatchEntity) {
        viewModelScope.launch {
            repository.deleteWatch(watch)
        }
    }

    fun toggleWearLog(watchId: Long, date: String, isCurrentlyWorn: Boolean) {
        viewModelScope.launch {
            if (isCurrentlyWorn) {
                repository.removeWearLog(watchId, date)
            } else {
                repository.addWearLog(watchId, date)
            }
        }
    }

    fun addWearLog(watchId: Long, date: String) {
        viewModelScope.launch {
            repository.addWearLog(watchId, date)
        }
    }

    fun removeWearLog(watchId: Long, date: String) {
        viewModelScope.launch {
            repository.removeWearLog(watchId, date)
        }
    }

    fun getAccuracyLogs(watchId: Long): Flow<List<AccuracyLogEntity>> {
        return repository.getAccuracyLogsForWatch(watchId)
    }

    fun addAccuracyLog(watchId: Long, deviationSeconds: Float, date: String, notes: String) {
        viewModelScope.launch {
            repository.addAccuracyLog(watchId, deviationSeconds, date, notes)
        }
    }

    fun deleteAccuracyLog(log: AccuracyLogEntity) {
        viewModelScope.launch {
            repository.deleteAccuracyLog(log)
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WatchViewModel(application) as T
        }
    }
}
