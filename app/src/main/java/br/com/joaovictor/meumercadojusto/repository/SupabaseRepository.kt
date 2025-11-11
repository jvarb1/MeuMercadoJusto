package br.com.joaovictor.meumercadojusto.repository

import br.com.joaovictor.meumercadojusto.model.*
import br.com.joaovictor.meumercadojusto.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Repositório que usa Supabase ao invés de Room
 * 
 * Para usar este repositório:
 * 1. Configure o SupabaseClient com suas credenciais
 * 2. Substitua CestaRepository por SupabaseRepository no ViewModelFactory
 */
class SupabaseRepository {
    
    private val client = SupabaseClient.client
        ?: throw IllegalStateException("Supabase não está configurado. Verifique local.properties")
    
    /**
     * Calcula a cesta mais barata comparando todos os estabelecimentos
     */
    suspend fun calcularCestaMaisBarata(produtoIds: List<Int>): List<ResultadoCesta> {
        if (produtoIds.isEmpty()) return emptyList()
        
        // Buscar produtos - fazer queries individuais se necessário
        val produtos = if (produtoIds.size == 1) {
            client.from("produtos")
                .select {
                    filter {
                        eq("id", produtoIds.first())
                    }
                }
                .decodeList<ProdutoSupabase>()
        } else {
            // Para múltiplos IDs, buscar todos e filtrar
            val todosProdutos = client.from("produtos")
                .select()
                .decodeList<ProdutoSupabase>()
            todosProdutos.filter { it.id in produtoIds }
        }
        
        // Buscar preços - fazer queries individuais se necessário
        val precos = if (produtoIds.size == 1) {
            client.from("precos_produtos")
                .select {
                    filter {
                        eq("produto_id", produtoIds.first())
                        eq("disponivel", true)
                    }
                }
                .decodeList<PrecoProdutoSupabase>()
        } else {
            // Para múltiplos IDs, buscar todos e filtrar
            val todosPrecos = client.from("precos_produtos")
                .select {
                    filter {
                        eq("disponivel", true)
                    }
                }
                .decodeList<PrecoProdutoSupabase>()
            todosPrecos.filter { it.produtoId in produtoIds }
        }
        
        // Buscar estabelecimentos
        val estabelecimentos = client.from("estabelecimentos")
            .select {
                filter {
                    eq("ativo", true)
                }
            }
            .decodeList<EstabelecimentoSupabase>()
        
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
                        produto = produto.toProduto(),
                        preco = preco.preco,
                        estabelecimento = estabelecimento.toEstabelecimento()
                    )
                }
                
                val precoTotal = itens.sumOf { it.preco }
                
                resultados.add(
                    ResultadoCesta(
                        estabelecimento = estabelecimento.toEstabelecimento(),
                        precoTotal = precoTotal,
                        quantidadeItens = itens.size,
                        itens = itens
                    )
                )
            }
        }
        
        // Ordenar por preço total
        val ordenados = resultados.sortedBy { it.precoTotal }
        
        // Calcular economia
        val maisCaro = ordenados.lastOrNull()?.precoTotal ?: 0.0
        return ordenados.map { resultado ->
            resultado.copy(economia = maisCaro - resultado.precoTotal)
        }
    }
    
    /**
     * Busca produtos por nome
     */
    suspend fun buscarProdutoPorNome(nome: String): List<ItemEncontrado> {
        if (nome.isBlank()) return emptyList()
        
        val produtos = client.from("produtos")
            .select {
                filter {
                    ilike("nome", "%$nome%")
                }
            }
            .decodeList<ProdutoSupabase>()
        
        val resultados = mutableListOf<ItemEncontrado>()
        
        produtos.forEach { produto ->
            val precos = client.from("precos_produtos")
                .select {
                    filter {
                        eq("produto_id", produto.id)
                        eq("disponivel", true)
                    }
                    order("preco", Order.ASCENDING)
                }
                .decodeList<PrecoProdutoSupabase>()
            
            precos.forEach { preco ->
                val estabelecimento = client.from("estabelecimentos")
                    .select {
                        filter {
                            eq("id", preco.estabelecimentoId)
                        }
                    }
                    .decodeSingle<EstabelecimentoSupabase>()
                
                resultados.add(
                    ItemEncontrado(
                        produto = produto.toProduto(),
                        preco = preco.preco,
                        estabelecimento = estabelecimento.toEstabelecimento(),
                        dataAtualizacao = preco.dataAtualizacao
                    )
                )
            }
        }
        
        return resultados.sortedBy { it.preco }
    }
    
    /**
     * Obtém todos os produtos
     */
    fun getAllProdutos(): Flow<List<Produto>> = flow {
        val produtos = client.from("produtos")
            .select {
                order("categoria", Order.ASCENDING)
                order("nome", Order.ASCENDING)
            }
            .decodeList<ProdutoSupabase>()
        
        emit(produtos.map { it.toProduto() })
    }
    
    /**
     * Obtém todos os estabelecimentos
     */
    fun getAllEstabelecimentos(): Flow<List<Estabelecimento>> = flow {
        val estabelecimentos = client.from("estabelecimentos")
            .select {
                filter {
                    eq("ativo", true)
                }
                order("nome", Order.ASCENDING)
            }
            .decodeList<EstabelecimentoSupabase>()
        
        emit(estabelecimentos.map { it.toEstabelecimento() })
    }
}

// Modelos para Supabase (com nomes de colunas em snake_case)
// Tornados públicos para uso em outros repositórios
@Serializable
data class ProdutoSupabase(
    val id: Int,
    val nome: String,
    val categoria: String,
    val preco: Double,
    val unidade: String,
    val descricao: String = "",
    @SerialName("imagem_url")
    val imagemUrl: String = "",
    @SerialName("em_estoque")
    val emEstoque: Boolean = true
) {
    fun toProduto() = Produto(
        id = id,
        nome = nome,
        categoria = categoria,
        preco = preco,
        unidade = unidade,
        descricao = descricao,
        imagemUrl = imagemUrl,
        emEstoque = emEstoque
    )
}

@Serializable
data class EstabelecimentoSupabase(
    val id: Int,
    val nome: String,
    val endereco: String,
    val cidade: String,
    val estado: String,
    val cep: String = "",
    val telefone: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val ativo: Boolean = true
) {
    fun toEstabelecimento() = Estabelecimento(
        id = id,
        nome = nome,
        endereco = endereco,
        cidade = cidade,
        estado = estado,
        cep = cep,
        telefone = telefone,
        latitude = latitude,
        longitude = longitude,
        ativo = ativo
    )
}

@Serializable
data class PrecoProdutoSupabase(
    val id: Int,
    @SerialName("produto_id")
    val produtoId: Int,
    @SerialName("estabelecimento_id")
    val estabelecimentoId: Int,
    val preco: Double,
    @SerialName("data_atualizacao")
    val dataAtualizacao: Long,
    val disponivel: Boolean = true
)

