package com.example.walletwiz.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
        BasicTextField(
            value = state.amount.toString(),
            onValueChange = {
                val amount = it.toDoubleOrNull() ?: 0.0
                onEvent(ExpenseEvent.SetAmount(amount))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    if (state.amount == 0.0) {
                        Text("Enter amount", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                    innerTextField()
                }
            }
        )

        // Description Input
        BasicTextField(
            value = state.description ?: "",
            onValueChange = {
                onEvent(ExpenseEvent.SetDescription(it))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    if (state.description.isNullOrEmpty()) {
                        Text("Enter description", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                    innerTextField()
                }
            }
        )

        // Save Button
        Button(
            onClick = { onEvent(ExpenseEvent.SaveExpense) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Save Expense")
        }

        // Cancel Button
        TextButton(
            onClick = { onEvent(ExpenseEvent.CancelExpense) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Cancel")
        }
    }
}
