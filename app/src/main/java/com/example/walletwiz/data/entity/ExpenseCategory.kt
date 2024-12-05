package com.example.walletwiz.data.entity

import androidx.room.*;

@Entity(tableName = "expense_category")
data class ExpenseCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "color")
    val color: String
)