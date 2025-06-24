package com.example.walletwiz.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.walletwiz.data.entity.ExpenseCategory
import com.example.walletwiz.events.ExpenseCategoryEvent
import com.example.walletwiz.states.ExpenseCategoryState
import androidx.core.graphics.toColorInt

@Composable
fun ExpenseCategoryScreen(state: ExpenseCategoryState, onEvent: (ExpenseCategoryEvent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Header Section
        Text(
            text = "Expense Categories",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Row for Add Category Button
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, top = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = {
                onEvent(ExpenseCategoryEvent.SetSelectedCategory(ExpenseCategory(id = null, name = "", description = null, color = "#FFFFFF")))
                onEvent(ExpenseCategoryEvent.ShowEditDialog)
            }) {
                Text("Add Category")
            }
        }

        // Categories List
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.categories.size) { index ->
                val category = state.categories[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = try {
                            Color(category.color.toColorInt())
                        } catch (e: IllegalArgumentException) {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    onClick = {
                        onEvent(ExpenseCategoryEvent.SetSelectedCategory(category))
                        onEvent(ExpenseCategoryEvent.ShowEditDialog)
                    }
                ) {
                    Text(
                        text = "${category.name} - ${category.description ?: "No description"}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        if (state.isEditing) {
            AlertDialog(
                onDismissRequest = { onEvent(ExpenseCategoryEvent.HideEditDialog) },
                title = { Text(if (state.selectedCategory?.id == null) "Add Category" else "Edit Category") },
                text = {
                    Column {
                        TextField(
                            value = state.selectedCategory?.name ?: "",
                            onValueChange = { onEvent(ExpenseCategoryEvent.SetName(it)) },
                            label = { Text("Name") },
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                        TextField(
                            value = state.selectedCategory?.description ?: "",
                            onValueChange = { onEvent(ExpenseCategoryEvent.SetDescription(it)) },
                            label = { Text("Description") },
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                        TextField(
                            value = state.selectedCategory?.color ?: "",
                            onValueChange = { onEvent(ExpenseCategoryEvent.SetColor(it)) },
                            label = { Text("Color (e.g. #FF0000)") },
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { onEvent(ExpenseCategoryEvent.SaveCategory) }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    if (state.selectedCategory?.id != null) {
                        Button(onClick = {
                            onEvent(ExpenseCategoryEvent.DeleteCategory(state.selectedCategory))
                            onEvent(ExpenseCategoryEvent.HideEditDialog)
                        }) {
                            Text("Delete")
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                textContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}