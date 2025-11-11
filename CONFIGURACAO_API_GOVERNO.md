# 🔧 Configuração da API Economiza Alagoas

## 📋 Passo a Passo

### 1. Ler a Documentação PDF

Abra o arquivo `Economiza_Alagoas_Manual_de_Orientação_do_Desenvolvedor_versao1.0 (7).pdf` e identifique:

- ✅ **URL Base da API** (ex: `https://api.economiza.alagoas.gov.br/`)
- ✅ **Método de Autenticação** (API Key, Bearer Token, etc)
- ✅ **Endpoints disponíveis** (GET /produtos, GET /estabelecimentos, etc)
- ✅ **Estrutura das respostas JSON**
- ✅ **Parâmetros de query aceitos**
- ✅ **Formato de datas**
- ✅ **Paginação** (se houver)

### 2. Configurar Credenciais

No arquivo `local.properties` (raiz do projeto), adicione:

```properties
# API Economiza Alagoas
api.governo.url=https://api.economiza.alagoas.gov.br/
api.governo.key=sua-chave-api-aqui
```

**Onde encontrar:**
- URL: Geralmente na documentação, seção "Base URL" ou "Endpoint"
- Key: Pode ser:
  - API Key fornecida pela secretaria
  - Token de acesso
  - Chave de desenvolvedor

### 3. Ajustar Endpoints

Edite o arquivo `EconomizaAlagoasService.kt` e ajuste:

#### 3.1. URL Base
Se a URL base for diferente, atualize em `ApiClient.kt`:
```kotlin
private val BASE_URL = BuildConfig.API_GOVERNO_URL.ifBlank { 
    "https://sua-url-real-aqui.gov.br/" // Substituir
}
```

#### 3.2. Endpoints
Ajuste os endpoints conforme a documentação:
```kotlin
@GET("produtos") // Pode ser "api/v1/produtos", "api/produtos", etc
suspend fun buscarProdutos(...)
```

#### 3.3. Autenticação
Em `ApiClient.kt`, ajuste o interceptor conforme o método de autenticação:

**API Key no Header:**
```kotlin
requestBuilder.addHeader("X-API-Key", API_KEY)
// ou
requestBuilder.addHeader("apikey", API_KEY)
```

**Bearer Token:**
```kotlin
requestBuilder.addHeader("Authorization", "Bearer $API_KEY")
```

**Query Parameter:**
```kotlin
val url = original.url.newBuilder()
    .addQueryParameter("api_key", API_KEY)
    .build()
requestBuilder.url(url)
```

#### 3.4. Estrutura de Resposta
Ajuste `EconomizaResponse` em `EconomizaAlagoasService.kt` conforme o JSON real:

```kotlin
data class EconomizaResponse<T>(
    // Ajustar campos conforme resposta real da API
    val data: T? = null,
    val message: String? = null,
    // ... outros campos
)
```

#### 3.5. Modelos de Dados
Ajuste `ProdutoApiResponse`, `EstabelecimentoApiResponse`, `PrecoApiResponse` conforme os campos reais:

```kotlin
data class ProdutoApiResponse(
    val id: Int,
    val nome: String, // Pode ser "name", "nome_produto", etc
    // ... ajustar conforme JSON real
)
```

### 4. Testar Conexão

Crie um teste simples ou use o Logcat:

```kotlin
// No ViewModel ou Repository
try {
    val response = ApiClient.economizaAlagoasService.buscarProdutos()
    Log.d("API", "Sucesso: ${response.data?.size} produtos")
} catch (e: Exception) {
    Log.e("API", "Erro: ${e.message}")
}
```

### 5. Sincronizar Dados

Use o `ApiSyncRepository` para sincronizar:

```kotlin
val syncRepo = ApiSyncRepository()
val result = syncRepo.sincronizarTudo()
if (result.isSuccess) {
    val sync = result.getOrNull()
    Log.d("Sync", "Sincronizados: ${sync?.produtosSincronizados} produtos")
}
```

## 🔍 Checklist de Ajustes

Baseado na documentação PDF, verifique e ajuste:

- [ ] URL base da API
- [ ] Método de autenticação (header, query param, etc)
- [ ] Endpoints (caminhos corretos)
- [ ] Estrutura de resposta JSON
- [ ] Nomes dos campos (camelCase vs snake_case)
- [ ] Formato de datas
- [ ] Paginação (page, limit, etc)
- [ ] Tratamento de erros
- [ ] Rate limiting (se houver)

## 📝 Exemplo de Ajuste

Se a documentação diz:

**Endpoint:** `GET /api/v1/produtos`
**Autenticação:** Header `X-API-Key`
**Resposta:**
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "nome_produto": "Arroz",
      "categoria": "Cereais"
    }
  ]
}
```

**Ajustes necessários:**

1. **Endpoint:**
```kotlin
@GET("api/v1/produtos")
suspend fun buscarProdutos(...)
```

2. **Autenticação:**
```kotlin
requestBuilder.addHeader("X-API-Key", API_KEY)
```

3. **Modelo:**
```kotlin
@SerialName("nome_produto")
val nomeProduto: String
```

4. **Resposta:**
```kotlin
data class EconomizaResponse<T>(
    val status: String,
    val data: T?
)
```

## 🐛 Troubleshooting

### Erro 401 (Unauthorized)
- Verifique se a API Key está correta
- Verifique o método de autenticação (header vs query)

### Erro 404 (Not Found)
- Verifique se a URL base está correta
- Verifique se o endpoint está correto

### Erro de Parsing JSON
- Verifique se os nomes dos campos correspondem
- Use `@SerialName` para mapear campos diferentes

### Timeout
- Aumente o timeout em `ApiClient.kt`
- Verifique a conexão com a internet

## 📚 Arquivos para Ajustar

1. **`ApiClient.kt`** - URL base e autenticação
2. **`EconomizaAlagoasService.kt`** - Endpoints e modelos de resposta
3. **`ApiSyncRepository.kt`** - Lógica de sincronização (se necessário)
4. **`local.properties`** - Credenciais

## ✅ Após Configurar

1. Sincronize o Gradle
2. Teste a conexão
3. Sincronize os dados
4. Use o `SupabaseRepository` para buscar dados locais

---

**Dica:** Use o Logcat para ver as requisições HTTP completas e facilitar o debug!

