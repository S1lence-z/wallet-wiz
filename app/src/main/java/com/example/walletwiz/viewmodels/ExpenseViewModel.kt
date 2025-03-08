package com.example.walletwiz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletwiz.data.dao.ExpenseDao
import com.example.walletwiz.data.dao.ExpenseCategoryDao
import com.example.walletwiz.data.entity.Expense
import com.example.walletwiz.data.entity.ExpenseCategory
import com.example.walletwiz.events.ExpenseEvent
import com.example.walletwiz.states.ExpenseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExpenseViewModel(
    private val expenseDao: ExpenseDao,
    private val expenseCategoryDao: ExpenseCategoryDao
) : ViewModel() {

    private val _state = MutableStateFlow(ExpenseState())
    val state get() = _state

    init {
        loadCategories()
    }

    fun onEvent(event: ExpenseEvent) {
        when (event) {
            ExpenseEvent.CancelExpense -> {
                _state.value = ExpenseState()
            }
            ExpenseEvent.SaveExpense -> {
                val state = _state.value
                val newExpense = Expense(
                    amount = state.amount,
                    expenseCategoryId = state.expenseCategoryId,
                    createdAt = state.createdAt,
                    description = state.description,
                    paymentMethod = state.paymentMethod
                )
                viewModelScope.launch {
                    expenseDao.insertExpense(newExpense)
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
            is ExpenseEvent.CreateExpenseCategory -> {
                createNewCategory(event.name)
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val categories = expenseCategoryDao.getAllCategories()
            _state.update { it.copy(categories = categories) }
        }
    }

    private fun createNewCategory(name: String) {
        viewModelScope.launch {
            val newCategory = ExpenseCategory(name = name, description = null, color = "#000000")
            val id = expenseCategoryDao.insert(newCategory).toInt()
            val updatedCategory = newCategory.copy(id = id)
            _state.update {
                it.copy(
                    categories = it.categories + updatedCategory,
                    expenseCategoryId = id
                )
            }
        }
    }
}
