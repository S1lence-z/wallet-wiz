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
import kotlinx.coroutines.Dispatchers
import android.util.Log // ✅ Import Log for debugging


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
                saveExpense()
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
                Log.d("ExpenseViewModel", "CreateExpenseCategory event received: ${event.name}")
                createNewCategory(event.name)
            }
        }
    }

    private fun saveExpense() {
        val state = _state.value
        if (state.amount > 0 && state.expenseCategoryId != null) {
            val newExpense = Expense(
                amount = state.amount,
                expenseCategoryId = state.expenseCategoryId,
                createdAt = state.createdAt,
                description = state.description,
                paymentMethod = state.paymentMethod
            )
            viewModelScope.launch(Dispatchers.IO) {
                expenseDao.insertExpense(newExpense)
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            val categories = expenseCategoryDao.getAllCategories()
            _state.update { it.copy(categories = categories) }
        }
    }

    private fun createNewCategory(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newCategory = ExpenseCategory(name = name, description = null, color = "#000000")
            val id = expenseCategoryDao.insert(newCategory).toInt()

            Log.d("ExpenseViewModel", "Inserted Category ID: $id")  // ✅ Log in Logcat
            println("Inserted Category ID: $id")  // ✅ Print in terminal (for testing)

            if (id > 0) {
                val updatedCategory = newCategory.copy(id = id)

                _state.update { currentState ->
                    currentState.copy(
                        categories = currentState.categories + updatedCategory,  // ✅ Update UI immediately
                        expenseCategoryId = id  // ✅ Set newly created category as selected
                    )
                }

                loadCategories() // ✅ Reload from DB to ensure persistence
            }
            else {
                Log.e("ExpenseViewModel", "Failed to insert category: $name")
            }
        }
    }

}
