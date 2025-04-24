package com.dieti.dietiestates25.ui.navigation

sealed class Screen(val route: String) {
    data object WelcomeScreen : Screen ("welcome_screen")
    data object HomeScreen : Screen ("home_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}