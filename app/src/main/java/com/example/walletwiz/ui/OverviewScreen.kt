/*package com.example.walletwiz.ui

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
*/


//PIE CHART


/*
package com.example.walletwiz.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.walletwiz.states.OverviewState
import com.example.walletwiz.ui.components.PieChart
import com.example.walletwiz.ui.components.PieChartSlice
import com.example.walletwiz.ui.components.toComposeColor // <--- IMPORT THIS
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun OverviewScreen(state: OverviewState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Total Expenses Section
        Text(
            text = "Total Expenses: $%.2f".format(state.totalExpenses),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- NEW: Pie Chart for Categories ---
        // Prepare data for the PieChart
        val pieChartSlices = if (state.totalExpenses > 0) {
            state.expensesByCategory.map { (category, amount) ->
                val percentage = (amount / state.totalExpenses * 100).toFloat()
                PieChartSlice(
                    categoryName = category.name,
                    amount = amount,
                    percentage = percentage,
                    color = category.color?.toComposeColor() ?: MaterialTheme.colorScheme.primary // Fallback color
                )
            }.sortedByDescending { it.amount } // Optional: sort slices by amount for consistent drawing/legend
        } else {
            emptyList()
        }

        PieChart(slices = pieChartSlices, modifier = Modifier.fillMaxWidth().height(300.dp)) // Adjust height as needed

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Expenses List
        Text(
            text = "Recent Expenses",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (state.recentExpenses.isEmpty()) {
            Text("No recent expenses yet. Add some!")
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(state.recentExpenses) { expense ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = expense.description.orEmpty(),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$%.2f".format(expense.amount),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                // Note: ExpenseState only has expenseCategoryId, not the full ExpenseCategory object
                                // You might need to adjust your OverviewState or ExpenseState to hold category name if needed here
                                // For now, we'll just show the payment method and date
                                Text(
                                    text = "${expense.paymentMethod} - ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(expense.createdAt)}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}*/



package com.example.walletwiz.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.walletwiz.states.OverviewState
import com.example.walletwiz.ui.components.PieChart
import com.example.walletwiz.ui.components.PieChartSlice
import com.example.walletwiz.ui.components.toComposeColor
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun OverviewScreen(state: OverviewState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Total Expenses Section
        Text(
            text = "Total Expenses: $%.2f".format(Locale.getDefault(), state.totalExpenses),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Pie Chart for Categories ---
        val pieChartSlices = if (state.totalExpenses > 0) {
            state.expensesByCategory.map { (category, amount) ->
                val percentage = (amount / state.totalExpenses * 100).toFloat()
                PieChartSlice(
                    categoryName = category.name,
                    amount = amount,
                    percentage = percentage,
                    color = category.color?.toComposeColor() ?: MaterialTheme.colorScheme.primary // Fallback color
                )
            }.sortedByDescending { it.amount }
        } else {
            emptyList()
        }

        PieChart(slices = pieChartSlices, modifier = Modifier.fillMaxWidth().height(300.dp))

        Spacer(modifier = Modifier.height(24.dp))

        // All Expenses List Section
        // --- MODIFIED TITLE AND LIST SOURCE ---
        Text(
            text = "All Expenses", // Changed from "Recent Expenses"
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Check against allExpenses now
        if (state.allExpenses.isEmpty()) {
            Text("No expenses recorded yet. Add some!")
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                // Iterate over allExpenses
                items(state.allExpenses) { expense ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = expense.description.orEmpty(),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$%.2f".format(Locale.getDefault(), expense.amount),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${expense.paymentMethod} - ${SimpleDateFormat("MMM dd,yyyy", Locale.getDefault()).format(expense.createdAt)}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}