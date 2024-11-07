package com.example.crudkotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.crudkotlin.ui.auth.ForgotPasswordScreen
import com.example.crudkotlin.ui.auth.LoginScreen
import com.example.crudkotlin.ui.auth.RegisterScreen
import com.example.crudkotlin.ui.profile.ProfileScreen


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
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("profile") },
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToForgotPassword = { navController.navigate("forgotPassword") } // Navegación corregida
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate("login") },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable("forgotPassword") { // Pantalla de recuperación de contraseña
            ForgotPasswordScreen(
                onPasswordResetSuccess = { navController.navigate("login") },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable("profile") {
            ProfileScreen(
                onLogout = { navController.navigate("login") }
            )
        }

        // Puedes agregar más pantallas aquí, como "profile", cuando estén listas
    }
}
