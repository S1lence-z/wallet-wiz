package com.example.walletwiz.utils

import java.util.Locale

enum class Currency(
    val symbol: String,
    val code: String,
    val displayName: String,
    val locale: Locale
) {
    USD("$", "USD", "US Dollar", Locale.US),
    CZK("Kč", "CZK", "Czech Koruna", Locale("cs", "CZ")),
    EUR("€", "EUR", "Euro", Locale.GERMANY),
    GBP("£", "GBP", "British Pound", Locale.UK);

    companion object {
        fun fromCode(code: String?): Currency? {
            return entries.find { it.code == code }
        }
        val DEFAULT: Currency = CZK
    }

    override fun toString(): String {
        return "$displayName ($code)"
    }
}