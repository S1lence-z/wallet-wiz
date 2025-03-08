package com.example.walletwiz

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.walletwiz.data.database.AppDatabase
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.walletwiz.data.entity.Expense
import com.example.walletwiz.data.entity.PaymentMethod
import com.example.walletwiz.ui.ExpenseScreen
import com.example.walletwiz.viewmodels.ExpenseViewModel
import kotlinx.coroutines.launch
import java.util.Date

class MainActivity : AppCompatActivity() {
    private val db by lazy {
        AppDatabase.invoke(this)  // Use singleton instance
    }

    private val expenseViewModel by lazy {
        ExpenseViewModel(
            expenseDao = db.expenseDao(),
            expenseCategoryDao = db.expenseCategoryDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseScreen(
                state = expenseViewModel.state.collectAsState().value,
                onEvent = expenseViewModel::onEvent
            )
        }
    }
}

