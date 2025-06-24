package com.example.walletwiz.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.walletwiz.utils.Currency
import com.example.walletwiz.utils.formatCurrency
import java.util.Locale
import androidx.core.graphics.toColorInt

fun String.toComposeColor(): Color {
    return Color(this.toColorInt())
}

data class PieChartSlice(
    val categoryName: String,
    val amount: Double,
    val percentage: Float,
    val color: Color
)

@Composable
fun PieChart(
    slices: List<PieChartSlice>,
    modifier: Modifier = Modifier
) {
    if (slices.isEmpty()) {
        Text(
            text = "No expenses to display for the graph.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        return
    }

    val legendDisplayCurrency = Currency.DEFAULT    /* TODO: Switchable currency */

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .padding(8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val totalPercentage = slices.sumOf { it.percentage.toDouble() }.toFloat()
                var currentStartAngle = 0f

                slices.forEach { slice ->
                    val normalizedPercentage = if (totalPercentage > 0) slice.percentage / totalPercentage else 0f
                    val sweepAngle = normalizedPercentage * 360f

                    drawArc(
                        color = slice.color,
                        startAngle = currentStartAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = Offset.Zero,
                        size = this.size
                    )
                    currentStartAngle += sweepAngle
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            slices.forEach { slice ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(slice.color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = slice.categoryName,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )

                    val formattedPercentage = "%.1f".format(Locale.getDefault(), slice.percentage)
                    val formattedAmount = formatCurrency(slice.amount, legendDisplayCurrency)

                    Text(
                        text = "$formattedPercentage%, $formattedAmount",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = slice.color
                    )
                }
            }
        }
    }
}