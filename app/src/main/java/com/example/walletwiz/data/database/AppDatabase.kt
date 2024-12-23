package com.example.walletwiz.data.database

import androidx.room.*
import com.example.walletwiz.data.entity.*
import com.example.walletwiz.data.Converters
import com.example.walletwiz.data.dao.*

@Database(entities = [Expense::class, ExpenseCategory::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun expenseCategoryDao(): ExpenseCategoryDao
}