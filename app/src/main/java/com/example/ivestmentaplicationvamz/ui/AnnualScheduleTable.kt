package com.example.ivestmentaplicationvamz.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ivestmentaplicationvamz.R
import java.text.NumberFormat
import java.util.Locale

data class ScheduleEntry(
    val year: Int,
    val deposit: Double,
    val interest: Double,
    val endingBalance: Double
)

@Composable
private fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFE0E7FF),
                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text      = stringResource(R.string.label_year),
            modifier  = Modifier
                .weight(0.3f)
                .fillMaxHeight(),
            textAlign = TextAlign.Center,
            style     = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text      = stringResource(R.string.label_deposit),
            modifier  = Modifier
                .weight(1f)
                .fillMaxHeight(),
            textAlign = TextAlign.Center,
            style     = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text      = stringResource(R.string.label_interest_col),
            modifier  = Modifier
                .weight(1f)
                .fillMaxHeight(),
            textAlign = TextAlign.Center,
            style     = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text      = stringResource(R.string.label_ending_balance),
            modifier  = Modifier
                .weight(1f)
                .fillMaxHeight(),
            textAlign = TextAlign.Center,
            style     = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
private fun TableRow(entry: ScheduleEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text      = entry.year.toString(),
            modifier  = Modifier.weight(0.3f),
            style     = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
        Text(
            text      = formatCurrency(entry.deposit, 2),
            modifier  = Modifier.weight(1f),
            style     = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
        Text(
            text      = formatCurrency(entry.interest, 2),
            modifier  = Modifier.weight(1f),
            style     = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
        Text(
            text      = formatCurrency(entry.endingBalance, 2),
            modifier  = Modifier.weight(1f),
            style     = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
    Divider()
}

@Composable
fun AnnualScheduleTable(
    entries: List<ScheduleEntry>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .background(Color(0xFFF8FAFF), RoundedCornerShape(16.dp))
            .padding(8.dp)
    ) {
        item { TableHeader() }
        items(entries) { TableRow(it) }
    }
}

private fun formatCurrency(amount: Double, currencyDecimals: Int = 2): String {
    val nf = NumberFormat.getNumberInstance(Locale("sk", "SK")).apply {
        minimumFractionDigits = currencyDecimals
        maximumFractionDigits = currencyDecimals
    }
    return nf.format(amount) + " €"
}