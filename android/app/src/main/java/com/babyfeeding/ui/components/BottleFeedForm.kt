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
import com.babyfeeding.data.model.MilkType

@Composable
fun BottleFeedForm(
    onCancel: () -> Unit,
    onSave: (amountMl: Int, milkType: MilkType, note: String?) -> Unit
) {
    var amountText by remember { mutableStateOf("120") }
    var selectedMilkType by remember { mutableStateOf(MilkType.BREAST_MILK) }
    var note by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it },
            label = { Text("奶量 (ml)") },
            modifier = Modifier.fillMaxWidth()
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "奶类型",
                style = MaterialTheme.typography.labelLarge
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MilkType.entries.forEach { option ->
                    val label = when (option) {
                        MilkType.BREAST_MILK -> "母乳"
                        MilkType.FORMULA -> "奶粉"
                    }
                    OutlinedButton(onClick = { selectedMilkType = option }) {
                        Text(
                            text = label,
                            color = if (option == selectedMilkType) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        }

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
                        amountText.toIntOrNull() ?: 0,
                        selectedMilkType,
                        note.takeIf { it.isNotBlank() }
                    )
                }
            ) {
                Text("保存")
            }
        }
    }
}
