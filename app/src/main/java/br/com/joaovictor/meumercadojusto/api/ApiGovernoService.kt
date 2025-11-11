package br.com.joaovictor.meumercadojusto.api

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface para integração com API do Governo
 * TODO: Atualizar com os endpoints reais da documentação PDF
 */
interface ApiGovernoService {
    
    /**
     * Busca preços de produtos em estabelecimentos
     * TODO: Ajustar conforme documentação da API
     */
    @GET("precos")
    suspend fun buscarPrecos(
        @Query("produto") produto: String? = null,
        @Query("estabelecimento") estabelecimento: String? = null,
        @Query("cidade") cidade: String? = null,
        @Query("estado") estado: String? = null
    ): ApiResponse<List<PrecoApiResponse>>
    
    /**
     * Busca estabelecimentos cadastrados
     */
    @GET("estabelecimentos")
    suspend fun buscarEstabelecimentos(
        @Query("cidade") cidade: String? = null,
        @Query("estado") estado: String? = null
    ): ApiResponse<List<EstabelecimentoApiResponse>>
    
    /**
     * Busca produtos disponíveis
     */
    @GET("produtos")
    suspend fun buscarProdutos(
        @Query("categoria") categoria: String? = null,
        @Query("nome") nome: String? = null
    ): ApiResponse<List<ProdutoApiResponse>>
}

/**
 * Resposta genérica da API
 */
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val errors: List<String>? = null
)

/**
 * Resposta de preço da API
 */
data class PrecoApiResponse(
    val id: Int,
    val produtoId: Int,
    val produtoNome: String,
    val estabelecimentoId: Int,
    val estabelecimentoNome: String,
    val preco: Double,
    val dataAtualizacao: String,
    val disponivel: Boolean
)

/**
 * Resposta de estabelecimento da API
 */
data class EstabelecimentoApiResponse(
    val id: Int,
    val nome: String,
    val endereco: String,
    val cidade: String,
    val estado: String,
    val cep: String,
    val telefone: String?,
    val latitude: Double?,
    val longitude: Double?
)

/**
 * Resposta de produto da API
 */
data class ProdutoApiResponse(
    val id: Int,
    val nome: String,
    val categoria: String,
    val unidade: String,
    val descricao: String?,
    val imagemUrl: String?
)

