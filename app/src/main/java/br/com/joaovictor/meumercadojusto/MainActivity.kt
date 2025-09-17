package br.com.joaovictor.meumercadojusto

// Removido OutlinedTextField daqui, pois não é usado diretamente na MainActivity modificada
// Se OutlinedTextField for usado em LoginScreen ou AppNavigation,
// o import estará nesses arquivos.
// import androidx.compose.material3.OutlinedTextField // Removido se não usado aqui
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField // Mantido pois TelaPrincipal usa
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.joaovictor.meumercadojusto.ui.theme.MeuMercadoJustoTheme
// Adicione a importação para AppNavigation se não for adicionada automaticamente
// import br.com.joaovictor.meumercadojusto.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MeuMercadoJustoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // A MainActivity agora apenas configura o AppNavigation
                    AppNavigation() // MODIFICADO AQUI
                }
            }
        }
    }
}

// Suas funções Composable existentes permanecem aqui, pois AppNavigation as usará
// quando navegar para a rota "principal".

@Composable
fun TelaPrincipal(viewModel: CestaViewModel = viewModel()) { // Assumindo que CestaViewModel existe
    // Variáveis para "lembrar" o que deve ser mostrado na tela
    var listaDeResultadosCesta by remember { mutableStateOf<List<ResultadoCesta>?>(null) } // Assumindo que ResultadoCesta existe
    var listaDeResultadosProdutos by remember { mutableStateOf<List<ItemEncontrado>?>(null) } // Assumindo que ItemEncontrado existe

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Meu Mercado Justo",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            listaDeResultadosCesta = viewModel.calcularCestaMaisBarata()
            listaDeResultadosProdutos = null
        }) {
            Text("Qual a Cesta Mais Barata?")
        }

        Spacer(modifier = Modifier.height(16.dp))

        var textoDaBusca by remember { mutableStateOf("") }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = textoDaBusca,
            onValueChange = { novoTexto -> textoDaBusca = novoTexto },
            label = { Text("Digite o nome de um produto") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            listaDeResultadosProdutos = viewModel.buscarProdutoPorNome(textoDaBusca)
            listaDeResultadosCesta = null
        }) {
            Text("Buscar Produto")
        }

        Spacer(modifier = Modifier.height(32.dp))

        listaDeResultadosCesta?.let { lista ->
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(lista) { resultado ->
                    CestaItem(resultado = resultado)
                }
            }
        }

        listaDeResultadosProdutos?.let { lista ->
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(lista) { item ->
                    ProdutoItem(item = item)
                }
            }
        }
    }
}

@Composable
fun CestaItem(resultado: ResultadoCesta) { // Assumindo que ResultadoCesta existe
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = resultado.nomeEstabelecimento,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = "Preço Total: R$ ${String.format("%.2f", resultado.precoTotal)}",
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            Text(
                text = resultado.endereco,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ProdutoItem(item: ItemEncontrado) { // Assumindo que ItemEncontrado existe
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "R$ ${String.format("%.2f", item.precoProduto)} - ${item.nomeProduto}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF006400) // Verde escuro
            )
            Text(
                text = item.nomeEstabelecimento,
                fontSize = 16.sp,
                color = Color.DarkGray
            )
        }
    }
}
