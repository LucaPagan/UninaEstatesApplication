package com.dieti.dietiestates25.ui.navigation

import android.net.Uri
import com.dieti.dietiestates25.data.model.FilterModel

sealed class Screen(val route: String) {

    // ... (TUTTE LE TUE ALTRE ROTTE RIMANGONO IDENTICHE) ...
    data object WelcomeScreen : Screen("welcome_screen")
    data object LoginScreen : Screen("login_screen")
    data object RegisterScreen : Screen("register_screen")
    data object HomeScreen : Screen("home_screen") { fun withIdUtente(idUtente: String): String = "$route/${Uri.encode(idUtente)}" }
    data object SearchScreen : Screen("search_screen") { fun withIdUtente(idUtente: String): String = "$route/${Uri.encode(idUtente)}" }
    data object SearchFilterScreen : Screen("search_filter_screen") { fun withInitialArgs(idUtente: String, comune: String, ricercaQueryText: String): String = "$route/${Uri.encode(idUtente)}/${Uri.encode(comune)}/${Uri.encode(ricercaQueryText)}" }
    data object SearchTypeSelectionScreen : Screen("search_type_selection_screen") { fun buildRoute(idUtentePath: String, comunePath: String, ricercaPath: String, filters: FilterModel? = null): String = "$route/${Uri.encode(idUtentePath)}/${Uri.encode(comunePath)}/${Uri.encode(ricercaPath)}" + buildFilterQuery(filters) }
    data object ApartmentListingScreen : Screen("apartment_listing_screen") { fun buildRoute(idUtentePath: String, comunePath: String, ricercaPath: String, filters: FilterModel? = null): String = "$route/${Uri.encode(idUtentePath)}/${Uri.encode(comunePath)}/${Uri.encode(ricercaPath)}" + buildFilterQuery(filters) }
    data object MapSearchScreen : Screen("map_search_screen") { fun buildRoute(idUtentePath: String, comunePath: String, ricercaPath: String, filters: FilterModel? = null): String = "$route/${Uri.encode(idUtentePath)}/${Uri.encode(comunePath)}/${Uri.encode(ricercaPath)}" + buildFilterQuery(filters) }
    data object FullScreenMapScreen : Screen("fullscreen_map_screen") { fun withPosition(latitude: Double, longitude: Double, zoom: Float): String = "$route/${Uri.encode(latitude.toString())}/${Uri.encode(longitude.toString())}/${Uri.encode(zoom.toString())}" }
    data object PropertyScreen : Screen("property_screen") { fun withId(idProperty: String): String = "$route/${Uri.encode(idProperty)}" }
    data object PriceProposalScreen : Screen("price_screen") { fun withId(immobileId: String): String = "$route/${Uri.encode(immobileId)}" }
    data object PropertySellScreen : Screen("property_sell_screen") { fun withIdUtente(idUtente: String): String = "$route/${Uri.encode(idUtente)}" }
    object EditPropertyScreen : Screen("edit_property_screen") { const val argId = "immobileId"; fun withId(id: String) = "$route/$id" }
    data object YourPropertyScreen : Screen("your_property_screen")
    data object ProfileScreen : Screen("profile_screen") { fun withIdUtente(idUtente: String): String = "$route/${Uri.encode(idUtente)}" }
    data object NotificationScreen : Screen("notification_screen")
    data object NotificationDetailScreen : Screen("notification_detail_screen") { fun createRoute(notificationId: String): String = "$route/$notificationId" }
    data object AdminDashboardScreen : Screen("admin_dashboard")
    data object AdminCreateAdminScreen : Screen("admin_create_admin")
    data object AdminCreateAgentScreen : Screen("admin_create_agent")
    data object AdminChangePasswordScreen : Screen("admin_change_password")
    data object AdminCreateAgencyScreen : Screen("admin_create_agency")
    data object ManagerScreen : Screen("manager_screen") { fun withIdUtente(idUtente: String): String = "$route/${Uri.encode(idUtente)}" }
    data object AppointmentDetailScreen : Screen("appointment_detail_screen")
    data object RequestsScreen : Screen("requests_screen") { fun withIdUtente(idUtente: String): String = "$route/${Uri.encode(idUtente)}" }
    data object ManagerNotificationScreen : Screen("manager_notification_screen") { fun withIdUtente(idUtente: String): String = "$route/${Uri.encode(idUtente)}" }
    data object ManagerRequestDetailScreen : Screen("manager_request_detail_screen") { fun withId(immobileId: String): String = "$route/${Uri.encode(immobileId)}" }
    object ManagerCreateAgentScreen : Screen("manager_create_agent")

    // --- NUOVA ROTTA PER LA PREVIEW DI ASSEGNAZIONE DEL MANAGER ---
    data object ManagerPendingPropertyScreen : Screen("manager_pending_property_screen") {
        fun withId(immobileId: String): String = "$route/${Uri.encode(immobileId)}"
    }

    companion object {
        fun buildFilterQuery(filters: FilterModel?): String {
            if (filters == null) return ""
            val params = mutableListOf<String>()
            filters.purchaseType?.let { params.add("purchaseType=${Uri.encode(it)}") }
            filters.minPrice?.let { params.add("minPrice=$it") }
            filters.maxPrice?.let { params.add("maxPrice=$it") }
            return if (params.isNotEmpty()) "?" + params.joinToString("&") else ""
        }
    }
}