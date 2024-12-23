package com.example.walletwiz.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.walletwiz.states.ExpenseState
import com.example.walletwiz.events.ExpenseEvent

@Composable
fun ExpenseScreen(
    state: ExpenseState,
    onEvent: (ExpenseEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Amount Input
        InputField(
            label = "Enter amount",
            value = if (state.amount == 0.0) "" else state.amount.toString(),
            onValueChange = {
                val amount = it.toDoubleOrNull() ?: 0.0
                onEvent(ExpenseEvent.SetAmount(amount))
            }
        )

        // Description Input
        InputField(
            label = "Enter description",
            value = state.description.orEmpty(),
            onValueChange = {
                onEvent(ExpenseEvent.SetDescription(it))
            }
        )

        // Save Button
        Button(
            onClick = { onEvent(ExpenseEvent.SaveExpense) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Expense")
        }
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = label,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                innerTextField()
            }
        }
    )
}
