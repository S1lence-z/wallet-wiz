package com.example.walletwiz.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.walletwiz.states.ExpenseState
import com.example.walletwiz.events.ExpenseEvent
import com.example.walletwiz.ui.components.*

@Composable
fun ExpenseScreen(
    state: ExpenseState,
    onEvent: (ExpenseEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Header Section
        Text(
            text = "Add Expense",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp, top = 16.dp)
        )

        // Section for all the input fields
        Column(
            modifier = Modifier.fillMaxWidth(),
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
                onValueChange = { onEvent(ExpenseEvent.SetDescription(it)) }
            )

            // TODO: Add a picker for the expense category
            Text(
                text = "TODO: Expense Category Picker",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
            )

            // Payment Method Input
            PaymentMethodDropdown(
                selectedMethod = state.paymentMethod,
                onMethodSelected = { onEvent(ExpenseEvent.SetPaymentMethod(it)) }
            )

            // Created At Input
            DateSelector(
                onDateSelected = { onEvent(ExpenseEvent.SetCreatedAt(it)) }
            )

            // Save Button Section
            Button(
                onClick = { onEvent(ExpenseEvent.SaveExpense) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                enabled = isNewExpenseValid(state)
            ) {
                Text("Save Expense")
            }
        }
    }
}

private fun isNewExpenseValid(state: ExpenseState): Boolean {
    return state.amount > 0.0
}