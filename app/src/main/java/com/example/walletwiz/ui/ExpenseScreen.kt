package com.example.walletwiz.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.walletwiz.states.ExpenseState
import com.example.walletwiz.events.ExpenseEvent
import com.example.walletwiz.ui.components.InputField

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