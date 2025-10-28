package br.com.joaovictor.meumercadojusto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import br.com.joaovictor.meumercadojusto.navigation.MeuMercadoJustoNavHost
import br.com.joaovictor.meumercadojusto.ui.theme.MeuMercadoJustoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MeuMercadoJustoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    MeuMercadoJustoApp {
                        MeuMercadoJustoNavHost(navController)
                    }
                }
            }
        }
    }
}

@Composable
fun MeuMercadoJustoApp(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        content()
    }
}