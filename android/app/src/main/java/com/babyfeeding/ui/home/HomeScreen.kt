package com.babyfeeding.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babyfeeding.data.model.Acceptance
import com.babyfeeding.data.model.FeedingRecord
import com.babyfeeding.data.model.FeedingType
import com.babyfeeding.data.model.FoodAmount
import com.babyfeeding.data.model.FoodType
import com.babyfeeding.data.model.MilkType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val DefaultBabyId = "default-baby"

private enum class AddFeedMode {
    BOTTLE,
    SOLID
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val records by viewModel.records.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Baby Feeding") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Text("+")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TodayOverviewCard(records = records)
            }
            item {
                Text(
                    text = "Feeding Records",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            items(records, key = { it.id ?: "${it.timestamp}-${it.type}" }) { record ->
                FeedingRecordCard(record = record)
            }
        }
    }

    if (showAddDialog) {
        AddFeedDialog(
            onDismiss = { showAddDialog = false },
            onAddBottleFeed = { amountMl, milkType, note ->
                viewModel.addBottleFeed(
                    babyId = DefaultBabyId,
                    amountMl = amountMl,
                    milkType = milkType,
                    note = note
                )
                showAddDialog = false
            },
            onAddSolidFood = { foodType, foodAmount, acceptance, note ->
                viewModel.addSolidFood(
                    babyId = DefaultBabyId,
                    foodType = foodType,
                    foodAmount = foodAmount,
                    acceptance = acceptance,
                    note = note
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun TodayOverviewCard(records: List<FeedingRecord>) {
    val startOfTodayMillis = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    val todayRecords = records.filter { it.timestamp >= startOfTodayMillis }
    val bottleCount = todayRecords.count { it.type == FeedingType.BOTTLE }
    val solidCount = todayRecords.count { it.type == FeedingType.SOLID_FOOD }
    val totalBottleMl = todayRecords.sumOf { it.amountMl ?: 0 }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Today's Overview",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$bottleCount bottle feeds, $solidCount solid meals",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "$totalBottleMl ml consumed today",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FeedingRecordCard(record: FeedingRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = record.type.toDisplayTitle(),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = formatTimestamp(record.timestamp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = record.summaryText(),
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

@Composable
private fun AddFeedDialog(
    onDismiss: () -> Unit,
    onAddBottleFeed: (Int, MilkType, String?) -> Unit,
    onAddSolidFood: (FoodType, FoodAmount, Acceptance, String?) -> Unit
) {
    var mode by remember { mutableStateOf(AddFeedMode.BOTTLE) }
    var amountText by remember { mutableStateOf("120") }
    var milkType by remember { mutableStateOf(MilkType.BREAST_MILK) }
    var foodType by remember { mutableStateOf(FoodType.RICE_CEREAL) }
    var foodAmount by remember { mutableStateOf(FoodAmount.SMALL) }
    var acceptance by remember { mutableStateOf(Acceptance.LIKED) }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Feeding Record") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { mode = AddFeedMode.BOTTLE }
                    ) {
                        Text("Bottle")
                    }
                    OutlinedButton(
                        onClick = { mode = AddFeedMode.SOLID }
                    ) {
                        Text("Solid Food")
                    }
                }

                if (mode == AddFeedMode.BOTTLE) {
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Amount (ml)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    EnumSelector(
                        label = "Milk Type",
                        options = MilkType.entries,
                        selected = milkType,
                        onSelect = { milkType = it }
                    )
                } else {
                    EnumSelector(
                        label = "Food Type",
                        options = FoodType.entries,
                        selected = foodType,
                        onSelect = { foodType = it }
                    )
                    EnumSelector(
                        label = "Amount",
                        options = FoodAmount.entries,
                        selected = foodAmount,
                        onSelect = { foodAmount = it }
                    )
                    EnumSelector(
                        label = "Acceptance",
                        options = Acceptance.entries,
                        selected = acceptance,
                        onSelect = { acceptance = it }
                    )
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (mode == AddFeedMode.BOTTLE) {
                        onAddBottleFeed(amountText.toIntOrNull() ?: 0, milkType, note)
                    } else {
                        onAddSolidFood(foodType, foodAmount, acceptance, note)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun <T : Enum<T>> EnumSelector(
    label: String,
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                OutlinedButton(onClick = { onSelect(option) }) {
                    Text(
                        text = option.name.replace('_', ' '),
                        color = if (option == selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        }
    }
}

private fun FeedingType.toDisplayTitle(): String = when (this) {
    FeedingType.BOTTLE -> "Bottle Feed"
    FeedingType.SOLID_FOOD -> "Solid Food"
}

private fun FeedingRecord.summaryText(): String = when (type) {
    FeedingType.BOTTLE -> {
        val amountLabel = amountMl?.let { "$it ml" } ?: "Amount not set"
        val milkLabel = milkType?.name?.replace('_', ' ') ?: "Unknown milk"
        "$amountLabel • $milkLabel"
    }

    FeedingType.SOLID_FOOD -> {
        val foodLabel = foodType?.name?.replace('_', ' ') ?: "Unknown food"
        val amountLabel = foodAmount?.name ?: "Unknown amount"
        val acceptanceLabel = acceptance?.name ?: "Unknown response"
        "$foodLabel • $amountLabel • $acceptanceLabel"
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val formatter = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}
