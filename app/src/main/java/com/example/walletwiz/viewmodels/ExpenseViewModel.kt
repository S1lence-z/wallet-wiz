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
    val state get() = _state

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
                Log.d("ExpenseViewModel", "CreateExpenseCategory event received: ${event.name}")
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
        }
    }

    private fun setDefaultFields() {
        _state.update {
            it.copy(
                amount = 0.0,
                expenseCategoryId = 0,
                paymentMethod = PaymentMethod.DEBIT_CARD,
                description = null,
                createdAt = Date.from(Instant.now()),
                selectedExpenseWithTags = null,
                selectedTags = emptyList()
            )
        }
    }

    private fun saveExpense() {
        val state = _state.value
        if (state.amount > 0 && state.expenseCategoryId != 0) {
            val newExpense = Expense(
                amount = state.amount,
                expenseCategoryId = state.expenseCategoryId,
                createdAt = state.createdAt,
                description = state.description,
                paymentMethod = state.paymentMethod
            )
            viewModelScope.launch(Dispatchers.IO) {
                val expenseId = expenseDao.insertExpense(newExpense).toInt()

                // ✅ Save associated tags if they exist
                if (state.selectedTags.isNotEmpty()) {
                    for (tag in state.selectedTags) {
                        addTagToExpense(expenseId, tag.name)
                    }
                } else {
                    Log.e("ExpenseViewModel", "Failed to insert expense")
                }
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            val categories = expenseCategoryDao.getAllCategories()
            _state.update { it.copy(categories = categories) }
        }
    }

    private fun createNewCategory(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newCategory = ExpenseCategory(name = name, description = null, color = "#000000")
            val id = expenseCategoryDao.insert(newCategory).toInt()

            Log.d("ExpenseViewModel", "Inserted Category ID: $id")
            println("Inserted Category ID: $id")

            if (id > 0) {
                val updatedCategory = newCategory.copy(id = id)

                _state.update { currentState ->
                    currentState.copy(
                        categories = currentState.categories + updatedCategory,
                        expenseCategoryId = id
                    )
                }

                loadCategories()
            } else {
                Log.e("ExpenseViewModel", "Failed to insert category: $name")
            }
        }
    }

    // -----------------------------------
    // 🔹 TAG MANAGEMENT FUNCTIONS
    // -----------------------------------

    private fun addTagToExpense(expenseId: Int, tagName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingTag = tagDao.getTagByName(tagName)
            val tagId = existingTag?.id ?: tagDao.insertTag(ExpenseTag(name = tagName)).toInt()

            tagDao.insertExpenseTagCrossRef(ExpenseTagCrossRef(expenseId, tagId))

            loadTagsForExpense(expenseId) // ✅ Refresh tags for expense
        }
    }

    private fun removeTagFromExpense(expenseId: Int, tagId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            tagDao.removeTagFromExpense(expenseId, tagId)
            loadTagsForExpense(expenseId) // ✅ Refresh tags for expense
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
