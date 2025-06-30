package com.example.walletwiz.ui.layout

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import com.example.walletwiz.events.ExpenseEvent
import com.example.walletwiz.states.ExpenseState
import com.example.walletwiz.ui.*
import com.example.walletwiz.viewmodels.*

@Composable
fun MainLayout(
    expenseViewModel: ExpenseViewModel,
    overviewViewModel: ExpenseOverviewViewModel,
    expenseCategoryViewModel: ExpenseCategoryViewModel,
    notificationSettingsViewModel: NotificationSettingsViewModel
) {
    val navItems = listOf(
        NavItem(
            title = "Overview",
            icon = Icons.Default.Home,
            route = "overview"
        ),
        NavItem(
            title = "Expenses",
            icon = Icons.Default.Add,
            route = "expenses"
        ),
        NavItem(
            title = "Categories",
            icon = Icons.Default.Add,
            route = "expenseCategories"
        ),
        NavItem(
            title = "Notifications",
            icon = Icons.Default.Notifications,
            route = "notifications"
        )
    )

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    val isExpenseSaved by expenseViewModel.isExpenseSaved.collectAsState()

    LaunchedEffect(isExpenseSaved) {
        if (isExpenseSaved) {
            selectedIndex = 0
            expenseViewModel.resetSavedFlag()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar ()
            {
                navItems.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            // Pokud přecházíme na Expense tab, vždy resetuj na přidávání nového výdaje
                            if (index == 1) {
                                expenseViewModel.onEvent(ExpenseEvent.CancelExpense)
                            }
                            selectedIndex = index
                        },
                        icon = {
                            Icon(imageVector = navItem.icon, contentDescription = navItem.title)
                        },
                        label = {
                            Text(text = navItem.title)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        ContentScreen(
            modifier = Modifier.padding(innerPadding),
            selectedIndex = selectedIndex,
            expenseViewModel = expenseViewModel,
            overviewViewModel = overviewViewModel,
            expenseCategoryViewModel = expenseCategoryViewModel,
            notificationSettingsViewModel = notificationSettingsViewModel,
            onNavigateToExpenseEdit = { expense ->
                expenseViewModel.onEvent(ExpenseEvent.SetExpenseForEdit(expense))
                selectedIndex = 1
            },
            navigateToExpenseOverview = {
                selectedIndex = 0
            }
        )
    }
}

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    expenseViewModel: ExpenseViewModel,
    overviewViewModel: ExpenseOverviewViewModel,
    expenseCategoryViewModel: ExpenseCategoryViewModel,
    notificationSettingsViewModel: NotificationSettingsViewModel,
    onNavigateToExpenseEdit: (ExpenseState) -> Unit,
    navigateToExpenseOverview: () -> Unit
) {
    when (selectedIndex) {
        0 -> OverviewScreen(
            modifier = modifier,
            state = overviewViewModel.state.collectAsState().value,
            overviewViewModel = overviewViewModel,
            onEditClicked = onNavigateToExpenseEdit
        )
        1 -> ExpenseScreen(
            state = expenseViewModel.state.collectAsState().value,
            onEvent = expenseViewModel::onEvent,
            onSaveClicked = navigateToExpenseOverview
        )
        2 -> ExpenseCategoryScreen(
            state = expenseCategoryViewModel.state.collectAsState().value,
            onEvent = expenseCategoryViewModel::onEvent
        )
        3 -> NotificationSettingsScreen(
            state = notificationSettingsViewModel.state.collectAsState().value,
            onEvent = notificationSettingsViewModel::onEvent
        )
        else -> {
            Text(text = "Invalid selection", modifier = modifier)
        }
    }
}