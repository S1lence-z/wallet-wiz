package com.example.walletwiz.states

import com.example.walletwiz.data.entity.ExpenseCategory

data class OverviewState(
    val totalExpenses: Double = 0.0,
    val expensesByCategory: Map<ExpenseCategory, Double> = emptyMap(),
    val recentExpenses: List<ExpenseState> = emptyList(),
    val isLoading: Boolean = false
)
