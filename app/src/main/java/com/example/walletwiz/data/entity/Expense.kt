package com.example.walletwiz.data.entity

import androidx.room.*
import com.example.walletwiz.utils.Currency
import java.util.Date

@Entity(
    tableName = "expense"
)

data class Expense(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "category")
    val expenseCategoryId: Int?,
    @ColumnInfo(name = "payment_method")
    val paymentMethod: PaymentMethod,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: Date,
    @ColumnInfo(name = "tags")
    val tags: String = "",
)
