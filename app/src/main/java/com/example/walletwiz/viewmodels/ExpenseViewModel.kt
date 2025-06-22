/*package com.example.walletwiz.viewmodels

import androidx.lifecycle.ViewModel
import com.example.walletwiz.data.dao.ExpenseDao
import com.example.walletwiz.events.ExpenseEvent
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.walletwiz.states.ExpenseState
import kotlinx.coroutines.flow.update
import com.example.walletwiz.data.entity.Expense
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ExpenseViewModel (
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
        }
    }
}*/


// SIMONS FIRST REVISION
/*
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
import android.util.Log

class ExpenseViewModel (
    private val expenseDao: ExpenseDao,
): ViewModel() {

    // Define a LOG_TAG for this ViewModel
    private val LOG_TAG = "ExpenseViewModel"

    private val _state = MutableStateFlow<ExpenseState>(ExpenseState())
    val state get() = _state

    fun onEvent(event: ExpenseEvent) {
        when(event) {
            ExpenseEvent.CancelExpense -> {
                Log.d(LOG_TAG, "ExpenseEvent: CancelExpense received. Resetting state.")
                _state.value = ExpenseState()
            }
            ExpenseEvent.SaveExpense -> {
                val state = _state.value
                // Validate if the expense can be saved based on current state.
                // You might want to move this validation logic directly here or use it to enable/disable the save button.
                if (state.amount <= 0.0) {
                    Log.w(LOG_TAG, "Attempted to save expense with invalid amount: ${state.amount}. Aborting save.")
                    // Optionally, you might want to show a message to the user here.
                    return
                }

                val newExpense = Expense(
                    amount = state.amount,
                    expenseCategoryId = state.expenseCategoryId,
                    createdAt = state.createdAt,
                    description = state.description,
                    paymentMethod = state.paymentMethod
                )

                // Log the expense details before attempting to save
                Log.d(LOG_TAG, "ExpenseEvent: SaveExpense received. Preparing to save new expense:")
                Log.d(LOG_TAG, "  Amount: ${newExpense.amount}")
                Log.d(LOG_TAG, "  Category ID: ${newExpense.expenseCategoryId}")
                Log.d(LOG_TAG, "  Description: ${newExpense.description}")
                Log.d(LOG_TAG, "  Payment Method: ${newExpense.paymentMethod}")
                Log.d(LOG_TAG, "  Created At: ${newExpense.createdAt}")


                viewModelScope.launch {
                    try {
                        expenseDao.insertExpense(newExpense)
                        Log.i(LOG_TAG, "Expense saved successfully: $newExpense")
                        // After successful save, you might want to clear the form or navigate back
                        _state.update { ExpenseState() } // Clear the state after saving
                    } catch (e: Exception) {
                        Log.e(LOG_TAG, "Error saving expense: ${e.message}", e)
                        // Handle the error (e.g., show a toast message to the user)
                    }
                }
            }
            is ExpenseEvent.SetAmount -> {
                _state.update { it.copy(amount = event.amount) }
                Log.v(LOG_TAG, "SetAmount: ${event.amount}")
            }
            is ExpenseEvent.SetExpenseCategory -> {
                _state.update { it.copy(expenseCategoryId = event.expenseCategoryId) }
                Log.v(LOG_TAG, "SetExpenseCategory: ${event.expenseCategoryId}")
            }
            is ExpenseEvent.SetCreatedAt -> {
                _state.update { it.copy(createdAt = event.createdAt) }
                Log.v(LOG_TAG, "SetCreatedAt: ${event.createdAt}")
            }
            is ExpenseEvent.SetDescription -> {
                _state.update { it.copy(description = event.description) }
                Log.v(LOG_TAG, "SetDescription: ${event.description}")
            }
            is ExpenseEvent.SetPaymentMethod -> {
                _state.update { it.copy(paymentMethod = event.paymentMethod) }
                Log.v(LOG_TAG, "SetPaymentMethod: ${event.paymentMethod}")
            }
        }
    }
}*/


//SEC

// app/src/main/java/com/example/walletwiz/viewmodels/ExpenseViewModel.kt
package com.example.walletwiz.viewmodels

import androidx.lifecycle.ViewModel
import com.example.walletwiz.data.dao.ExpenseDao
import com.example.walletwiz.data.dao.ExpenseCategoryDao
import com.example.walletwiz.events.ExpenseEvent
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.walletwiz.states.ExpenseState
import kotlinx.coroutines.flow.update
import com.example.walletwiz.data.entity.Expense
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.util.Log
import com.example.walletwiz.data.entity.ExpenseCategory
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow // <--- Import this
import kotlinx.coroutines.Dispatchers // <--- Import this
import kotlinx.coroutines.withContext // <--- Import this

class ExpenseViewModel (
    private val expenseDao: ExpenseDao,
    private val expenseCategoryDao: ExpenseCategoryDao,
): ViewModel() {

    private val LOG_TAG = "ExpenseViewModel"

    private val _state = MutableStateFlow<ExpenseState>(ExpenseState())
    val state get() = _state

    // <--- MODIFIED: Use MutableStateFlow for categories and populate it in an init block
    private val _categories = MutableStateFlow<List<ExpenseCategory>>(emptyList())
    val categories: StateFlow<List<ExpenseCategory>> = _categories.asStateFlow()

    init {
        // Fetch categories when the ViewModel is initialized
        viewModelScope.launch {
            // Ensure this blocking database call runs on a background thread
            withContext(Dispatchers.IO) {
                try {
                    val allCategories = expenseCategoryDao.getAllExpenseCategories()
                    _categories.value = allCategories // Update the MutableStateFlow
                    Log.d(LOG_TAG, "Fetched categories: ${allCategories.size}")
                } catch (e: Exception) {
                    Log.e(LOG_TAG, "Error fetching categories: ${e.message}", e)
                    // Handle error (e.g., show a toast, empty list)
                }
            }
        }
    }


    fun onEvent(event: ExpenseEvent) {
        when(event) {
            ExpenseEvent.CancelExpense -> {
                Log.d(LOG_TAG, "ExpenseEvent: CancelExpense received. Resetting state.")
                _state.value = ExpenseState()
            }
            ExpenseEvent.SaveExpense -> {
                val state = _state.value
                if (state.amount <= 0.0 || state.expenseCategoryId == null || state.expenseCategoryId <= 0) {
                    Log.w(LOG_TAG, "Attempted to save expense with invalid data. Amount: ${state.amount}, Category ID: ${state.expenseCategoryId}. Aborting save.")
                    return
                }

                val newExpense = Expense(
                    amount = state.amount,
                    expenseCategoryId = state.expenseCategoryId,
                    createdAt = state.createdAt,
                    description = state.description,
                    paymentMethod = state.paymentMethod
                )

                Log.d(LOG_TAG, "ExpenseEvent: SaveExpense received. Preparing to save new expense:")
                Log.d(LOG_TAG, "  Amount: ${newExpense.amount}")
                Log.d(LOG_TAG, "  Category ID: ${newExpense.expenseCategoryId}")
                Log.d(LOG_TAG, "  Description: ${newExpense.description}")
                Log.d(LOG_TAG, "  Payment Method: ${newExpense.paymentMethod}")
                Log.d(LOG_TAG, "  Created At: ${newExpense.createdAt}")


                viewModelScope.launch {
                    try {
                        // Ensure this blocking database call runs on a background thread
                        withContext(Dispatchers.IO) {
                            expenseDao.insertExpense(newExpense)
                        }
                        Log.i(LOG_TAG, "Expense saved successfully: $newExpense")
                        _state.update { ExpenseState() } // Clear the state after saving
                    } catch (e: Exception) {
                        Log.e(LOG_TAG, "Error saving expense: ${e.message}", e)
                    }
                }
            }
            is ExpenseEvent.SetAmount -> {
                _state.update { it.copy(amount = event.amount) }
                Log.v(LOG_TAG, "SetAmount: ${event.amount}")
            }
            is ExpenseEvent.SetExpenseCategory -> {
                _state.update { it.copy(expenseCategoryId = event.expenseCategoryId) }
                Log.v(LOG_TAG, "SetExpenseCategory: ${event.expenseCategoryId}")
            }
            is ExpenseEvent.SetCreatedAt -> {
                _state.update { it.copy(createdAt = event.createdAt) }
                Log.v(LOG_TAG, "SetCreatedAt: ${event.createdAt}")
            }
            is ExpenseEvent.SetDescription -> {
                _state.update { it.copy(description = event.description) }
                Log.v(LOG_TAG, "SetDescription: ${event.description}")
            }
            is ExpenseEvent.SetPaymentMethod -> {
                _state.update { it.copy(paymentMethod = event.paymentMethod) }
                Log.v(LOG_TAG, "SetPaymentMethod: ${event.paymentMethod}")
            }
        }
    }
}