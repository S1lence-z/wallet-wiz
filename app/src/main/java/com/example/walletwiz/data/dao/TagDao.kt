package com.example.walletwiz.data.dao

import androidx.room.*
import com.example.walletwiz.data.entity.ExpenseTag
import com.example.walletwiz.data.entity.ExpenseTagCrossRef
import com.example.walletwiz.data.entity.ExpenseWithTags

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: ExpenseTag): Long

    @Query("SELECT * FROM expense_tag ORDER BY name ASC")
    suspend fun getAllTags(): List<ExpenseTag>

    @Query("SELECT * FROM expense_tag WHERE name = :tagName LIMIT 1")
    suspend fun getTagByName(tagName: String): ExpenseTag?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpenseTagCrossRef(crossRef: ExpenseTagCrossRef)

    @Query("DELETE FROM expense_tag_cross_ref WHERE expenseId = :expenseId AND tagId = :tagId")
    suspend fun removeTagFromExpense(expenseId: Int, tagId: Int)

    @Query("DELETE FROM expense_tag_cross_ref WHERE expenseId = :expenseId")
    suspend fun deleteAllTagsForExpense(expenseId: Int?)

    @Transaction
    @Query("SELECT * FROM expense WHERE id = :expenseId")
    suspend fun getExpenseWithTags(expenseId: Int): ExpenseWithTags

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
