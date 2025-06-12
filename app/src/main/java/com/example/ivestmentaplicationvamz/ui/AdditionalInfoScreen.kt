@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.ivestmentaplicationvamz.ui

import InvestmentViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.ivestmentaplicationvamz.R
import com.example.ivestmentaplicationvamz.ui.component.HeaderLogo
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
fun AdditionalInfoScreen(
    viewModel: InvestmentViewModel,
    onBack: () -> Unit,
    onCalculate: () -> Unit
) {
    val years by viewModel.yearsRaw.collectAsState()
    val principal by viewModel.startingAmountFormatted.collectAsState()
    val contribution by viewModel.additionalContributionRaw.collectAsState()
    val returnRate by viewModel.returnPercentRaw.collectAsState()
    val frequency by viewModel.frequencyRaw.collectAsState()

    val simulationEnabled by viewModel.showMonteCarlo.collectAsState()
    val inflationEnabled by viewModel.showInflation.collectAsState()
    val inflationRate by viewModel.inflationRaw.collectAsState()

    val taxEnabled by viewModel.showTax.collectAsState()
    val taxRate by viewModel.taxPercentRaw.collectAsState()

    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val showMonteCarlo by viewModel.showMonteCarlo.collectAsState()
    val sceneOptions = listOf("Low", "Medium", "High")
    var sceneSelection by remember { mutableStateOf(sceneOptions[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HeaderLogo()

        Text(text = stringResource(R.string.info_years, years))
        Text(text = stringResource(R.string.info_principal, principal))
        Text(text = stringResource(R.string.info_contribution, contribution))
        Text(text = stringResource(R.string.info_return_rate, returnRate))
        Text(text = stringResource(R.string.info_frequency, frequency))

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(
                R.string.info_simulation,
                if (simulationEnabled) stringResource(R.string.option_yes) else stringResource(R.string.option_no)
            )
        )

        Text(
            text = stringResource(
                R.string.info_inflation_enabled,
                if (inflationEnabled) stringResource(R.string.option_yes) else stringResource(R.string.option_no)
            )
        )
        if (inflationEnabled) {
            Text(text = stringResource(R.string.info_inflation_rate, inflationRate))
        }

        Text(
            text = stringResource(
                R.string.info_tax_enabled,
                if (taxEnabled) stringResource(R.string.option_yes) else stringResource(R.string.option_no)
            )
        )
        if (taxEnabled) {
            Text(text = stringResource(R.string.info_tax_rate, taxRate))
        }

        Spacer(Modifier.weight(1f))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = onBack) {
                Text(stringResource(R.string.btn_back))
            }
            Button(
                onClick = {
                    focusManager.clearFocus()
                    if (showMonteCarlo) {

                        val volPct = when (sceneSelection) {
                            sceneOptions[0] -> 0.15
                            sceneOptions[1] -> 0.10
                            sceneOptions[2] -> 0.25
                            else -> 0.15
                        }

                        coroutineScope.launch {
                            viewModel.runMonteCarlo(sims = 10_000, volPct = volPct)
                            onCalculate()
                        }
                    } else {

                        onCalculate()
                    }
                }
            ) {
                Text(stringResource(R.string.btn_calculate))
            }
        }
    }
}
