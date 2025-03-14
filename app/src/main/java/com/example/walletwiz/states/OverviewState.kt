package com.example.walletwiz.states

import com.example.walletwiz.data.entity.ExpenseCategory
import com.example.walletwiz.data.entity.PaymentMethod
import java.time.Instant
import java.util.Date

data class OverviewState(
    val totalExpenses: Double = 0.0,
    val expensesByCategory: Map<ExpenseCategory, Double> = emptyMap(),
    val recentExpenses: List<ExpenseState> = emptyList(), // ✅ Now has recent expenses
    val isLoading: Boolean = false
)

data class ExpenseState(
    val amount: Double = 0.0,
    val expenseCategoryId: Int? = 0,        //? WAS NEEDED, BUT IS IT RIGHT?
    val paymentMethod: PaymentMethod = PaymentMethod.DEBIT_CARD,
    val description: String? = null,
    val createdAt: Date = Date.from(Instant.now())
)
