package com.example.walletwiz.events

import com.example.walletwiz.data.entity.ExpenseCategory

sealed interface ExpenseCategoryEvent {
    data class SetName(val name: String) : ExpenseCategoryEvent
    data class SetDescription(val description: String) : ExpenseCategoryEvent
    data class SetColor(val color: String) : ExpenseCategoryEvent
    data class SetSelectedCategory(val category: ExpenseCategory) : ExpenseCategoryEvent
    data class DeleteCategory(val category: ExpenseCategory) : ExpenseCategoryEvent

    data object ShowEditDialog : ExpenseCategoryEvent
    data object HideEditDialog : ExpenseCategoryEvent
    data object SaveCategory : ExpenseCategoryEvent
}