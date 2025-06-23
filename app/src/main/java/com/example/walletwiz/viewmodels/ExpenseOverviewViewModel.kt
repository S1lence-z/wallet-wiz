package com.example.walletwiz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.walletwiz.data.dao.ExpenseCategoryDao
import com.example.walletwiz.data.dao.ExpenseDao
import com.example.walletwiz.data.entity.Expense
import com.example.walletwiz.data.entity.ExpenseCategory
import com.example.walletwiz.states.ExpenseState
import com.example.walletwiz.states.OverviewState
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import kotlin.collections.associateBy

class ExpenseOverviewViewModel(
    private val expenseDao: ExpenseDao,
    private val expenseCategoryDao: ExpenseCategoryDao
) : ViewModel() {

    private val LOG_TAG = "ExpenseOverviewViewModel"

    private val _state = MutableStateFlow(OverviewState())
    val state: StateFlow<OverviewState> = _state

    init {
        Log.d(LOG_TAG, "ViewModel initialized. Performing initial data refresh.")
        viewModelScope.launch {
            refreshOverviewData()
        }
    }

    suspend fun refreshOverviewData() {
        Log.d(LOG_TAG, "Starting data refresh for overview screen.")
        try {
            val expenses = withContext(Dispatchers.IO) {
                expenseDao.getAllExpenses()
            }
            Log.d(LOG_TAG, "Fetched ${expenses.size} expenses.")

            val categories = withContext(Dispatchers.IO) {
                expenseCategoryDao.getAllExpenseCategories()
            }

            val expenseStates = expenses.map { expense: Expense ->
                ExpenseState(
                   // id = expense.id,
                    amount = expense.amount,
                    expenseCategoryId = expense.expenseCategoryId ?: 0,
                    paymentMethod = expense.paymentMethod,
                    description = expense.description,
                    createdAt = expense.createdAt,
                   // tags = expense.tags
                )
            }

            val categoriesMap = categories.associateBy { category: ExpenseCategory -> category.id!! }

            val total = expenseStates.sumOf { it.amount }

            val expensesByCategory = expenseStates
                .groupBy { it.expenseCategoryId }
                .mapNotNull { (categoryId, expenseList) ->
                    categoriesMap[categoryId]?.let { category ->
                        category to expenseList.sumOf { it.amount }
                    }
                }
                .toMap()

            val allSortedExpenses = expenseStates.sortedByDescending { it.createdAt }
            val recentExpensesLimited = allSortedExpenses.take(5)

            _state.value = OverviewState(
                totalExpenses = total,
                expensesByCategory = expensesByCategory,
                recentExpenses = recentExpensesLimited,
                allExpenses = allSortedExpenses
            )
            Log.d(LOG_TAG, "Overview data refreshed successfully. Total expenses: $total")

        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error during data refresh: ${e.message}", e)
        }
    }

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