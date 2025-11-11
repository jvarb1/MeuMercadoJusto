# 🧪 Como Testar a API Economiza Alagoas

## ✅ Status Atual

- ✅ API configurada e implementada
- ✅ AppToken configurado no `local.properties`
- ✅ Repositório pronto para uso
- ⚠️ **Ainda não está integrada nas telas** (telas usam Room Database)

## 🔍 Teste Rápido

### Opção 1: Teste via Código (Recomendado)

Adicione este código temporariamente em qualquer tela para testar:

```kotlin
// No SearchScreen.kt ou MainScreen.kt, adicione:

import br.com.joaovictor.meumercadojusto.repository.EconomizaAlagoasRepository
import kotlinx.coroutines.launch

// Dentro de um LaunchedEffect ou onClick:
LaunchedEffect(Unit) {
    scope.launch {
        if (ApiClient.isConfigured()) {
            val repository = EconomizaAlagoasRepository()
            
            // Teste 1: Pesquisar produtos por descrição
            val result = repository.pesquisarESincronizarProdutos(
                descricao = "LEITE",
                codigoIBGE = "2704302", // Maceió
                dias = 7
            )
            
            result.fold(
                onSuccess = { response ->
                    Log.d("API_TEST", "✅ Sucesso! Total: ${response.totalRegistros}")
                    Log.d("API_TEST", "Produtos encontrados: ${response.conteudo?.size ?: 0}")
                },
                onFailure = { error ->
                    Log.e("API_TEST", "❌ Erro: ${error.message}")
                    error.printStackTrace()
                }
            )
        } else {
            Log.e("API_TEST", "❌ API não configurada!")
        }
    }
}
```

### Opção 2: Teste via Logcat

1. Execute o app
2. Abra o Logcat no Android Studio
3. Filtre por "API_TEST"
4. Você verá:
   - ✅ "Sucesso! Total: X" se funcionar
   - ❌ "Erro: ..." se houver problema

### Opção 3: Teste Manual via Postman/Insomnia

**URL:** `http://api.sefaz.al.gov.br/sfz-economiza-alagoas-api/api/public/produto/pesquisa`

**Método:** POST

**Headers:**
```
AppToken: 1a6ef663634828e89e4520895c8026d39796b49e
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "produto": {
    "descricao": "LEITE"
  },
  "estabelecimento": {
    "municipio": {
      "codigoIBGE": "2704302"
    }
  },
  "dias": 7,
  "pagina": 1,
  "registrosPorPagina": 100
}
```

## 🔧 Verificações

### 1. Verificar se está configurado:

```kotlin
if (ApiClient.isConfigured()) {
    // API está configurada
} else {
    // Verificar local.properties
}
```

### 2. Verificar AppToken:

No `local.properties`:
```properties
api.governo.key=1a6ef663634828e89e4520895c8026d39796b49e
```

### 3. Verificar URL:

```properties
api.governo.url=http://api.sefaz.al.gov.br/sfz-economiza-alagoas-api/api/public/
```

## ⚠️ Possíveis Problemas

### 1. Erro 401 (Unauthorized)
- **Causa:** AppToken inválido ou expirado
- **Solução:** Solicitar novo token em api@sefaz.al.gov.br

### 2. Erro 400 (Bad Request)
- **Causa:** Formato do JSON incorreto
- **Solução:** Verificar estrutura do request

### 3. Erro de Rede
- **Causa:** Sem internet ou URL incorreta
- **Solução:** Verificar conexão e URL

### 4. Timeout
- **Causa:** API demorando para responder
- **Solução:** Aumentar timeout no ApiClient (já está em 30s)

## 📊 Exemplo de Resposta de Sucesso

```json
{
  "success": true,
  "totalRegistros": 10,
  "totalPaginas": 1,
  "conteudo": [
    {
      "produto": {
        "descricao": "LEITE INTEGRAL",
        "unidadeMedida": "L",
        "venda": {
          "valorVenda": 5.99,
          "dataVenda": "2024-01-15T10:30:00Z"
        }
      },
      "estabelecimento": {
        "nomeFantasia": "Supermercado Exemplo",
        "endereco": {
          "municipio": "MACEIÓ",
          "latitude": -9.568061,
          "longitude": -35.794248
        }
      }
    }
  ]
}
```

## 🚀 Próximos Passos

Para integrar a API nas telas:

1. **Criar ViewModel para API:**
   ```kotlin
   class ApiViewModel(private val repository: EconomizaAlagoasRepository) : ViewModel()
   ```

2. **Adicionar botão na SearchScreen:**
   - "Buscar na API do Governo"
   - Chama `repository.pesquisarESincronizarProdutos()`

3. **Mostrar resultados:**
   - Exibir produtos encontrados
   - Mostrar preços e estabelecimentos

## 📝 Notas

- A API sincroniza automaticamente com Supabase
- Os dados ficam disponíveis para uso local após sincronização
- O AppToken pode expirar - verificar periodicamente

