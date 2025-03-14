package com.example.walletwiz.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.walletwiz.states.ExpenseState
import com.example.walletwiz.states.OverviewState


@Composable
fun OverviewScreen(state: OverviewState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Header Section
        Text(
            text = "Expense Overview",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp, top = 16.dp)
        )

        // Expense List Section
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Assuming state.recentExpenses holds the list of expenses
            items(state.recentExpenses) { expense ->
                ExpenseItem(expense)
            }
        }
    }
}

@Composable
fun ExpenseItem(expense: ExpenseState) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Amount: \$${expense.amount}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Description: ${expense.description.orEmpty()}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Category ID: ${expense.expenseCategoryId}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Payment Method: ${expense.paymentMethod.name}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Created At: ${expense.createdAt}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
