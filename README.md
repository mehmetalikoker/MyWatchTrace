# WatchTrace ⌚

**WatchTrace** is a modern, feature-rich Android application designed specifically for watch enthusiasts and collectors. Built with **Jetpack Compose** and **Kotlin**, it offers a seamless way to manage watch collections, track daily wear history, analyze collection statistics, manage portfolio finances, and back up data securely.

---

## 🚀 Key Features

### 1. 📋 Watch Collection Management ("Saatlerim")
* **Detailed Attributes:** Manage brand, model, category (*Diver, Dress, Spor, Pilot*), production year, movement type (*Otomatik, Quartz, Kurmalı, Solar, Hibrit*), movement model/caliber, purchase price & currency (*TL, USD, EUR, CHF*), and status (*Aktif, Satıldı*).
* **Box & Papers with Serial Number:** Record box and certificate status with a conditional, optional serial number field.
* **Sold Watch Details:** Track sale price, currency, sale date, and buyer information for sold pieces.
* **Photo Gallery & Lightbox:** Attach watch photos stored securely in the app's internal private storage for permanent persistence. Tap any watch thumbnail to view it in an immersive full-screen lightbox.
* **CRUD Operations:** Add, update, and delete watches effortlessly with clean Material 3 dialogs.

### 2. 📅 Monthly Calendar View ("Takvim")
* **Visual Wear History:** Monthly grid calendar displaying wear indicators with watch photo thumbnails directly on each day cell.
* **Interactive Day Sheet:** Tap any day to open a bottom sheet and log or toggle which watch(es) you wore on that date.

### 3. 📝 Daily Wear Logging ("Günlük")
* **Quick Log:** Fast interface to record today's wrist rotation with one tap.

### 4. 📊 Advanced Statistics ("İstatistik")
* **Current Month Top 5:** Dynamic ranking of the most worn watches for the current month.
* **All-Time Top 10:** Historical ranking of the most worn watches across all time.
* **Neglected Watches (Top 10 Least Worn):** Discover active watches that haven't received wrist time yet this month or all-time, helping you give neglected pieces their turn.
* **Interactive Dialogs:** Clean menu cards with arrow navigation opening detailed ranked lists with medal badges and watch photos.

### 5. 💰 Portfolio & Finance ("Finans")
* **Active Portfolio Valuation:** Total investment breakdown by currency (*TL, USD, EUR, CHF*).
* **Collection Overview:** Total active and sold watch counts at a glance.

### 6. 📂 CSV Backup & Restore ("Yedekle & Geri Yükle")
* **Export (Backup):** Export your complete watch collection to a `.csv` file via Android's document creator.
* **Import (Restore):** Seamlessly restore your watch collection from a CSV backup file.

### 7. 🎨 Atmospheric Design & Splash Screen
* **Watermark Integration:** Subtle atmospheric background integration featuring *"Saatleri Ayarlama Enstitüsü"* artwork across splash screens and watermarks.
* **Randomized Splash:** 2-second launch splash screen with random image rotation (`img.png`, `img2.png`, etc.).
* **Material 3 UI:** Clean, modern dark/light adaptive Material Design 3 interface with polished single-word bottom navigation tabs.

---

## 🛠️ Tech Stack & Architecture

* **UI Toolkit:** Jetpack Compose, Material 3, Coil (for asynchronous image loading).
* **Architecture:** MVVM (Model-View-ViewModel) with Repository Pattern and Kotlin Coroutines / Flows.
* **Database:** Room Database (SQLite) with reactive StateFlows.
* **Navigation:** Jetpack Navigation Compose.
* **Language:** 100% Kotlin.

---

## 📱 Project Structure

```
com.example.mywatchtrace/
├── data/
│   ├── AppDatabase.kt
│   ├── WatchRepository.kt
│   ├── dao/
│   │   └── WatchDao.kt
│   └── entity/
│       ├── WatchEntity.kt
│       └── WearLogEntity.kt
├── ui/
│   ├── MainScreen.kt
│   ├── WatchViewModel.kt
│   └── screens/
│       ├── MyWatchesScreen.kt
│       ├── TopWatchesScreen.kt
│       ├── DailyLogScreen.kt
│       ├── CalendarScreen.kt
│       ├── FinanceScreen.kt
│       └── SplashScreen.kt
└── utils/
    └── WatchCsvUtils.txt / WatchCsvUtils.kt
```

---

## 📦 Building and Installing

### Prerequisites
* Android Studio (Koala / Ladybug or newer)
* Android SDK (API level 24+)

### Build via Gradle
```bash
# Build debug APK
./gradlew assembleDebug

# Install debug APK to connected device / emulator
./gradlew installDebug
```

---

## 📦 GitHub Repository

Repository URL: [https://github.com/mehmetalikoker/MyWatchTrace.git](https://github.com/mehmetalikoker/MyWatchTrace.git)

---

## 📄 License

This project is personal software developed for watch collection management.
