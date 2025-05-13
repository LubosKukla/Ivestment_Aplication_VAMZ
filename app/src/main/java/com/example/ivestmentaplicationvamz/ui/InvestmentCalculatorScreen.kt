@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.ivestmentaplicationvamz.ui

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



@Composable
fun InvestmentCalculatorScreen() {
    val focusManager = LocalFocusManager.current

    var startingAmount by rememberSaveable { mutableStateOf("") }
    var additionalContribution by rememberSaveable { mutableStateOf("") }
    var returnPercent by rememberSaveable { mutableStateOf("") }
    var years by rememberSaveable { mutableStateOf("") }

    val yearLabel       = stringResource(R.string.option_yearly)
    val monthlyLabel    = stringResource(R.string.option_monthly)
    val semiAnnualLabel = stringResource(R.string.option_semiannually)
    val quarterlyLabel  = stringResource(R.string.option_quarterly)
    val weeklyLabel     = stringResource(R.string.option_weekly)
    val dailyLabel      = stringResource(R.string.option_daily)

    val compoundOptions = listOf(
        yearLabel,
        monthlyLabel,
        semiAnnualLabel,
        quarterlyLabel,
        weeklyLabel,
        dailyLabel
    )
    var compoundSelection by rememberSaveable { mutableStateOf(yearLabel) }
    var compoundExpanded  by rememberSaveable { mutableStateOf(false) }

    var showAdvanced    by rememberSaveable { mutableStateOf(false) }
    var monteCarlo      by rememberSaveable { mutableStateOf(false) }
    var inflationClear  by rememberSaveable { mutableStateOf(false) }
    var taxFees         by rememberSaveable { mutableStateOf(false) }

    val sceneOptions = listOf(
        stringResource(R.string.option_scene_realistic),
        stringResource(R.string.option_scene_optimistic),
        stringResource(R.string.option_scene_pessimistic)
    )
    var sceneSelection by rememberSaveable { mutableStateOf(sceneOptions.first()) }
    var sceneExpanded  by rememberSaveable { mutableStateOf(false) }

    var inflationPercent by rememberSaveable { mutableStateOf("") }

    var taxPercent by rememberSaveable { mutableStateOf("") }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color    = MaterialTheme.colorScheme.background
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)      // ← toto pridaj
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1) HEADER
            InvestmentCalculatorHeader()


            // 2) INPUT
            StyledNumberField(
                labelRes       = R.string.label_after_years,
                placeholderRes = R.string.placeholder_after_years,
                value          = years,
                onValueChange  = { years = it }
            )

            StyledNumberField(
                labelRes       = R.string.label_starting_amount,
                placeholderRes = R.string.placeholder_starting_amount,
                value          = startingAmount,
                onValueChange  = { startingAmount = it }
            )

            StyledNumberField(
                labelRes       = R.string.label_additional_contribution,
                placeholderRes = R.string.placeholder_additional_contribution,
                value          = additionalContribution,
                onValueChange  = { additionalContribution = it }
            )

            StyledNumberField(
                labelRes       = R.string.label_return_percent,
                placeholderRes = R.string.placeholder_return_percent,
                value          = returnPercent,
                onValueChange  = { returnPercent = it }
            )


            //SELECT
            StyledDropdownField(
                labelRes        = R.string.label_compound,
                options         = compoundOptions,
                selectedOption  = compoundSelection,
                onOptionSelected= { compoundSelection = it }
            )

            // 4) Zobraziť VIAC
            Row(
                modifier           = Modifier
                    .fillMaxWidth()
                    .clickable { showAdvanced = !showAdvanced }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text  = if (showAdvanced)
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
                        monteCarlo               = monteCarlo,
                        onMonteCarloChange       = { monteCarlo = it },

                        sceneOptions             = sceneOptions,
                        sceneSelection           = sceneSelection,
                        onSceneSelectionChange   = { sceneSelection = it },

                        inflationClear           = inflationClear,
                        onInflationClearChange   = { inflationClear = it },
                        inflationPercent         = inflationPercent,
                        onInflationPercentChange = { inflationPercent = it },

                        taxFees                  = taxFees,
                        onTaxFeesChange          = { taxFees = it },
                        taxPercent               = taxPercent,
                        onTaxPercentChange       = { taxPercent = it }
                )
            }

            Spacer(Modifier.weight(1f))

            // 6) BUTTON
            CalculateButton {
                focusManager.clearFocus()
                // TODO:
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
fun InvestmentCalculatorHeader() {
    Row(
        modifier             = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text      = stringResource(R.string.header_investment),
                style     = MaterialTheme.typography.headlineMedium.copy(
                    fontSize   = 45.sp,
                    fontWeight = FontWeight.Normal
                ),
                color     = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text      = stringResource(R.string.header_calculator),
                style     = MaterialTheme.typography.headlineMedium.copy(
                    fontSize   = 42.sp,
                    fontWeight = FontWeight.Bold
                ),
                color     = MaterialTheme.colorScheme.primary
            )
        }
        Image(
            painter            = painterResource(R.drawable.obrazok),
            contentDescription = null,
            modifier           = Modifier.size(126.dp)
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

    var inflationPercent by rememberSaveable { mutableStateOf("") }
    var taxPercent       by rememberSaveable { mutableStateOf("") }


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
                        Text("OK")
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
                        Text("OK")
                    }
                }
            )
        }

        if (inflationClear) {
            if (inflationClear) {
                Spacer(Modifier.height(12.dp))
                StyledNumberField(
                    labelRes        = R.string.label_inflation_percent,
                    placeholderRes  = R.string.placeholder_inflation_percent,
                    value           = inflationPercent,
                    onValueChange   = { inflationPercent = it }
                )
            }
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
                        Text("OK")
                    }
                }
            )
        }


        if (taxFees) {

            if (taxFees) {
                Spacer(Modifier.height(12.dp))
                StyledNumberField(
                    labelRes        = R.string.label_tax_percent,
                    placeholderRes  = R.string.placeholder_tax_percent,
                    value           = taxPercent,
                    onValueChange   = { taxPercent = it }
                )
            }
            Spacer(Modifier.height(6.dp))
        }
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

                if (new.all(Char::isDigit)) {
                    onValueChange(new)
                }
            },
            placeholder = {
                Text(
                    text  = stringResource(placeholderRes),
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFFAAAAAA))
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
