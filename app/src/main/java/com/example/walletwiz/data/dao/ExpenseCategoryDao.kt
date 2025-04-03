package com.example.walletwiz.data.dao

import androidx.room.*
import com.example.walletwiz.data.entity.ExpenseCategory

@Dao
interface ExpenseCategoryDao {
    // Data Access Object class
    @Upsert
    suspend fun insertExpenseCategory(expenseCategory: ExpenseCategory): Long //maybe long

    @Query("SELECT * FROM expense_category")
    suspend fun getAllExpenseCategories(): List<ExpenseCategory>

    @Query("SELECT * FROM expense_category WHERE id = :id")
    suspend fun getExpenseCategoryById(id: Int): ExpenseCategory

    @Update
    suspend fun updateExpenseCategory(expenseCategory: ExpenseCategory)

    @Delete
    suspend fun deleteExpenseCategory(expenseCategory: ExpenseCategory)
}