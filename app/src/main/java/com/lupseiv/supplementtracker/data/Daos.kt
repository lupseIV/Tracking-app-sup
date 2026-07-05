package com.lupseiv.supplementtracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SupplementDao {
    @Query("SELECT * FROM supplements ORDER BY name COLLATE NOCASE")
    fun getAll(): Flow<List<Supplement>>

    @Query("SELECT * FROM supplements WHERE isTracked = 1 ORDER BY name COLLATE NOCASE")
    fun getTracked(): Flow<List<Supplement>>

    @Query("SELECT * FROM supplements WHERE id = :id")
    fun getById(id: Long): Flow<Supplement?>

    @Query("SELECT * FROM buy_options WHERE supplementId = :supplementId ORDER BY storeName")
    fun getBuyOptions(supplementId: Long): Flow<List<BuyOption>>

    @Insert
    suspend fun insert(supplement: Supplement): Long

    @Insert
    suspend fun insertBuyOptions(options: List<BuyOption>)

    @Query("UPDATE supplements SET isTracked = :tracked WHERE id = :id")
    suspend fun setTracked(id: Long, tracked: Boolean)

    @Query("DELETE FROM supplements WHERE id = :id AND isCustom = 1")
    suspend fun deleteCustom(id: Long)
}

@Dao
interface IntakeDao {
    @Query("SELECT * FROM intake_logs WHERE epochDay = :epochDay")
    fun getForDay(epochDay: Long): Flow<List<IntakeLog>>

    @Query(
        """
        SELECT l.epochDay AS epochDay, s.name AS supplementName, l.takenAtMillis AS takenAtMillis
        FROM intake_logs l JOIN supplements s ON s.id = l.supplementId
        WHERE l.epochDay >= :fromEpochDay
        ORDER BY l.epochDay DESC, s.name COLLATE NOCASE
        """
    )
    fun getHistory(fromEpochDay: Long): Flow<List<IntakeWithSupplement>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(log: IntakeLog)

    @Query("DELETE FROM intake_logs WHERE supplementId = :supplementId AND epochDay = :epochDay")
    suspend fun deleteForDay(supplementId: Long, epochDay: Long)
}
