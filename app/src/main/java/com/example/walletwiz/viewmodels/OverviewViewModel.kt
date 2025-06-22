/*package com.example.walletwiz.viewmodels

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
import kotlinx.coroutines.launch

class ExpenseOverviewViewModel(
    private val expenseDao: ExpenseDao,
    private val expenseCategoryDao: ExpenseCategoryDao
) : ViewModel() {

    private val _state = MutableStateFlow(OverviewState())
    val state: StateFlow<OverviewState> = _state

    init {
        loadExpenses()
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            val expenses = expenseDao.getAllExpenses().map {
                ExpenseState(
                    amount = it.amount,
                    expenseCategoryId = it.expenseCategoryId,
                    paymentMethod = it.paymentMethod,
                    description = it.description,
                    createdAt = it.createdAt
                )
            }

            val categories = expenseCategoryDao.getAllExpenseCategories().associateBy { it.id ?: 0 }
            val expensesByCategory = expenses.groupBy { it.expenseCategoryId }
                .mapValues { (_, expenses) -> expenses.sumOf { it.amount } }

            _state.value = OverviewState(
                totalExpenses = expenses.sumOf { it.amount },
                expensesByCategory = expensesByCategory.mapKeys { categories[it.key] ?: error("Category not found") },
                recentExpenses = expenses
            )
        }
    }

    // ViewModel factory
    class Factory(
        private val expenseDao: ExpenseDao,
        private val expenseCategoryDao: ExpenseCategoryDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ExpenseOverviewViewModel(expenseDao, expenseCategoryDao) as T
        }
    }
}*/


//SEC
// app/src/main/java/com/example/walletwiz/viewmodels/ExpenseOverviewViewModel.kt
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

            _state.value = OverviewState(
                totalExpenses = total,
                expensesByCategory = expensesByCategory,
                recentExpenses = recentExpenses
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
import com.example.walletwiz.data.entity.Expense
import com.example.walletwiz.data.entity.ExpenseCategory
import com.example.walletwiz.data.entity.PaymentMethod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import com.example.walletwiz.states.OverviewState
import com.example.walletwiz.states.ExpenseState


class ExpenseOverviewViewModel(
    private val expenseDao: ExpenseDao,
    private val expenseCategoryDao: ExpenseCategoryDao
) : ViewModel() {

    private val _state = MutableStateFlow(OverviewState())
    val state: StateFlow<OverviewState> get() = _state

    init {
        viewModelScope.launch {
            populateDatabase()
            loadExpenses()
        }
    }

    fun populateDatabase() {
        viewModelScope.launch {
            // Insert categories and retrieve their IDs
            val foodCategoryId = expenseCategoryDao.insertExpenseCategory(
                ExpenseCategory(name = "Food", description = "Restaurant and groceries", color = "#FF5733")
            ).toInt()

            val travelCategoryId = expenseCategoryDao.insertExpenseCategory(
                ExpenseCategory(name = "Travel", description = "Transportation expenses", color = "#33FF57")
            ).toInt()

            // Insert expenses using the retrieved IDs
            expenseDao.insertExpense(
                Expense(amount = 50.0, expenseCategoryId = foodCategoryId, paymentMethod = PaymentMethod.CASH, description = "Dinner", createdAt = Date())
            )
            expenseDao.insertExpense(
                Expense(amount = 120.0, expenseCategoryId = travelCategoryId, paymentMethod = PaymentMethod.CREDIT_CARD, description = "Taxi Ride", createdAt = Date())
            )
        }
    }


    private fun loadExpenses() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val expenses = expenseDao.getAllExpenses()
                val total = expenses.sumOf { it.amount }
                val categories = expenseCategoryDao.getAllExpenseCategories().associateBy { it.id }
                val expensesByCategory = expenses.groupBy { it.expenseCategoryId }
                    .mapNotNull { (categoryId, expenses) ->
                        categories[categoryId]?.let { category ->
                            category to expenses.sumOf { it.amount }
                        }
                    }.toMap()

                _state.update {
                    it.copy(
                        totalExpenses = total,
                        expensesByCategory = expensesByCategory,
                        recentExpenses = expenses
                            .sortedByDescending { it.createdAt }
                            .take(5)
                            .map { expense ->
                                ExpenseState(
                                    amount = expense.amount,
                                    expenseCategoryId = expense.expenseCategoryId,
                                    paymentMethod = expense.paymentMethod,
                                    description = expense.description,
                                    createdAt = expense.createdAt
                                )
                            },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}
*/
