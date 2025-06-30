package com.example.walletwiz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletwiz.data.dao.ExpenseDao
import com.example.walletwiz.data.dao.ExpenseCategoryDao
import com.example.walletwiz.data.dao.TagDao
import com.example.walletwiz.data.entity.*
import com.example.walletwiz.events.ExpenseEvent
import com.example.walletwiz.states.ExpenseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import android.util.Log
import java.time.Instant
import java.util.Date

class ExpenseViewModel(
    private val expenseDao: ExpenseDao,
    private val expenseCategoryDao: ExpenseCategoryDao,
    private val tagDao: TagDao
) : ViewModel() {

    private val _state = MutableStateFlow(ExpenseState())
    val state = _state.asStateFlow()
    private val _isExpenseSaved = MutableStateFlow(false)
    val isExpenseSaved = _isExpenseSaved.asStateFlow()

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
            is ExpenseEvent.SetExpenseForEdit -> {
                _state.update {
                    it.copy(
                        id = event.expense.id,
                        amount = event.expense.amount,
                        expenseCategoryId = event.expense.expenseCategoryId,
                        paymentMethod = event.expense.paymentMethod,
                        description = event.expense.description,
                        createdAt = event.expense.createdAt,
                        categoryName = event.expense.categoryName,
                        selectedTags = event.expense.selectedTags
                    )
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

    fun resetSavedFlag() {
        _isExpenseSaved.value = false
    }

    private fun saveExpense() {
        val state = _state.value
        if (state.amount > 0 && state.expenseCategoryId != 0) {
            val expense = Expense(
                id = state.id ?: 0,
                amount = state.amount,
                expenseCategoryId = state.expenseCategoryId,
                createdAt = state.createdAt,
                description = state.description,
                paymentMethod = state.paymentMethod
            )
            viewModelScope.launch(Dispatchers.IO) {
                if (expense.id == 0) {
                    val expenseId = expenseDao.insertExpense(expense).toInt()
                    if (expenseId > 0) {
                        if (state.selectedTags.isNotEmpty()) {
                            for (tag in state.selectedTags) {
                                tagDao.insertExpenseTagCrossRef(ExpenseTagCrossRef(expenseId, tag.id))
                            }
                        }
                        _isExpenseSaved.value = true
                    } else {
                        Log.e("ExpenseViewModel", "Failed to insert expense")
                    }
                } else {
                    expenseDao.updateExpense(expense)
                    tagDao.deleteAllTagsForExpense(expense.id ?: 0)
                    if (state.selectedTags.isNotEmpty()) {
                        for (tag in state.selectedTags) {
                            tagDao.insertExpenseTagCrossRef(ExpenseTagCrossRef(expense.id, tag.id))
                        }
                    }
                    _isExpenseSaved.value = true
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

    private fun createNewCategory(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newCategory = ExpenseCategory(name = name, description = null, color = "#000000")
            val id = expenseCategoryDao.insertCategory(newCategory).toInt()

            if (id > 0) {
                _state.update { currentState ->
                    currentState.copy(
                        expenseCategoryId = id
                    )
                }
            } else {
                Log.e("ExpenseViewModel", "Failed to insert category: $name")
            }
        }
    }

    private fun addTagToExpense(expenseId: Int, tagName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (expenseId == 0) {
                var tag = tagDao.getTagByName(tagName)

                if (tag == null) {
                    val newTagId = tagDao.insertTag(ExpenseTag(name = tagName))
                    tag = ExpenseTag(id = newTagId.toInt(), name = tagName)
                }

                _state.update {
                    if (it.selectedTags.any { t -> t.id == tag.id }) {
                        it
                    } else {
                        it.copy(selectedTags = it.selectedTags + tag)
                    }
                }
            } else {
                val existingTag = tagDao.getTagByName(tagName)
                val tagId = existingTag?.id ?: tagDao.insertTag(ExpenseTag(name = tagName)).toInt()

                tagDao.insertExpenseTagCrossRef(ExpenseTagCrossRef(expenseId, tagId))

                loadTagsForExpense(expenseId)
            }
        }
    }

    private fun removeTagFromExpense(expenseId: Int, tagId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (expenseId == 0) {
                _state.update {
                    it.copy(selectedTags = it.selectedTags.filterNot { it.id == tagId })
                }
            } else {
                tagDao.removeTagFromExpense(expenseId, tagId)
                loadTagsForExpense(expenseId)
            }
        }
    }

    private fun loadTagsForExpense(expenseId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val expenseWithTags = tagDao.getExpenseWithTags(expenseId)
            _state.update { it.copy(selectedExpenseWithTags = expenseWithTags) }
        }
    }

    private fun loadAllTags() {
        viewModelScope.launch(Dispatchers.IO) {
            val allTags = tagDao.getAllTags()
            _state.update { it.copy(allTags = allTags) }
        }
    }
}