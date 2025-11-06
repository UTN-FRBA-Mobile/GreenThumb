package com.utn.greenthumb.ui.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.utn.greenthumb.R
import com.utn.greenthumb.state.UiState
import com.utn.greenthumb.ui.main.camera.CameraScreen
import com.utn.greenthumb.ui.main.home.HomeScreen
import com.utn.greenthumb.ui.main.login.LoginScreen
import com.utn.greenthumb.ui.main.my.plants.MyPlantsScreen
import com.utn.greenthumb.ui.main.my.plants.PlantScreen
import com.utn.greenthumb.ui.main.profile.ProfileScreen
import com.utn.greenthumb.ui.main.remember.RememberScreen
import com.utn.greenthumb.ui.main.result.IdentificationSuccessScreen
import com.utn.greenthumb.ui.main.result.ResultScreen
import com.utn.greenthumb.viewmodel.AuthViewModel
import com.utn.greenthumb.viewmodel.NotificationViewModel
import com.utn.greenthumb.viewmodel.PlantViewModel
import com.utn.greenthumb.viewmodel.WateringConfigViewModel


@SuppressLint("RestrictedApi")
@Composable
fun AppNavHost(
    authViewModel: AuthViewModel,
    plantViewModel: PlantViewModel,
    modifier: Modifier = Modifier,
    notificationViewModel: NotificationViewModel = viewModel(),
    wateringConfigViewModel: WateringConfigViewModel,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val logoutState by authViewModel.logoutState.collectAsState()

    val selectedPlant by plantViewModel.selectedPlant.collectAsState()
    val selectedPlantBackRoute by plantViewModel.backRoute.collectAsState()


    var hasNavigatedInitially by remember { mutableStateOf(false) }

    // Navegar automáticamente basado en el estado de autenticación
    LaunchedEffect(isUserLoggedIn, currentUser) {
        if (!hasNavigatedInitially) {
            val startDestination = if (isUserLoggedIn && currentUser != null) {
                NavRoutes.Home.route
            } else {
                NavRoutes.Login.route
            }
            navController.navigate(startDestination) {
                popUpTo(0) { inclusive = true }
            }
            hasNavigatedInitially = true
            Log.d("AppNavHost", "Initial navigation to: $startDestination")
        }
    }

    // Manejar logout exitoso
    LaunchedEffect(logoutState) {
        when (logoutState) {
            is UiState.Success -> {
                Log.d("AppNavHost", "Logout successful, navigating to login")
                navController.navigate(NavRoutes.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
                authViewModel.clearAllErrors()
            }
            is UiState.Error -> {
                Log.e("AppNavHost", "Logout failed: ${(logoutState as UiState.Error).message}")
            }
            else -> { }
        }
    }

    // Para notificaciones
    LaunchedEffect(selectedPlant) {
        selectedPlant?.let {
            navController.navigate(NavRoutes.PlantDetail.route)
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home.route,
        modifier = modifier
    ) {

        // ===== LOGIN SCREEN =====
        composable(
            route = NavRoutes.Login.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) }
        ) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    Log.d("AppNavHost", "Login successful, navigating to home")
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Login.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }


        // ===== HOME SCREEN =====
        composable(
            route = NavRoutes.Home.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        )  {
            val backStackState = navController.currentBackStack.collectAsState()

            if (!isUserLoggedIn || currentUser == null) {
                LoadingAuthContent()
            } else {
                key(currentUser!!.uid, backStackState.value.size) {
                    ScreenWithBottomBar(
                        currentRoute = currentRoute ?: NavRoutes.Home.route,
                        navController = navController
                    ) { navigation ->
                        HomeScreen(
                            authViewModel = authViewModel,
                            notificationViewModel = notificationViewModel,
                            currentUser = currentUser,
                            onHome = navigation::onHome,
                            onMyPlants = navigation::onMyPlants,
                            onCamera = navigation::onCamera,
                            onRemembers = navigation::onRemembers,
                            onProfile = navigation::onProfile,
                            onPlantSelected = { plant ->
                                Log.d("AppNavHost", "Plant selected: ${plant.name}")
                                plantViewModel.selectPlant(plant, NavRoutes.Home.route)
                                navController.navigate(NavRoutes.PlantDetail.route) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }


        // ===== CAMERA SCREEN =====
        composable(
            route = NavRoutes.Camera.route,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            CameraScreen(
                plantViewModel = plantViewModel,
                onNavigateBack = {
                    Log.d("AppNavHost", "Navigating back from camera")
                    navController.popBackStack()
                },
                onNavigateToResult = {
                    Log.d("AppNavHost", "Plants identified, navigating to results")
                    navController.navigate(NavRoutes.Result.route) {
                        launchSingleTop = true
                    }
                }
            )
        }


        // ===== MY PLANTS SCREEN =====
        composable(
            route = NavRoutes.MyPlants.route,
            enterTransition = { slideInVertically(initialOffsetY = { it }) },
            exitTransition = { slideOutVertically(targetOffsetY = { it }) }
        ) {
            if (!isUserLoggedIn || currentUser == null) {
                RedirectToLogin(navController)
            } else {
                ScreenWithBottomBar(
                    currentRoute = currentRoute ?: NavRoutes.MyPlants.route,
                    navController = navController
                ) { navigation ->
                    MyPlantsScreen(
                        onHome = navigation::onHome,
                        onMyPlants = navigation::onMyPlants,
                        onCamera = navigation::onCamera,
                        onRemembers = navigation::onRemembers,
                        onProfile = navigation::onProfile,
                        onNavigateBack = { navController.popBackStack() },
                        onPlantSelected = { plant ->
                            Log.d("AppNavHost", "Plant selected: ${plant.name}")
                            plantViewModel.selectPlant(plant, NavRoutes.MyPlants.route)
                            navController.navigate(NavRoutes.PlantDetail.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }


        // ===== PLANT DETAIL SCREEN =====
        composable(
            route = NavRoutes.PlantDetail.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            selectedPlant?.let { plant->
                PlantScreen(
                    onBackPressed = {
                        Log.d("AppNavHost", "Navigating back from plant detail")
                        plantViewModel.clearSelectedPlant()
                        navController.popBackStack(
                            route = selectedPlantBackRoute.toString(), //NavRoutes.MyPlants.route,
                            inclusive = false
                        )
                    },
                    plantSelected = plant
                )
            } ?: run {
                LaunchedEffect(Unit) {
                    Log.w("AppNavHost", "No Plant selected, navigating back to My Plants")
                    navController.popBackStack(
                        route = selectedPlantBackRoute.toString(), //route = NavRoutes.MyPlants.route,
                        inclusive = false
                    )
                }
            }
        }


        // ===== RESULT SCREEN =====
        composable(
            route = NavRoutes.Result.route,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            ResultScreen(
                onBackPressed = {
                    Log.d("AppNavHost", "Navigating back from results")
                    navController.popBackStack(
                        route = NavRoutes.Home.route,
                        inclusive = false
                    )
                },
                plantViewModel = plantViewModel,
                onSuccessfulIdentification = {
                    navController.navigateToBottomBarDestination(
                        route = NavRoutes.Success.route,
                        currentRoute = currentRoute
                    )
                }
            )
        }


        // ===== PROFILE SCREEN =====
        composable(
            route = NavRoutes.Profile.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            if (!isUserLoggedIn || currentUser == null) {
                RedirectToLogin(navController)
            } else {
                ScreenWithBottomBar(
                    currentRoute = currentRoute ?: NavRoutes.Profile.route,
                    navController = navController
                ) { navigation ->
                    ProfileScreen(
                        user = currentUser,
                        onHome = navigation::onHome,
                        onMyPlants = navigation::onMyPlants,
                        onCamera = navigation::onCamera,
                        onRemembers = navigation::onRemembers,
                        onProfile = navigation::onProfile,
                        onNavigateBack = { navController.popBackStack() },
                        onLogout = { authViewModel.logout() }
                    )
                }
            }
        }


        // ===== SUCCESS SCREEN =====
        composable(
            route = NavRoutes.Success.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            IdentificationSuccessScreen(
                message = stringResource(R.string.identification_successful),
                onAnimationEnds = {
                    navController.navigate(NavRoutes.MyPlants.route) {
                        popUpTo(NavRoutes.Home.route) {
                            inclusive = false
                        }
                    }
                }
            )
        }


        // ===== REMEMBER SCREEN =====
        composable(
            route = NavRoutes.Remember.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            ScreenWithBottomBar(
                currentRoute = currentRoute ?: NavRoutes.Profile.route,
                navController = navController
            ) { navigation ->
                RememberScreen(
                    onHome = navigation::onHome,
                    onMyPlants = navigation::onMyPlants,
                    onCamera = navigation::onCamera,
                    onRemembers = navigation::onRemembers,
                    onProfile = navigation::onProfile,
                    onNavigateBack = { navController.popBackStack() },
                    wateringConfigViewModel = wateringConfigViewModel,
                )
            }
        }
    }
}


// ===== COMPONENTES EXTRAS =====
@Composable
private fun LoadingAuthContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.verifying_authentication),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@Composable
private fun RedirectToLogin(
    navController: NavController
) {
    LaunchedEffect(Unit) {
        navController.navigate(NavRoutes.Login.route) {
            popUpTo(0) { inclusive = true }
        }
    }
}
