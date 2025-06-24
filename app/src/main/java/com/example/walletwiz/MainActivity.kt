package com.example.walletwiz

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.walletwiz.data.database.AppDatabase
import com.example.walletwiz.data.NotificationSettingsRepository
import androidx.activity.compose.setContent
import com.example.walletwiz.viewmodels.ExpenseViewModel
import com.example.walletwiz.ui.layout.*
import com.example.walletwiz.ui.theme.WalletWizTheme
import com.example.walletwiz.viewmodels.ExpenseCategoryViewModel
import com.example.walletwiz.viewmodels.NotificationSettingsViewModel
import com.example.walletwiz.viewmodels.ExpenseOverviewViewModel

class MainActivity : AppCompatActivity() {
    private val db by lazy {
        AppDatabase.invoke(this)
    }

    private val notificationSettingsRepository by lazy {
        NotificationSettingsRepository(this)
    }

    private val expenseViewModel by lazy {
        ExpenseViewModel(
            expenseDao = db.expenseDao(),
            expenseCategoryDao = db.expenseCategoryDao(),
            tagDao = db.tagDao()
        )
    }

    private val overviewViewModel by lazy {
        ExpenseOverviewViewModel(
            expenseDao = db.expenseDao(),
            expenseCategoryDao = db.expenseCategoryDao()
        )
    }

    private val expenseCategoryViewModel by lazy {
        ExpenseCategoryViewModel(
            expenseCategoryDao = db.expenseCategoryDao()
        )
    }

    private val notificationSettingsViewModel by lazy {
        NotificationSettingsViewModel(
            notificationSettingsRepository = notificationSettingsRepository,
            workManager = androidx.work.WorkManager.getInstance(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WalletWizTheme {
                MainLayout(
                    expenseViewModel = expenseViewModel,
                    overviewViewModel = overviewViewModel,
                    expenseCategoryViewModel = expenseCategoryViewModel,
                    notificationSettingsViewModel = notificationSettingsViewModel
                )
            }
        }
    }
}