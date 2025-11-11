package br.com.joaovictor.meumercadojusto.model

data class ResultadoCesta(
    val estabelecimento: Estabelecimento,
    val precoTotal: Double,
    val quantidadeItens: Int,
    val economia: Double? = null, // Comparado com o mais caro
    val itens: List<ItemCesta> = emptyList()
)

data class ItemCesta(
    val produto: Produto,
    val preco: Double,
    val estabelecimento: Estabelecimento
)

