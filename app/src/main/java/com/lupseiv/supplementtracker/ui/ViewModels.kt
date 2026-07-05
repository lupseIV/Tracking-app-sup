package com.lupseiv.supplementtracker.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.lupseiv.supplementtracker.SupplementApp
import com.lupseiv.supplementtracker.data.BuyOption
import com.lupseiv.supplementtracker.data.IntakeLog
import com.lupseiv.supplementtracker.data.IntakeWithSupplement
import com.lupseiv.supplementtracker.data.Supplement
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private fun CreationExtras.app(): SupplementApp = this[APPLICATION_KEY] as SupplementApp

private fun <T> ViewModel.stateIn(flow: kotlinx.coroutines.flow.Flow<T>, initial: T): StateFlow<T> =
    flow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initial)

// ---------- Today ----------

data class TodayItem(val supplement: Supplement, val taken: Boolean)

class TodayViewModel(private val app: SupplementApp) : ViewModel() {
    private val todayEpochDay = LocalDate.now().toEpochDay()
    private val supplementDao = app.database.supplementDao()
    private val intakeDao = app.database.intakeDao()

    val items: StateFlow<List<TodayItem>?> = stateIn(
        combine(supplementDao.getTracked(), intakeDao.getForDay(todayEpochDay)) { tracked, logs ->
            val takenIds = logs.map { it.supplementId }.toSet()
            tracked.map { TodayItem(it, it.id in takenIds) }
        },
        initial = null,
    )

    fun setTaken(supplementId: Long, taken: Boolean) {
        viewModelScope.launch {
            if (taken) {
                intakeDao.insert(
                    IntakeLog(
                        supplementId = supplementId,
                        epochDay = todayEpochDay,
                        takenAtMillis = System.currentTimeMillis(),
                    )
                )
            } else {
                intakeDao.deleteForDay(supplementId, todayEpochDay)
            }
        }
    }

    companion object {
        val Factory = viewModelFactory { initializer { TodayViewModel(app()) } }
    }
}

// ---------- Supplement list ----------

class SupplementsViewModel(app: SupplementApp) : ViewModel() {
    private val dao = app.database.supplementDao()

    val query = MutableStateFlow("")

    val supplements: StateFlow<List<Supplement>?> = stateIn(
        combine(dao.getAll(), query) { all, q ->
            if (q.isBlank()) all
            else all.filter {
                it.name.contains(q, ignoreCase = true) || it.category.contains(q, ignoreCase = true)
            }
        },
        initial = null,
    )

    companion object {
        val Factory = viewModelFactory { initializer { SupplementsViewModel(app()) } }
    }
}

// ---------- Detail ----------

class DetailViewModel(app: SupplementApp, savedStateHandle: SavedStateHandle) : ViewModel() {
    private val dao = app.database.supplementDao()
    private val supplementId: Long = checkNotNull(savedStateHandle["id"])

    val supplement: StateFlow<Supplement?> = stateIn(dao.getById(supplementId), initial = null)
    val buyOptions: StateFlow<List<BuyOption>> = stateIn(dao.getBuyOptions(supplementId), initial = emptyList())

    fun setTracked(tracked: Boolean) {
        viewModelScope.launch { dao.setTracked(supplementId, tracked) }
    }

    fun deleteCustom(onDeleted: () -> Unit) {
        viewModelScope.launch {
            dao.deleteCustom(supplementId)
            onDeleted()
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { DetailViewModel(app(), createSavedStateHandle()) }
        }
    }
}

// ---------- Add custom supplement ----------

class AddSupplementViewModel(app: SupplementApp) : ViewModel() {
    private val dao = app.database.supplementDao()

    fun save(
        name: String,
        category: String,
        description: String,
        dosage: String,
        benefitsText: String,
        buyOptions: List<Pair<String, String>>,
        onSaved: () -> Unit,
    ) {
        viewModelScope.launch {
            val id = dao.insert(
                Supplement(
                    name = name.trim(),
                    category = category.trim().ifBlank { "Custom" },
                    description = description.trim(),
                    dosage = dosage.trim(),
                    benefits = benefitsText.lines().map { it.trim() }.filter { it.isNotEmpty() },
                    isTracked = true,
                    isCustom = true,
                )
            )
            val options = buyOptions
                .map { (store, url) -> store.trim() to url.trim() }
                .filter { (store, url) -> store.isNotEmpty() && url.isNotEmpty() }
                .map { (store, url) ->
                    val fullUrl = if (url.startsWith("http")) url else "https://$url"
                    BuyOption(supplementId = id, storeName = store, url = fullUrl)
                }
            if (options.isNotEmpty()) dao.insertBuyOptions(options)
            onSaved()
        }
    }

    companion object {
        val Factory = viewModelFactory { initializer { AddSupplementViewModel(app()) } }
    }
}

// ---------- History ----------

class HistoryViewModel(app: SupplementApp) : ViewModel() {
    private val intakeDao = app.database.intakeDao()

    /** Last 30 days of intakes, newest day first. */
    val historyByDay: StateFlow<List<Pair<Long, List<IntakeWithSupplement>>>?> = stateIn(
        intakeDao.getHistory(LocalDate.now().minusDays(30).toEpochDay()).map { rows ->
            rows.groupBy { it.epochDay }
                .toSortedMap(compareByDescending { it })
                .map { (day, entries) -> day to entries }
        },
        initial = null,
    )

    companion object {
        val Factory = viewModelFactory { initializer { HistoryViewModel(app()) } }
    }
}
