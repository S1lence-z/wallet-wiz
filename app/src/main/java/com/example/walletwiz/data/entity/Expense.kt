package com.example.walletwiz.data.entity

import androidx.room.*
import java.util.Date

@Entity(
    tableName = "expense",
    foreignKeys = [
        ForeignKey(
            entity = ExpenseCategory::class,
            parentColumns = ["id"],
            childColumns = ["category"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["category"])]
)
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
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
