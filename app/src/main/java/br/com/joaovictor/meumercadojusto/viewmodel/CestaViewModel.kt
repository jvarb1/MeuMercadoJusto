package br.com.joaovictor.meumercadojusto.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import br.com.joaovictor.meumercadojusto.model.ItemEncontrado
import br.com.joaovictor.meumercadojusto.model.Produto
import br.com.joaovictor.meumercadojusto.model.ResultadoCesta
import br.com.joaovictor.meumercadojusto.repository.CestaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CestaUiState(
    val resultadosCesta: List<ResultadoCesta> = emptyList(),
    val resultadosProdutos: List<ItemEncontrado> = emptyList(),
    val produtos: List<Produto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class CestaViewModel(
    private val repository: CestaRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CestaUiState())
    val uiState: StateFlow<CestaUiState> = _uiState.asStateFlow()
    
    init {
        carregarProdutos()
    }
    
    /**
     * Carrega todos os produtos disponíveis
     */
    private fun carregarProdutos() {
        viewModelScope.launch {
            repository.getAllProdutos().collect { produtos ->
                _uiState.value = _uiState.value.copy(produtos = produtos)
            }
        }
    }
    
    /**
     * Calcula a cesta mais barata com base nos produtos selecionados
     * Se nenhum produto for passado, usa todos os produtos disponíveis
     */
    fun calcularCestaMaisBarata(produtoIds: List<Int>? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                resultadosProdutos = emptyList()
            )
            
            try {
                val ids = produtoIds ?: _uiState.value.produtos.map { it.id }
                
                if (ids.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Nenhum produto disponível"
                    )
                    return@launch
                }
                
                val resultados = repository.calcularCestaMaisBarata(ids)
                
                _uiState.value = _uiState.value.copy(
                    resultadosCesta = resultados,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erro ao calcular cesta: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Busca produtos por nome
     */
    fun buscarProdutoPorNome(nome: String) {
        if (nome.isBlank()) {
            _uiState.value = _uiState.value.copy(
                resultadosProdutos = emptyList(),
                resultadosCesta = emptyList()
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                resultadosCesta = emptyList()
            )
            
            try {
                val resultados = repository.buscarProdutoPorNome(nome)
                
                _uiState.value = _uiState.value.copy(
                    resultadosProdutos = resultados,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erro ao buscar produto: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Limpa os resultados
     */
    fun limparResultados() {
        _uiState.value = _uiState.value.copy(
            resultadosCesta = emptyList(),
            resultadosProdutos = emptyList(),
            error = null
        )
    }
}

class CestaViewModelFactory(
    private val repository: CestaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CestaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CestaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

