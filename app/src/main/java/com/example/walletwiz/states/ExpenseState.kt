package com.example.walletwiz.states

import com.example.walletwiz.data.entity.ExpenseCategory
import com.example.walletwiz.data.entity.ExpenseTag
import com.example.walletwiz.data.entity.ExpenseWithTags
import java.util.Date
import com.example.walletwiz.data.entity.PaymentMethod
import com.example.walletwiz.utils.Currency
import java.time.Instant

data class ExpenseState(
    val id: Int? = null,
    val amount: Double = 0.0,
    val expenseCategoryId: Int = 0,
    val paymentMethod: PaymentMethod = PaymentMethod.DEBIT_CARD,
    val description: String? = null,
    val createdAt: Date = Date.from(Instant.now()),
    val categories: List<ExpenseCategory> = emptyList(),
    val currency: Currency = Currency.DEFAULT,
    val categoryName: String? = "Uncategorized",

    val selectedExpenseWithTags: ExpenseWithTags? = null,
    val selectedTags: List<ExpenseTag> = emptyList(),
    val allTags: List<ExpenseTag> = emptyList()
)