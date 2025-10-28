package br.com.joaovictor.meumercadojusto.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit
) {
    var textoDaBusca by remember { mutableStateOf("") }
    var resultadosCesta by remember { mutableStateOf<List<ResultadoCesta>?>(null) }
    var resultadosProdutos by remember { mutableStateOf<List<ItemEncontrado>?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Buscar Produtos",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título
            Text(
                text = "Meu Mercado Justo",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Botão para calcular cesta mais barata
            Button(
                onClick = {
                    resultadosCesta = calcularCestaMaisBarata()
                    resultadosProdutos = null
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Qual a Cesta Mais Barata?")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo de busca
            OutlinedTextField(
                value = textoDaBusca,
                onValueChange = { novoTexto -> textoDaBusca = novoTexto },
                label = { Text("Digite o nome de um produto") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Botão de buscar produto
            Button(
                onClick = {
                    resultadosProdutos = buscarProdutoPorNome(textoDaBusca)
                    resultadosCesta = null
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Buscar Produto")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Resultados da cesta
            resultadosCesta?.let { lista ->
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(lista) { resultado ->
                        CestaItem(resultado = resultado)
                    }
                }
            }
            
            // Resultados de produtos
            resultadosProdutos?.let { lista ->
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(lista) { item ->
                        ProdutoItem(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun CestaItem(resultado: ResultadoCesta) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
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
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = resultado.endereco,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ProdutoItem(item: ItemEncontrado) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "R$ ${String.format("%.2f", item.precoProduto)} - ${item.nomeProduto}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = item.nomeEstabelecimento,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Dados fictícios para demonstração
data class ResultadoCesta(
    val nomeEstabelecimento: String,
    val precoTotal: Double,
    val endereco: String
)

data class ItemEncontrado(
    val nomeProduto: String,
    val precoProduto: Double,
    val nomeEstabelecimento: String
)

fun calcularCestaMaisBarata(): List<ResultadoCesta> {
    return listOf(
        ResultadoCesta(
            nomeEstabelecimento = "Supermercado Bom Preço",
            precoTotal = 89.50,
            endereco = "Rua das Flores, 123 - Centro"
        ),
        ResultadoCesta(
            nomeEstabelecimento = "Mercado Econômico",
            precoTotal = 92.30,
            endereco = "Av. Principal, 456 - Bairro Novo"
        ),
        ResultadoCesta(
            nomeEstabelecimento = "Supermercado Popular",
            precoTotal = 95.80,
            endereco = "Rua da Paz, 789 - Vila Verde"
        )
    )
}

fun buscarProdutoPorNome(nome: String): List<ItemEncontrado> {
    val produtos = listOf(
        ItemEncontrado("Arroz Branco", 4.50, "Supermercado Bom Preço"),
        ItemEncontrado("Feijão Preto", 6.80, "Mercado Econômico"),
        ItemEncontrado("Leite Integral", 4.20, "Supermercado Popular"),
        ItemEncontrado("Frango Inteiro", 8.90, "Supermercado Bom Preço"),
        ItemEncontrado("Banana Prata", 3.80, "Mercado Econômico")
    )
    
    return if (nome.isBlank()) {
        emptyList()
    } else {
        produtos.filter { 
            it.nomeProduto.contains(nome, ignoreCase = true) 
        }
    }
}
