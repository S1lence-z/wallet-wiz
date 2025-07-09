package com.example.walletwiz.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings

object NavigationItems {
    fun getBottomNavItems(): List<BottomNavItem> = listOf(
        BottomNavItem("Overview", Icons.Filled.Home, AppDestinations.OVERVIEW_ROUTE),
        BottomNavItem("Add New", Icons.Filled.AddCircle, AppDestinations.ADD_EDIT_EXPENSE_ROUTE),
        BottomNavItem("Categories", Icons.Filled.Menu, AppDestinations.CATEGORIES_ROUTE),
        BottomNavItem("Settings", Icons.Filled.Settings, AppDestinations.NOTIFICATION_SETTINGS_ROUTE)
    )
}
