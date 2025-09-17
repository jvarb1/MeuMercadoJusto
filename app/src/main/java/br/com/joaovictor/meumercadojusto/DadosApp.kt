package br.com.joaovictor.meumercadojusto

// Molde para representar UM PRODUTO encontrado em UM SUPERMERCADO.
// Pense nisso como uma única linha da sua pesquisa de preços.
data class ItemEncontrado(
    // Dados do produto
    val nomeProduto: String,
    val precoProduto: Double,

    // Dados do estabelecimento que vende este produto
    val nomeEstabelecimento: String,
    val enderecoEstabelecimento: String
)