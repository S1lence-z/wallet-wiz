package com.example.walletwiz.viewmodels

import androidx.lifecycle.ViewModel
import com.example.walletwiz.data.dao.ExpenseDao
import com.example.walletwiz.events.ExpenseEvent
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.walletwiz.states.ExpenseState
import kotlinx.coroutines.flow.update
import com.example.walletwiz.data.entity.Expense
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val expenseDao: ExpenseDao,
): ViewModel() {
    private val _state = MutableStateFlow<ExpenseState>(ExpenseState())

    fun onEvent(event: ExpenseEvent) {
        when(event) {
            ExpenseEvent.CancelExpense -> {
                _state.value = ExpenseState()
            }
            ExpenseEvent.SaveExpense -> {
                val amount = _state.value.amount
                val expenseCategoryId = _state.value.expenseCategoryId
                val paymentMethod = _state.value.paymentMethod
                val description = _state.value.description
                val createdAt = _state.value.createdAt
                // Check if all data is present
                if (amount == 0.0 || expenseCategoryId == 0 || description == null) {
                    return
                }
                val newExpense = Expense(
                    amount = amount,
                    expenseCategoryId = expenseCategoryId,
                    paymentMethod = paymentMethod,
                    description = description,
                    createdAt = createdAt
                )
                viewModelScope.launch {
                    expenseDao.insertExpense(newExpense)
                    _state.value = ExpenseState()
                }
            }
            is ExpenseEvent.SetAmount -> {
                _state.update { it.copy(amount = event.amount) }
            }
            is ExpenseEvent.SetExpenseCategory -> {
                _state.update { it.copy(expenseCategoryId = event.expenseCategoryId) }
            }
            is ExpenseEvent.SetCreatedAt -> {
                _state.update { it.copy(createdAt = event.createdAt) }
            }
            is ExpenseEvent.SetDescription -> {
                _state.update { it.copy(description = event.description) }
            }
            is ExpenseEvent.SetPaymentMethod -> {
                _state.update { it.copy(paymentMethod = event.paymentMethod) }
            }
        }
    }
}