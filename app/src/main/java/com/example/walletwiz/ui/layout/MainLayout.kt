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
import com.example.walletwiz.ui.*
import com.example.walletwiz.viewmodels.*

@Composable
fun MainLayout(
    expenseViewModel: ExpenseViewModel,
    overviewViewModel: OverviewViewModel,
    expenseCategoryViewModel: ExpenseCategoryViewModel
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
        )
    )

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed() { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                        },
                        icon = {
                            Icon(imageVector = navItem.icon, contentDescription = null)
                        },
                        label = {
                            Text(text = navItem.title)
                        }
                    )
                }
            }
        }
    ) {
        innerPadding ->
        ContentScreen(
            modifier = Modifier.padding(innerPadding),
            selectedIndex = selectedIndex,
            expenseViewModel = expenseViewModel,
            overviewViewModel = overviewViewModel,
            expenseCategoryViewModel = expenseCategoryViewModel
        )
    }
}

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    expenseViewModel: ExpenseViewModel,
    overviewViewModel: OverviewViewModel,
    expenseCategoryViewModel: ExpenseCategoryViewModel
    ) {
    when (selectedIndex) {
        0 -> OverviewScreen(
            state = overviewViewModel.state.collectAsState().value
        )
        1 -> ExpenseScreen(
            state = expenseViewModel.state.collectAsState().value,
            onEvent = expenseViewModel::onEvent
        )
        2 -> ExpenseCategoryScreen(
            state = expenseCategoryViewModel.state.collectAsState().value
        )
    }
}