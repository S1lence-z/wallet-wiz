package com.example.walletwiz.data.repository

import com.example.walletwiz.data.entity.Expense
import com.example.walletwiz.states.ExpenseState
import com.example.walletwiz.utils.Result
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the Expense Repository.
 * Defines the contract for all expense-related data operations.
 */
interface IExpenseRepository {
    /**
     * Inserts a new expense into the database or updates an existing one.
     * @param expense The Expense entity to insert or update.
     * @return A Result indicating the success (with the ID) or failure of the operation.
     */
    suspend fun insertOrUpdateExpense(expense: Expense): Result<Long>

    /**
     * Retrieves all expenses from the database as a Flow.
     * @return A Flow emitting a list of Expense entities. Error handling for flow collection
     * is typically done at the collector side (ViewModel).
     */
    fun getAllExpenses(): Flow<List<Expense>>

    /**
     * Retrieves a specific expense by its ID.
     * @param id The ID of the expense to retrieve.
     * @return A Result indicating the success (with the Expense entity) or failure (if not found or error).
     */
    suspend fun getExpenseById(id: Int): Result<Expense?>

    /**
     * Deletes an expense from the database.
     * @param expense The Expense entity to delete.
     * @return A Result indicating the success or failure of the operation.
     */
    suspend fun deleteExpense(expense: Expense): Result<Unit>

    /**
     * Retrieves the total amount spent today.
     * @return A Result indicating the success (with the total amount) or failure.
     */
    suspend fun getTodayTotal(): Result<Double>

    /**
     * Retrieves the total amount spent this week.
     * @return A Result indicating the success (with the total amount) or failure.
     */
    suspend fun getWeekTotal(): Result<Double>

    /**
     * Retrieves the total amount spent this month.
     * @return A Result indicating the success (with the total amount) or failure.
     */
    suspend fun getMonthTotal(): Result<Double>

    /**
     * Retrieves the count of expenses today.
     * @return A Result indicating the success (with the count) or failure.
     */
    suspend fun getTodayExpenseCount(): Result<Int>

    /**
     * Retrieves the count of expenses this week.
     * @return A Result indicating the success (with the count) or failure.
     */
    suspend fun getWeekExpenseCount(): Result<Int>

    /**
     * Retrieves the count of expenses this month.
     * @return A Result indicating the success (with the count) or failure.
     */
    suspend fun getMonthExpenseCount(): Result<Int>
}
