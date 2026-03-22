package com.babyfeeding.ui.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.babyfeeding.data.model.FeedingType
import com.babyfeeding.ui.home.HomeViewModel
import java.util.Calendar

@Composable
fun StatsScreen(
    innerPadding: PaddingValues = PaddingValues(),
    viewModel: HomeViewModel = hiltViewModel()
) {
    val records by viewModel.records.collectAsState()
    val startOfTodayMillis = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    val todayRecords = records.filter { it.timestamp >= startOfTodayMillis }
    val todayFeedCount = todayRecords.size
    val bottleRecords = todayRecords.filter { it.type == FeedingType.BOTTLE && it.amountMl != null }
    val totalMilk = bottleRecords.sumOf { it.amountMl ?: 0 }
    val averageMilk = if (bottleRecords.isNotEmpty()) totalMilk / bottleRecords.size else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatsCard(
            title = "Today's Feeding Count",
            value = todayFeedCount.toString(),
            subtitle = "Bottle and solid food records combined"
        )
        StatsCard(
            title = "Total Milk Today",
            value = "$totalMilk ml",
            subtitle = "Bottle feeds only"
        )
        StatsCard(
            title = "Average Bottle Feed",
            value = "$averageMilk ml",
            subtitle = "Average across today's bottle feeds"
        )
    }
}

@Composable
private fun StatsCard(
    title: String,
    value: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
