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
import com.example.walletwiz.data.entity.ExpenseCategory
import com.example.walletwiz.data.entity.PaymentMethod
import com.example.walletwiz.ui.ExpenseScreen
//import com.example.walletwiz.ui.OverviewScreen
import com.example.walletwiz.viewmodels.ExpenseViewModel
import com.example.walletwiz.viewmodels.ExpenseOverviewViewModel
import kotlinx.coroutines.launch
import java.util.Date
import com.example.walletwiz.states.ExpenseState
import com.example.walletwiz.states.OverviewState
import androidx.compose.material3.MaterialTheme
import com.example.walletwiz.ui.OverviewScreen

import androidx.compose.material3.Text

class MainActivity : AppCompatActivity() {
    private val db by lazy {
        AppDatabase(this)
    }

    /*
    private val expenseViewModel by lazy {
        ExpenseViewModel(db.expenseDao())
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        /*
        setContent {
            ExpenseScreen(
                state = expenseViewModel.state.collectAsState().value,
                onEvent = expenseViewModel::onEvent
            )
        }
         */


        setContent {
            // Use the default MaterialTheme
            MaterialTheme {
                // Create a mock state for OverviewScreen
                val mockState = OverviewState(
                    totalExpenses = 500.0,
                    expensesByCategory = mapOf(
                        ExpenseCategory(name = "Food", description = "Restaurant and groceries", color = "#FF5733") to 200.0,
                        ExpenseCategory(name = "Travel", description = "Transportation expenses", color = "#33FF57") to 300.0
                    ),
                    recentExpenses = listOf(
                        ExpenseState(amount = 100.0, expenseCategoryId = 1, paymentMethod = PaymentMethod.CASH, description = "Dinner", createdAt = Date()),
                        ExpenseState(amount = 150.0, expenseCategoryId = 2, paymentMethod = PaymentMethod.CREDIT_CARD, description = "Taxi Ride", createdAt = Date())
                    )
                )

                // Call OverviewScreen with the mock state
                OverviewScreen(state = mockState)
            }
        }

        /*
        setContent {
            Text("Hello World!")


            //TO FIX !!!!
            /*
            // Collecting the overview state from the ViewModel
            val overviewState by expenseOverviewViewModel.state.collectAsState()

            // Now passing the overview state to OverviewScreen
            OverviewScreen(
                state = overviewState, // this holds the overview state
                onEvent = { event ->
                    // Handle events here if needed, e.g., calling onEvent in the ViewModel
                    expenseOverviewViewModel.onEvent(event)
                }
            )

             */
        }*/


    }
}