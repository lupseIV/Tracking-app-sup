# Supplement Tracker

A simple Android app for tracking the supplements you take, built with Kotlin, Jetpack Compose (Material 3), and Room.

## Features

- **Today** — a daily checklist of the supplements you take, with a progress bar. Check items off as you take them.
- **Supplements** — a built-in catalog of 18 common supplements (Vitamin D3, Omega-3, Magnesium, Creatine, Ashwagandha, …) with search, plus the ability to add your own custom supplements.
- **Detail view** — for each supplement: description, typical dosage, a list of **benefits**, and **where to buy** options (Amazon, iHerb, eMAG) that open a pre-filled search in your browser. Toggle *"I take this"* to add it to your daily checklist.
- **History** — see what you took on each of the last 30 days.

All data is stored locally on the device (Room/SQLite). No account, no network access required.

> Benefits shown in the app are general information, not medical advice.

## Building

Open the project in Android Studio (Ladybug or newer) and run the `app` configuration, or build from the command line:

```bash
./gradlew assembleDebug
```

The APK ends up in `app/build/outputs/apk/debug/app-debug.apk`.

- Min SDK: 26 (Android 8.0) · Target SDK: 35
- Kotlin 2.0, Jetpack Compose (Material 3), Room 2.6, Navigation Compose

## Project structure

```
app/src/main/java/com/lupseiv/supplementtracker/
├── MainActivity.kt          # Navigation, bottom bar, scaffold
├── SupplementApp.kt         # Application class holding the database
├── data/
│   ├── Entities.kt          # Supplement, BuyOption, IntakeLog entities
│   ├── Daos.kt              # Room DAOs
│   ├── AppDatabase.kt       # Database + first-run seeding
│   └── SeedData.kt          # Built-in supplement catalog
└── ui/
    ├── ViewModels.kt        # One ViewModel per screen
    ├── TodayScreen.kt
    ├── SupplementsScreen.kt
    ├── DetailScreen.kt
    ├── AddSupplementScreen.kt
    ├── HistoryScreen.kt
    └── theme/Theme.kt
```
