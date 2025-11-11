package br.com.joaovictor.meumercadojusto.repository

import br.com.joaovictor.meumercadojusto.api.ApiClient
import br.com.joaovictor.meumercadojusto.api.EconomizaAlagoasService
import br.com.joaovictor.meumercadojusto.api.*
import br.com.joaovictor.meumercadojusto.model.*
import br.com.joaovictor.meumercadojusto.repository.EstabelecimentoSupabase
import br.com.joaovictor.meumercadojusto.repository.ProdutoSupabase
import br.com.joaovictor.meumercadojusto.repository.PrecoProdutoSupabase
import br.com.joaovictor.meumercadojusto.supabase.SupabaseClient as SupabaseClientObject
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 * Repositório para buscar dados da API Economiza Alagoas e sincronizar com Supabase
 */
class EconomizaAlagoasRepository {
    
    // Usar lazy para evitar inicialização prematura
    private val apiService: EconomizaAlagoasService by lazy {
        ApiClient.economizaAlagoasService
    }
    
    private val supabase: SupabaseClient? by lazy {
        try {
            // Só tentar inicializar se estiver configurado
            if (SupabaseClientObject.isConfigured()) {
                SupabaseClientObject.client
            } else {
                android.util.Log.d("EconomizaAlagoasRepository", "Supabase não configurado, continuando sem sincronização")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("EconomizaAlagoasRepository", "Erro ao inicializar Supabase: ${e.message}", e)
            null
        }
    }
    
    /**
     * Pesquisa produtos na API e sincroniza com Supabase
     */
    suspend fun pesquisarESincronizarProdutos(
        descricao: String? = null,
        gtin: String? = null,
        codigoIBGE: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        raio: Int? = null,
        dias: Int = 7
    ): Result<PesquisaProdutoResponse> {
        return try {
            // Construir critérios de estabelecimento
            val estabelecimentoCriteria = when {
                codigoIBGE != null -> {
                    // Garantir que o código IBGE está no formato correto (7 dígitos)
                    // IMPORTANTE: Converter para Int conforme a API Python que funciona
                    val codigoIBGEFormatado = codigoIBGE.trim()
                    if (codigoIBGEFormatado.length != 7) {
                        throw IllegalArgumentException("Código IBGE deve ter 7 dígitos")
                    }
                    // Validar que é numérico e converter para Int
                    val codigoIBGEInt = codigoIBGEFormatado.toIntOrNull()
                        ?: throw IllegalArgumentException("Código IBGE deve conter apenas dígitos")
                    EstabelecimentoCriteria(
                        municipio = MunicipioCriteria(codigoIBGE = codigoIBGEInt),
                        individual = null,
                        geolocalizacao = null
                    )
                }
                latitude != null && longitude != null && raio != null -> {
                    val raioValidado = raio.coerceIn(1, 15)
                    EstabelecimentoCriteria(
                        geolocalizacao = GeolocalizacaoCriteria(
                            latitude = latitude,
                            longitude = longitude,
                            raio = raioValidado
                        ),
                        individual = null,
                        municipio = null
                    )
                }
                else -> {
                    // Se não especificado, usar Arapiraca como padrão (2700300)
                    // IMPORTANTE: Usar Int conforme a API Python que funciona
                    EstabelecimentoCriteria(
                        municipio = MunicipioCriteria(codigoIBGE = 2700300), // Arapiraca - Int
                        individual = null,
                        geolocalizacao = null
                    )
                }
            }
            
            // Construir critérios de produto
            val produtoCriteria = when {
                gtin != null && descricao != null -> {
                    throw IllegalArgumentException("Deve informar APENAS gtin OU descricao, não ambos")
                }
                gtin != null -> {
                    ProdutoCriteria(
                        gtin = gtin.trim(),
                        descricao = null,
                        ncm = null,
                        gpc = null
                    )
                }
                descricao != null -> {
                    val descricaoLimpa = descricao.trim()
                    if (descricaoLimpa.length < 3 || descricaoLimpa.length > 50) {
                        throw IllegalArgumentException("Descrição deve ter entre 3 e 50 caracteres")
                    }
                    ProdutoCriteria(
                        descricao = descricaoLimpa,
                        gtin = null,
                        ncm = null,
                        gpc = null
                    )
                }
                else -> throw IllegalArgumentException("Deve informar gtin ou descricao")
            }
            
            // Validar dias
            val diasValidados = dias.coerceIn(1, 10)
            
            val request = PesquisaProdutoRequest(
                produto = produtoCriteria,
                estabelecimento = estabelecimentoCriteria,
                dias = diasValidados,
                pagina = 1,
                registrosPorPagina = 100
            )
            
            // Log do request para debug
            val codigoIBGELog = estabelecimentoCriteria.municipio?.codigoIBGE ?: "geolocalização"
            android.util.Log.d("EconomizaAlagoas", "Request: produto=${produtoCriteria.descricao ?: produtoCriteria.gtin}, dias=$diasValidados")
            android.util.Log.d("EconomizaAlagoas", "Estabelecimento: $codigoIBGELog (tipo: ${codigoIBGELog.javaClass.simpleName})")
            
            // Log do código IBGE (agora é Int)
            if (estabelecimentoCriteria.municipio != null) {
                val codigo = estabelecimentoCriteria.municipio.codigoIBGE
                android.util.Log.d("EconomizaAlagoas", "Código IBGE: $codigo (tipo: Int)")
            }
            
            val response = try {
                apiService.pesquisarProdutos(request)
            } catch (e: retrofit2.HttpException) {
                // Capturar resposta de erro da API
                val errorBody = e.response()?.errorBody()?.string()
                android.util.Log.e("EconomizaAlagoas", "HTTP ${e.code()}: $errorBody")
                throw Exception("Erro HTTP ${e.code()}: ${errorBody ?: e.message()}")
            } catch (e: Exception) {
                android.util.Log.e("EconomizaAlagoas", "Erro na requisição: ${e.message}", e)
                throw e
            }
            
            // Sincronizar com Supabase (se configurado)
            if (supabase != null) {
                try {
                    sincronizarResultadosParaSupabase(response)
                } catch (e: Exception) {
                    // Log do erro mas não falha a requisição
                    // Os dados da API ainda são retornados mesmo se a sincronização falhar
                }
            }
            
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Sincroniza resultados da API para o Supabase
     */
    private suspend fun sincronizarResultadosParaSupabase(response: PesquisaProdutoResponse) {
        val client = supabase ?: return // Retorna se Supabase não estiver configurado
        
        response.conteudo.forEach { resultado ->
            // Sincronizar estabelecimento
            val estabelecimento = resultado.estabelecimento
            val estabelecimentoSupabase = EstabelecimentoSupabase(
                id = 0, // Será gerado pelo Supabase
                nome = estabelecimento.nomeFantasia.ifBlank { estabelecimento.razaoSocial },
                endereco = "${estabelecimento.endereco.nomeLogradouro}, ${estabelecimento.endereco.numeroImovel}",
                cidade = estabelecimento.endereco.municipio,
                estado = "AL",
                cep = estabelecimento.endereco.cep,
                telefone = estabelecimento.telefone,
                latitude = estabelecimento.endereco.latitude,
                longitude = estabelecimento.endereco.longitude,
                ativo = true
            )
            
            // Buscar ou criar estabelecimento
            val estabelecimentoId = try {
                val existentes = client.from("estabelecimentos")
                    .select {
                        filter {
                            eq("nome", estabelecimentoSupabase.nome)
                            eq("cidade", estabelecimentoSupabase.cidade)
                        }
                    }
                    .decodeList<EstabelecimentoSupabase>()
                
                if (existentes.isNotEmpty()) {
                    existentes.first().id
                } else {
                    val novo = client.from("estabelecimentos")
                        .insert(estabelecimentoSupabase) {
                            select()
                        }
                        .decodeSingle<EstabelecimentoSupabase>()
                    novo.id
                }
            } catch (e: Exception) {
                // Se der erro, criar novo
                val novo = client.from("estabelecimentos")
                    .insert(estabelecimentoSupabase) {
                        select()
                    }
                    .decodeSingle<EstabelecimentoSupabase>()
                novo.id
            }
            
            // Sincronizar produto
            val produto = resultado.produto
            val produtoSupabase = ProdutoSupabase(
                id = 0,
                nome = produto.descricao,
                categoria = "Geral", // A API não fornece categoria diretamente
                preco = produto.venda.valorVenda,
                unidade = produto.unidadeMedida,
                descricao = produto.descricaoSefaz ?: produto.descricao,
                imagemUrl = "",
                emEstoque = true
            )
            
            // Buscar ou criar produto
            val produtoId = try {
                val existentes = client.from("produtos")
                    .select {
                        filter {
                            eq("nome", produtoSupabase.nome)
                        }
                    }
                    .decodeList<ProdutoSupabase>()
                
                if (existentes.isNotEmpty()) {
                    existentes.first().id
                } else {
                    val novo = client.from("produtos")
                        .insert(produtoSupabase) {
                            select()
                        }
                        .decodeSingle<ProdutoSupabase>()
                    novo.id
                }
            } catch (e: Exception) {
                // Se der erro, criar novo
                val novo = client.from("produtos")
                    .insert(produtoSupabase) {
                        select()
                    }
                    .decodeSingle<ProdutoSupabase>()
                novo.id
            }
            
            // Sincronizar preço
            val dataAtualizacao = parseDataVenda(produto.venda.dataVenda)
            val precoSupabase = PrecoProdutoSupabase(
                id = 0,
                produtoId = produtoId,
                estabelecimentoId = estabelecimentoId,
                preco = produto.venda.valorVenda,
                dataAtualizacao = dataAtualizacao,
                disponivel = true
            )
            
            // Upsert preço (atualizar se existir)
            client.from("precos_produtos")
                .upsert(precoSupabase) {
                    onConflict = "produto_id,estabelecimento_id"
                }
        }
    }
    
    /**
     * Converte data ISO 8601 UTC para timestamp
     */
    private fun parseDataVenda(dataString: String): Long {
        return try {
            // Formato: YYYY-MM-DDThh:mm:ssZ
            val formatter = DateTimeFormatter.ISO_INSTANT
            Instant.parse(dataString).toEpochMilli()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
    
    /**
     * Pesquisa combustíveis na API
     */
    suspend fun pesquisarCombustiveis(
        tipoCombustivel: Int, // 1-6 conforme documentação
        codigoIBGE: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        raio: Int? = null,
        dias: Int = 7
    ): Result<PesquisaCombustivelResponse> {
        return try {
            val estabelecimentoCriteria = when {
                codigoIBGE != null -> {
                    // Converter String para Int conforme API Python
                    val codigoIBGEFormatado = codigoIBGE.trim()
                    val codigoIBGEInt = codigoIBGEFormatado.toIntOrNull()
                        ?: throw IllegalArgumentException("Código IBGE deve conter apenas dígitos")
                    EstabelecimentoCriteria(
                        municipio = MunicipioCriteria(codigoIBGE = codigoIBGEInt),
                        individual = null,
                        geolocalizacao = null
                    )
                }
                latitude != null && longitude != null && raio != null -> {
                    val raioValidado = raio.coerceIn(1, 15)
                    EstabelecimentoCriteria(
                        geolocalizacao = GeolocalizacaoCriteria(
                            latitude = latitude,
                            longitude = longitude,
                            raio = raioValidado
                        ),
                        individual = null,
                        municipio = null
                    )
                }
                else -> {
                    // Se não especificado, usar Arapiraca como padrão (2700300)
                    EstabelecimentoCriteria(
                        municipio = MunicipioCriteria(codigoIBGE = 2700300), // Arapiraca - Int
                        individual = null,
                        geolocalizacao = null
                    )
                }
            }
            
            val request = PesquisaCombustivelRequest(
                produto = CombustivelCriteria(tipoCombustivel = tipoCombustivel),
                estabelecimento = estabelecimentoCriteria,
                dias = dias.coerceIn(1, 10),
                pagina = 1,
                registrosPorPagina = 100
            )
            
            val response = apiService.pesquisarCombustiveis(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

