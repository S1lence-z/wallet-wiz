package com.example.walletwiz.data.dao

import androidx.room.*
import com.example.walletwiz.data.entity.ExpenseCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseCategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(expenseCategory: ExpenseCategory): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDefaultCategories(expenseCategories: List<ExpenseCategory>)

    @Query("SELECT * FROM expense_category")
    fun getAllCategories(): Flow<List<ExpenseCategory>>

    @Query("SELECT * FROM expense_category WHERE id = :id")
    suspend fun getCategoryById(id: Int): ExpenseCategory?

    @Update
    suspend fun updateCategory(expenseCategory: ExpenseCategory)

    @Delete
    suspend fun deleteCategory(expenseCategory: ExpenseCategory)
}
