package com.example.walletwiz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletwiz.data.entity.Expense
import com.example.walletwiz.data.repository.IExpenseCategoryRepository
import com.example.walletwiz.data.repository.IExpenseRepository
import com.example.walletwiz.utils.TimePeriod
import com.example.walletwiz.states.ExpenseState
import com.example.walletwiz.states.OverviewState
import com.example.walletwiz.utils.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.util.Log
import java.util.Calendar

class ExpenseOverviewViewModel(
    private val expenseRepository: IExpenseRepository,
    private val expenseCategoryRepository: IExpenseCategoryRepository
) : ViewModel() {

    private val _selectedTimePeriod = MutableStateFlow(TimePeriod.ALL_TIME)
    val selectedTimePeriod: StateFlow<TimePeriod> = _selectedTimePeriod.asStateFlow()

    fun setTimePeriod(period: TimePeriod) {
        _selectedTimePeriod.value = period
    }

    val state: StateFlow<OverviewState> = combine(
        expenseRepository.getAllExpenses(),
        expenseCategoryRepository.getAllCategories(),
        _selectedTimePeriod
    ) { allExpensesFromDb, categoriesList, currentPeriod ->

        val (startDate, endDate) = calculateDateRange(currentPeriod)

        val filteredExpensesFromDb = if (currentPeriod == TimePeriod.ALL_TIME) {
            allExpensesFromDb
        } else {
            allExpensesFromDb.filter { expense ->
                val expenseDateTimeMillis = expense.createdAt.time

                expenseDateTimeMillis >= startDate && expenseDateTimeMillis <= endDate
            }
        }

        val categoryIdToNameMap = categoriesList
            .filter { it.id != null }
            .associate { it.id to it.name }

        val expenseStates = filteredExpensesFromDb.map { expenseEntity ->
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
            .associateBy { it.id ?: 0 }

        val total = expenseStates.sumOf { it.amount }

        val expensesByCategory = expenseStates
            .groupBy { it.expenseCategoryId }
            .mapNotNull { (categoryId, currentExpenseList) ->
                categoriesMapForPieChart[categoryId]?.let { category ->
                    category to currentExpenseList.sumOf { it.amount }
                }
            }
            .toMap()

        val allSortedFilteredExpenses = expenseStates.sortedByDescending { it.createdAt }

        OverviewState(
            totalExpenses = total,
            expensesByCategory = expensesByCategory,
            recentExpenses = allSortedFilteredExpenses.take(5),
            allExpenses = allSortedFilteredExpenses,
            selectedTimePeriod = currentPeriod
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OverviewState()
    )

    private fun calculateDateRange(period: TimePeriod): Pair<Long, Long> {
        val calendar = Calendar.getInstance()

        return when (period) {
            TimePeriod.DAY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startOfDay = calendar.timeInMillis

                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
                val endOfDay = calendar.timeInMillis
                Pair(startOfDay, endOfDay)
            }
            TimePeriod.WEEK -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                val startOfWeek = calendar.timeInMillis

                calendar.add(Calendar.WEEK_OF_YEAR, 1)
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                val endOfWeek = calendar.timeInMillis
                Pair(startOfWeek, endOfWeek)
            }
            TimePeriod.MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                val startOfMonth = calendar.timeInMillis

                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                val endOfMonth = calendar.timeInMillis
                Pair(startOfMonth, endOfMonth)
            }
            TimePeriod.ALL_TIME -> Pair(0L, Long.MAX_VALUE)
        }
    }

    fun deleteExpense(expenseStateToDelete: ExpenseState) {
        viewModelScope.launch { // No Dispatchers.IO here
            if (expenseStateToDelete.id == null) {
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

            when (expenseRepository.deleteExpense(expenseEntityToDelete)) {
                is Result.Success -> { }
                is Result.Error -> { }
                Result.Loading -> { }
            }
        }
    }
}
