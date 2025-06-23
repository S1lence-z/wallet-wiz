package com.example.walletwiz.data.dao

import androidx.room.*
import com.example.walletwiz.data.entity.ExpenseTag
import com.example.walletwiz.data.entity.ExpenseTagCrossRef
import com.example.walletwiz.data.entity.ExpenseWithTags

@Dao
interface TagDao {
    // ✅ Insert a new tag (Ignore duplicates)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: ExpenseTag): Long

    // ✅ Get all tags
    @Query("SELECT * FROM expense_tag ORDER BY name ASC")
    suspend fun getAllTags(): List<ExpenseTag>

    // ✅ Find a tag by name (returns null if not found)
    @Query("SELECT * FROM expense_tag WHERE name = :tagName LIMIT 1")
    suspend fun getTagByName(tagName: String): ExpenseTag?

    // ✅ Insert a new tag-expense relationship
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpenseTagCrossRef(crossRef: ExpenseTagCrossRef)

    // ✅ Remove a tag from an expense
    @Query("DELETE FROM expense_tag_cross_ref WHERE expenseId = :expenseId AND tagId = :tagId")
    suspend fun removeTagFromExpense(expenseId: Int, tagId: Int)

    // ✅ Get an expense with all its associated tags
    @Transaction
    @Query("SELECT * FROM expense WHERE id = :expenseId")
    suspend fun getExpenseWithTags(expenseId: Int): ExpenseWithTags

    // ✅ Get all expenses linked to a specific tag
    @Transaction
    @Query("""
        SELECT * FROM expense 
        WHERE id IN (
            SELECT expenseId FROM expense_tag_cross_ref
            INNER JOIN expense_tag ON expense_tag_cross_ref.tagId = expense_tag.id
            WHERE expense_tag.name = :tagName
        )
    """)
    suspend fun getExpensesByTag(tagName: String): List<ExpenseWithTags>
}
