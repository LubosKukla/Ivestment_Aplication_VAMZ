package com.example.ivestmentaplicationvamz.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.ivestmentaplicationvamz.R

@Composable
fun InvestmentCalculatorScreen() {
    var startingAmount by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Nadpis aplikácie
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium
        )

        // Pole pre počiatočný vklad
        OutlinedTextField(
            value = startingAmount,
            onValueChange = { startingAmount = it },
            label = { Text(stringResource(R.string.label_starting_amount)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        // Tlačidlo Vypočítať
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(stringResource(R.string.btn_calculate))
        }
    }
}
