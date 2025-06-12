package com.example.ivestmentaplicationvamz.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ivestmentaplicationvamz.R

@Composable
fun HeaderLogo() {
        Row(
            modifier             = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.header_investment),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 45.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(R.string.header_calculator),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Image(
                painter = painterResource(R.drawable.obrazok),
                contentDescription = null,
                modifier = Modifier.size(126.dp)
            )
        }
    }
