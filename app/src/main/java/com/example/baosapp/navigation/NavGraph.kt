// app/src/main/java/com/example/baosapp/navigation/NavGraph.kt
package com.example.baosapp.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.baosapp.ui.login.LoginScreen
import com.example.baosapp.ui.map.MapScreen
import com.example.baosapp.ui.map.MapViewModel
import com.example.baosapp.ui.review.RateBathroomScreen
import com.example.baosapp.ui.review.RateBathroomViewModel
import com.example.baosapp.ui.information.InformationScreen
import com.example.baosapp.ui.shared.SharedToiletViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    // Aquí instanciamos el shared VM que durará durante todo el graph
    val sharedVM: SharedToiletViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Destinations.MAP
    ) {
        composable(Destinations.LOGIN) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Destinations.MAP) {
                    popUpTo(Destinations.LOGIN) { inclusive = true }
                }
            })
        }

        composable(Destinations.MAP) {
            val mapVm: MapViewModel = viewModel()
            MapScreen(
                viewModel = mapVm,
                modifier = Modifier.fillMaxSize(),
                onReviewClick = { toilet ->
                    // guardamos selección y navegamos
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
            // usamos el mismo nombreBano del toilet seleccionado
            val nombre = sharedVM.selectedToilet.value?.name.orEmpty()
            RateBathroomScreen(
                nombreBano      = nombre,
                viewModel       = reviewVm,
                onSubmitSuccess = { navController.popBackStack() }
            )
        }

        composable(Destinations.INFORMATION) {
            // leemos el toilet seleccionado aquí
            val toilet = sharedVM.selectedToilet.collectAsState().value
            InformationScreen(toilet = toilet)
        }
    }
}
