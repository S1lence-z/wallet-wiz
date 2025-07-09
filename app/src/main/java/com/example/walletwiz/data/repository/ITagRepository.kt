package com.example.walletwiz.data.repository

import com.example.walletwiz.data.entity.ExpenseTag
import com.example.walletwiz.data.entity.ExpenseTagCrossRef
import com.example.walletwiz.data.entity.ExpenseWithTags
import com.example.walletwiz.utils.Result

/**
 * Interface for the Tag Repository.
 * Defines the contract for all expense tag-related data operations.
 */
interface ITagRepository {
    /**
     * Inserts a new tag into the database.
     * @param tag The ExpenseTag entity to insert.
     * @return A Result indicating the success (with the ID) or failure of the operation.
     */
    suspend fun insertTag(tag: ExpenseTag): Result<Long>

    /**
     * Retrieves all tags from the database.
     * @return A Result indicating the success (with the list of tags) or failure.
     */
    suspend fun getAllTags(): Result<List<ExpenseTag>>

    /**
     * Retrieves a tag by its name.
     * @param tagName The name of the tag to retrieve.
     * @return A Result indicating the success (with the ExpenseTag entity or null) or failure.
     */
    suspend fun getTagByName(tagName: String): Result<ExpenseTag?>

    /**
     * Inserts a cross-reference between an expense and a tag.
     * @param crossRef The ExpenseTagCrossRef entity to insert.
     * @return A Result indicating the success or failure of the operation.
     */
    suspend fun insertExpenseTagCrossRef(crossRef: ExpenseTagCrossRef): Result<Unit>

    /**
     * Removes a specific tag from an expense.
     * @param expenseId The ID of the expense.
     * @param tagId The ID of the tag to remove.
     * @return A Result indicating the success or failure of the operation.
     */
    suspend fun removeTagFromExpense(expenseId: Int, tagId: Int): Result<Unit>

    /**
     * Deletes all tags associated with a specific expense.
     * @param expenseId The ID of the expense.
     * @return A Result indicating the success or failure of the operation.
     */
    suspend fun deleteAllTagsForExpense(expenseId: Int?): Result<Unit>

    /**
     * Retrieves an expense along with its associated tags.
     * @param expenseId The ID of the expense.
     * @return A Result indicating the success (with the ExpenseWithTags entity) or failure.
     */
    suspend fun getExpenseWithTags(expenseId: Int): Result<ExpenseWithTags>

    /**
     * Retrieves a list of expenses associated with a specific tag name.
     * @param tagName The name of the tag.
     * @return A Result indicating the success (with the list of ExpenseWithTags) or failure.
     */
    suspend fun getExpensesByTag(tagName: String): Result<List<ExpenseWithTags>>
}
