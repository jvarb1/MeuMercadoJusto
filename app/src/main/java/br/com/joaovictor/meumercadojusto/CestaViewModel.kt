package br.com.joaovictor.meumercadojusto

// O ViewModel precisa herdar de androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModel

// Nova classe de dados para guardar o resultado do cálculo da cesta
data class ResultadoCesta(
    val nomeEstabelecimento: String,
    val precoTotal: Double,
    val endereco: String
)

class CestaViewModel : ViewModel() {

    // Pega a lista completa de todos os produtos de todos os mercados
    private val todosOsItens = BancoDeDadosFicticio.gerarDadosFicticios()

    /**
     * Função nº 1: Calcula o custo total da cesta em cada supermercado.
     */
    fun calcularCestaMaisBarata(): List<ResultadoCesta> {
        // 1. Agrupa todos os itens pelo nome do estabelecimento
        val itensPorEstabelecimento = todosOsItens.groupBy { it.nomeEstabelecimento }

        // 2. Para cada estabelecimento, calcula a soma dos preços dos produtos
        val cestaPorEstabelecimento = itensPorEstabelecimento.map { (nome, itens) ->
            val precoTotal = itens.sumOf { it.precoProduto }
            ResultadoCesta(
                nomeEstabelecimento = nome,
                precoTotal = precoTotal,
                endereco = itens.first().enderecoEstabelecimento // Pega o endereço do primeiro item
            )
        }

        // 3. Retorna a lista de resultados, ordenada do menor preço para o maior
        return cestaPorEstabelecimento.sortedBy { it.precoTotal }
    }

    /**
     * Função nº 2: Busca um produto específico pelo nome.
     */
    fun buscarProdutoPorNome(nomeDoProduto: String): List<ItemEncontrado> {
        // Se a busca estiver vazia, retorna uma lista vazia
        if (nomeDoProduto.isBlank()) {
            return emptyList()
        }

        // 1. Filtra a lista completa, procurando por itens cujo nome contenha o texto da busca
        val produtosEncontrados = todosOsItens.filter {
            it.nomeProduto.contains(nomeDoProduto, ignoreCase = true)
        }

        // 2. Retorna a lista de produtos encontrados, ordenada do menor preço para o maior
        return produtosEncontrados.sortedBy { it.precoProduto }
    }
}