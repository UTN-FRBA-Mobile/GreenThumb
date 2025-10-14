package com.utn.greenthumb.ui.navigation

sealed class NavRoutes(val route: String) {
    object Login : NavRoutes("login")
    object Home : NavRoutes("home")
    object Result : NavRoutes("result")
    object Camera: NavRoutes("camera")
    object Profile : NavRoutes("profile")
    object Remember : NavRoutes("remember")
    object MyPlants : NavRoutes("my.plants")
    object Success : NavRoutes("success")
}
