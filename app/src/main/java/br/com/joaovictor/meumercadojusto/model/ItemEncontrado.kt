package br.com.joaovictor.meumercadojusto.model

data class ItemEncontrado(
    val produto: Produto,
    val preco: Double,
    val estabelecimento: Estabelecimento,
    val dataAtualizacao: Long
)

