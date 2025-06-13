@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.ivestmentaplicationvamz.ui

import InvestmentViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ivestmentaplicationvamz.R
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.unit.sp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.ivestmentaplicationvamz.data.InvestmentEntity
import com.example.ivestmentaplicationvamz.ui.component.HeaderLogo
import com.example.ivestmentaplicationvamz.ui.component.RepeatInterval
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@androidx.annotation.OptIn(UnstableApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InvestmentCalculatorScreen(
    viewModel: InvestmentViewModel = viewModel(),
    onSchedule: (LocalDateTime) -> Unit = {},
    onNext: () -> Unit,
    onHistory: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    var showDialog by remember { mutableStateOf(false) }
    var triggerTime by remember { mutableStateOf(LocalDateTime.now()) }



    val yearlyLabel = stringResource(R.string.option_yearly)

    val startingAmountRaw by viewModel.startingAmountRaw.collectAsState()

    val additionalContributionRaw by viewModel.additionalContributionRaw.collectAsState()

    val yearsRaw by viewModel.yearsRaw.collectAsState()
    val frequencyRaw by viewModel.frequencyRaw.collectAsState()

    val returnPercentRaw by viewModel.returnPercentRaw.collectAsState()



    val inflationRaw by viewModel.inflationRaw.collectAsState()


    val yearLabel = stringResource(R.string.option_yearly)
    val monthlyLabel = stringResource(R.string.option_monthly)
    val semiAnnualLabel = stringResource(R.string.option_semiannually)
    val quarterlyLabel = stringResource(R.string.option_quarterly)
    val weeklyLabel = stringResource(R.string.option_weekly)
    val dailyLabel = stringResource(R.string.option_daily)

    val taxPercentRaw by viewModel.taxPercentRaw.collectAsState()

    val compoundOptions = listOf(
        yearLabel,
        monthlyLabel,
        semiAnnualLabel,
        quarterlyLabel,
        weeklyLabel,
        dailyLabel
    )



    var showAdvanced by rememberSaveable { mutableStateOf(false) }

    val showInflation by viewModel.showInflation.collectAsState()
    val showTax by viewModel.showTax.collectAsState()
    val showMonteCarlo by viewModel.showMonteCarlo.collectAsState()

    LaunchedEffect(showMonteCarlo, showInflation, showTax) {
        if (showMonteCarlo || showInflation || showTax) {
            showAdvanced = true
        }
    }

    val sceneOptions = listOf(
        stringResource(R.string.option_scene_realistic),
        stringResource(R.string.option_scene_optimistic),
        stringResource(R.string.option_scene_pessimistic)
    )
    var sceneSelection by rememberSaveable { mutableStateOf(sceneOptions.first()) }




    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1) HEADER
            HeaderLogo()

            TextButton(
                onClick = onHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(text = stringResource(R.string.btn_history), style = MaterialTheme.typography.bodyLarge)
            }


            // 2) INPUT
            StyledNumberField(
                labelRes = R.string.label_after_years,
                placeholderRes = R.string.placeholder_after_years,
                value = yearsRaw,
                onValueChange = { viewModel.onYearsChange(it) }
            )

            StyledNumberField(
                labelRes = R.string.label_starting_amount,
                placeholderRes = R.string.placeholder_starting_amount,
                value = startingAmountRaw,
                onValueChange = { viewModel.onStartingAmountChange(it) }
            )

            StyledNumberField(
                labelRes = R.string.label_additional_contribution,
                placeholderRes = R.string.placeholder_additional_contribution,
                value = additionalContributionRaw,
                onValueChange = { viewModel.onAdditionalContributionChange(it) }
            )

            StyledNumberField(
                labelRes = R.string.label_return_percent,
                placeholderRes = R.string.placeholder_return_percent,
                value = returnPercentRaw,
                onValueChange = { viewModel.onReturnPercentChange(it) }
            )


            //SELECT
            StyledDropdownField(
                labelRes = R.string.label_compound,
                options = compoundOptions,
                selectedOption = frequencyRaw,
                onOptionSelected = { viewModel.onFrequencyChange(it) }
            )

            // 4) Zobraziť VIAC
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAdvanced = !showAdvanced }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showAdvanced)
                        stringResource(R.string.btn_less_options)
                    else
                        stringResource(R.string.btn_more_options),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = if (showAdvanced)
                        Icons.Default.KeyboardArrowUp
                    else
                        Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            // 5) ZOBRAZIŤ VIAC AKtívne
            if (showAdvanced) {
                AdvancedOptionsSection(
                    monteCarlo = showMonteCarlo,
                    onMonteCarloChange = { viewModel.onMonteCarloToggle(it) },

                    sceneOptions = sceneOptions,
                    sceneSelection = sceneSelection,
                    onSceneSelectionChange = { sceneSelection = it },

                    //inflácis
                    inflationClear = showInflation,
                    onInflationClearChange = { viewModel.onInflationToggle(it) },
                    inflationPercent = inflationRaw,
                    onInflationPercentChange = { viewModel.onInflationChange(it) },

                    //daň
                    taxFees = showTax,
                    onTaxFeesChange = { viewModel.onTaxToggle(it) },
                    taxPercent = taxPercentRaw,
                    onTaxPercentChange = { viewModel.onTaxPercentChange(it) },
                )
            }

            Spacer(Modifier.weight(1f))

            val coroutineScope = rememberCoroutineScope()

            // 6) BUTTON
           /* CalculateButton {
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
            }*/

            OutlinedButton(
                onClick = {
                    focusManager.clearFocus()
                    coroutineScope.launch {
                        // 1) zostavíme timestamp
                        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)

                        // 2) pripravíme entitu zo stavu ViewModelu
                        val entity = InvestmentEntity(
                            principal          = viewModel.startingAmountRaw.value.toDoubleOrNull() ?: 0.0,
                            contribution       = viewModel.additionalContributionRaw.value.toDoubleOrNull() ?: 0.0,
                            years              = viewModel.yearsRaw.value.toIntOrNull() ?: 0,
                            ratePercent        = viewModel.returnPercentRaw.value.toDoubleOrNull() ?: 0.0,
                            frequency          = viewModel.frequencyRaw.value,
                            timestamp          = now,
                            simulationEnabled  = viewModel.showMonteCarlo.value,
                            inflationEnabled   = viewModel.showInflation.value,
                            inflationRate      = viewModel.inflationRaw.value.toDoubleOrNull(),
                            taxEnabled         = viewModel.showTax.value,
                            taxRate            = viewModel.taxPercentRaw.value.toDoubleOrNull()
                        )

                        // 3) uložíme do databázy
                        val newId = withContext(Dispatchers.IO) {
                            viewModel.saveToDbSuspend(entity)
                        }
                        Log.d("DB", "✅ Uložené id=$newId")

                        // 4) spustíme Monte Carlo (ak treba) a až potom navigujeme
                        if (showMonteCarlo) {
                            // vypočítame volPct z výberu
                            val volPct = when (sceneSelection) {
                                sceneOptions[0] -> 0.15
                                sceneOptions[1] -> 0.10
                                sceneOptions[2] -> 0.25
                                else             -> 0.15
                            }
                            viewModel.runMonteCarlo(sims = 10_000, volPct = volPct)
                        }

                        // 5) navigácia ďalej
                        onNext()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(2.dp, Color(0xFF1B1464))
            ) {
                Text(
                    text = stringResource(R.string.btn_next),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            OutlinedButton(onClick = {
                focusManager.clearFocus()

                triggerTime = LocalDateTime.now().plusSeconds(10)

                val interval = when (frequencyRaw) {
                    dailyLabel -> RepeatInterval.DAILY
                    weeklyLabel -> RepeatInterval.WEEKLY
                    monthlyLabel -> RepeatInterval.MONTHLY
                    yearlyLabel -> RepeatInterval.YEARLY
                    else -> RepeatInterval.DAILY
                }

                viewModel.scheduleReminder(
                    dateTime       = triggerTime,
                    interval       = interval,
                    principal      = startingAmountRaw,
                    contribution   = additionalContributionRaw,
                    years          = yearsRaw,
                    rate           = returnPercentRaw,
                    frequencyLabel = frequencyRaw
                )

                showDialog = true
            }) {
                Text(stringResource(R.string.btn_schedule_reminder))
            }

            if (showDialog) {
                ShowReminderDialog(
                    triggerTime = triggerTime,
                    onDismiss = { showDialog = false },
                    viewModel   = viewModel
                )
            }
        }
    }
}

@Composable
fun CalculateButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                clip = false
            ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(2.dp, Color(0xFF1B1464)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor   = Color.Black
        )
    ) {
        Text(
            text  = stringResource(R.string.btn_calculate),
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp)
        )
    }
}


@Composable
fun AdvancedOptionsSection(
    monteCarlo: Boolean,
    onMonteCarloChange: (Boolean) -> Unit,
    sceneOptions: List<String>,
    sceneSelection: String,
    onSceneSelectionChange: (String) -> Unit,

    inflationClear: Boolean,
    onInflationClearChange: (Boolean) -> Unit,
    inflationPercent: String,
    onInflationPercentChange: (String) -> Unit,

    taxFees: Boolean,
    onTaxFeesChange: (Boolean) -> Unit,
    taxPercent: String,
    onTaxPercentChange: (String) -> Unit

) {
    var showMonteDialog     by remember { mutableStateOf(false) }
    var showInflationDialog by remember { mutableStateOf(false) }
    var showTaxDialog       by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = monteCarlo, onCheckedChange = onMonteCarloChange)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.label_monte_carlo), Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { showMonteDialog = true }
            )
        }
        if (showMonteDialog) {
            AlertDialog(
                onDismissRequest = { showMonteDialog = false },
                title = { Text(stringResource(R.string.label_monte_carlo)) },
                text = { Text(stringResource(R.string.tooltip_monte_carlo)) },
                confirmButton = {
                    TextButton(onClick = { showMonteDialog = false }) {
                        Text(stringResource(R.string.btn_ok))
                    }
                }
            )
        }

        if (monteCarlo) {
            StyledDropdownField(
                labelRes         = R.string.label_scene,
                options          = sceneOptions,
                selectedOption   = sceneSelection,
                onOptionSelected = onSceneSelectionChange
            )
            Spacer(Modifier.height(6.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = inflationClear,
                onCheckedChange = onInflationClearChange
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.label_inflation_clear),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { showInflationDialog = true }
            )
        }
        if (showInflationDialog) {
            AlertDialog(
                onDismissRequest = { showInflationDialog = false },
                title = { Text(stringResource(R.string.label_inflation_clear)) },
                text = { Text(stringResource(R.string.tooltip_inflation_clear)) },
                confirmButton = {
                    TextButton(onClick = { showInflationDialog = false }) {
                        Text(stringResource(R.string.btn_ok))
                    }
                }
            )
        }

        if (inflationClear) {
                Spacer(Modifier.height(12.dp))
                StyledNumberField(
                    labelRes        = R.string.label_inflation_percent,
                    placeholderRes  = R.string.placeholder_inflation_percent,
                    value           = inflationPercent,
                    onValueChange   = onInflationPercentChange
                )
            Spacer(Modifier.height(6.dp))
        }


        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = taxFees,
                onCheckedChange = onTaxFeesChange
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.label_tax_fees),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { showTaxDialog = true }
            )
        }


        if (showTaxDialog) {
            AlertDialog(
                onDismissRequest = { showTaxDialog = false },
                title = { Text(stringResource(R.string.label_tax_fees)) },
                text = { Text(stringResource(R.string.tooltip_tax_fees)) },
                confirmButton = {
                    TextButton(onClick = { showTaxDialog = false }) {
                        Text(stringResource(R.string.btn_ok))
                    }
                }
            )
        }


        if (taxFees) {
                Spacer(Modifier.height(12.dp))
                StyledNumberField(
                    labelRes        = R.string.label_tax_percent,
                    placeholderRes  = R.string.placeholder_tax_percent,
                    value           = taxPercent,
                    onValueChange   = onTaxPercentChange
                )
            }
            Spacer(Modifier.height(6.dp))

    }
}

@Composable
fun StyledNumberField(
    @StringRes labelRes: Int,
    @StringRes placeholderRes: Int,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {

        Text(
            text  = stringResource(labelRes),
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF888888),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value         = value,
            onValueChange = { new ->
                val filtered = new.filter { it.isDigit() || it == ',' || it == '.' }
                onValueChange(filtered)
            },
            placeholder = {
                Text(
                    text  = stringResource(placeholderRes),
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFFAAAAAA))
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape    = RoundedCornerShape(16.dp),
            colors   = TextFieldDefaults.outlinedTextFieldColors(
                containerColor       = Color(0xFFF8FAFF),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor   = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp))
        )
    }
}

@Composable
fun StyledDropdownField(
    @StringRes labelRes: Int,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {

        Text(
            text      = stringResource(labelRes),
            style     = MaterialTheme.typography.bodyLarge,
            color     = Color(0xFF888888),
            modifier  = Modifier.padding(bottom = 0.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .background(Color(0xFFF8FAFF), RoundedCornerShape(16.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text     = selectedOption,
                    style    = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF1B1464)),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF1B1464)
                )
            }

            DropdownMenu(
                expanded        = expanded,
                onDismissRequest= { expanded = false },
                modifier        = Modifier
                    .fillMaxWidth()
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowReminderDialog(
    triggerTime: LocalDateTime,
    onDismiss: () -> Unit,
    viewModel: InvestmentViewModel
) {
    val princ by viewModel.startingAmountRaw.collectAsState()
    val contrib by viewModel.additionalContributionRaw.collectAsState()
    val yrs by viewModel.yearsRaw.collectAsState()
    val rate by viewModel.returnPercentRaw.collectAsState()
    val freqLbl by viewModel.frequencyRaw.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.reminder_set)) },
        text = {
            Column {
                Text(stringResource(R.string.next_trigger, triggerTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))))
                Spacer(Modifier.height(8.dp))
                Text(stringResource(R.string.principal_amount, princ))
                Text(stringResource(R.string.contribution_yearly, contrib))
                Text(stringResource(R.string.investment_duration, yrs))
                Text(stringResource(R.string.interest_rate, rate))
                Text(stringResource(R.string.repeat_interval, freqLbl))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_ok))
            }
        }
    )
}