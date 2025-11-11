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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.joaovictor.meumercadojusto.api.ApiClient
import br.com.joaovictor.meumercadojusto.db.DatabaseHelper
import br.com.joaovictor.meumercadojusto.db.DatabaseInitializer
import br.com.joaovictor.meumercadojusto.model.Estabelecimento
import br.com.joaovictor.meumercadojusto.model.ItemEncontrado
import br.com.joaovictor.meumercadojusto.model.Produto
import br.com.joaovictor.meumercadojusto.model.ResultadoCesta
import br.com.joaovictor.meumercadojusto.repository.CestaRepository
import br.com.joaovictor.meumercadojusto.repository.EconomizaAlagoasRepository
import br.com.joaovictor.meumercadojusto.viewmodel.CestaViewModel
import br.com.joaovictor.meumercadojusto.viewmodel.CestaViewModelFactory
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Inicializar ViewModel
    val repository = remember {
        CestaRepository(DatabaseHelper.getInstance(context))
    }
    
    val viewModel: CestaViewModel = viewModel(
        factory = CestaViewModelFactory(repository)
    )
    
    val uiState by viewModel.uiState.collectAsState()
    var textoDaBusca by remember { mutableStateOf("") }
    
    // Estado para API do governo
    var apiLoading by remember { mutableStateOf(false) }
    var apiError by remember { mutableStateOf<String?>(null) }
    var apiResults by remember { mutableStateOf<List<ItemEncontrado>>(emptyList()) }
    var apiRepository by remember { 
        mutableStateOf<EconomizaAlagoasRepository?>(null) 
    }
    
    // Inicializar banco de dados e API na primeira vez
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                android.util.Log.d("SearchScreen", "Iniciando inicialização...")
                DatabaseInitializer.initialize(context)
                android.util.Log.d("SearchScreen", "Database inicializado")
                
                // Inicializar repositório da API se estiver configurado
                try {
                    if (ApiClient.isConfigured()) {
                        android.util.Log.d("SearchScreen", "API configurada, criando repositório...")
                        try {
                            apiRepository = EconomizaAlagoasRepository()
                            android.util.Log.d("SearchScreen", "Repositório da API criado com sucesso")
                        } catch (e: Exception) {
                            // Erro ao criar repositório - continuar sem API
                            android.util.Log.e("SearchScreen", "Erro ao criar repositório da API: ${e.message}", e)
                            android.util.Log.e("SearchScreen", "Stack trace: ${e.stackTraceToString()}")
                        }
                    } else {
                        android.util.Log.d("SearchScreen", "API não configurada")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("SearchScreen", "Erro ao verificar configuração da API: ${e.message}", e)
                    android.util.Log.e("SearchScreen", "Stack trace: ${e.stackTraceToString()}")
                }
                android.util.Log.d("SearchScreen", "Inicialização concluída")
            } catch (e: Exception) {
                android.util.Log.e("SearchScreen", "ERRO CRÍTICO na inicialização: ${e.message}", e)
                android.util.Log.e("SearchScreen", "Stack trace completo: ${e.stackTraceToString()}")
                // Não re-throw para não crashar o app
            }
        }
    }
    
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
                    viewModel.calcularCestaMaisBarata()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Qual a Cesta Mais Barata?")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo de busca
            OutlinedTextField(
                value = textoDaBusca,
                onValueChange = { novoTexto -> 
                    textoDaBusca = novoTexto
                    if (novoTexto.isBlank()) {
                        viewModel.limparResultados()
                    }
                },
                label = { Text("Digite o nome de um produto") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                },
                enabled = !uiState.isLoading
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Botão de buscar produto (usa API do governo se disponível)
            Button(
                onClick = {
                    if (apiRepository != null && ApiClient.isConfigured()) {
                        // Buscar na API do governo
                        scope.launch {
                            apiLoading = true
                            apiError = null
                            apiResults = emptyList()
                            
                            try {
                                // Validar entrada antes de enviar
                                val descricaoBusca = textoDaBusca.trim()
                                if (descricaoBusca.length < 3) {
                                    apiError = "Digite pelo menos 3 caracteres para buscar"
                                    apiLoading = false
                                    return@launch
                                }
                                
                                val result = apiRepository!!.pesquisarESincronizarProdutos(
                                    descricao = descricaoBusca,
                                    codigoIBGE = "2700300", // Arapiraca (7 dígitos) - será convertido para Int
                                    dias = 7
                                )
                                
                                result.fold(
                                    onSuccess = { response ->
                                        try {
                                            // Converter resultados da API para ItemEncontrado
                                            apiResults = response.conteudo.mapNotNull { resultado ->
                                                try {
                                                    val produto = resultado.produto
                                                    val estabelecimento = resultado.estabelecimento
                                                    
                                                    ItemEncontrado(
                                                        produto = Produto(
                                                            id = 0,
                                                            nome = produto.descricao,
                                                            categoria = "Geral",
                                                            preco = produto.venda.valorVenda,
                                                            unidade = produto.unidadeMedida,
                                                            descricao = produto.descricaoSefaz ?: produto.descricao,
                                                            imagemUrl = "",
                                                            emEstoque = true
                                                        ),
                                                        preco = produto.venda.valorVenda,
                                                        estabelecimento = Estabelecimento(
                                                            id = 0,
                                                            nome = estabelecimento.nomeFantasia.ifBlank { 
                                                                estabelecimento.razaoSocial 
                                                            },
                                                            endereco = "${estabelecimento.endereco.nomeLogradouro}, ${estabelecimento.endereco.numeroImovel} - ${estabelecimento.endereco.bairro}",
                                                            cidade = estabelecimento.endereco.municipio,
                                                            estado = "AL",
                                                            cep = estabelecimento.endereco.cep,
                                                            telefone = estabelecimento.telefone ?: "",
                                                            latitude = estabelecimento.endereco.latitude,
                                                            longitude = estabelecimento.endereco.longitude,
                                                            ativo = true
                                                        ),
                                                        dataAtualizacao = parseDataVenda(produto.venda.dataVenda)
                                                    )
                                                } catch (e: Exception) {
                                                    // Ignorar itens com erro na conversão
                                                    null
                                                }
                                            }.sortedBy { it.preco }
                                            
                                            if (apiResults.isEmpty()) {
                                                apiError = "Nenhum produto encontrado"
                                            }
                                        } catch (e: Exception) {
                                            apiError = "Erro ao processar resultados: ${e.message}"
                                        }
                                    },
                                    onFailure = { error ->
                                        apiError = "Erro ao buscar na API: ${error.message ?: "Erro desconhecido"}"
                                    }
                                )
                            } catch (e: Exception) {
                                apiError = "Erro: ${e.message ?: "Erro desconhecido"}"
                            } finally {
                                apiLoading = false
                            }
                        }
                    } else {
                        // Fallback: buscar no banco local
                        viewModel.buscarProdutoPorNome(textoDaBusca)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                enabled = (!uiState.isLoading && !apiLoading) && textoDaBusca.isNotBlank()
            ) {
                if (uiState.isLoading || apiLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        if (apiRepository != null) "Buscar na API do Governo" else "Buscar Produto"
                    )
                }
            }
            
            // Indicador de fonte de dados
            if (apiRepository != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "🔗 Conectado à API Economiza Alagoas",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Mensagens de erro
            apiError?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Resultados da cesta
            if (uiState.resultadosCesta.isNotEmpty()) {
                Text(
                    text = "Resultados da Cesta:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.resultadosCesta) { resultado ->
                        CestaItem(resultado = resultado)
                    }
                }
            }
            
            // Resultados de produtos (API ou Local)
            val produtosParaExibir = if (apiResults.isNotEmpty()) {
                apiResults
            } else {
                uiState.resultadosProdutos
            }
            
            if (produtosParaExibir.isNotEmpty()) {
                Text(
                    text = if (apiResults.isNotEmpty()) {
                        "Resultados da API (${apiResults.size} encontrados):"
                    } else {
                        "Resultados da Busca:"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(produtosParaExibir) { item ->
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
                text = resultado.estabelecimento.nome,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Preço Total: R$ ${String.format("%.2f", resultado.precoTotal)}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "${resultado.quantidadeItens} itens",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = resultado.estabelecimento.endereco,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            resultado.economia?.let { economia ->
                if (economia > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "💰 Economia: R$ ${String.format("%.2f", economia)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// Função auxiliar para parse de data
fun parseDataVenda(dataString: String): Long {
    return try {
        Instant.from(DateTimeFormatter.ISO_INSTANT.parse(dataString)).toEpochMilli()
    } catch (e: Exception) {
        System.currentTimeMillis()
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
                text = "R$ ${String.format("%.2f", item.preco)} - ${item.produto.nome}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = item.estabelecimento.nome,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = item.estabelecimento.endereco,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
