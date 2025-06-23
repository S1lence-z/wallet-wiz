package com.example.walletwiz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.walletwiz.data.dao.ExpenseCategoryDao
import com.example.walletwiz.data.dao.ExpenseDao
import com.example.walletwiz.data.entity.Expense
import com.example.walletwiz.data.entity.ExpenseCategory
import com.example.walletwiz.data.entity.PaymentMethod
import com.example.walletwiz.states.ExpenseState
import com.example.walletwiz.states.OverviewState
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Dispatchers // <--- IMPORT THIS
import kotlinx.coroutines.withContext // <--- IMPORT THIS
import android.util.Log // <--- IMPORT THIS for logging

class ExpenseOverviewViewModel(
    private val expenseDao: ExpenseDao,
    private val expenseCategoryDao: ExpenseCategoryDao
) : ViewModel() {

    private val LOG_TAG = "ExpenseOverviewViewModel" // Log tag for this ViewModel

    private val _state = MutableStateFlow(OverviewState())
    val state: StateFlow<OverviewState> = _state

    init {
        // Initial load of data when the ViewModel is created
        Log.d(LOG_TAG, "ViewModel initialized. Performing initial data refresh.")
        viewModelScope.launch {
            refreshOverviewData()
        }
    }

    /**
     * Refreshes all overview data by re-fetching from the database.
     * This function should be called whenever the underlying data might have changed
     * (e.g., after adding/deleting an expense).
     */
    suspend fun refreshOverviewData() {
        Log.d(LOG_TAG, "Starting data refresh for overview screen.")
        // Optionally, you can set a loading state here if your OverviewState has one
        // _state.update { it.copy(isLoading = true) }

        try {
            // All DAO calls should be on an IO dispatcher as they are blocking operations.
            val expenses = withContext(Dispatchers.IO) {
                expenseDao.getAllExpenses()
            }
            Log.d(LOG_TAG, "Fetched ${expenses.size} expenses.")

            val categories = withContext(Dispatchers.IO) {
                expenseCategoryDao.getAllExpenseCategories()
            }
            Log.d(LOG_TAG, "Fetched ${categories.size} categories.")

            val expenseStates = expenses.map {
                ExpenseState(
                    amount = it.amount,
                    expenseCategoryId = it.expenseCategoryId,
                    paymentMethod = it.paymentMethod,
                    description = it.description,
                    createdAt = it.createdAt
                )
            }

            val categoriesMap = categories.associateBy { it.id } // Map for quick category lookup

            val total = expenseStates.sumOf { it.amount }

            val expensesByCategory = expenseStates
                .groupBy { it.expenseCategoryId }
                .mapNotNull { (categoryId, expenseList) ->
                    categoriesMap[categoryId]?.let { category -> // Ensure category exists
                        category to expenseList.sumOf { it.amount }
                    }
                }
                .toMap()

            val recentExpenses = expenseStates
                .sortedByDescending { it.createdAt }
                .take(5) // Taking top 5 recent expenses

            // --- MODIFIED LOGIC HERE ---
            val allSortedExpenses = expenseStates.sortedByDescending { it.createdAt }
            val recentExpensesLimited = allSortedExpenses.take(5) // Still keep a 'recent' list if desired

            _state.value = OverviewState(
                totalExpenses = total,
                expensesByCategory = expensesByCategory,
                //recentExpenses = recentExpenses,
                recentExpenses = recentExpensesLimited,
                allExpenses = allSortedExpenses
            )
            Log.d(LOG_TAG, "Overview data refreshed successfully. Total expenses: $total")

        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error during data refresh: ${e.message}", e)
            // Handle error, e.g., set an error state or show a message
            // _state.update { it.copy(isError = true) }
        } finally {
            // _state.update { it.copy(isLoading = false) } // If you used a loading state
        }
    }

    // ViewModel factory (remains unchanged)
    class Factory(
        private val expenseDao: ExpenseDao,
        private val expenseCategoryDao: ExpenseCategoryDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ExpenseOverviewViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ExpenseOverviewViewModel(expenseDao, expenseCategoryDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}


// REALLY OLD
/*package com.example.walletwiz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletwiz.data.dao.ExpenseDao
import com.example.walletwiz.data.dao.ExpenseCategoryDao
import com.example.walletwiz.states.OverviewState
import kotlinx.coroutines.flow.MutableStateFlow

class OverviewViewModel(
    private val expenseDao: ExpenseDao,
    private val expenseCategoryDao: ExpenseCategoryDao
) : ViewModel() {
    private val _state = MutableStateFlow(OverviewState())
    val state get() = _state
}