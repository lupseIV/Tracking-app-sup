package com.lupseiv.supplementtracker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TodayScreen(
    viewModel: TodayViewModel,
    onSupplementClick: (Long) -> Unit,
) {
    val items by viewModel.items.collectAsStateWithLifecycle()

    when (val list = items) {
        null -> LoadingBox()
        else -> {
            if (list.isEmpty()) {
                EmptyMessage(
                    "Nothing tracked yet.\n\nGo to the Supplements tab, open a supplement and turn on “I take this” to see it here."
                )
            } else {
                val takenCount = list.count { it.taken }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        Column(Modifier.padding(bottom = 8.dp)) {
                            Text(
                                text = LocalDate.now().format(
                                    DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.getDefault())
                                ),
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Text(
                                text = "$takenCount of ${list.size} taken",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            LinearProgressIndicator(
                                progress = { if (list.isEmpty()) 0f else takenCount / list.size.toFloat() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                            )
                        }
                    }
                    items(list, key = { it.supplement.id }) { item ->
                        Card(onClick = { onSupplementClick(item.supplement.id) }) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Checkbox(
                                    checked = item.taken,
                                    onCheckedChange = { viewModel.setTaken(item.supplement.id, it) },
                                )
                                Column(Modifier.weight(1f)) {
                                    Text(item.supplement.name, style = MaterialTheme.typography.titleMedium)
                                    if (item.supplement.dosage.isNotBlank()) {
                                        Text(
                                            item.supplement.dosage,
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

@Composable
fun LoadingBox() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun EmptyMessage(text: String) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
