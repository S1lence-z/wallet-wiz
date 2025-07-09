package com.example.walletwiz.data.repository

import com.example.walletwiz.data.entity.ExpenseCategory
import com.example.walletwiz.utils.Result
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the Expense Category Repository.
 * Defines the contract for all expense category-related data operations.
 */
interface IExpenseCategoryRepository {
    /**
     * Inserts a new expense category into the database or updates an existing one.
     * @param expenseCategory The ExpenseCategory entity to insert or update.
     * @return A Result indicating the success (with the ID) or failure of the operation.
     */
    suspend fun insertOrUpdateCategory(expenseCategory: ExpenseCategory): Result<Long>

    /**
     * Inserts a list of default categories into the database.
     * @param expenseCategories The list of default ExpenseCategory entities to insert.
     * @return A Result indicating the success or failure of the operation.
     */
    suspend fun insertDefaultCategories(expenseCategories: List<ExpenseCategory>): Result<Unit>

    /**
     * Retrieves all expense categories from the database as a Flow.
     * @return A Flow emitting a list of ExpenseCategory entities. Error handling for flow collection
     * is typically done at the collector side (ViewModel).
     */
    fun getAllCategories(): Flow<List<ExpenseCategory>>

    /**
     * Retrieves a specific expense category by its ID as a Flow.
     * @param id The ID of the expense category to retrieve.
     * @return A Flow emitting the ExpenseCategory entity, or null if not found. Error handling for flow collection
     * is typically done at the collector side (ViewModel).
     */
    fun getCategoryById(id: Int): Flow<ExpenseCategory?>

    /**
     * Deletes an expense category from the database.
     * @param expenseCategory The ExpenseCategory entity to delete.
     * @return A Result indicating the success or failure of the operation.
     */
    suspend fun deleteCategory(expenseCategory: ExpenseCategory): Result<Unit>
}
