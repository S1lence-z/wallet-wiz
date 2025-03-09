package com.example.walletwiz.data.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ExpenseWithTags(
    @Embedded val expense: Expense,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(ExpenseTagCrossRef::class)
    )
    val tags: List<ExpenseTag>
)
