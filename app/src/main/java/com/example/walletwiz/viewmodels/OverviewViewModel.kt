package com.example.walletwiz.viewmodels

import androidx.lifecycle.ViewModel
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