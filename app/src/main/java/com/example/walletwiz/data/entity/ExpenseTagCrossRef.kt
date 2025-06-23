package com.example.walletwiz.data.entity

import androidx.room.*

/**
@Entity(
    tableName = "expense_tag_cross_ref",
    primaryKeys = ["expenseId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = Expense::class,
            parentColumns = ["id"],  // ✅ Matches `Expense.id`
            childColumns = ["expenseId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExpenseTag::class,
            parentColumns = ["id"],  // ✅ Matches `ExpenseTag.id`
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["expenseId"]), Index(value = ["tagId"])]
)
**/
@Entity(tableName = "expense_tag_cross_ref")
data class ExpenseTagCrossRef(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,
    val expenseId: Int? = null,
    val tagId: Int? = null
)
