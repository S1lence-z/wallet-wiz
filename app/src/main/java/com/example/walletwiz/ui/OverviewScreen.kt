package com.example.walletwiz.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.walletwiz.utils.Currency
import com.example.walletwiz.states.ExpenseState
import com.example.walletwiz.states.OverviewState
import com.example.walletwiz.ui.components.PieChart
import com.example.walletwiz.ui.components.PieChartSlice
import com.example.walletwiz.ui.components.TimePeriodSelector
import com.example.walletwiz.ui.components.toComposeColor
import com.example.walletwiz.utils.formatCurrency
import com.example.walletwiz.viewmodels.ExpenseOverviewViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.walletwiz.ui.components.DeleteConfirmationDialog

@Composable
fun OverviewScreen(
    modifier: Modifier = Modifier,
    state: OverviewState,
    overviewViewModel: ExpenseOverviewViewModel,
    onEditClicked: (ExpenseState) -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var expenseToDeleteState: ExpenseState? by remember { mutableStateOf(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "Total Expenses:",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))

            val displayCurrencyForTotal = state.allExpenses.firstOrNull()?.currency ?: Currency.DEFAULT
            Text(
                text = formatCurrency(state.totalExpenses, displayCurrencyForTotal),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val pieChartSlices = if (state.totalExpenses > 0) {
            state.expensesByCategory.map { (category, amount) ->
                val percentage = (amount / state.totalExpenses * 100).toFloat()
                PieChartSlice(
                    categoryName = category.name,
                    amount = amount,
                    percentage = percentage,
                    color = category.color.toComposeColor()
                )
            }.sortedByDescending { it.amount }
        } else {
            emptyList()
        }

        PieChart(
            slices = pieChartSlices,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        TimePeriodSelector(
            selectedPeriod = state.selectedTimePeriod,
            onPeriodSelected = { period ->
                overviewViewModel.setTimePeriod(period)
            }
        )

        Spacer(modifier = Modifier.height(4.dp))


        if (state.allExpenses.isEmpty()) {
            Text("No expenses recorded for this period.")
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(
                    items = state.allExpenses,
                    key = { expense ->
                        expense.id ?: System.identityHashCode(expense)
                    }
                ) { expense ->
                    ExpenseListItem(
                        expenseState = expense,
                        onEditClicked = onEditClicked,
                        onDeleteClicked = { currentExpenseToDelete ->
                            expenseToDeleteState = currentExpenseToDelete
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }

        if (showDeleteDialog && expenseToDeleteState != null) {
            DeleteConfirmationDialog(
                onConfirm = {
                    expenseToDeleteState?.let { expense ->
                        overviewViewModel.deleteExpense(expense)
                    }
                    showDeleteDialog = false
                    expenseToDeleteState = null
                },
                onDismiss = {
                    showDeleteDialog = false
                    expenseToDeleteState = null
                }
            )
        }
    }
}

@Composable
fun ExpenseListItem(
    expenseState: ExpenseState,
    onEditClicked: (ExpenseState) -> Unit,
    onDeleteClicked: (ExpenseState) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)) {
                Text(
                    text = expenseState.description.orEmpty(),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatCurrency(expenseState.amount, expenseState.currency),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${expenseState.categoryName ?: "Uncategorized"} - ${expenseState.paymentMethod.toReadableString()} - ${formatDate(expenseState.createdAt.time)}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "More options for ${expenseState.description.orEmpty()}"
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            onEditClicked(expenseState)
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            onDeleteClicked(expenseState)
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    if (timestamp == 0L) return "N/A"
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}