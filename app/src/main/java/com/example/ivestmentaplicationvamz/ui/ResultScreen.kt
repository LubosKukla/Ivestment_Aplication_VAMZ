package com.example.ivestmentaplicationvamz.ui

import InvestmentViewModel
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ivestmentaplicationvamz.R
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.example.ivestmentaplicationvamz.ui.component.HeaderLogo

@Composable
fun ResultScreen(
    viewModel: InvestmentViewModel = viewModel(),
    onRecalculate: () -> Unit
) {
    val endBalance by viewModel.endBalanceFormatted.collectAsState()
    val startingAmount by viewModel.startingAmountFormatted.collectAsState()
    val totalContributions by viewModel.totalContributionsFormatted.collectAsState()
    val totalInterest by viewModel.totalInterestFormatted.collectAsState()
    val entries by viewModel.annualSchedule.collectAsState(initial = emptyList())

    val scheduleAsScheduleEntryList: List<ScheduleEntry> = entries.map { annualEntry ->
        ScheduleEntry(
            year           = annualEntry.year,
            deposit        = annualEntry.deposit,
            interest       = annualEntry.interest,
            endingBalance  = annualEntry.endingBalance
        )
    }

    val interestWithInflation by viewModel.totalInterestWInflationFormatted.collectAsState()
    val inflationRate by viewModel.inflationRateFormatted.collectAsState()
    val inflationYearly by viewModel.inflationLossYearlyFormatted.collectAsState()
    val inflationTotal by viewModel.inflationLossTotalFormatted.collectAsState()

    val interestWithTax by viewModel.interestAfterTaxFormatted.collectAsState()
    val taxPercent by viewModel.taxPercentRaw.collectAsState()
    val taxAmount by viewModel.taxAmountFormatted.collectAsState()
    val lastYearGain by viewModel.lastYearGainFormatted.collectAsState()
    val taxEarly by viewModel.taxYearlyFormatted.collectAsState()

    val showInflation by viewModel.showInflation.collectAsState()
    val showTax by viewModel.showTax.collectAsState()
    val showMonteCarlo by viewModel.showMonteCarlo.collectAsState()

    val monteMedian by viewModel.monteMedianFormatted.collectAsState()
    val monteMin by viewModel.monteMinFormatted.collectAsState()
    val monteMax by viewModel.monteMaxFormatted.collectAsState()
    val monteP10 by viewModel.monteP10Formatted.collectAsState()
    val monteP25 by viewModel.monteP25Formatted.collectAsState()
    val monteP50 by viewModel.monteP50Formatted.collectAsState()
    val monteP90 by viewModel.monteP90Formatted.collectAsState()
    val successProbabilityMonte by viewModel.successProbFormatted.collectAsState()
    val lossProbabilityMonte by viewModel.lossProbFormatted.collectAsState()
    val monteAverageReturn by viewModel.monteAvgAnnReturnFormatted.collectAsState()
    val monteStdDeviation by viewModel.monteStdDevFormatted.collectAsState()
    val monteSimulations by viewModel.monteCountFormatted.collectAsState()

    val successProbability by viewModel.successProbFormatted.collectAsState()

    val scrollState = rememberScrollState()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // header rovnaký ako na vstupnej obrazovke
            HeaderLogo()

            // 1) Výsledky
            ExpandableSection(titleRes = R.string.tab_results) {
                DataRow(labelRes = R.string.label_end_balance, value = endBalance)
                DataRow(labelRes = R.string.label_starting_amount, value = startingAmount)
                DataRow(labelRes = R.string.label_total_contributions, value = totalContributions)
                DataRow(labelRes = R.string.label_total_interest, value = totalInterest)


                if (showInflation) {
                    DataRow(labelRes = R.string.label_interest_inflation, value = interestWithInflation)
                }


                if (showTax) {
                    DataRow(labelRes = R.string.label_interest_tax, value = interestWithTax)
                }

                if (showMonteCarlo) {
                    DataRow(
                        labelRes = R.string.label_success_probability,
                        value = successProbability
                    )
                }
            }

            // 2) ExpandableSection: Annual Schedule – teraz už s entries z vonkajška
            ExpandableSection(titleRes = R.string.tab_annual_schedule) {
                AnnualScheduleTable(
                    entries = scheduleAsScheduleEntryList,
                    modifier = Modifier.height(240.dp)
                )
            }


// 3) Inflation
            if (showInflation) {
                ExpandableSection(titleRes = R.string.tab_inflation) {
                    DataRow(labelRes = R.string.label_percentage, value = inflationRate)
                    DataRow(labelRes = R.string.label_yearly_amount, value = inflationYearly)
                    DataRow(labelRes = R.string.label_total_amount, value = inflationTotal)
                    DataRow(labelRes = R.string.label_total_end_balance, value = interestWithInflation)
                }
            }
// 4) Tax
            if (showTax) {
                ExpandableSection(titleRes = R.string.tab_tax) {
                    DataRow(labelRes = R.string.label_tax_percent, value = taxPercent)
                    DataRow(labelRes = R.string.label_last_year, value = lastYearGain)
                    DataRow(labelRes = R.string.label_final_tax, value = taxAmount)
                    DataRow(labelRes = R.string.label_yearly_amount, value = taxEarly)
                    DataRow(labelRes = R.string.label_total_end_balance, value = interestWithTax)
                }
            }

                // 5) Monte Carlo
            if (showMonteCarlo) {
                ExpandableSection(titleRes = R.string.tab_monte_carlo) {
                    DataRow(labelRes = R.string.label_median_end_balance, value = monteMedian)
                    DataRow(labelRes = R.string.label_min_end_balance, value = monteMin)
                    DataRow(labelRes = R.string.label_max_end_balance, value = monteMax)
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.label_percentile))
                    DataRow(label = "10 %", value = monteP10)
                    DataRow(label = "25 %", value = monteP25)
                    DataRow(label = "50 %", value = monteP50)
                    DataRow(label = "75 %", value = monteP90)
                    Spacer(Modifier.height(8.dp))
                    DataRow(labelRes = R.string.label_success_probability, value = successProbabilityMonte)
                    DataRow(labelRes = R.string.label_loss_probability, value = lossProbabilityMonte)
                    DataRow(labelRes = R.string.label_average_return, value = monteAverageReturn)
                    DataRow(labelRes = R.string.label_std_deviation, value = monteStdDeviation)
                    DataRow(labelRes = R.string.label_simulations, value = monteSimulations)
                }
            }

            Spacer(Modifier.weight(1f))

            // 6) Re-calculate button
            OutlinedButton(
                onClick = onRecalculate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp), clip = false),
                shape  = RoundedCornerShape(24.dp),
                border = BorderStroke(2.dp, Color(0xFF1B1464)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor   = Color.Black
                )
            ) {
                Text(
                    text  = stringResource(R.string.btn_recalculate),
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp)
                )
            }
        }
    }
}

@Composable
private fun ExpandableSection(
    @StringRes titleRes: Int,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val bgColor = Color(0xFFD8E0FF)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        // header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor, RoundedCornerShape(16.dp))
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text  = stringResource(titleRes),
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
        }
        if (expanded) {
            Column(
                modifier           = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color(0xFFF8FAFF), RoundedCornerShape(16.dp)),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun DataRow(
    @StringRes labelRes: Int,
    value: String
) {
    Row(Modifier.fillMaxWidth()) {
        Text(stringResource(labelRes), modifier = Modifier.weight(1f))
        Text(value)
    }
}

@Composable
private fun DataRow(
    label: String,
    value: String
) {
    Row(Modifier.fillMaxWidth()) {
        Text(label, modifier = Modifier.weight(1f))
        Text(value)
    }
}
