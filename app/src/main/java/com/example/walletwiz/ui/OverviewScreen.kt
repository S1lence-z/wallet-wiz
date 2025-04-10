package com.example.walletwiz.ui

import androidx.compose.runtime.Composable
import com.example.walletwiz.states.OverviewState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment

@Composable
fun OverviewScreen(state: OverviewState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Header Section
        Text(
            text = "Expense Overview",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp, top = 16.dp)
        )
        // Expense List Section
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // TODO: add expense items here
        }
    }
}