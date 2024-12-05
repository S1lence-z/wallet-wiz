package com.example.walletwiz.data.dao

import androidx.room.*
import com.example.walletwiz.data.entity.ExpenseCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseCategoryDao {
    // Data Access Object class
    @Upsert
    suspend fun insertExpenseCategory(expenseCategory: ExpenseCategory)

    @Query("SELECT * FROM expense_category")
    suspend fun getAllExpenseCategories(): Flow<List<ExpenseCategory>>

    @Query("SELECT * FROM expense_category WHERE id = :id")
    suspend fun getExpenseCategoryById(id: Int): ExpenseCategory

    @Update
    suspend fun updateExpenseCategory(expenseCategory: ExpenseCategory)

    @Delete
    suspend fun deleteExpenseCategory(expenseCategory: ExpenseCategory)
}