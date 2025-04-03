package com.example.walletwiz

/*

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.walletwiz.data.database.AppDatabase
import com.example.walletwiz.ui.OverviewScreen
import com.example.walletwiz.viewmodels.ExpenseOverviewViewModel
import kotlinx.coroutines.launch

import com.example.walletwiz.data.dao.ExpenseCategoryDao
import com.example.walletwiz.data.dao.ExpenseDao
import com.example.walletwiz.data.entity.Expense
import com.example.walletwiz.data.entity.ExpenseCategory
import com.example.walletwiz.data.entity.PaymentMethod
import com.example.walletwiz.states.ExpenseState
import com.example.walletwiz.states.OverviewState
import java.util.Date
import android.util.Log


class MainActivity : AppCompatActivity() {


    private val db by lazy {
        AppDatabase.invoke(this).also {
            Log.d("MainActivity", "Database initialized: $it")  // Log when the database is initialized
        }
    }

    private val expenseOverviewViewModel by lazy {
        ViewModelProvider(
            this,
            ExpenseOverviewViewModel.Factory(db.expenseDao(), db.expenseCategoryDao())
        ).get(ExpenseOverviewViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MainActivity", "MainActivity created and initialized.")

        enableEdgeToEdge()

        // Populate database if needed
        lifecycleScope.launch {
            populateDatabase()
        }

        setContent {
            MaterialTheme {
                // Collect ViewModel state
                val overviewState by expenseOverviewViewModel.state.collectAsState()

                // Pass state to OverviewScreen
                OverviewScreen(state = overviewState)
            }
        }
    }

    private suspend fun populateDatabase() {
        val categoryDao = db.expenseCategoryDao()
        val expenseDao = db.expenseDao()

        if (categoryDao.getAllExpenseCategories().isEmpty()) {
            val foodCategoryId = categoryDao.insertExpenseCategory(
                ExpenseCategory(name = "Food", description = "Restaurant and groceries", color = "#FF5733")
            ).toInt()

            val travelCategoryId = categoryDao.insertExpenseCategory(
                ExpenseCategory(name = "Travel", description = "Transportation expenses", color = "#33FF57")
            ).toInt()

            // Insert expenses using the retrieved IDs
            expenseDao.insertExpense(
                Expense(amount = 50.0, expenseCategoryId = foodCategoryId, paymentMethod = PaymentMethod.CASH, description = "Dinner", createdAt = Date())
            )
            expenseDao.insertExpense(
                Expense(amount = 120.0, expenseCategoryId = travelCategoryId, paymentMethod = PaymentMethod.CREDIT_CARD, description = "Taxi Ride", createdAt = Date())
            )
        }
    }
}
*/



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
import com.example.walletwiz.ui.OverviewScreen
import com.example.walletwiz.viewmodels.ExpenseViewModel
import com.example.walletwiz.viewmodels.ExpenseOverviewViewModel
import kotlinx.coroutines.launch
import java.util.Date
import com.example.walletwiz.states.ExpenseState
import com.example.walletwiz.states.OverviewState
import androidx.compose.material3.MaterialTheme
import com.example.walletwiz.ui.OverviewScreen

import androidx.compose.material3.Text

import android.util.Log

import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable

import com.example.walletwiz.ui.OverviewScreen

import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    // Log tag for easy identification of logs related to database
    private val LOG_TAG = "MainActivity"


    /*
    private val db by lazy {
        Log.d(LOG_TAG, "Initializing the database...")
        // Initialize database and log it
        AppDatabase.invoke(this).also {
            Log.d(LOG_TAG, "Database initialized: $it")
        }
    }
    */

    private val db by lazy {
        Log.d(LOG_TAG, "Initializing the database...")
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration()
            .build().also {
                Log.d(LOG_TAG, "Database initialized: $it")
            }
    }

    private val expenseViewModel by lazy {
        ExpenseViewModel(db.expenseDao())
    }

    private val expenseOverviewViewModel by lazy {
        ExpenseOverviewViewModel(db.expenseDao(), db.expenseCategoryDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Log.d(LOG_TAG, "MainActivity created")

        enableEdgeToEdge()

        // Populate database if needed and log steps
        lifecycleScope.launch {
            populateDatabase()
        }

        /*
        //EXPENSE (Matěj)
        setContent {
            ExpenseScreen(
                state = expenseViewModel.state.collectAsState().value,
                onEvent = expenseViewModel::onEvent
            )
        }*/

        ///////OVERVIEW + VIZUALIZACE




        // List View Content
        setContent {
            MaterialTheme {
                ListViewContent()
            }
        }



        // Graph View Content
        /*
        setContent {
            MaterialTheme {
                GraphViewContent()
            }
        }*/

        /////////////////////////////////////////


        /*
        setContent {
            MaterialTheme {
                // Collect state from ViewModel
                val overviewState by expenseOverviewViewModel.state.collectAsState()

                // Call OverviewScreen with the populated state
                OverviewScreen(state = overviewState)
            }
        }
        */


        /*
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
                        ExpenseState(amount = 150.0, expenseCategoryId = 2, paymentMethod = PaymentMethod.CREDIT_CARD, description = "Taxi Ride", createdAt = Date()),
                        ExpenseState(amount = 150.0, expenseCategoryId = 2, paymentMethod = PaymentMethod.CREDIT_CARD, description = "Taxi Ride2", createdAt = Date()),
                        ExpenseState(amount = 150.0, expenseCategoryId = 2, paymentMethod = PaymentMethod.CREDIT_CARD, description = "Taxi Ride3", createdAt = Date())
                    )
                )

                // Call OverviewScreen with the mock state
                OverviewScreen(state = mockState)
            }
        }
         */

    }

    // List View Content
    @Composable
    private fun ListViewContent() {
        val overviewState by expenseOverviewViewModel.state.collectAsState()

        OverviewScreen(state = overviewState)
    }


    private suspend fun populateDatabase() {
        val categoryDao = db.expenseCategoryDao()
        val expenseDao = db.expenseDao()

        val categories = categoryDao.getAllExpenseCategories()
        Log.d(LOG_TAG, "Categories in DB: ${categories.size}")  // Log the size of the categories

        if (categories.isEmpty()) {
            Log.d(LOG_TAG, "Populating the database with sample data...")

            // Insert sample expense categories and log their IDs
            val foodCategoryId = categoryDao.insertExpenseCategory(
                ExpenseCategory(name = "Food", description = "Restaurant and groceries", color = "#FF5733")
            ).toInt()
            Log.d(LOG_TAG, "Food category inserted with ID: $foodCategoryId")

            val travelCategoryId = categoryDao.insertExpenseCategory(
                ExpenseCategory(name = "Travel", description = "Transportation expenses", color = "#33FF57")
            ).toInt()
            Log.d(LOG_TAG, "Travel category inserted with ID: $travelCategoryId")

            // Insert expenses and log their creation
            expenseDao.insertExpense(
                Expense(amount = 50.0, expenseCategoryId = foodCategoryId, paymentMethod = PaymentMethod.CASH, description = "Dinner", createdAt = Date())
            )
            Log.d(LOG_TAG, "Inserted expense: Dinner (Amount: 50.0)")

            expenseDao.insertExpense(
                Expense(amount = 120.0, expenseCategoryId = travelCategoryId, paymentMethod = PaymentMethod.CREDIT_CARD, description = "Taxi Ride", createdAt = Date())
            )
            Log.d(LOG_TAG, "Inserted expense: Taxi Ride (Amount: 120.0)")
        } else {
            Log.d(LOG_TAG, "Database already populated with categories.")
        }
    }

}


