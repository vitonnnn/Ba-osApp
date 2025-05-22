// app/src/main/java/com/example/baosapp/navigation/AppNavGraph.kt
package com.example.baosapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.baosapp.data.local.SessionManager
import com.example.baosapp.data.repositories.FavoritesRepository
import com.example.baosapp.ui.login.LoginScreen
import com.example.baosapp.ui.login.LoginViewModel
import com.example.baosapp.ui.map.MapScreen
import com.example.baosapp.ui.map.MapViewModel
import com.example.baosapp.ui.favorites.FavoritesScreen
import com.example.baosapp.ui.favorites.FavoritesViewModel
import com.example.baosapp.ui.favorites.FavoritesViewModelFactory
import com.example.baosapp.ui.review.RateBathroomScreen
import com.example.baosapp.ui.review.RateBathroomViewModel
import com.example.baosapp.ui.information.InformationScreen
import com.example.baosapp.ui.map.MapViewModelFactory
import com.example.baosapp.ui.review.RateBathroomViewModelFactory
import com.example.baosapp.ui.shared.SharedToiletViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    navController: NavHostController
) {
    val context = LocalContext.current
    // ViewModel compartido para pasar el Toilet seleccionado
    val sharedVM: SharedToiletViewModel = viewModel()

    // Observamos si hay un token guardado
    val loggedIn by SessionManager
        .isLoggedIn(context)
        .collectAsState(initial = false)

    // Elegimos la pantalla inicial según el estado de login
    val startDestination = if (loggedIn) Destinations.MAP else Destinations.LOGIN

    val favoritesRepo  = FavoritesRepository(context)
    val coroutineScope = rememberCoroutineScope()


    NavHost(
        navController    = navController,
        startDestination = startDestination
    ) {
        // Pantalla de login
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

        // Pantalla de mapa
        composable(Destinations.MAP) {
            val context = LocalContext.current
            val mapVm: MapViewModel = viewModel(
                factory = MapViewModelFactory(context)
            )

            MapScreen(
                viewModel        = mapVm,
                modifier         = Modifier.fillMaxSize(),
                onToggleFavorite = { mapVm.toggleFavorite(it) },
                onReviewClick    = { toilet ->
                    sharedVM.select(toilet)
                    navController.navigate(Destinations.RATE_BATHROOM)
                },
                onInfoClick      = { toilet ->
                    sharedVM.select(toilet)
                    navController.navigate(Destinations.INFORMATION)
                }
            )
        }


        // Pantalla de favoritos
        // dentro de NavHost { … }

        composable(Destinations.FAVORITES) {
            val favVm: FavoritesViewModel = viewModel(
                factory = FavoritesViewModelFactory(LocalContext.current)
            )
            FavoritesScreen(
                viewModel     = favVm,
                onLocateClick = { toilet ->
                    // seleccionamos el toilet y navegamos al mapa
                    sharedVM.select(toilet)
                    navController.navigate(Destinations.MAP)
                },
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


        // Pantalla para valorar un baño
        composable(Destinations.RATE_BATHROOM) {
            val reviewVm: RateBathroomViewModel = viewModel(
                factory = RateBathroomViewModelFactory(LocalContext.current)
            )
            val selected by sharedVM.selectedToilet.collectAsState(initial = null)
            selected?.let { toilet ->
                RateBathroomScreen(
                    toiletId        = toilet.id,
                    toiletName      = toilet.name,
                    viewModel       = reviewVm,
                    onSubmitSuccess = { navController.popBackStack() }
                )
            }
        }

        // Pantalla de información detallada
        composable(Destinations.INFORMATION) {
            val selectedToilet by sharedVM.selectedToilet.collectAsState(initial = null)
            InformationScreen(toilet = selectedToilet)
        }
    }
}
