package com.example.walletwiz.viewmodels

import androidx.lifecycle.ViewModel
import com.example.walletwiz.data.dao.ExpenseCategoryDao
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.walletwiz.states.ExpenseCategoryState

class ExpenseCategoryViewModel(
    private val expenseCategoryDao: ExpenseCategoryDao
) : ViewModel() {
    private val _state = MutableStateFlow(ExpenseCategoryState())
    val state get() = _state
}