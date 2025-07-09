package com.example.walletwiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.work.WorkManager

import com.example.walletwiz.data.NotificationSettingsRepository
import com.example.walletwiz.data.database.AppDatabase
import com.example.walletwiz.data.repository.ExpenseCategoryRepositoryImpl
import com.example.walletwiz.data.repository.ExpenseRepositoryImpl
import com.example.walletwiz.data.repository.TagRepositoryImpl
import com.example.walletwiz.navigation.AppNavigationHost

class MainActivity : ComponentActivity() {
    private val appDatabase by lazy { AppDatabase.invoke(this.applicationContext) }
    private val expenseRepository by lazy { ExpenseRepositoryImpl(appDatabase.expenseDao()) }
    private val expenseCategoryRepository by lazy { ExpenseCategoryRepositoryImpl(appDatabase.expenseCategoryDao()) }
    private val tagRepository by lazy { TagRepositoryImpl(appDatabase.tagDao()) }
    private val notificationSettingsRepository by lazy { NotificationSettingsRepository(this.applicationContext) }
    private val workManager by lazy { WorkManager.getInstance(this.applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppNavigationHost(
                    expenseRepository = expenseRepository,
                    expenseCategoryRepository = expenseCategoryRepository,
                    tagRepository = tagRepository,
                    notificationSettingsRepository = notificationSettingsRepository,
                    workManager = workManager
                )
            }
        }
    }
}
