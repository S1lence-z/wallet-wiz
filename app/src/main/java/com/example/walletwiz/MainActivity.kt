package com.example.walletwiz

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.walletwiz.data.database.AppDatabase
import androidx.activity.compose.setContent
import com.example.walletwiz.viewmodels.ExpenseViewModel
import com.example.walletwiz.ui.layout.*
import com.example.walletwiz.viewmodels.ExpenseCategoryViewModel
import com.example.walletwiz.viewmodels.ExpenseOverviewViewModel

class MainActivity : AppCompatActivity() {
    private val db by lazy {
        AppDatabase.invoke(this)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainLayout(
                expenseViewModel = expenseViewModel,
                overviewViewModel = overviewViewModel,
                expenseCategoryViewModel = expenseCategoryViewModel
            )
        }
    }
}