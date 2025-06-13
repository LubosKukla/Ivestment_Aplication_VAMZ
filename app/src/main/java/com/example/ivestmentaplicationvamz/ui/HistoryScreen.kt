package com.example.ivestmentaplicationvamz.ui

import InvestmentViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ivestmentaplicationvamz.R
import com.example.ivestmentaplicationvamz.data.InvestmentEntity
import com.example.ivestmentaplicationvamz.viewmodel.InvestmentDataViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: InvestmentViewModel = viewModel(),
    dataViewModel: InvestmentDataViewModel  = viewModel(),
    onBack: () -> Unit,
    onLoadAndBack: () -> Unit
) {
    val investments by dataViewModel.allInvestments.collectAsState()

    var showDeletedDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.history_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.btn_back))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(investments) { inv ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "ID: ${inv.id}", style = MaterialTheme.typography.bodySmall)
                        Text(text = stringResource(R.string.history_principal, inv.principal), style = MaterialTheme.typography.bodyMedium)
                        Text(text = stringResource(R.string.history_contribution, inv.contribution), style = MaterialTheme.typography.bodyMedium)
                        Text(text = stringResource(R.string.history_years, inv.years), style = MaterialTheme.typography.bodyMedium)
                        Text(text = stringResource(R.string.history_rate, inv.ratePercent), style = MaterialTheme.typography.bodyMedium)
                        Text(text = stringResource(R.string.history_frequency, inv.frequency), style = MaterialTheme.typography.bodyMedium)
                        Text(text = stringResource(R.string.history_savedAt, inv.timestamp), style = MaterialTheme.typography.bodySmall)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = {
                                viewModel.loadIntoInputs(inv)
                                onLoadAndBack()
                            }) {
                                Text(text = stringResource(R.string.btn_load))
                            }
                            Button(onClick = {
                                dataViewModel.delete(inv) {
                                    showDeletedDialog = true
                                }
                            }) {
                                Text(text = stringResource(R.string.btn_delete))
                            }
                        }
                    }
                    if (showDeletedDialog) {
                        AlertDialog(
                            onDismissRequest = { showDeletedDialog = false },
                            title = { Text(stringResource(R.string.deleted_title)) },
                            text = { Text(stringResource(R.string.deleted_message)) },
                            confirmButton = {
                                TextButton(onClick = { showDeletedDialog = false }) {
                                    Text(stringResource(R.string.ok))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
