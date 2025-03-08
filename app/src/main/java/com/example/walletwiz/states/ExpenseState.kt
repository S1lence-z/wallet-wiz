package com.example.walletwiz.states

import com.example.walletwiz.data.entity.ExpenseCategory
import java.util.Date
import com.example.walletwiz.data.entity.PaymentMethod
import java.time.Instant

data class ExpenseState(
    val amount: Double = 0.0,
    val expenseCategoryId: Int = 0,
    val paymentMethod: PaymentMethod = PaymentMethod.DEBIT_CARD,
    val description: String? = null,
    val createdAt: Date = Date.from(Instant.now()),
    val categories: List<ExpenseCategory> = emptyList()
)