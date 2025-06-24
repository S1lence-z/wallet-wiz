package com.example.walletwiz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletwiz.data.dao.ExpenseCategoryDao
import com.example.walletwiz.data.dao.ExpenseDao
import com.example.walletwiz.data.entity.Expense
import com.example.walletwiz.states.ExpenseState
import com.example.walletwiz.states.OverviewState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExpenseOverviewViewModel(
    private val expenseDao: ExpenseDao,
    expenseCategoryDao: ExpenseCategoryDao
) : ViewModel() {

    val state: StateFlow<OverviewState> = combine(
        expenseDao.getAllExpenses(),
        expenseCategoryDao.getAllExpenseCategories()
    ) { expensesList, categoriesList ->

        val categoryIdToNameMap = categoriesList
            .filter { it.id != null }
            .associate { it.id!! to it.name }

        val expenseStates = expensesList.map { expenseEntity ->
            val resolvedCategoryName = expenseEntity.expenseCategoryId?.let { catId ->
                categoryIdToNameMap[catId]
            } ?: "Uncategorized"

            ExpenseState(
                id = expenseEntity.id,
                amount = expenseEntity.amount,
                expenseCategoryId = expenseEntity.expenseCategoryId ?: 0,
                categoryName = resolvedCategoryName,
                paymentMethod = expenseEntity.paymentMethod,
                description = expenseEntity.description,
                createdAt = expenseEntity.createdAt,
            )
        }

        val categoriesMapForPieChart = categoriesList
            .filter { it.id != null }
            .associateBy { it.id!! }

        val total = expenseStates.sumOf { it.amount }

        val expensesByCategory = expenseStates
            .groupBy { it.expenseCategoryId }
            .mapNotNull { (categoryId, currentExpenseList) ->
                categoriesMapForPieChart[categoryId]?.let { category ->
                    category to currentExpenseList.sumOf { it.amount }
                }
            }
            .toMap()

        val allSortedExpenses = expenseStates.sortedByDescending { it.createdAt }

        OverviewState(
            totalExpenses = total,
            expensesByCategory = expensesByCategory,
            recentExpenses = allSortedExpenses.take(5),
            allExpenses = allSortedExpenses
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OverviewState()
    )

    fun deleteExpense(expenseStateToDelete: ExpenseState) {
        viewModelScope.launch {
            if (expenseStateToDelete.id?.toLong() == 0L || expenseStateToDelete.id == 0) {
                return@launch
            }

            val expenseEntityToDelete = Expense(
                id = expenseStateToDelete.id,
                amount = expenseStateToDelete.amount,
                expenseCategoryId = expenseStateToDelete.expenseCategoryId,
                paymentMethod = expenseStateToDelete.paymentMethod,
                description = expenseStateToDelete.description,
                createdAt = expenseStateToDelete.createdAt
            )

            try {
                expenseDao.deleteExpense(expenseEntityToDelete)
            } catch (_: Exception) {
            }
        }
    }
}