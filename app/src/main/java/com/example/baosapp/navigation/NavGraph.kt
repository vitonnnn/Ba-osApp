// app/src/main/java/com/example/baosapp/navigation/NavGraph.kt
package com.example.baosapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.baosapp.ui.login.LoginScreen
import com.example.baosapp.ui.login.LoginViewModel
import com.example.baosapp.ui.map.MapScreen
import com.example.baosapp.ui.map.MapViewModel
import com.example.baosapp.ui.review.RateBathroomScreen
import com.example.baosapp.ui.review.RateBathroomViewModel
import com.example.baosapp.ui.information.InformationScreen
import com.example.baosapp.ui.shared.SharedToiletViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val sharedVM: SharedToiletViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Destinations.MAP
    ) {
        composable(Destinations.LOGIN) {
            // obtiene el LoginViewModel
            val loginVm: LoginViewModel = viewModel()
            LoginScreen(
                viewModel = loginVm,
                onLoginSuccess = {
                    navController.navigate(Destinations.MAP) {
                        popUpTo(Destinations.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Destinations.MAP) {
            val mapVm: MapViewModel = viewModel()
            MapScreen(
                viewModel = mapVm,
                modifier = Modifier.fillMaxSize(),
                onReviewClick = { toilet ->
                    sharedVM.select(toilet)
                    navController.navigate(Destinations.RATE_BATHROOM)
                },
                onInfoClick = { toilet ->
                    sharedVM.select(toilet)
                    navController.navigate(Destinations.INFORMATION)
                }
            )
        }

        composable(Destinations.RATE_BATHROOM) {
            val reviewVm: RateBathroomViewModel = viewModel()
            // nombre del baño desde sharedVM
            val nombre = sharedVM.selectedToilet.collectAsState().value?.name.orEmpty()
            RateBathroomScreen(
                nombreBano      = nombre,
                viewModel       = reviewVm,
                onSubmitSuccess = { navController.popBackStack() }
            )
        }

        composable(Destinations.INFORMATION) {
            // pasa el toilet seleccionado
            val toilet = sharedVM.selectedToilet.collectAsState().value
            InformationScreen(toilet = toilet)
        }
    }
}
