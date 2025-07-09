package com.example.walletwiz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletwiz.data.entity.*
import com.example.walletwiz.data.repository.IExpenseCategoryRepository
import com.example.walletwiz.data.repository.IExpenseRepository
import com.example.walletwiz.data.repository.ITagRepository
import com.example.walletwiz.events.ExpenseEvent
import com.example.walletwiz.states.ExpenseState
import com.example.walletwiz.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Log
import java.time.Instant
import java.util.Date

class ExpenseViewModel(
    private val expenseRepository: IExpenseRepository,
    private val expenseCategoryRepository: IExpenseCategoryRepository,
    private val tagRepository: ITagRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ExpenseState())
    val state = _state.asStateFlow()
    private val _isExpenseSaved = MutableStateFlow(false)

    init {
        loadCategories()
        loadAllTags()
    }

    fun onEvent(event: ExpenseEvent) {
        when (event) {
            is ExpenseEvent.SaveExpense -> {
                saveExpense()
            }
            is ExpenseEvent.SetAmount -> {
                _state.update { it.copy(amount = event.amount) }
            }
            is ExpenseEvent.SetExpenseCategory -> {
                _state.update { it.copy(expenseCategoryId = event.expenseCategoryId) }
            }
            is ExpenseEvent.SetCreatedAt -> {
                _state.update { it.copy(createdAt = event.createdAt) }
            }
            is ExpenseEvent.SetDescription -> {
                _state.update { it.copy(description = event.description) }
            }
            is ExpenseEvent.SetPaymentMethod -> {
                _state.update { it.copy(paymentMethod = event.paymentMethod) }
            }
            is ExpenseEvent.CreateExpenseCategory -> {
                createNewCategory(event.name)
            }
            is ExpenseEvent.AddTagToExpense -> {
                addTagToExpense(event.expenseId, event.tagName)
            }
            is ExpenseEvent.RemoveTagFromExpense -> {
                removeTagFromExpense(event.expenseId, event.tagId)
            }
            is ExpenseEvent.LoadTagsForExpense -> {
                loadTagsForExpense(event.expenseId)
            }
            is ExpenseEvent.CancelExpense -> {
                setDefaultFields()
            }
            is ExpenseEvent.LoadExpenseForEdit -> {
                if (event.expenseId != null) {
                    loadExpenseForEdit(event.expenseId)
                } else {
                    setDefaultFields()
                }
            }
        }
    }

    private fun setDefaultFields() {
        _state.update {
            it.copy(
                id = null,
                amount = 0.0,
                expenseCategoryId = 0,
                paymentMethod = PaymentMethod.DEBIT_CARD,
                description = null,
                createdAt = Date.from(Instant.now()),
                selectedExpenseWithTags = null,
                selectedTags = emptyList()
            )
        }
        _isExpenseSaved.value = false
    }

    private fun saveExpense() {
        val state = _state.value
        if (state.amount > 0 && state.expenseCategoryId != 0) {
            viewModelScope.launch {
                if (state.id == null) {
                    val newExpense = Expense(
                        id = null,
                        amount = state.amount,
                        expenseCategoryId = state.expenseCategoryId,
                        createdAt = state.createdAt,
                        description = state.description,
                        paymentMethod = state.paymentMethod
                    )
                    when (val result = expenseRepository.insertOrUpdateExpense(newExpense)) {
                        is Result.Success -> {
                            val expenseId = result.data.toInt()
                            Log.d("ExpenseViewModel", "Inserted expense with ID: $expenseId")
                            if (expenseId > 0) {
                                if (state.selectedTags.isNotEmpty()) {
                                    for (tag in state.selectedTags) {
                                        tagRepository.insertExpenseTagCrossRef(ExpenseTagCrossRef(expenseId, tag.id))
                                    }
                                }
                                _isExpenseSaved.value = true
                            } else {
                                Log.e("ExpenseViewModel", "Failed to insert expense: Returned ID was not positive.")
                            }
                        }
                        is Result.Error -> {
                            Log.e("ExpenseViewModel", "Failed to insert expense: ${result.exception.message}")
                        }
                        Result.Loading -> { }
                    }
                } else {
                    val existingExpense = Expense(
                        id = state.id,
                        amount = state.amount,
                        expenseCategoryId = state.expenseCategoryId,
                        createdAt = state.createdAt,
                        description = state.description,
                        paymentMethod = state.paymentMethod
                    )
                    when (val result = expenseRepository.insertOrUpdateExpense(existingExpense)) {
                        is Result.Success -> {
                            when (val deleteTagsResult = tagRepository.deleteAllTagsForExpense(state.id)) {
                                is Result.Success -> {
                                    if (state.selectedTags.isNotEmpty()) {
                                        for (tag in state.selectedTags) {
                                            tagRepository.insertExpenseTagCrossRef(ExpenseTagCrossRef(state.id, tag.id))
                                        }
                                    }
                                    _isExpenseSaved.value = true
                                }
                                is Result.Error -> {
                                    Log.e("ExpenseViewModel", "Failed to delete old tags: ${deleteTagsResult.exception.message}")
                                }
                                Result.Loading -> {}
                            }
                        }
                        is Result.Error -> {
                            Log.e("ExpenseViewModel", "Failed to update expense: ${result.exception.message}")
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

    private fun createNewCategory(name: String) {
        viewModelScope.launch {
            val newCategory = ExpenseCategory(name = name, description = null, color = "#000000")
            when (val result = expenseCategoryRepository.insertOrUpdateCategory(newCategory)) {
                is Result.Success -> {
                    val id = result.data.toInt()
                    if (id > 0) {
                        _state.update { currentState ->
                            currentState.copy(
                                expenseCategoryId = id
                            )
                        }
                    } else {
                        Log.e("ExpenseViewModel", "Failed to insert category: Returned ID was not positive.")
                    }
                }
                is Result.Error -> {
                    Log.e("ExpenseViewModel", "Failed to insert category: ${result.exception.message}")
                }
                Result.Loading -> { }
            }
        }
    }

    private fun addTagToExpense(expenseId: Int, tagName: String) {
        viewModelScope.launch {
            if (expenseId == 0) {
                when (val tagResult = tagRepository.getTagByName(tagName)) {
                    is Result.Success -> {
                        var tag = tagResult.data
                        if (tag == null) {
                            when (val newTagResult = tagRepository.insertTag(ExpenseTag(name = tagName))) {
                                is Result.Success -> tag = ExpenseTag(id = newTagResult.data.toInt(), name = tagName)
                                is Result.Error -> {
                                    Log.e("ExpenseViewModel", "Failed to insert new tag: ${newTagResult.exception.message}")
                                    return@launch
                                }
                                Result.Loading -> { }
                            }
                        }

                        tag?.let { nonNullTag ->
                            _state.update {
                                if (it.selectedTags.any { t -> t.id == nonNullTag.id }) {
                                    it
                                } else {
                                    it.copy(selectedTags = it.selectedTags + nonNullTag)
                                }
                            }
                        }
                    }
                    is Result.Error -> {
                        Log.e("ExpenseViewModel", "Failed to get tag by name: ${tagResult.exception.message}")
                    }
                    Result.Loading -> { }
                }
            } else {
                when (val existingTagResult = tagRepository.getTagByName(tagName)) {
                    is Result.Success -> {
                        var tagId: Int? = existingTagResult.data?.id
                        if (tagId == null) {
                            when (val newTagResult = tagRepository.insertTag(ExpenseTag(name = tagName))) {
                                is Result.Success -> tagId = newTagResult.data.toInt()
                                is Result.Error -> {
                                    Log.e("ExpenseViewModel", "Failed to insert new tag for cross-ref: ${newTagResult.exception.message}")
                                    return@launch
                                }
                                Result.Loading -> { }
                            }
                        }

                        tagId?.let { id ->
                            when (val crossRefResult = tagRepository.insertExpenseTagCrossRef(ExpenseTagCrossRef(expenseId, id))) {
                                is Result.Success -> loadTagsForExpense(expenseId)
                                is Result.Error -> Log.e("ExpenseViewModel", "Failed to insert cross-ref: ${crossRefResult.exception.message}")
                                Result.Loading -> {}
                            }
                        }
                    }
                    is Result.Error -> {
                        Log.e("ExpenseViewModel", "Failed to get tag by name for cross-ref: ${existingTagResult.exception.message}")
                    }
                    Result.Loading -> { }
                }
            }
        }
    }

    private fun removeTagFromExpense(expenseId: Int, tagId: Int) {
        viewModelScope.launch {
            if (expenseId == 0) {
                _state.update {
                    it.copy(selectedTags = it.selectedTags.filterNot { it.id == tagId })
                }
            } else {
                when (val result = tagRepository.removeTagFromExpense(expenseId, tagId)) {
                    is Result.Success -> loadTagsForExpense(expenseId)
                    is Result.Error -> Log.e("ExpenseViewModel", "Failed to remove tag from expense: ${result.exception.message}")
                    Result.Loading -> { }
                }
            }
        }
    }

    private fun loadTagsForExpense(expenseId: Int) {
        viewModelScope.launch {
            when (val result = tagRepository.getExpenseWithTags(expenseId)) {
                is Result.Success -> {
                    Log.d("ExpenseViewModel", "Loaded tags for expense ID $expenseId: ${result.data.tags}")
                    _state.update {
                        it.copy(
                            selectedExpenseWithTags = result.data,
                            selectedTags = result.data.tags
                        )
                    }
                }
                is Result.Error -> Log.e("ExpenseViewModel", "Failed to load tags for expense: ${result.exception.message}")
                Result.Loading -> { }
            }
        }
    }

    private fun loadAllTags() {
        viewModelScope.launch {
            when (val result = tagRepository.getAllTags()) {
                is Result.Success -> _state.update { it.copy(allTags = result.data) }
                is Result.Error -> Log.e("ExpenseViewModel", "Failed to load all tags: ${result.exception.message}")
                Result.Loading -> { }
            }
        }
    }

    private fun loadExpenseForEdit(expenseId: Int) {
        viewModelScope.launch {
            when (val result = expenseRepository.getExpenseById(expenseId)) {
                is Result.Success -> {
                    val expense = result.data
                    if (expense == null) {
                        Log.e("ExpenseViewModel", "Expense with ID $expenseId not found.")
                        return@launch
                    }

                    // Update the state with the loaded expense details
                    _state.update {
                        it.copy(
                            id = expense.id,
                            amount = expense.amount,
                            expenseCategoryId = expense.expenseCategoryId ?: 0,
                            paymentMethod = expense.paymentMethod,
                            description = expense.description,
                            createdAt = expense.createdAt
                        )
                    }
                    // Load the category name
                    val categoryResult = expenseCategoryRepository.getCategoryById(expense.expenseCategoryId ?: 0)
                    categoryResult.collect { category ->
                        _state.update { it.copy(categoryName = category?.name ?: "Uncategorized") }
                    }
                    // Load the tags
                    loadTagsForExpense(expenseId)
                }
                is Result.Error -> { }
                is Result.Loading -> { }
            }
        }
    }
}