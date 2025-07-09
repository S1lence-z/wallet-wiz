package com.example.walletwiz.data.repository

import com.example.walletwiz.data.dao.ExpenseCategoryDao
import com.example.walletwiz.data.entity.ExpenseCategory
import com.example.walletwiz.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of IExpenseCategoryRepository.
 * This class acts as a single source of truth for expense category data.
 */
class ExpenseCategoryRepositoryImpl(private val expenseCategoryDao: ExpenseCategoryDao) : IExpenseCategoryRepository {

    /**
     * Inserts a new expense category or updates an existing one.
     * Delegates the operation to the ExpenseCategoryDao and handles potential errors.
     * @param expenseCategory The ExpenseCategory entity to insert or update.
     * @return A Result indicating the success (with the ID) or failure of the operation.
     */
    override suspend fun insertOrUpdateCategory(expenseCategory: ExpenseCategory): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val id = expenseCategoryDao.insertCategory(expenseCategory)
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Inserts a list of default categories.
     * Delegates the operation to the ExpenseCategoryDao and handles potential errors.
     * @param expenseCategories The list of default ExpenseCategory entities to insert.
     * @return A Result indicating the success or failure of the operation.
     */
    override suspend fun insertDefaultCategories(expenseCategories: List<ExpenseCategory>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            expenseCategoryDao.insertDefaultCategories(expenseCategories)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Retrieves all expense categories.
     * Delegates the operation to the ExpenseCategoryDao. Flow collection handles its own dispatching.
     * @return A Flow emitting a list of ExpenseCategory entities.
     */
    override fun getAllCategories(): Flow<List<ExpenseCategory>> {
        return expenseCategoryDao.getAllCategories()
    }

    /**
     * Retrieves a specific expense category by its ID.
     * Delegates the operation to the ExpenseCategoryDao. Flow collection handles its own dispatching.
     * @param id The ID of the expense category to retrieve.
     * @return A Flow emitting the ExpenseCategory entity, or null if not found.
     */
    override fun getCategoryById(id: Int): Flow<ExpenseCategory?> {
        return expenseCategoryDao.getCategoryById(id)
    }

    /**
     * Deletes an expense category.
     * Delegates the operation to the ExpenseCategoryDao and handles potential errors.
     * @param expenseCategory The ExpenseCategory entity to delete.
     * @return A Result indicating the success or failure of the operation.
     */
    override suspend fun deleteCategory(expenseCategory: ExpenseCategory): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            expenseCategoryDao.deleteCategory(expenseCategory)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
