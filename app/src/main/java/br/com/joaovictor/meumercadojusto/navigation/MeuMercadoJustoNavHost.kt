package br.com.joaovictor.meumercadojusto.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.com.joaovictor.meumercadojusto.screens.LoginScreen
import br.com.joaovictor.meumercadojusto.screens.MainScreen
import br.com.joaovictor.meumercadojusto.screens.SearchScreen

@Composable
fun MeuMercadoJustoNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        
        composable("main") {
            MainScreen(
                onNavigateToSearch = {
                    navController.navigate("search")
                }
            )
        }
        
        composable("search") {
            SearchScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
