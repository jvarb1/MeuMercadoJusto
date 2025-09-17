package br.com.joaovictor.meumercadojusto

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("principal") {
                    // Remove a tela de login da pilha de navegação
                    // para que o usuário não possa voltar para ela apertando "voltar"
                    popUpTo("login") { inclusive = true }
                }
            })
        }
        composable("principal") {
            // Se sua TelaPrincipal precisar de um ViewModel, você o instanciaria aqui
            // ou o deixaria ser instanciado dentro da própria TelaPrincipal como está agora.
            // Exemplo: val viewModel: CestaViewModel = viewModel()
            // TelaPrincipal(viewModel = viewModel)

            // Se TelaPrincipal não precisar de argumentos específicos passados aqui,
            // e já lida com seu próprio ViewModel internamente (como no seu código original),
            // a chamada simples é suficiente.
            TelaPrincipal()
        }
    }
}