package com.example.walletwiz.viewmodels

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
    private val expenseCategoryDao: ExpenseCategoryDao ) : ViewModel() {

    private val _state = MutableStateFlow(OverviewState())
    val state: StateFlow<OverviewState> get() = _state

    init {
        populateDatabase()
        loadExpenses()
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) } // Start loading
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
                            .map { expense ->  // Convert Expense to ExpenseState
                                ExpenseState(
                                    amount = expense.amount,
                                    expenseCategoryId = expense.expenseCategoryId,
                                    paymentMethod = expense.paymentMethod,
                                    description = expense.description,
                                    createdAt = expense.createdAt
                                )
                            },
                        isLoading = false // Stop loading
                    )
                }

            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) } // Stop loading in case of error
                // Handle error, maybe log or show a message
            }
        }



    /*
    private fun loadExpenses() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) } // Start loading
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

                // Convert expenses into ExpenseState objects if necessary
                val expenseStates = expenses.map { expense ->
                    ExpenseState(
                        amount = expense.amount,
                        expenseCategoryId = expense.expenseCategoryId,
                        paymentMethod = expense.paymentMethod,
                        description = expense.description,
                        createdAt = expense.createdAt.toString()
                    )
                }

                _state.update {
                    it.copy(
                        totalExpenses = total,
                        expensesByCategory = expensesByCategory,
                        recentExpenses = expenses.sortedByDescending { it.createdAt }.take(5),
                        expenses = expenseStates, // ✅ Add full expenses list
                        isLoading = false // Stop loading
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) } // Stop loading in case of error
                // Handle error, maybe log or show a message
            }
        }
    }

     */

}


    fun populateDatabase() {
        viewModelScope.launch {
            // Insert expense categories
            val foodCategory = ExpenseCategory(name = "Food", description = "Restaurant and groceries", color = "#FF5733")
            val travelCategory = ExpenseCategory(name = "Travel", description = "Transportation expenses", color = "#33FF57")

            expenseCategoryDao.insertExpenseCategory(foodCategory)
            expenseCategoryDao.insertExpenseCategory(travelCategory)

            // Fetch the inserted categories
            val categories = expenseCategoryDao.getAllExpenseCategories().associateBy { it.name }

            // Insert sample expenses
            expenseDao.insertExpense(Expense(amount = 50.0, expenseCategoryId = categories["Food"]?.id, paymentMethod = PaymentMethod.CASH, description = "Dinner", createdAt = Date()))
            expenseDao.insertExpense(Expense(amount = 120.0, expenseCategoryId = categories["Travel"]?.id, paymentMethod = PaymentMethod.CREDIT_CARD, description = "Taxi Ride", createdAt = Date()))
        }
    }
}
