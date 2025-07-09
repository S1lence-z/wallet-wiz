package com.example.walletwiz.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import org.koin.androidx.compose.koinViewModel
import com.example.walletwiz.ui.*
import com.example.walletwiz.viewmodels.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationHost(
    navController: NavHostController = rememberNavController()
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
                val overviewViewModel: ExpenseOverviewViewModel = koinViewModel()
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
                val expenseViewModel: ExpenseViewModel = koinViewModel()
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
                val categoryViewModel: ExpenseCategoryViewModel = koinViewModel()
                val categoryState by categoryViewModel.state.collectAsStateWithLifecycle()
                ExpenseCategoryScreen(
                    state = categoryState,
                    onEvent = categoryViewModel::onEvent
                )
            }

            composable(route = AppDestinations.NOTIFICATION_SETTINGS_ROUTE) {
                val settingsViewModel: NotificationSettingsViewModel = koinViewModel()
                val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()
                NotificationSettingsScreen(
                    state = settingsState,
                    onEvent = settingsViewModel::onEvent
                )
            }
        }
    }
}
