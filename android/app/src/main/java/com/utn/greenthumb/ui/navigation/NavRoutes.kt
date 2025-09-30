package com.utn.greenthumb.ui.navigation

sealed class NavRoutes(val route: String) {
    object Login : NavRoutes("login")
    object Home : NavRoutes("home")
    object Result : NavRoutes("result")
    object Profile : NavRoutes("profile")
}
