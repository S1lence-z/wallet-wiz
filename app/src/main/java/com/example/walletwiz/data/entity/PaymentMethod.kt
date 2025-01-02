package com.example.walletwiz.data.entity

enum class PaymentMethod {
    CASH,
    CREDIT_CARD,
    DEBIT_CARD;

    fun toReadableString(): String {
        return when (this) {
            CASH -> "Cash"
            CREDIT_CARD -> "Credit Card"
            DEBIT_CARD -> "Debit Card"
        }
    }
}