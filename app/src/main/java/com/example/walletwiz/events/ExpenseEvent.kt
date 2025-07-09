package com.example.walletwiz.events

import com.example.walletwiz.data.entity.PaymentMethod
import com.example.walletwiz.states.ExpenseState
import java.util.Date

sealed interface ExpenseEvent {
    data class SetAmount(val amount: Double) : ExpenseEvent
    data class SetExpenseCategory(val expenseCategoryId: Int) : ExpenseEvent
    data class SetPaymentMethod(val paymentMethod: PaymentMethod) : ExpenseEvent
    data class SetDescription(val description: String) : ExpenseEvent
    data class SetCreatedAt(val createdAt: Date) : ExpenseEvent
    data class CreateExpenseCategory(val name: String) : ExpenseEvent
    data object SaveExpense : ExpenseEvent
    data object CancelExpense : ExpenseEvent

    data class AddTagToExpense(val expenseId: Int, val tagName: String) : ExpenseEvent
    data class RemoveTagFromExpense(val expenseId: Int, val tagId: Int) : ExpenseEvent
    data class LoadTagsForExpense(val expenseId: Int) : ExpenseEvent

    data class LoadExpenseForEdit(val expenseId: Int?) : ExpenseEvent
}
