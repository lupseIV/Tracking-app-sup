package com.lupseiv.supplementtracker.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "supplements")
data class Supplement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val description: String,
    val dosage: String,
    val benefits: List<String>,
    val isTracked: Boolean = false,
    val isCustom: Boolean = false,
)

@Entity(
    tableName = "buy_options",
    foreignKeys = [
        ForeignKey(
            entity = Supplement::class,
            parentColumns = ["id"],
            childColumns = ["supplementId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("supplementId")],
)
data class BuyOption(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val supplementId: Long,
    val storeName: String,
    val url: String,
)

@Entity(
    tableName = "intake_logs",
    foreignKeys = [
        ForeignKey(
            entity = Supplement::class,
            parentColumns = ["id"],
            childColumns = ["supplementId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("supplementId"), Index(value = ["supplementId", "epochDay"], unique = true)],
)
data class IntakeLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val supplementId: Long,
    /** Day the supplement was taken, as LocalDate.toEpochDay(). */
    val epochDay: Long,
    val takenAtMillis: Long,
)

/** Row for the history screen: an intake joined with its supplement name. */
data class IntakeWithSupplement(
    val epochDay: Long,
    val supplementName: String,
    val takenAtMillis: Long,
)

class Converters {
    @TypeConverter
    fun benefitsToString(benefits: List<String>): String = benefits.joinToString(SEPARATOR)

    @TypeConverter
    fun stringToBenefits(value: String): List<String> =
        if (value.isEmpty()) emptyList() else value.split(SEPARATOR)

    private companion object {
        const val SEPARATOR = "||"
    }
}
