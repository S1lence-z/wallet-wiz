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
    onEvent: (ExpenseEvent) -> Unit,
    onSaveClicked: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = if (state.id != null) "Edit Expense" else "Add Expense",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp, top = 16.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InputField(
                label = "Enter amount",
                value = if (state.amount == 0.0) "" else state.amount.toString(),
                onValueChange = {
                    val amount = it.toDoubleOrNull() ?: 0.0
                    onEvent(ExpenseEvent.SetAmount(amount))
                }
            )

            InputField(
                label = "Enter description",
                value = state.description.orEmpty(),
                onValueChange = { onEvent(ExpenseEvent.SetDescription(it)) }
            )

            ExpenseCategoryDropdown(
                categories = state.categories,
                selectedCategoryId = state.expenseCategoryId,
                onCategorySelected = { onEvent(ExpenseEvent.SetExpenseCategory(it)) },
                onNewCategoryCreated = { onEvent(ExpenseEvent.CreateExpenseCategory(it)) }
            )

            PaymentMethodDropdown(
                selectedMethod = state.paymentMethod,
                onMethodSelected = { onEvent(ExpenseEvent.SetPaymentMethod(it)) }
            )

            TagInput(
                tags = state.selectedExpenseWithTags?.tags ?: state.selectedTags,
                onTagAdded = { tag -> onEvent(ExpenseEvent.AddTagToExpense(state.selectedExpenseWithTags?.expense?.id ?: 0, tag)) },
                onTagRemoved = { tag -> onEvent(ExpenseEvent.RemoveTagFromExpense(state.selectedExpenseWithTags?.expense?.id ?: 0, tag.id ?: 0)) }
            )

            DateSelector(
                initialDate = state.createdAt,
                onDateSelected = { onEvent(ExpenseEvent.SetCreatedAt(it)) }
            )

            Button(
                onClick = {
                    onEvent(ExpenseEvent.SaveExpense)
                    onSaveClicked()
                          },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.amount > 0
            ) {
                Text(if (state.id != null) "Update Expense" else "Save Expense")
            }
        }
    }
}