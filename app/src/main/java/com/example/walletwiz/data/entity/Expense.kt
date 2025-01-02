package com.example.walletwiz.data.entity

import androidx.room.*
import java.util.Date

@Entity(
    tableName = "expense"
)
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "category")
    val expenseCategoryId: Int?,
    @ColumnInfo(name = "payment_method")
    val paymentMethod: PaymentMethod,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: Date
)
