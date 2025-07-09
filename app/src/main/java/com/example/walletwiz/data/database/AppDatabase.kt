package com.example.walletwiz.data.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.walletwiz.data.entity.*
import com.example.walletwiz.data.Converters
import com.example.walletwiz.data.dao.*
import com.example.walletwiz.data.repository.ExpenseCategoryRepositoryImpl
import com.example.walletwiz.utils.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log

@TypeConverters(Converters::class)
@Database(entities = [Expense::class, ExpenseCategory::class, ExpenseTag::class, ExpenseTagCrossRef::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun expenseCategoryDao(): ExpenseCategoryDao
    abstract fun tagDao(): TagDao

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
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        instance?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                val expenseCategoryRepository = ExpenseCategoryRepositoryImpl(database.expenseCategoryDao())
                                insertDefaultCategories(expenseCategoryRepository)
                            }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
    }
}

suspend fun insertDefaultCategories(expenseCategoryRepository: ExpenseCategoryRepositoryImpl) {
    val defaultCategories = listOf(
        ExpenseCategory(name = "Uncategorized", description = "No category", color = "#FFFFE0"),
        ExpenseCategory(name = "Food", description = "Meals and groceries", color = "#FF5733"),
        ExpenseCategory(name = "Transport", description = "Public transport and fuel", color = "#4287f5"),
        ExpenseCategory(name = "Entertainment", description = "Movies, music, games", color = "#f5a742"),
        ExpenseCategory(name = "Utilities", description = "Electricity, water, rent", color = "#34a853"),
        ExpenseCategory(name = "Health", description = "Medical and fitness expenses", color = "#8e44ad"),
        ExpenseCategory(name = "Shopping", description = "Clothing and accessories", color = "#e74c3c")
    )

    when (val result = expenseCategoryRepository.insertDefaultCategories(defaultCategories)) {
        is Result.Success -> Log.d("AppDatabase", "Default categories inserted successfully.")
        is Result.Error -> Log.e("AppDatabase", "Failed to insert default categories: ${result.exception.message}")
        Result.Loading -> { }
    }
}
