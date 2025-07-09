package com.example.walletwiz.data.repository

import com.example.walletwiz.data.dao.ExpenseDao
import com.example.walletwiz.data.entity.Expense
import com.example.walletwiz.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of IExpenseRepository.
 * This class acts as a single source of truth for expense data,
 * abstracting the underlying data source (Room DAO in this case).
 */
class ExpenseRepositoryImpl(private val expenseDao: ExpenseDao) : IExpenseRepository {

    /**
     * Inserts a new expense or updates an existing one.
     * Delegates the operation to the ExpenseDao and handles potential errors.
     * @param expense The Expense entity to insert or update.
     * @return A Result indicating the success (with the ID) or failure of the operation.
     */
    override suspend fun insertOrUpdateExpense(expense: Expense): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val id = expenseDao.insertExpense(expense)
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Retrieves all expenses.
     * Delegates the operation to the ExpenseDao. Flow collection handles its own dispatching,
     * so explicit withContext is not needed here for the flow itself, but the underlying
     * DAO operations are already on IO.
     * @return A Flow emitting a list of Expense entities.
     */
    override fun getAllExpenses(): Flow<List<Expense>> {
        return expenseDao.getAllExpenses()
    }

    /**
     * Retrieves a specific expense by its ID.
     * Delegates the operation to the ExpenseDao and handles potential errors.
     * @param id The ID of the expense to retrieve.
     * @return A Result indicating the success (with the Expense entity) or failure (if not found or error).
     */
    override suspend fun getExpenseById(id: Int): Result<Expense?> = withContext(Dispatchers.IO) {
        try {
            val expense = expenseDao.getExpenseById(id)
            Result.Success(expense)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Deletes an expense.
     * Delegates the operation to the ExpenseDao and handles potential errors.
     * @param expense The Expense entity to delete.
     * @return A Result indicating the success or failure of the operation.
     */
    override suspend fun deleteExpense(expense: Expense): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            expenseDao.deleteExpense(expense)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Retrieves the total amount spent today.
     * Delegates the operation to the ExpenseDao and handles potential errors.
     * @return A Result indicating the success (with the total amount) or failure.
     */
    override suspend fun getTodayTotal(): Result<Double> = withContext(Dispatchers.IO) {
        try {
            val total = expenseDao.getTodayTotal()
            Result.Success(total)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Retrieves the total amount spent this week.
     * Delegates the operation to the ExpenseDao and handles potential errors.
     * @return A Result indicating the success (with the total amount) or failure.
     */
    override suspend fun getWeekTotal(): Result<Double> = withContext(Dispatchers.IO) {
        try {
            val total = expenseDao.getWeekTotal()
            Result.Success(total)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Retrieves the total amount spent this month.
     * Delegates the operation to the ExpenseDao and handles potential errors.
     * @return A Result indicating the success (with the total amount) or failure.
     */
    override suspend fun getMonthTotal(): Result<Double> = withContext(Dispatchers.IO) {
        try {
            val total = expenseDao.getMonthTotal()
            Result.Success(total)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Retrieves the count of expenses today.
     * Delegates the operation to the ExpenseDao and handles potential errors.
     * @return A Result indicating the success (with the count) or failure.
     */
    override suspend fun getTodayExpenseCount(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val count = expenseDao.getTodayExpenseCount()
            Result.Success(count)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Retrieves the count of expenses this week.
     * Delegates the operation to the ExpenseDao and handles potential errors.
     * @return A Result indicating the success (with the count) or failure.
     */
    override suspend fun getWeekExpenseCount(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val count = expenseDao.getWeekExpenseCount()
            Result.Success(count)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Retrieves the count of expenses this month.
     * Delegates the operation to the ExpenseDao and handles potential errors.
     * @return A Result indicating the success (with the count) or failure.
     */
    override suspend fun getMonthExpenseCount(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val count = expenseDao.getMonthExpenseCount()
            Result.Success(count)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
