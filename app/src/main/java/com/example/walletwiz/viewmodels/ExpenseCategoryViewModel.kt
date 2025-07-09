package com.example.walletwiz.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletwiz.data.repository.IExpenseCategoryRepository
import com.example.walletwiz.events.ExpenseCategoryEvent
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.walletwiz.states.ExpenseCategoryState
import com.example.walletwiz.utils.Result
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExpenseCategoryViewModel(
    private val expenseCategoryRepository: IExpenseCategoryRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ExpenseCategoryState())
    val state get() = _state

    init {
        loadCategories()
    }

    fun onEvent(event: ExpenseCategoryEvent) {
        when (event) {
            is ExpenseCategoryEvent.SetName -> {
                _state.update { it.copy(selectedCategory = it.selectedCategory?.copy(name = event.name)) }
            }
            is ExpenseCategoryEvent.SetDescription -> {
                _state.update { it.copy(selectedCategory = it.selectedCategory?.copy(description = event.description)) }
            }
            is ExpenseCategoryEvent.SetColor -> {
                _state.update { it.copy(selectedCategory = it.selectedCategory?.copy(color = event.color)) }
            }
            is ExpenseCategoryEvent.SaveCategory -> {
                Log.d("ExpenseCategoryViewModel", "Saving category: ${_state.value.selectedCategory}")
                viewModelScope.launch { // No Dispatchers.IO here
                    _state.value.selectedCategory?.let { category ->
                        when (val result = expenseCategoryRepository.insertOrUpdateCategory(category)) {
                            is Result.Success -> {
                                val id = result.data.toInt()
                                if (id > 0) {
                                    _state.update { it.copy(selectedCategory = category.copy(id = id)) }
                                } else {
                                    Log.e("ExpenseCategoryViewModel", "Failed to insert/update category: Returned ID was not positive.")
                                }
                                loadCategories()
                                _state.update { it.copy(isEditing = false) }
                            }
                            is Result.Error -> {
                                Log.e("ExpenseCategoryViewModel", "Failed to insert/update category: ${result.exception.message}")
                            }
                            Result.Loading -> { }
                        }
                    }
                }
            }
            is ExpenseCategoryEvent.ShowEditDialog -> {
                _state.update { it.copy(isEditing = true) }
            }
            is ExpenseCategoryEvent.HideEditDialog -> {
                _state.update { it.copy(isEditing = false) }
            }
            is ExpenseCategoryEvent.SetSelectedCategory -> {
                _state.update { it.copy(selectedCategory = event.category) }
            }
            is ExpenseCategoryEvent.DeleteCategory -> {
                viewModelScope.launch { // No Dispatchers.IO here
                    when (val result = expenseCategoryRepository.deleteCategory(event.category)) {
                        is Result.Success -> {
                            Log.d("ExpenseCategoryViewModel", "Category deleted successfully.")
                            loadCategories()
                        }
                        is Result.Error -> {
                            Log.e("ExpenseCategoryViewModel", "Failed to delete category: ${result.exception.message}")
                        }
                        Result.Loading -> { }
                    }
                }
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            expenseCategoryRepository.getAllCategories().collect { categories ->
                _state.update { it.copy(categories = categories) }
            }
        }
    }
}
