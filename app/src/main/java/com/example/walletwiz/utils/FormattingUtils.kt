package com.example.walletwiz.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun formatCurrency(amount: Double, currency: Currency): String {
    val symbols: DecimalFormatSymbols
    val pattern: String

    when (currency) {
        Currency.USD -> {
            symbols = DecimalFormatSymbols(Locale.US).apply {
                this.currencySymbol = "$"
                this.groupingSeparator = ','
                this.decimalSeparator = '.'
            }
            pattern = "'$'" + "#,##0.00"
        }
        Currency.CZK -> {
            symbols = DecimalFormatSymbols(Locale("cs", "CZ")).apply {
                this.currencySymbol = "Kč"
                this.groupingSeparator = ' '
                this.decimalSeparator = ','
            }
            pattern = "#,##0.00'\u00A0Kč'"
        }
        Currency.EUR -> {
            symbols = DecimalFormatSymbols(Locale.GERMANY)
            symbols.groupingSeparator = ' '
            symbols.decimalSeparator = ','
            pattern = "#,##0.00'\u00A0€'"
        }
        Currency.GBP -> {
            symbols = DecimalFormatSymbols(Locale.UK)
            pattern = "'£'#,##0.00"
        }
    }

    val formatter = DecimalFormat(pattern, symbols)
    return formatter.format(amount)
}