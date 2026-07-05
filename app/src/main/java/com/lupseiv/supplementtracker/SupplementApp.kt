package com.lupseiv.supplementtracker

import android.app.Application
import com.lupseiv.supplementtracker.data.AppDatabase

class SupplementApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.get(this) }
}
