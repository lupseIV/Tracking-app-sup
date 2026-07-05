package com.lupseiv.supplementtracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val historyByDay by viewModel.historyByDay.collectAsStateWithLifecycle()

    when (val days = historyByDay) {
        null -> LoadingBox()
        else -> {
            if (days.isEmpty()) {
                EmptyMessage("No history yet.\n\nCheck off supplements on the Today tab and they will show up here.")
            } else {
                val dayFormatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.getDefault())
                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
                val today = LocalDate.now()
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(days, key = { it.first }) { (epochDay, entries) ->
                        val date = LocalDate.ofEpochDay(epochDay)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = when (date) {
                                    today -> "Today"
                                    today.minusDays(1) -> "Yesterday"
                                    else -> date.format(dayFormatter)
                                },
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Card {
                                Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    entries.forEach { entry ->
                                        Row(Modifier.fillMaxWidth()) {
                                            Text(
                                                entry.supplementName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.weight(1f),
                                            )
                                            Text(
                                                Instant.ofEpochMilli(entry.takenAtMillis)
                                                    .atZone(ZoneId.systemDefault())
                                                    .format(timeFormatter),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
