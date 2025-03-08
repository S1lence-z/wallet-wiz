package com.example.walletwiz.data.dao

import androidx.room.*
import com.example.walletwiz.data.entity.ExpenseCategory

@Dao
interface ExpenseCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expenseCategory: ExpenseCategory): Long  // Returns inserted ID

    @Query("SELECT * FROM expense_category")
    suspend fun getAllCategories(): List<ExpenseCategory>

    @Query("SELECT * FROM expense_category WHERE id = :id")
    suspend fun getCategoryById(id: Int): ExpenseCategory?

    @Update
    suspend fun update(expenseCategory: ExpenseCategory)

    @Delete
    suspend fun delete(expenseCategory: ExpenseCategory)
}
