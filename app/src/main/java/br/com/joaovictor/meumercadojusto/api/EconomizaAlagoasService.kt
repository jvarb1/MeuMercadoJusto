package br.com.joaovictor.meumercadojusto.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interface para integração com a API Economiza Alagoas
 * 
 * Documentação: Economiza_Alagoas_Manual_de_Orientação_do_Desenvolvedor_versao1.0
 * 
 * IMPORTANTE: Todos os métodos usam POST (não GET!)
 * URL Base: http://api.sefaz.al.gov.br/sfz-economiza-alagoas-api/api/public/
 * Autenticação: Header "AppToken" com o token fornecido pela SEFAZ/AL
 */
interface EconomizaAlagoasService {
    
    /**
     * 6.1 Pesquisa de preço de produtos
     * Método: "produto/pesquisa"
     * 
     * Permite a pesquisa de preços de produtos em geral através de diversos critérios.
     */
    @POST("produto/pesquisa")
    suspend fun pesquisarProdutos(
        @Body request: PesquisaProdutoRequest
    ): PesquisaProdutoResponse
    
    /**
     * 6.2 Pesquisa de preço de combustíveis
     * Método: "combustivel/pesquisa"
     * 
     * Permite a pesquisa de preços de combustíveis baseada no código da ANP.
     */
    @POST("combustivel/pesquisa")
    suspend fun pesquisarCombustiveis(
        @Body request: PesquisaCombustivelRequest
    ): PesquisaCombustivelResponse
}

/**
 * 6.1.1 Leiaute de dados de entrada - Pesquisa de Produtos
 */
data class PesquisaProdutoRequest(
    val produto: ProdutoCriteria,
    val estabelecimento: EstabelecimentoCriteria,
    val dias: Int, // 1-10 dias
    val pagina: Int? = 1, // Default: 1
    val registrosPorPagina: Int? = 100 // Default: 100, min: 50, max: 5000
)

data class ProdutoCriteria(
    val gtin: String? = null, // Código de barras (GTIN-8, GTIN-12, GTIN-13 ou GTIN-14)
    val descricao: String? = null, // Palavras chaves de pesquisa (3-50 caracteres)
    val ncm: String? = null, // Código NCM (8 dígitos) - apenas com descrição
    val gpc: String? = null // Código GPC (8 dígitos) - apenas com descrição
)

data class EstabelecimentoCriteria(
    val individual: IndividualCriteria? = null,
    val municipio: MunicipioCriteria? = null,
    val geolocalizacao: GeolocalizacaoCriteria? = null
)

data class IndividualCriteria(
    val cnpj: String // Raiz do CNPJ (8 dígitos) ou CNPJ completo (14 dígitos)
)

data class MunicipioCriteria(
    @SerializedName("codigoIBGE")
    val codigoIBGE: Int // Código do município de Alagoas (7 dígitos) - deve ser Int conforme API Python
)

data class GeolocalizacaoCriteria(
    val latitude: Double, // Latitude do dispositivo
    val longitude: Double, // Longitude do dispositivo
    val raio: Int // Raio em km (1-15 km)
)

/**
 * 6.1.2 Leiaute de dados de saída - Pesquisa de Produtos
 */
data class PesquisaProdutoResponse(
    val totalRegistros: Int,
    val totalPaginas: Int,
    val pagina: Int,
    val registrosPorPagina: Int,
    val registrosPagina: Int,
    val primeiraPagina: Boolean,
    val ultimaPagina: Boolean,
    val conteudo: List<ResultadoProduto>
)

data class ResultadoProduto(
    val produto: ProdutoInfo,
    val estabelecimento: EstabelecimentoInfo
)

data class ProdutoInfo(
    val codigo: String, // Código do produto fornecido pelo estabelecimento
    val descricao: String, // Descrição do produto (1-120 caracteres)
    val descricaoSefaz: String? = null, // Descrição definida pela Sefaz (quando existir)
    val gtin: String? = null, // Código de barras (0/8-14 dígitos)
    val ncm: String, // Código NCM (8 dígitos)
    val gpc: String, // Código GPC (1/8 dígitos) - 0 se não classificado
    val unidadeMedida: String, // Unidade de medida (1-6 caracteres)
    val venda: VendaInfo
)

data class VendaInfo(
    val dataVenda: String, // Data e hora no formato UTC ISO 8601: YYYY-MM-DDThh:mm:ssZ
    val valorDeclarado: Double, // Valor declarado pelo contribuinte
    val valorVenda: Double // Valor efetivo da venda
)

data class EstabelecimentoInfo(
    val cnpj: String, // CNPJ completo (14 dígitos)
    val razaoSocial: String, // Razão Social (150 caracteres)
    val nomeFantasia: String, // Nome fantasia (150 caracteres)
    val telefone: String, // Telefone (21 caracteres)
    val endereco: EnderecoInfo
)

data class EnderecoInfo(
    val nomeLogradouro: String, // Nome da rua (80 caracteres)
    val numeroImovel: String, // Número do imóvel (7 caracteres)
    val bairro: String, // Bairro (50 caracteres)
    val cep: String, // CEP (8 dígitos)
    val codigoIBGE: String, // Código do município IBGE (7 dígitos)
    val municipio: String, // Nome do município (100 caracteres)
    val latitude: Double, // Latitude do estabelecimento
    val longitude: Double // Longitude do estabelecimento
)

/**
 * 6.2.1 Leiaute de dados de entrada - Pesquisa de Combustíveis
 */
data class PesquisaCombustivelRequest(
    val produto: CombustivelCriteria,
    val estabelecimento: EstabelecimentoCriteria,
    val dias: Int, // 1-10 dias
    val pagina: Int? = 1, // Default: 1
    val registrosPorPagina: Int? = 100 // Default: 100, min: 50, max: 5000
)

data class CombustivelCriteria(
    val tipoCombustivel: Int // 1=Gasolina Comum, 2=Gasolina Aditivada, 3=Álcool, 
                             // 4=Diesel Comum, 5=Diesel Aditivado (S10), 6=GNV
)

/**
 * 6.2.2 Leiaute de dados de saída - Pesquisa de Combustíveis
 */
data class PesquisaCombustivelResponse(
    val totalRegistros: Int,
    val totalPaginas: Int,
    val pagina: Int,
    val registrosPorPagina: Int,
    val registrosPagina: Int,
    val primeiraPagina: Boolean,
    val ultimaPagina: Boolean,
    val conteudo: List<ResultadoCombustivel>
)

data class ResultadoCombustivel(
    val produto: CombustivelInfo,
    val estabelecimento: EstabelecimentoInfo
)

data class CombustivelInfo(
    val codigo: String, // Código do produto definido pelo estabelecimento
    val descricao: String, // Descrição do produto (1-120 caracteres)
    val unidadeMedida: String, // Unidade de medida (1-6 caracteres)
    val venda: VendaInfo
)

/**
 * 7 Mensagem de Erro
 */
data class ErrorResponse(
    val timestamp: String, // Data de emissão no formato UTC ISO 8601
    val message: String // Mensagem de erro (1-150 caracteres)
)
