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
import com.example.crudkotlin.ui.auth.LoginScreen
import com.example.crudkotlin.ui.auth.RegisterScreen


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
                onNavigateToForgotPassword = { /* Navegar a la pantalla de recuperación */ }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate("login") },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        // Puedes agregar más pantallas aquí, como "profile" o "forgotPassword", cuando estén listas
    }
}
