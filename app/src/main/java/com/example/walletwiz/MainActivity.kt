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
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "wallet.db"
        ).build()
    }

    private val viewModel by viewModels<ExpenseViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ExpenseViewModel(db.expenseDao()) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state by viewModel.state.collectAsState()
            ExpenseScreen(
                state = state,
                onEvent = viewModel::onEvent
            )
        }
    }
}