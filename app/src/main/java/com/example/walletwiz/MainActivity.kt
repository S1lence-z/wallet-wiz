package com.example.walletwiz

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.walletwiz.data.database.AppDatabase
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.walletwiz.ui.layout.*
import com.example.walletwiz.viewmodels.ExpenseCategoryViewModel
import com.example.walletwiz.viewmodels.OverviewViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

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
        AppDatabase.invoke(this)  // Use singleton instance
    }

    private val expenseViewModel by lazy {
        ExpenseViewModel(
            expenseDao = db.expenseDao(),
            expenseCategoryDao = db.expenseCategoryDao(),
            tagDao = db.tagDao()
        )
    }

    private val overviewViewModel by lazy {
        OverviewViewModel(
            expenseDao = db.expenseDao(),
            expenseCategoryDao = db.expenseCategoryDao()
        )
    }

    private val expenseCategoryViewModel by lazy {
        ExpenseCategoryViewModel(
            expenseCategoryDao = db.expenseCategoryDao()
        )
    }


    /*  // Option 2: Database initialization using Room.databaseBuilder (currently active)
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
*/

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
            MainLayout(
                expenseViewModel = expenseViewModel,
                overviewViewModel = overviewViewModel,
                expenseCategoryViewModel = expenseCategoryViewModel
            )
        }
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

                val funCategoryId = categoryDao.insertExpenseCategory(
                    ExpenseCategory(name = "Fun", description = "Hobbies, free-time activities", color = "#6495ED")
                ).toInt()

                // Insert expenses and log their creationS
                expenseDao.insertExpense(
                    Expense(amount = 50.0, expenseCategoryId = foodCategoryId, paymentMethod = PaymentMethod.CASH, description = "Dinner", createdAt = Date())
                )
                Log.d(LOG_TAG, "Inserted expense: Dinner (Amount: 50.0)")

                expenseDao.insertExpense(
                    Expense(amount = 120.0, expenseCategoryId = travelCategoryId, paymentMethod = PaymentMethod.CREDIT_CARD, description = "Taxi Ride", createdAt = Date())
                )
                Log.d(LOG_TAG, "Inserted expense: Taxi Ride (Amount: 120.0)")

                expenseDao.insertExpense(
                    Expense(amount = 10.0, expenseCategoryId = funCategoryId, paymentMethod = PaymentMethod.DEBIT_CARD, description = "Guitar Strings", createdAt = Date())
                )
                Log.d(LOG_TAG, "Inserted expense: Guitar Strings (Amount: 10.0)")
            } else {
                Log.d(LOG_TAG, "Database already populated with categories. Skipping sample data insertion.")
            }
        }
    }

}


        /*setContent {
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
        } */