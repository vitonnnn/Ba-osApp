// app/src/main/java/com/example/baosapp/navigation/AppNavGraph.kt
package com.example.baosapp.navigation

import android.app.Application
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.baosapp.data.local.SessionManager
import com.example.baosapp.ui.contributions.ContributionsScreen
import com.example.baosapp.ui.contributions.ContributionsViewModel
import com.example.baosapp.ui.contributions.ContributionsViewModelFactory
import com.example.baosapp.ui.create.CreateToiletScreen
import com.example.baosapp.ui.create.CreateToiletViewModel
import com.example.baosapp.ui.create.CreateToiletViewModelFactory
import com.example.baosapp.ui.filter.FilterScreen
import com.example.baosapp.ui.filter.FilterViewModel
import com.example.baosapp.ui.filter.FilterViewModelFactory
import com.example.baosapp.ui.favorites.FavoritesScreen
import com.example.baosapp.ui.favorites.FavoritesViewModel
import com.example.baosapp.ui.favorites.FavoritesViewModelFactory
import com.example.baosapp.ui.information.InformationScreen
import com.example.baosapp.ui.login.LoginScreen
import com.example.baosapp.ui.login.LoginViewModel
import com.example.baosapp.ui.login.RegisterScreen
import com.example.baosapp.ui.login.RegisterViewModel
import com.example.baosapp.ui.map.MapScreen
import com.example.baosapp.ui.map.MapViewModel
import com.example.baosapp.ui.map.MapViewModelFactory
import com.example.baosapp.ui.review.RateBathroomScreen
import com.example.baosapp.ui.review.RateBathroomViewModel
import com.example.baosapp.ui.review.RateBathroomViewModelFactory
import com.example.baosapp.ui.shared.SharedToiletViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(
    navController: NavHostController
) {
    val application = LocalContext.current.applicationContext as Application
    val context = LocalContext.current
    val sharedVM: SharedToiletViewModel = viewModel()
    val userId by SessionManager
        .getUserId(context)
        .collectAsState(initial = 0L)

    val loggedIn by SessionManager
        .isLoggedIn(context)
        .collectAsState(initial = false)

    val startDestination = if (loggedIn) Destinations.MAP else Destinations.LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ---------------------------
        // Pantallas “normales”
        // ---------------------------
        composable(Destinations.LOGIN) {
            val loginVm: LoginViewModel = viewModel()
            LoginScreen(
                viewModel = loginVm,
                onLoginSuccess = {
                    navController.navigate(Destinations.MAP) {
                        popUpTo(Destinations.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Destinations.REGISTER)
                }
            )
        }

        composable(Destinations.REGISTER) {
            val registerVm: RegisterViewModel = viewModel()
            RegisterScreen(
                viewModel = registerVm,
                onRegisterSuccess = {
                    navController.popBackStack(Destinations.LOGIN, false)
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Destinations.MAP) {
            val mapVm: MapViewModel = viewModel(
                factory = MapViewModelFactory(context)
            )
            MapScreen(
                viewModel = mapVm,
                modifier = Modifier.fillMaxSize(),
                onToggleFavorite = { toilet -> mapVm.toggleFavorite(toilet) },
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

        composable(Destinations.FAVORITES) {
            val favVm: FavoritesViewModel = viewModel(
                factory = FavoritesViewModelFactory(context)
            )
            FavoritesScreen(
                viewModel = favVm,
                onLocateClick = { toilet ->
                    sharedVM.select(toilet)
                    navController.navigate(Destinations.MAP)
                },
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
            val reviewVm: RateBathroomViewModel = viewModel(
                factory = RateBathroomViewModelFactory(context)
            )
            val selected by sharedVM.selectedToilet.collectAsState(initial = null)
            selected?.let { toilet ->
                RateBathroomScreen(
                    toiletId = toilet.id,
                    toiletName = toilet.name,
                    viewModel = reviewVm,
                    onSubmitSuccess = { navController.popBackStack() }
                )
            }
        }

        composable(Destinations.INFORMATION) {
            val selectedToilet by sharedVM.selectedToilet.collectAsState(initial = null)
            InformationScreen(toilet = selectedToilet)
        }

        // ---------------------------
        // Destinations.CREATE como Bottom Sheet
        // ---------------------------

        composable(Destinations.CREATE) {
            // Creamos el estado para el ModalBottomSheet
            val sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true // evita estado “semi abierto”
            )

            // Instanciamos el ViewModel con Context (no con Application)
            val createVm: CreateToiletViewModel = viewModel(
                factory = CreateToiletViewModelFactory(context)
            )

            // ModalBottomSheet con containerColor = Color(0xFF64B5F6)
            ModalBottomSheet(
                onDismissRequest = {
                    navController.popBackStack()
                },
                sheetState = sheetState,
                containerColor = Color(0xFF547792)  // <-- fondo del sheet en azul claro medio
            ) {
                CreateToiletScreen(
                    viewModel = createVm,
                    onCreated = { /* se cierra en el mismo efecto de UIState */ },
                    onCloseSheet = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ---------------------------
        // Destinations.FILTER (pantalla de filtros)
        // ---------------------------
        composable(Destinations.FILTER) {
            val filterVm: FilterViewModel = viewModel(
                factory = FilterViewModelFactory(context.applicationContext as Application)
            )
            FilterScreen(
                viewModel = filterVm,
                onSelectToilet = { toilet ->
                    sharedVM.select(toilet)
                    navController.navigate(Destinations.INFORMATION)
                },
                onBack = { navController.popBackStack() },
                onToggleFavorite = { /* dentro de FilterScreen ya lo llama a viewModel.toggleFavorite */ },
                onReviewClick = { toilet ->
                    sharedVM.select(toilet)
                    navController.navigate(Destinations.RATE_BATHROOM)
                },
                onInfoClick = { toilet ->
                    sharedVM.select(toilet)
                    navController.navigate(Destinations.INFORMATION)
                },
                onLocateClick = { /* opcional: centrar mapa */ }
            )
        }
        composable(route = Destinations.MY_CONTRIBUTIONS) {
            // Creamos el ViewModel usando siempre el valor (0L si no ha cargado aún)
            val vm: ContributionsViewModel = viewModel(
                factory = ContributionsViewModelFactory(context, userId)
            )
            ContributionsScreen(
                viewModel      = vm,
                onDeleteToilet = { tmId -> vm.deleteToilet(tmId) },
                onDeleteReview = { tId, rId -> vm.deleteReview(tId, rId) }
            )
        }
    }
}

