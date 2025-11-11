# Integração com API do Governo

## Status
⚠️ **Pendente**: Aguardando documentação PDF da API do governo para implementação completa.

## Estrutura Preparada

### Arquivos Criados:
1. **`ApiGovernoService.kt`** - Interface Retrofit para os endpoints da API
2. **`ApiClient.kt`** - Cliente HTTP configurado com Retrofit e OkHttp

### Próximos Passos:

1. **Atualizar URL Base**:
   - Editar `ApiClient.kt` e substituir `BASE_URL` pela URL real da API

2. **Ajustar Endpoints**:
   - Revisar `ApiGovernoService.kt` conforme documentação PDF
   - Ajustar métodos HTTP (GET, POST, etc.)
   - Ajustar parâmetros de query conforme necessário

3. **Ajustar Modelos de Resposta**:
   - Atualizar `PrecoApiResponse`, `EstabelecimentoApiResponse`, `ProdutoApiResponse`
   - Garantir que correspondem à estrutura JSON da API

4. **Integrar no Repository**:
   - Adicionar métodos no `CestaRepository` para buscar dados da API
   - Implementar sincronização entre API e Room DB
   - Adicionar tratamento de erros e retry logic

## Exemplo de Uso (Após Implementação):

```kotlin
// No CestaRepository
suspend fun sincronizarDadosDaAPI() {
    try {
        val precos = ApiClient.apiGovernoService.buscarPrecos()
        // Processar e salvar no Room DB
    } catch (e: Exception) {
        // Tratar erro
    }
}
```

## Notas:
- A estrutura está preparada para trabalhar com Retrofit
- O cliente HTTP já está configurado com logging para debug
- Timeout configurado para 30 segundos
- Pronto para adicionar interceptors de autenticação se necessário

