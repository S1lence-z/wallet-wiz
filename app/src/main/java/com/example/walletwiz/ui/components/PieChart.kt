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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Color as AndroidColor // Alias Android's Color to avoid conflict
import java.util.Locale // Import Locale for consistent number formatting

// Utility function to convert Hex String to Compose Color
fun String.toComposeColor(): Color {
    return Color(AndroidColor.parseColor(this))
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

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        // Pie Chart Drawing Area
        Box(
            modifier = Modifier
                .size(200.dp) // Fixed size for the pie chart itself
                .padding(8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val totalPercentage = slices.sumOf { it.percentage.toDouble() }.toFloat()
                var currentStartAngle = 0f

                slices.forEach { slice ->
                    val sweepAngle = (slice.percentage / totalPercentage) * 360f

                    drawArc(
                        color = slice.color,
                        startAngle = currentStartAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, size.height)
                    )
                    currentStartAngle += sweepAngle
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legend
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
                    // Color box
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(slice.color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Category Name
                    Text(
                        text = slice.categoryName,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    // Percentage and Amount
                    // *** CRUCIAL MODIFICATION HERE (Line 109 equivalent) ***
                    val formattedPercentage = "%.1f".format(Locale.getDefault(), slice.percentage)
                    val formattedAmount = "$%.2f".format(Locale.getDefault(), slice.amount) // This correctly formats "e.g. $24.50"

                    Text(
                        text = "$formattedPercentage%, $formattedAmount", // Use Kotlin's string template safely
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = slice.color
                    )
                }
            }
        }
    }
}