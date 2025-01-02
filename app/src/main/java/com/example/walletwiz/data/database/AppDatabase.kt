package com.example.walletwiz.data.database

import android.content.Context
import androidx.room.*
import com.example.walletwiz.data.entity.*
import com.example.walletwiz.data.Converters
import com.example.walletwiz.data.dao.*

@Database(entities = [Expense::class, ExpenseCategory::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun expenseCategoryDao(): ExpenseCategoryDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context): AppDatabase {
            return instance ?: synchronized(LOCK) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "wallet.db"
            ).build()
    }
}