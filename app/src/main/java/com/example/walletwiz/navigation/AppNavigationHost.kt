package com.example.walletwiz.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.example.walletwiz.ui.OverviewScreen
import com.example.walletwiz.ui.ExpenseScreen
import com.example.walletwiz.ui.ExpenseCategoryScreen
import com.example.walletwiz.ui.NotificationSettingsScreen
import com.example.walletwiz.viewmodels.ExpenseOverviewViewModel
import com.example.walletwiz.viewmodels.ExpenseViewModel
import com.example.walletwiz.viewmodels.ExpenseCategoryViewModel
import com.example.walletwiz.viewmodels.NotificationSettingsViewModel
import com.example.walletwiz.data.repository.ExpenseRepositoryImpl
import com.example.walletwiz.data.repository.ExpenseCategoryRepositoryImpl
import com.example.walletwiz.data.repository.TagRepositoryImpl
import com.example.walletwiz.data.NotificationSettingsRepository
import androidx.work.WorkManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationHost(
    navController: NavHostController = rememberNavController(),
    expenseRepository: ExpenseRepositoryImpl,
    expenseCategoryRepository: ExpenseCategoryRepositoryImpl,
    tagRepository: TagRepositoryImpl,
    notificationSettingsRepository: NotificationSettingsRepository,
    workManager: WorkManager
) {
    val bottomNavItems = NavigationItems.getBottomNavItems()

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
                val overviewViewModel: ExpenseOverviewViewModel = viewModel {
                    ExpenseOverviewViewModel(
                        expenseRepository = expenseRepository,
                        expenseCategoryRepository = expenseCategoryRepository
                    )
                }
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
            ) { _ ->
                val expenseViewModel: ExpenseViewModel = viewModel {
                    ExpenseViewModel(
                        expenseRepository = expenseRepository,
                        expenseCategoryRepository = expenseCategoryRepository,
                        tagRepository = tagRepository
                    )
                }
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
                val categoryViewModel: ExpenseCategoryViewModel = viewModel {
                    ExpenseCategoryViewModel(expenseCategoryRepository = expenseCategoryRepository)
                }
                val categoryState by categoryViewModel.state.collectAsStateWithLifecycle()
                ExpenseCategoryScreen(
                    state = categoryState,
                    onEvent = categoryViewModel::onEvent
                )
            }

            composable(route = AppDestinations.NOTIFICATION_SETTINGS_ROUTE) {
                val settingsViewModel: NotificationSettingsViewModel = viewModel {
                    NotificationSettingsViewModel(
                        notificationSettingsRepository = notificationSettingsRepository,
                        workManager = workManager
                    )
                }
                val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()
                NotificationSettingsScreen(
                    state = settingsState,
                    onEvent = settingsViewModel::onEvent
                )
            }
        }
    }
}
