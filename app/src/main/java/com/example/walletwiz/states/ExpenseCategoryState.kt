package com.example.walletwiz.states

import com.example.walletwiz.data.entity.ExpenseCategory

data class ExpenseCategoryState(
    val categories: List<ExpenseCategory> = emptyList(),
    val selectedCategory: ExpenseCategory? = null,
    val isEditing: Boolean = false
)