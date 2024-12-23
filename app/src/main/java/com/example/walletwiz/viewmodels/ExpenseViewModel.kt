package com.example.walletwiz.viewmodels

import androidx.lifecycle.ViewModel
import com.example.walletwiz.data.dao.ExpenseDao
import com.example.walletwiz.events.ExpenseEvent
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.walletwiz.states.ExpenseState
import kotlinx.coroutines.flow.update
import com.example.walletwiz.data.entity.Expense
import androidx.lifecycle.viewModelScope
import com.example.walletwiz.data.entity.PaymentMethod
import kotlinx.coroutines.launch
import java.util.Date

class ExpenseViewModel(
    private val expenseDao: ExpenseDao,
): ViewModel() {
    private val _state = MutableStateFlow<ExpenseState>(ExpenseState())
    val state get() = _state

    fun onEvent(event: ExpenseEvent) {
        when(event) {
            ExpenseEvent.CancelExpense -> {
                _state.value = ExpenseState()
            }
            ExpenseEvent.SaveExpense -> {
                val amount = 999.0
                val expenseCategoryId = 1
                val paymentMethod = PaymentMethod.CASH
                val description = "Test vole"
                val createdAt = Date(System.currentTimeMillis())
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