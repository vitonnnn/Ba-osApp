// app/src/main/java/com/example/baosapp/navigation/AppNavGraph.kt
package com.example.baosapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.baosapp.data.local.SessionManager
import com.example.baosapp.ui.favorites.FavoritesScreen
import com.example.baosapp.ui.favorites.FavoritesViewModel
import com.example.baosapp.ui.favorites.FavoritesViewModelFactory
import com.example.baosapp.ui.information.InformationScreen
import com.example.baosapp.ui.login.LoginScreen
import com.example.baosapp.ui.login.LoginViewModel
import com.example.baosapp.ui.map.MapScreen
import com.example.baosapp.ui.map.MapViewModel
import com.example.baosapp.ui.review.RateBathroomScreen
import com.example.baosapp.ui.review.RateBathroomViewModel
import com.example.baosapp.ui.shared.SharedToiletViewModel

@Composable
fun AppNavGraph() {
    val context       = LocalContext.current
    val navController = rememberNavController()

    // ViewModel compartido para pasar el Toilet seleccionado
    val sharedVM: SharedToiletViewModel = viewModel()

    // Observamos si hay un token guardado
    val loggedIn by SessionManager
        .isLoggedIn(context)
        .collectAsState(initial = false)

    // Elegimos la pantalla inicial según el estado de login
    val startDestination = if (loggedIn) Destinations.MAP else Destinations.LOGIN

    NavHost(
        navController    = navController,
        startDestination = startDestination
    ) {
        composable(Destinations.LOGIN) {
            val loginVm: LoginViewModel = viewModel()
            LoginScreen(
                viewModel      = loginVm,
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
                viewModel     = mapVm,
                modifier      = Modifier.fillMaxSize(),
                onReviewClick = { toilet ->
                    sharedVM.select(toilet)
                    navController.navigate(Destinations.RATE_BATHROOM)
                },
                onInfoClick   = { toilet ->
                    sharedVM.select(toilet)
                    navController.navigate(Destinations.INFORMATION)
                }
            )
        }



        composable(Destinations.INFORMATION) {
            val selectedToilet by sharedVM.selectedToilet.collectAsState(initial = null)
            InformationScreen(toilet = selectedToilet)
        }

        composable(Destinations.FAVORITES) {
            val favVm: FavoritesViewModel = viewModel(
                factory = FavoritesViewModelFactory(LocalContext.current)
            )
            FavoritesScreen(
                viewModel     = favVm,
                onReviewClick = { toilet ->
                    sharedVM.select(toilet)
                    navController.navigate(Destinations.RATE_BATHROOM)
                },
                onInfoClick   = { toilet ->
                    sharedVM.select(toilet)
                    navController.navigate(Destinations.INFORMATION)
                }
            )
        }
        composable(Destinations.RATE_BATHROOM) {
            val reviewVm: RateBathroomViewModel = viewModel()
            val selected by sharedVM.selectedToilet.collectAsState(initial = null)
            selected?.let { toilet ->
                RateBathroomScreen(
                    toiletId       = toilet.id,     // <–– aquí
                    toiletName     = toilet.name,
                    viewModel      = reviewVm,
                    onSubmitSuccess= { navController.popBackStack() }
                )
            }
        }

    }
}
