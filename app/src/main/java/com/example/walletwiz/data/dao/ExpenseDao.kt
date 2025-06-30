package com.example.walletwiz.data.dao

import androidx.room.*
import com.example.walletwiz.data.entity.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Query("SELECT * FROM expense ORDER BY created_at DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expense WHERE id = :id")
    suspend fun getExpenseById(id: Int): Expense

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expense WHERE DATE(created_at/1000, 'unixepoch', 'localtime') = DATE('now', 'localtime')")
    suspend fun getTodayTotal(): Double

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expense WHERE DATE(created_at/1000, 'unixepoch', 'localtime') >= DATE('now', 'localtime', '-7 days')")
    suspend fun getWeekTotal(): Double

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expense WHERE DATE(created_at/1000, 'unixepoch', 'localtime') >= DATE('now', 'localtime', 'start of month')")
    suspend fun getMonthTotal(): Double

    @Query("SELECT COUNT(*) FROM expense WHERE DATE(created_at/1000, 'unixepoch', 'localtime') = DATE('now', 'localtime')")
    suspend fun getTodayExpenseCount(): Int

    @Query("SELECT COUNT(*) FROM expense WHERE DATE(created_at/1000, 'unixepoch', 'localtime') >= DATE('now', 'localtime', '-7 days')")
    suspend fun getWeekExpenseCount(): Int

    @Query("SELECT COUNT(*) FROM expense WHERE DATE(created_at/1000, 'unixepoch', 'localtime') >= DATE('now', 'localtime', 'start of month')")
    suspend fun getMonthExpenseCount(): Int
}