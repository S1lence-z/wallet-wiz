package com.example.walletwiz.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletwiz.data.dao.ExpenseCategoryDao
import com.example.walletwiz.events.ExpenseCategoryEvent
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.walletwiz.states.ExpenseCategoryState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExpenseCategoryViewModel(
    private val expenseCategoryDao: ExpenseCategoryDao
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
                viewModelScope.launch(Dispatchers.IO) {
                    _state.value.selectedCategory?.let { category ->
                        if (category.id == null) {
                            val id = expenseCategoryDao.insertCategory(category)
                            _state.update { it.copy(selectedCategory = category.copy(id = id.toInt())) }
                        } else {
                            expenseCategoryDao.updateCategory(category)
                        }
                    }
                    loadCategories()
                    _state.update { it.copy(isEditing = false) }
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
                viewModelScope.launch(Dispatchers.IO) {
                    expenseCategoryDao.deleteCategory(event.category)
                    loadCategories()
                }
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            expenseCategoryDao.getAllCategories().collect { categories ->
                _state.update { it.copy(categories = categories) }
            }
        }
    }
}