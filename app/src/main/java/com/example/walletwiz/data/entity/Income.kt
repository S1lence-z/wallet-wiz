package com.example.walletwiz.data.entity

import androidx.room.*

@Entity
data class Income(
    @PrimaryKey
    val id: Int,
    val amount: Double,
    val date: String,
    val type: String,
    val description: String
)
