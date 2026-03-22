package com.babyfeeding.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babyfeeding.data.model.FeedingRecord
import com.babyfeeding.data.model.FeedingType
import com.babyfeeding.ui.home.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    innerPadding: PaddingValues = PaddingValues(),
    viewModel: HomeViewModel = hiltViewModel()
) {
    val records by viewModel.records.collectAsState()
    val groupedRecords = records.groupBy { historyDateKey(it.timestamp) }.toList()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        groupedRecords.forEach { (dateLabel, itemsForDate) ->
            item(key = "header-$dateLabel") {
                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            items(
                items = itemsForDate,
                key = { it.id ?: "${it.timestamp}-${it.type}" }
            ) { record ->
                HistoryRecordCard(record = record)
            }
        }
    }
}

@Composable
private fun HistoryRecordCard(record: FeedingRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = when (record.type) {
                    FeedingType.BOTTLE -> "Bottle Feed"
                    FeedingType.SOLID_FOOD -> "Solid Food"
                },
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = timeOnly(record.timestamp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = historySummary(record),
                style = MaterialTheme.typography.bodyLarge
            )
            record.note?.takeIf { it.isNotBlank() }?.let { note ->
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun historyDateKey(timestamp: Long): String =
    SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date(timestamp))

private fun timeOnly(timestamp: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))

private fun historySummary(record: FeedingRecord): String = when (record.type) {
    FeedingType.BOTTLE -> {
        val amountLabel = record.amountMl?.let { "$it ml" } ?: "Amount not set"
        val milkLabel = record.milkType?.name?.replace('_', ' ') ?: "Unknown milk"
        "$amountLabel • $milkLabel"
    }

    FeedingType.SOLID_FOOD -> {
        val foodLabel = record.foodType?.name?.replace('_', ' ') ?: "Unknown food"
        val amountLabel = record.foodAmount?.name?.replace('_', ' ') ?: "Unknown amount"
        val acceptanceLabel = record.acceptance?.name?.replace('_', ' ') ?: "Unknown response"
        "$foodLabel • $amountLabel • $acceptanceLabel"
    }
}
