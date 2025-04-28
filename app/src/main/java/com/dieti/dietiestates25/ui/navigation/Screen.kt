package com.dieti.dietiestates25.ui.navigation

sealed class Screen(val route: String) {
    data object WelcomeScreen : Screen ("welcome_screen")
    data object HomeScreen : Screen ("home_screen")
    data object SearchScreen : Screen ("search_screen")
    data object SearchFilterScreen : Screen ("search_filter_screen")
    data object ApartmentListingScreen : Screen ("apartment_listing_screen")
    data object PropertyScreen : Screen ("property_screen")
    data object PriceProposalScreen : Screen ("price_screen")
    data object AppointmentBookingScreen : Screen ("appointment_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}