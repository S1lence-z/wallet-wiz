package com.example.walletwiz.data.repository

import com.example.walletwiz.data.dao.TagDao
import com.example.walletwiz.data.entity.ExpenseTag
import com.example.walletwiz.data.entity.ExpenseTagCrossRef
import com.example.walletwiz.data.entity.ExpenseWithTags
import com.example.walletwiz.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of ITagRepository.
 * This class acts as a single source of truth for expense tag data.
 */
class TagRepositoryImpl(private val tagDao: TagDao) : ITagRepository {

    /**
     * Inserts a new tag.
     * Delegates the operation to the TagDao and handles potential errors.
     * @param tag The ExpenseTag entity to insert.
     * @return A Result indicating the success (with the ID) or failure of the operation.
     */
    override suspend fun insertTag(tag: ExpenseTag): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val id = tagDao.insertTag(tag)
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Retrieves all tags.
     * Delegates the operation to the TagDao and handles potential errors.
     * @return A Result indicating the success (with the list of tags) or failure.
     */
    override suspend fun getAllTags(): Result<List<ExpenseTag>> = withContext(Dispatchers.IO) {
        try {
            val tags = tagDao.getAllTags()
            Result.Success(tags)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Retrieves a tag by its name.
     * Delegates the operation to the TagDao and handles potential errors.
     * @param tagName The name of the tag to retrieve.
     * @return A Result indicating the success (with the ExpenseTag entity or null) or failure.
     */
    override suspend fun getTagByName(tagName: String): Result<ExpenseTag?> = withContext(Dispatchers.IO) {
        try {
            val tag = tagDao.getTagByName(tagName)
            Result.Success(tag)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Inserts a cross-reference between an expense and a tag.
     * Delegates the operation to the TagDao and handles potential errors.
     * @param crossRef The ExpenseTagCrossRef entity to insert.
     * @return A Result indicating the success or failure of the operation.
     */
    override suspend fun insertExpenseTagCrossRef(crossRef: ExpenseTagCrossRef): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            tagDao.insertExpenseTagCrossRef(crossRef)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Removes a specific tag from an expense.
     * Delegates the operation to the TagDao and handles potential errors.
     * @param expenseId The ID of the expense.
     * @param tagId The ID of the tag to remove.
     * @return A Result indicating the success or failure of the operation.
     */
    override suspend fun removeTagFromExpense(expenseId: Int, tagId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            tagDao.removeTagFromExpense(expenseId, tagId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Deletes all tags associated with a specific expense.
     * Delegates the operation to the TagDao and handles potential errors.
     * @param expenseId The ID of the expense.
     * @return A Result indicating the success or failure of the operation.
     */
    override suspend fun deleteAllTagsForExpense(expenseId: Int?): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            tagDao.deleteAllTagsForExpense(expenseId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Retrieves an expense along with its associated tags.
     * Delegates the operation to the TagDao and handles potential errors.
     * @param expenseId The ID of the expense.
     * @return A Result indicating the success (with the ExpenseWithTags entity) or failure.
     */
    override suspend fun getExpenseWithTags(expenseId: Int): Result<ExpenseWithTags> = withContext(Dispatchers.IO) {
        try {
            val expenseWithTags = tagDao.getExpenseWithTags(expenseId)
            Result.Success(expenseWithTags)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Retrieves a list of expenses associated with a specific tag name.
     * Delegates the operation to the TagDao and handles potential errors.
     * @param tagName The name of the tag.
     * @return A Result indicating the success (with the list of ExpenseWithTags) or failure.
     */
    override suspend fun getExpensesByTag(tagName: String): Result<List<ExpenseWithTags>> = withContext(Dispatchers.IO) {
        try {
            val expenses = tagDao.getExpensesByTag(tagName)
            Result.Success(expenses)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
