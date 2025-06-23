package com.example.walletwiz.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.example.walletwiz.data.entity.ExpenseTag

@Composable
fun TagInput(
    tags: List<ExpenseTag>,
    onTagAdded: (String) -> Unit,
    onTagRemoved: (ExpenseTag) -> Unit
) {
    var tagInput by remember { mutableStateOf("") }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = tagInput,
                onValueChange = { tagInput = it },
                label = { Text("Add a Tag") },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    if (tagInput.isNotBlank()) {
                        onTagAdded(tagInput.trim())
                        tagInput = ""
                    }
                },
                modifier = Modifier.padding(start = 8.dp).align(Alignment.CenterVertically)
            ) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { tag ->
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(8.dp)
                        .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = tag.name, modifier = Modifier.padding(start = 8.dp, end = 16.dp))
                        IconButton(
                            onClick = { onTagRemoved(tag) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Remove tag")
                        }
                    }
                }
            }
        }
    }
}
