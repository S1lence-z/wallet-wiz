package com.example.walletwiz

/*
// Original commented block from the first request, preserved as-is.
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


//SEC

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
import androidx.compose.material3.Scaffold // Import Scaffold
import androidx.compose.material3.NavigationBar // Import NavigationBar
import androidx.compose.material3.NavigationBarItem // Import NavigationBarItem
import androidx.compose.material3.Icon // Import Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home


import android.util.Log

import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf // Import mutableStateOf
import androidx.compose.runtime.remember // Import remember
import androidx.compose.runtime.setValue // Import setValue


import com.example.walletwiz.ui.OverviewScreen
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import androidx.compose.runtime.LaunchedEffect
import com.example.walletwiz.events.ExpenseEvent


class MainActivity : AppCompatActivity() {

    // Log tag for easy identification of logs related to database
    private val LOG_TAG = "MainActivity"

    // >>> NEW FEATURE: Flag to clear the database on every app start
    // Set to 'true' to clear all data (categories and expenses) when the app launches.
    // Set to 'false' to preserve existing data and only populate if the DB is empty.
    private val CLEAR_DB_ON_START = false // Set to true to start with a clean DB every time.


    /*
    // Option 1: Database initialization using AppDatabase.invoke (if it were a singleton/companion object method)
    private val db by lazy {
        Log.d(LOG_TAG, "Initializing the database...")
        // Initialize database and log it
        AppDatabase.invoke(this).also {
            Log.d(LOG_TAG, "Database initialized: $it")
        }
    }
    */

    // Option 2: Database initialization using Room.databaseBuilder (currently active)
    private val db by lazy {
        Log.d(LOG_TAG, "Initializing the database...")
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration() // Allows Room to recreate the database if schema changes
            .build().also {
                Log.d(LOG_TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!! Database initialized: $it")
            }
    }

    private val expenseViewModel by lazy {
        //ExpenseViewModel(db.expenseDao())                                     /// CHANGE HERE !
        ExpenseViewModel(db.expenseDao(), db.expenseCategoryDao())
    }

    private val expenseOverviewViewModel by lazy {
        ExpenseOverviewViewModel(db.expenseDao(), db.expenseCategoryDao())
    }

    // Define sealed class for navigation screens
    sealed class Screen(val route: String, val icon: @Composable () -> Unit, val title: String) {
        object Overview : Screen("overview", { Icon(Icons.Filled.Home, contentDescription = "Overview") }, "Overview")
        object AddExpense : Screen("add_expense", { Icon(Icons.Filled.Add, contentDescription = "Add Expense") }, "Add Expense")
        // Add more screens here if needed
    }


    @OptIn(ExperimentalMaterial3Api::class) // Required for TopAppBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Log.d(LOG_TAG, "MainActivity created")

        enableEdgeToEdge()

        // Populate database if needed and log steps
        lifecycleScope.launch {
            // >>> NEW FEATURE: Clear database if CLEAR_DB_ON_START is true
            if (CLEAR_DB_ON_START) {
                Log.d(LOG_TAG, "CLEAR_DB_ON_START is true. Clearing database...")
                clearDatabase()
                Log.d(LOG_TAG, "Database cleared.")
            }
            populateDatabase()
        }


        // --- Consolidating all UI setup into a single setContent call with navigation ---
        setContent {
            MaterialTheme {
                // State to manage the currently selected screen
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Overview) }

                // <--- NEW: Collect categories state for ExpenseScreen
                val expenseCategories by expenseViewModel.categories.collectAsState()

                Scaffold(
                    // Optional: Top AppBar for screen title
                    topBar = {
                        TopAppBar(title = { Text(currentScreen.title) })
                    },
                    bottomBar = {
                        NavigationBar {
                            // Navigation item for Overview Screen
                            NavigationBarItem(
                                icon = { Screen.Overview.icon() },
                                label = { Text(Screen.Overview.title) },
                                selected = currentScreen == Screen.Overview,

                                // CHANGE !
                                onClick = {
                                    currentScreen = Screen.Overview
                                    // <--- NEW: Trigger refresh when navigating to Overview
                                    lifecycleScope.launch {
                                        expenseOverviewViewModel.refreshOverviewData()
                                    }
                                }
                            )
                            // Navigation item for Add Expense Screen
                            NavigationBarItem(
                                icon = { Screen.AddExpense.icon() },
                                label = { Text(Screen.AddExpense.title) },
                                selected = currentScreen == Screen.AddExpense,
                                onClick = { currentScreen = Screen.AddExpense }
                            )
                        }
                    }
                ) { paddingValues ->
                    // Content based on the selected screen
                    when (currentScreen) {
                        Screen.Overview -> {
                            // Collect state from ExpenseOverviewViewModel for OverviewScreen
                            val overviewState by expenseOverviewViewModel.state.collectAsState()
                            OverviewScreen(state = overviewState)
                        }
                        /*Screen.AddExpense -> {
                            // Display ExpenseScreen for adding expenses
                            ExpenseScreen(
                                state = expenseViewModel.state.collectAsState().value,
                                onEvent = expenseViewModel::onEvent,
                                categories = expenseCategories
                            )
                        }*/
                        Screen.AddExpense -> {
                            ExpenseScreen(
                                state = expenseViewModel.state.collectAsState().value,
                                onEvent = { event ->
                                    expenseViewModel.onEvent(event)
                                    // <--- NEW: After saving, refresh the overview data
                                    if (event == ExpenseEvent.SaveExpense) {
                                        lifecycleScope.launch {
                                            // Give a moment for DB operation to complete, though often not strictly necessary
                                            // delay(100)
                                            expenseOverviewViewModel.refreshOverviewData()
                                        }
                                        // Optional: Navigate back to Overview after saving
                                        currentScreen = Screen.Overview
                                    }
                                },
                                categories = expenseCategories
                            )
                        }
                    }
                }
            }
        }


        /*
        // --- PREVIOUSLY ACTIVE: Old setContent for Expense Screen (now integrated into navigation) ---
        // This block is commented out as its functionality is now part of the `Scaffold` above.
        // setContent {
        //     ExpenseScreen(
        //         state = expenseViewModel.state.collectAsState().value,
        //         onEvent = expenseViewModel::onEvent
        //     )
        // }
        */

        /*
        // --- PREVIOUSLY ACTIVE: Old setContent for List View Content (now integrated into navigation) ---
        // This block is commented out as its functionality is now part of the `Scaffold` above.
        // setContent {
        //     MaterialTheme {
        //         ListViewContent()
        //     }
        // }
        */

        /*
        // --- PREVIOUSLY ACTIVE: Old setContent for Graph View Content (now commented out for clarity) ---
        // This block is commented out as its functionality can be integrated into the Scaffold if needed.
        // setContent {
        //     MaterialTheme {
        //         GraphViewContent()
        //     }
        // }
        */

        /*
        // --- PREVIOUSLY ACTIVE: Old setContent for main Overview (now integrated into navigation) ---
        // This block is commented out as its functionality is now part of the `Scaffold` above.
        // setContent {
        //     MaterialTheme {
        //         val overviewState by expenseOverviewViewModel.state.collectAsState()
        //         OverviewScreen(state = overviewState)
        //     }
        // }
        */

        /*
        // --- PREVIOUSLY ACTIVE: Old setContent for Mock Data View (now commented out for clarity) ---
        // This block is commented out, as it was primarily for testing UI in isolation.
        // setContent {
        //     MaterialTheme {
        //         val mockState = OverviewState(
        //             totalExpenses = 500.0,
        //             expensesByCategory = mapOf(
        //                 ExpenseCategory(name = "Food", description = "Restaurant and groceries", color = "#FF5733") to 200.0,
        //                 ExpenseCategory(name = "Travel", description = "Transportation expenses", color = "#33FF57") to 300.0
        //             ),
        //             recentExpenses = listOf(
        //                 ExpenseState(amount = 100.0, expenseCategoryId = 1, paymentMethod = PaymentMethod.CASH, description = "Dinner", createdAt = Date()),
        //                 ExpenseState(amount = 150.0, expenseCategoryId = 2, paymentMethod = PaymentMethod.CREDIT_CARD, description = "Taxi Ride", createdAt = Date()),
        //                 ExpenseState(amount = 150.0, expenseCategoryId = 2, paymentMethod = PaymentMethod.CREDIT_CARD, description = "Taxi Ride2", createdAt = Date()),
        //                 ExpenseState(amount = 150.0, expenseCategoryId = 2, paymentMethod = PaymentMethod.CREDIT_CARD, description = "Taxi Ride3", createdAt = Date())
        //             )
        //         )
        //         OverviewScreen(state = mockState)
        //     }
        // }
        */
    }

    // Composable for ListViewContent, now integrated directly into the main setContent's `when` statement
    // for `Screen.Overview`. It's kept here as a separate function, but its direct call in `setContent` is removed.
    @Composable
    private fun ListViewContent() {
        val overviewState by expenseOverviewViewModel.state.collectAsState()

        OverviewScreen(state = overviewState)
    }

    // Placeholder for GraphViewContent, currently not implemented.
    @Composable
    private fun GraphViewContent() {
        Text("Graph View Content Here (Not Implemented)")
        // You would typically have a ViewModel and collect state here for graph data
    }

    // >>> NEW FUNCTION: Clears all data from the database tables.
    private suspend fun clearDatabase() {
        // IMPORTANT: Database operations MUST run on a background thread.
        // `withContext(Dispatchers.IO)` ensures this.
        withContext(Dispatchers.IO) {
            Log.d(LOG_TAG, "Attempting to clear database...")
            db.clearAllTables() // This Room method deletes all data from all tables.
            Log.d(LOG_TAG, "Database cleared successfully.")
        }
    }


    private suspend fun populateDatabase() {
        // IMPORTANT: All Room DAO calls (like getAllExpenseCategories(), insertExpenseCategory(), insertExpense())
        // are blocking and MUST run on a background thread.
        // `withContext(Dispatchers.IO)` ensures this for the entire block.
        withContext(Dispatchers.IO) {
            val categoryDao = db.expenseCategoryDao()
            val expenseDao = db.expenseDao()

            val categories = categoryDao.getAllExpenseCategories()
            Log.d(LOG_TAG, "Categories in DB: ${categories.size}")  // Log the size of the categories

            // IMPORTANT: The 'if (categories.isEmpty())' condition below ensures that
            // sample data is only inserted if the database is currently empty.
            // This is crucial, especially when using the 'CLEAR_DB_ON_START' flag,
            // as clearing the DB will make it empty, allowing population.
            // If you always want to re-insert regardless of existing data, change to 'if (true)'.
            if (categories.isEmpty()) { // Changed from 'if (true)' to correctly populate only if empty
                Log.d(LOG_TAG, "Database is empty or was cleared. Populating with sample data...")

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
                Log.d(LOG_TAG, "Database already populated with categories. Skipping sample data insertion.")
            }
        }
    }

}

