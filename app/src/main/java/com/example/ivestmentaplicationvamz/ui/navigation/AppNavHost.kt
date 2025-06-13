package com.example.ivestmentaplicationvamz.ui.navigation

import InvestmentViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ivestmentaplicationvamz.ui.InvestmentCalculatorScreen
import com.example.ivestmentaplicationvamz.ui.ResultScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ivestmentaplicationvamz.ui.AdditionalInfoScreen
import com.example.ivestmentaplicationvamz.ui.HistoryScreen
import com.example.ivestmentaplicationvamz.viewmodel.InvestmentDataViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    viewModel: InvestmentViewModel = viewModel(),
    dataViewModel: InvestmentDataViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = "calculator"
    ) {
        composable("calculator") {
            InvestmentCalculatorScreen(
                viewModel = viewModel,
                dataViewModel  = dataViewModel,
                onNext    = { navController.navigate("info") },
                onHistory = { navController.navigate("history") }
            )
        }



        composable("info") {
            AdditionalInfoScreen(
                viewModel     = viewModel,
                onBack        = { navController.popBackStack() },
                onCalculate   = { navController.navigate("result") }
            )
        }

        composable("history") {
            HistoryScreen(
                viewModel      = viewModel,
                dataViewModel  = dataViewModel,
                onBack         = { navController.popBackStack() },
                onLoadAndBack  = { navController.popBackStack() }
            )
        }


        composable("result") {
            ResultScreen(
                viewModel       = viewModel,
                onRecalculate   = { navController.navigate("calculator") }
            )
        }
    }
}