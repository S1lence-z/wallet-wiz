package com.example.walletwiz.data.entity

import androidx.room.*

@Entity(tableName = "expense_tag_cross_ref")
data class ExpenseTagCrossRef(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,
    val expenseId: Int? = null,
    val tagId: Int? = null
)
