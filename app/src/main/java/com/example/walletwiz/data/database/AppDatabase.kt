package com.example.walletwiz.data.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.walletwiz.data.entity.*
import com.example.walletwiz.data.Converters
import com.example.walletwiz.data.dao.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@TypeConverters(Converters::class)
@Database(entities = [Expense::class, ExpenseCategory::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun expenseCategoryDao(): ExpenseCategoryDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context): AppDatabase {
            return instance ?: synchronized(LOCK) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "wallet.db"
            )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        instance?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                insertDefaultCategories(database.expenseCategoryDao())
                            }
                        }
                    }
                })
                .build()
    }
}

// ✅ Function to insert default expense categories
suspend fun insertDefaultCategories(expenseCategoryDao: ExpenseCategoryDao) {
    val defaultCategories = listOf(
        ExpenseCategory(name = "Food", description = "Meals and groceries", color = "#FF5733"),
        ExpenseCategory(name = "Transport", description = "Public transport and fuel", color = "#4287f5"),
        ExpenseCategory(name = "Entertainment", description = "Movies, music, games", color = "#f5a742"),
        ExpenseCategory(name = "Utilities", description = "Electricity, water, rent", color = "#34a853"),
        ExpenseCategory(name = "Health", description = "Medical and fitness expenses", color = "#8e44ad"),
        ExpenseCategory(name = "Shopping", description = "Clothing and accessories", color = "#e74c3c")
    )

    expenseCategoryDao.insertDefaultCategories(defaultCategories)
}
