package com.babyfeeding.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.babyfeeding.data.model.Acceptance
import com.babyfeeding.data.model.FeedingRecord
import com.babyfeeding.data.model.FeedingType
import com.babyfeeding.data.model.FoodAmount
import com.babyfeeding.data.model.FoodType
import com.babyfeeding.data.model.MilkType
import com.babyfeeding.data.repository.FeedingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FeedingRepository
) : ViewModel() {
    private val _records = MutableStateFlow<List<FeedingRecord>>(emptyList())
    val records: StateFlow<List<FeedingRecord>> = _records.asStateFlow()

    init {
        loadRecords()
    }

    fun loadRecords() {
        viewModelScope.launch {
            runCatching {
                repository.getRecords().first()
                    .filterNot { it.deleted }
                    .sortedByDescending { it.timestamp }
            }.onSuccess { loadedRecords ->
                _records.value = loadedRecords
            }
        }
    }

    fun addBottleFeed(
        babyId: String,
        amountMl: Int,
        milkType: MilkType,
        note: String? = null
    ) {
        val now = System.currentTimeMillis()
        val record = FeedingRecord(
            babyId = babyId,
            type = FeedingType.BOTTLE,
            timestamp = now,
            amountMl = amountMl,
            milkType = milkType,
            note = note?.takeIf { it.isNotBlank() },
            createdAt = now,
            updatedAt = now
        )

        submitRecord(record)
    }

    fun addSolidFood(
        babyId: String,
        foodType: FoodType,
        foodAmount: FoodAmount,
        acceptance: Acceptance,
        note: String? = null
    ) {
        val now = System.currentTimeMillis()
        val record = FeedingRecord(
            babyId = babyId,
            type = FeedingType.SOLID_FOOD,
            timestamp = now,
            foodType = foodType,
            foodAmount = foodAmount,
            acceptance = acceptance,
            note = note?.takeIf { it.isNotBlank() },
            createdAt = now,
            updatedAt = now
        )

        submitRecord(record)
    }

    private fun submitRecord(record: FeedingRecord) {
        viewModelScope.launch {
            runCatching {
                repository.createRecord(record).first()
            }.onSuccess {
                loadRecords()
            }
        }
    }
}
