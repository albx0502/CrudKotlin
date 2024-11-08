package com.example.crudkotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.crudkotlin.ui.auth.ForgotPasswordScreen
import com.example.crudkotlin.ui.auth.LoginScreen
import com.example.crudkotlin.ui.auth.RegisterScreen
import com.example.crudkotlin.ui.profile.ProfileScreen
import com.example.crudkotlin.ui.search.UserSearchScreen
import com.example.crudkotlin.ui.admin.AdminUserManagementScreen
import com.example.crudkotlin.ui.profile.UserProfileScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { isAdmin ->
                    if (isAdmin) {
                        navController.navigate("profile")
                    } else {
                        navController.navigate("profile")
                    }
                },
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgotPassword") }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate("login") },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable("forgotPassword") {
            ForgotPasswordScreen(
                onPasswordResetSuccess = { navController.navigate("login") },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable("profile") {
            ProfileScreen(
                onLogout = { navController.navigate("login") },
                onNavigateToUserSearch = { navController.navigate("userSearch") },
                onNavigateToUserManagement = { navController.navigate("admin") } // Navegación para el administrador
            )
        }
        composable("userSearch") {
            UserSearchScreen(
                onUserSelected = { userId -> navController.navigate("profile/$userId") }
            )
        }
        composable("admin") {
            AdminUserManagementScreen(
                onUserEdit = { userId -> navController.navigate("editUser/$userId") },
                onUserDelete = { userId ->
                    // Implementa la función deleteUser y actualiza la lista al borrar un usuario
                }
            )
        }
        composable("profile/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                UserProfileScreen(
                    userId = userId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable("editUser/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                UserProfileScreen(
                    userId = userId,
                    onBack = { navController.popBackStack() },
                    isEditable = true // Permite la edición cuando se navega desde el administrador
                )
            }
        }
    }
}
