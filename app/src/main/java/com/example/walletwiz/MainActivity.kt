package com.example.walletwiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkManager

import com.example.walletwiz.data.NotificationSettingsRepository
import com.example.walletwiz.data.database.AppDatabase
import com.example.walletwiz.data.repository.ExpenseCategoryRepositoryImpl
import com.example.walletwiz.data.repository.ExpenseRepositoryImpl
import com.example.walletwiz.data.repository.TagRepositoryImpl
import com.example.walletwiz.ui.OverviewScreen
import com.example.walletwiz.ui.ExpenseScreen
import com.example.walletwiz.ui.ExpenseCategoryScreen
import com.example.walletwiz.ui.NotificationSettingsScreen
import com.example.walletwiz.viewmodels.ExpenseOverviewViewModel
import com.example.walletwiz.viewmodels.ExpenseViewModel
import com.example.walletwiz.viewmodels.ExpenseCategoryViewModel
import com.example.walletwiz.viewmodels.NotificationSettingsViewModel

object AppDestinations {
    const val OVERVIEW_ROUTE = "overview"
    const val ADD_EDIT_EXPENSE_ROUTE = "add_edit_expense"
    const val CATEGORIES_ROUTE = "categories"
    const val NOTIFICATION_SETTINGS_ROUTE = "notification_settings"
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

class MainActivity : ComponentActivity() {

    private val appDatabase by lazy { AppDatabase.invoke(this.applicationContext) }

    private val expenseRepository by lazy { ExpenseRepositoryImpl(appDatabase.expenseDao()) }
    private val expenseCategoryRepository by lazy { ExpenseCategoryRepositoryImpl(appDatabase.expenseCategoryDao()) }
    private val tagRepository by lazy { TagRepositoryImpl(appDatabase.tagDao()) }
    private val notificationSettingsRepository by lazy { NotificationSettingsRepository(this.applicationContext) }
    private val workManager by lazy { WorkManager.getInstance(this.applicationContext) }


    private val expenseOverviewViewModelFactory by lazy {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ExpenseOverviewViewModel::class.java)) {
                    return ExpenseOverviewViewModel(
                        expenseRepository = expenseRepository,
                        expenseCategoryRepository = expenseCategoryRepository
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }

    private val expenseViewModelFactory by lazy {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
                    return ExpenseViewModel(
                        expenseRepository = expenseRepository,
                        expenseCategoryRepository = expenseCategoryRepository,
                        tagRepository = tagRepository
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }

    private val expenseCategoryViewModelFactory by lazy {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ExpenseCategoryViewModel::class.java)) {
                    // Pass the repository interface
                    return ExpenseCategoryViewModel(expenseCategoryRepository = expenseCategoryRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }

    private val notificationSettingsViewModelFactory by lazy {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(NotificationSettingsViewModel::class.java)) {
                    return NotificationSettingsViewModel(
                        notificationSettingsRepository = notificationSettingsRepository,
                        workManager = workManager
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppNavigationHost(
                    expenseOverviewViewModelFactory = expenseOverviewViewModelFactory,
                    expenseViewModelFactory = expenseViewModelFactory,
                    expenseCategoryViewModelFactory = expenseCategoryViewModelFactory,
                    notificationSettingsViewModelFactory = notificationSettingsViewModelFactory
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationHost(
    navController: NavHostController = rememberNavController(),
    expenseOverviewViewModelFactory: ViewModelProvider.Factory,
    expenseViewModelFactory: ViewModelProvider.Factory,
    expenseCategoryViewModelFactory: ViewModelProvider.Factory,
    notificationSettingsViewModelFactory: ViewModelProvider.Factory
) {
    val bottomNavItems = listOf(
        BottomNavItem("Overview", Icons.Filled.Home, AppDestinations.OVERVIEW_ROUTE),
        BottomNavItem("Add New", Icons.Filled.AddCircle, AppDestinations.ADD_EDIT_EXPENSE_ROUTE),
        BottomNavItem("Categories", Icons.Filled.List, AppDestinations.CATEGORIES_ROUTE),
        BottomNavItem("Settings", Icons.Filled.Settings, AppDestinations.NOTIFICATION_SETTINGS_ROUTE)
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val shouldShowBottomBar = bottomNavItems.any { it.route == currentRoute }

            if (shouldShowBottomBar) {
                AppBottomNavigationBar(navController = navController, items = bottomNavItems)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestinations.OVERVIEW_ROUTE,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = AppDestinations.OVERVIEW_ROUTE) {
                val overviewViewModel: ExpenseOverviewViewModel = viewModel(factory = expenseOverviewViewModelFactory)
                val overviewState by overviewViewModel.state.collectAsStateWithLifecycle()
                OverviewScreen(
                    state = overviewState,
                    overviewViewModel = overviewViewModel,
                    onEditClicked = { expenseState ->
                        if (expenseState.id != null) {
                            navController.navigate("${AppDestinations.ADD_EDIT_EXPENSE_ROUTE}?expenseId=${expenseState.id}")
                        } else {
                            navController.navigate(AppDestinations.ADD_EDIT_EXPENSE_ROUTE)
                        }
                    }
                )
            }

            composable(
                route = "${AppDestinations.ADD_EDIT_EXPENSE_ROUTE}?expenseId={expenseId}"
            ) { backStackEntry ->
                val expenseIdString = backStackEntry.arguments?.getString("expenseId")
                val expenseViewModel: ExpenseViewModel = viewModel(factory = expenseViewModelFactory)
                val expenseState by expenseViewModel.state.collectAsStateWithLifecycle()

                ExpenseScreen(
                    state = expenseState,
                    onEvent = expenseViewModel::onEvent,
                    onSaveClicked = {
                        navController.popBackStack()
                    }
                )
            }

            composable(route = AppDestinations.CATEGORIES_ROUTE) {
                val categoryViewModel: ExpenseCategoryViewModel = viewModel(factory = expenseCategoryViewModelFactory)
                val categoryState by categoryViewModel.state.collectAsStateWithLifecycle()
                ExpenseCategoryScreen(
                    state = categoryState,
                    onEvent = categoryViewModel::onEvent
                )
            }

            composable(route = AppDestinations.NOTIFICATION_SETTINGS_ROUTE) {
                val settingsViewModel: NotificationSettingsViewModel = viewModel(factory = notificationSettingsViewModelFactory)
                val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()
                NotificationSettingsScreen(
                    state = settingsState,
                    onEvent = settingsViewModel::onEvent
                )
            }
        }
    }
}

@Composable
fun AppBottomNavigationBar(navController: NavHostController, items: List<BottomNavItem>) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.route?.startsWith(item.route) == true } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
