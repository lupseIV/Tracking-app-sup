package com.lupseiv.supplementtracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
    entities = [Supplement::class, BuyOption::class, IntakeLog::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun supplementDao(): SupplementDao
    abstract fun intakeDao(): IntakeDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "supplement-tracker.db",
                )
                    .addCallback(SeedCallback(context.applicationContext))
                    .build()
                    .also { instance = it }
            }
    }

    /** Populates the built-in catalog the first time the database is created. */
    private class SeedCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                val dao = get(context).supplementDao()
                SeedData.catalog.forEach { entry ->
                    val id = dao.insert(entry.supplement)
                    dao.insertBuyOptions(SeedData.buyOptionsFor(id, entry.searchTerm))
                }
            }
        }
    }
}
