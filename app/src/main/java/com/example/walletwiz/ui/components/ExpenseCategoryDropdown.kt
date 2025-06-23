package com.example.walletwiz.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.walletwiz.data.entity.ExpenseCategory
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseCategoryDropdown(
    categories: List<ExpenseCategory>,
    selectedCategoryId: Int?,
    onCategorySelected: (Int) -> Unit,
    onNewCategoryCreated: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var isCreatingNew by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Expense Category", style = MaterialTheme.typography.labelMedium)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = categories.find { it.id == selectedCategoryId }?.name ?: "Select a category",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand category menu")
                    }
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            onCategorySelected(category.id ?: 0)
                            expanded = false
                        }
                    )
                }
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Create new category") },
                    onClick = { isCreatingNew = true; expanded = false }
                )
            }
        }

        if (isCreatingNew) {
            OutlinedTextField(
                value = newCategoryName,
                onValueChange = { newCategoryName = it },
                label = { Text("New Category Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    if (newCategoryName.isNotBlank()) {
                        onNewCategoryCreated(newCategoryName)
                        isCreatingNew = false
                        newCategoryName = ""
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Add Category")
            }
        }
    }
}

