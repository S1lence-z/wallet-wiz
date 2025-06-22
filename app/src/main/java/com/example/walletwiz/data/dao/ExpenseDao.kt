/*package com.example.walletwiz.data.dao

import androidx.room.*
import com.example.walletwiz.data.entity.Expense

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expense ORDER BY created_at DESC")
    suspend fun getAllExpenses(): List<Expense>

    @Query("SELECT * FROM expense WHERE id = :id")
    suspend fun getExpenseById(id: Int): Expense

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)
}*/


// SEC
package com.example.walletwiz.data.dao

import androidx.room.*
import com.example.walletwiz.data.entity.Expense
// IMPORTANT: DO NOT import kotlinx.coroutines.flow.Flow here if you want List<Expense>
// If it's imported, remove it for this setup.

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    // THIS IS THE CRUCIAL LINE:
    // It MUST be 'suspend' and MUST return 'List<Expense>'
    @Query("SELECT * FROM expense ORDER BY created_at DESC")
    suspend fun getAllExpenses(): List<Expense> // <--- Ensure this is EXACTLY how it looks

    @Query("SELECT * FROM expense WHERE id = :id")
    suspend fun getExpenseById(id: Int): Expense

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)
}