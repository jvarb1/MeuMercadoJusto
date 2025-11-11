# 🔧 Configuração da API Economiza Alagoas - COMPLETA

## ✅ O que foi Implementado

Baseado na documentação oficial, implementei:

1. **EconomizaAlagoasService.kt** - Interface completa com:
   - `pesquisarProdutos()` - Pesquisa de produtos (seção 6.1)
   - `pesquisarCombustiveis()` - Pesquisa de combustíveis (seção 6.2)

2. **EconomizaAlagoasRepository.kt** - Repositório que:
   - Busca dados da API
   - Sincroniza automaticamente com Supabase
   - Converte formatos de dados

3. **ApiClient.kt** - Configurado com:
   - URL base correta: `http://api.sefaz.al.gov.br/sfz-economiza-alagoas-api/api/public/`
   - Método POST (conforme documentação)
   - Header AppToken (autenticação)

## 📋 Passo a Passo para Usar

### 1. Solicitar AppToken

Envie um e-mail para **api@sefaz.al.gov.br** com:
- CPF do responsável
- Nome completo do responsável
- Nome do aplicativo: "Meu Mercado Justo"
- URL da página WEB (se houver)

Você receberá um **AppToken** para usar nas requisições.

### 2. Configurar AppToken

No arquivo `local.properties`, adicione:

```properties
api.governo.key=seu-app-token-aqui
```

A URL já está configurada:
```properties
api.governo.url=http://api.sefaz.al.gov.br/sfz-economiza-alagoas-api/api/public/
```

### 3. Sincronizar Gradle

- File > Sync Project with Gradle Files

### 4. Usar no Código

```kotlin
// Exemplo de uso
val repository = EconomizaAlagoasRepository()

// Pesquisar produtos por descrição
val result = repository.pesquisarESincronizarProdutos(
    descricao = "LEITE",
    codigoIBGE = "2704302", // Maceió
    dias = 7
)

if (result.isSuccess) {
    val response = result.getOrNull()
    val produtos = response?.conteudo ?: emptyList()
    // Usar os produtos...
}

// Pesquisar por geolocalização
val result2 = repository.pesquisarESincronizarProdutos(
    descricao = "ARROZ",
    latitude = -9.568061100000001,
    longitude = -35.79424830000001,
    raio = 15, // 15 km
    dias = 7
)

// Pesquisar combustíveis
val combustiveis = repository.pesquisarCombustiveis(
    tipoCombustivel = 1, // 1=Gasolina Comum
    codigoIBGE = "2704302",
    dias = 5
)
```

## 📊 Estrutura de Dados

### Pesquisa de Produtos

**Entrada:**
- `produto`: Critérios (gtin OU descricao)
- `estabelecimento`: Critérios (individual OU municipio OU geolocalizacao)
- `dias`: 1-10 dias
- `pagina`: Número da página (opcional)
- `registrosPorPagina`: 50-5000 (opcional)

**Saída:**
- `totalRegistros`: Total encontrado
- `totalPaginas`: Total de páginas
- `conteudo`: Lista de resultados com produto e estabelecimento

### Pesquisa de Combustíveis

**Entrada:**
- `produto.tipoCombustivel`: 1-6
  - 1 = Gasolina Comum
  - 2 = Gasolina Aditivada
  - 3 = Álcool
  - 4 = Diesel Comum
  - 5 = Diesel Aditivado (S10)
  - 6 = GNV
- `estabelecimento`: Mesmos critérios
- `dias`: 1-10 dias

## 🔍 Códigos IBGE de Alagoas (Exemplos)

- **2704302** - MACEIÓ
- **2700300** - ARAPIRACA
- **2700409** - ATALAIA
- (Ver Anexo II da documentação para lista completa)

## ⚠️ Validações Importantes

1. **Produto**: Deve informar OU `gtin` OU `descricao` (não ambos)
2. **Estabelecimento**: Deve informar UM dos critérios:
   - `individual.cnpj` OU
   - `municipio.codigoIBGE` OU
   - `geolocalizacao` (latitude, longitude, raio)
3. **Dias**: Entre 1 e 10
4. **Raio**: Entre 1 e 15 km
5. **Página**: Entre 1 e 9999
6. **Registros por página**: Entre 50 e 5000

## 🔄 Sincronização Automática

O `EconomizaAlagoasRepository` automaticamente:
1. Busca dados da API
2. Converte para modelos do Supabase
3. Insere/atualiza estabelecimentos
4. Insere/atualiza produtos
5. Insere/atualiza preços

## 📝 Notas Importantes

- ⚠️ **Datas**: A API retorna em formato UTC ISO 8601 (sem timezone)
- ⚠️ **Dados**: Baseados em vendas reais dos últimos 10 dias
- ⚠️ **Preços**: Podem variar (promoções, descontos, etc)
- ⚠️ **Descrições**: Cada estabelecimento define sua própria descrição
- ✅ **Uso**: Livre para uso particular ou comercial

## 🐛 Troubleshooting

### Erro: "TOKEN inválido ou sem autorização"
- Verifique se o AppToken está correto no `local.properties`
- Verifique se solicitou o token em api@sefaz.al.gov.br

### Erro: "Critério de pesquisa não informado"
- Verifique se informou gtin OU descricao (não ambos)
- Verifique se informou UM critério de estabelecimento

### Erro: "Período da pesquisa fora do intervalo"
- Dias deve estar entre 1 e 10

### Erro: "Raio de alcance fora do intervalo"
- Raio deve estar entre 1 e 15 km

## ✅ Checklist

- [ ] AppToken solicitado em api@sefaz.al.gov.br
- [ ] AppToken adicionado no `local.properties`
- [ ] Gradle sincronizado
- [ ] Testado pesquisa de produtos
- [ ] Dados sincronizando com Supabase

---

**Tudo pronto!** 🎉 A API está completamente configurada conforme a documentação oficial!

