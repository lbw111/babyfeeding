package com.babyfeeding.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.babyfeeding.data.model.Acceptance
import com.babyfeeding.data.model.FoodAmount
import com.babyfeeding.data.model.FoodType

@Composable
fun SolidFoodForm(
    onCancel: () -> Unit,
    onSave: (
        foodType: FoodType,
        foodAmount: FoodAmount,
        acceptance: Acceptance,
        note: String?
    ) -> Unit
) {
    var selectedFoodType by remember { mutableStateOf(FoodType.RICE_CEREAL) }
    var selectedFoodAmount by remember { mutableStateOf(FoodAmount.SMALL) }
    var selectedAcceptance by remember { mutableStateOf(Acceptance.LIKED) }
    var note by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ChineseOptionSelector(
            label = "食物类型",
            options = FoodType.entries,
            selected = selectedFoodType,
            optionLabel = {
                when (it) {
                    FoodType.RICE_CEREAL -> "米粉"
                    FoodType.FRUIT_PUREE -> "果泥"
                    FoodType.VEGETABLE_PUREE -> "菜泥"
                    FoodType.MEAT_PUREE -> "肉泥"
                    FoodType.OTHER -> "其他"
                }
            },
            onSelect = { selectedFoodType = it }
        )

        ChineseOptionSelector(
            label = "食量",
            options = FoodAmount.entries,
            selected = selectedFoodAmount,
            optionLabel = {
                when (it) {
                    FoodAmount.SMALL -> "少量"
                    FoodAmount.MEDIUM -> "中等"
                    FoodAmount.LARGE -> "大量"
                }
            },
            onSelect = { selectedFoodAmount = it }
        )

        ChineseOptionSelector(
            label = "接受度",
            options = Acceptance.entries,
            selected = selectedAcceptance,
            optionLabel = {
                when (it) {
                    Acceptance.LIKED -> "喜欢吃"
                    Acceptance.OKAY -> "一般"
                    Acceptance.REFUSED -> "拒绝"
                }
            },
            onSelect = { selectedAcceptance = it }
        )

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("备注") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel) {
                Text("取消")
            }
            TextButton(
                onClick = {
                    onSave(
                        selectedFoodType,
                        selectedFoodAmount,
                        selectedAcceptance,
                        note.takeIf { it.isNotBlank() }
                    )
                }
            ) {
                Text("保存")
            }
        }
    }
}

@Composable
private fun <T> ChineseOptionSelector(
    label: String,
    options: List<T>,
    selected: T,
    optionLabel: (T) -> String,
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
                        text = optionLabel(option),
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
