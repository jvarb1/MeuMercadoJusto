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
import java.io.PrintWriter
import java.io.StringWriter

class MainActivity : ComponentActivity() {
    
    companion object {
        private var defaultExceptionHandler: Thread.UncaughtExceptionHandler? = null
        
        init {
            // Salvar o handler padrão ANTES de substituir
            defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
            
            // Handler global para capturar exceções não tratadas
            Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
                val stackTrace = StringWriter()
                exception.printStackTrace(PrintWriter(stackTrace))
                val errorReport = """
                    ========================================
                    CRASH REPORT - Meu Mercado Justo
                    ========================================
                    Thread: ${thread.name}
                    Exception: ${exception.javaClass.simpleName}
                    Message: ${exception.message}
                    
                    Stack Trace:
                    ${stackTrace.toString()}
                    ========================================
                """.trimIndent()
                
                android.util.Log.e("CRASH", errorReport)
                
                // Chamar o handler padrão (salvo anteriormente) para manter o comportamento normal
                defaultExceptionHandler?.uncaughtException(thread, exception) 
                    ?: android.os.Process.killProcess(android.os.Process.myPid())
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        android.util.Log.d("MainActivity", "onCreate iniciado")
        
        setContent {
            MeuMercadoJustoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    android.util.Log.d("MainActivity", "NavController criado")
                    MeuMercadoJustoApp {
                        MeuMercadoJustoNavHost(navController)
                    }
                }
            }
        }
        
        android.util.Log.d("MainActivity", "onCreate concluído com sucesso")
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