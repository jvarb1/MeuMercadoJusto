package br.com.joaovictor.meumercadojusto.repository

import br.com.joaovictor.meumercadojusto.db.DatabaseHelper
import br.com.joaovictor.meumercadojusto.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class CestaRepository(private val db: DatabaseHelper) {
    
    /**
     * Calcula a cesta mais barata comparando todos os estabelecimentos
     * Retorna lista ordenada do mais barato para o mais caro
     */
    suspend fun calcularCestaMaisBarata(produtoIds: List<Int>): List<ResultadoCesta> {
        if (produtoIds.isEmpty()) return emptyList()
        
        val precos = db.precoProdutoDao().getPrecosPorProdutos(produtoIds)
        val estabelecimentos = db.estabelecimentoDao().getAllEstabelecimentos().first()
        
        val produtos = produtoIds.mapNotNull { id ->
            db.produtoDao().getProdutoPorId(id)
        }
        
        // Agrupar preços por estabelecimento
        val precosPorEstabelecimento = precos.groupBy { it.estabelecimentoId }
        
        val resultados = mutableListOf<ResultadoCesta>()
        
        estabelecimentos.forEach { estabelecimento ->
            val precosEstabelecimento = precosPorEstabelecimento[estabelecimento.id] ?: return@forEach
            
            // Verificar se tem todos os produtos
            val produtosEncontrados = produtos.filter { produto ->
                precosEstabelecimento.any { it.produtoId == produto.id }
            }
            
            if (produtosEncontrados.size == produtoIds.size) {
                val itens = produtosEncontrados.map { produto ->
                    val preco = precosEstabelecimento.first { it.produtoId == produto.id }
                    ItemCesta(
                        produto = produto,
                        preco = preco.preco,
                        estabelecimento = estabelecimento
                    )
                }
                
                val precoTotal = itens.sumOf { it.preco }
                
                resultados.add(
                    ResultadoCesta(
                        estabelecimento = estabelecimento,
                        precoTotal = precoTotal,
                        quantidadeItens = itens.size,
                        itens = itens
                    )
                )
            }
        }
        
        // Ordenar por preço total
        val ordenados = resultados.sortedBy { it.precoTotal }
        
        // Calcular economia (diferença entre o mais caro e cada um)
        val maisCaro = ordenados.lastOrNull()?.precoTotal ?: 0.0
        return ordenados.map { resultado ->
            resultado.copy(economia = maisCaro - resultado.precoTotal)
        }
    }
    
    /**
     * Busca produtos por nome e retorna os preços em cada estabelecimento
     */
    suspend fun buscarProdutoPorNome(nome: String): List<ItemEncontrado> {
        if (nome.isBlank()) return emptyList()
        
        val todosProdutos = db.produtoDao().getAllProdutos().first()
        
        val produtosFiltrados = todosProdutos.filter {
            it.nome.contains(nome, ignoreCase = true)
        }
        
        val resultados = mutableListOf<ItemEncontrado>()
        
        produtosFiltrados.forEach { produto ->
            val precos = db.precoProdutoDao().getPrecosPorProduto(produto.id).first()
            
            precos.forEach { preco ->
                val estabelecimento = db.estabelecimentoDao()
                    .getEstabelecimentoPorId(preco.estabelecimentoId)
                
                estabelecimento?.let {
                    resultados.add(
                        ItemEncontrado(
                            produto = produto,
                            preco = preco.preco,
                            estabelecimento = it,
                            dataAtualizacao = preco.dataAtualizacao
                        )
                    )
                }
            }
        }
        
        // Ordenar por preço
        return resultados.sortedBy { it.preco }
    }
    
    /**
     * Obtém todos os produtos disponíveis
     */
    fun getAllProdutos(): Flow<List<Produto>> {
        return db.produtoDao().getAllProdutos()
    }
    
    /**
     * Obtém todos os estabelecimentos
     */
    fun getAllEstabelecimentos(): Flow<List<Estabelecimento>> {
        return db.estabelecimentoDao().getAllEstabelecimentos()
    }
}

