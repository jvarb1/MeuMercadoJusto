# 🔧 Guia de Configuração do Supabase

## 📋 Passo a Passo

### 1. Criar Projeto no Supabase

1. Acesse [https://supabase.com](https://supabase.com)
2. Faça login ou crie uma conta
3. Clique em **"New Project"**
4. Preencha:
   - **Name**: `meu-mercado-justo` (ou o nome que preferir)
   - **Database Password**: Crie uma senha forte (guarde ela!)
   - **Region**: Escolha a região mais próxima (ex: South America)
5. Clique em **"Create new project"**
6. Aguarde alguns minutos enquanto o projeto é criado

### 2. Obter Credenciais

1. No painel do Supabase, vá em **Settings** (ícone de engrenagem) > **API**
2. Você verá duas informações importantes:
   - **Project URL**: `https://xxxxx.supabase.co`
   - **anon public key**: `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`

### 3. Configurar no Projeto Android

1. Abra o arquivo `local.properties` na **raiz do projeto** (mesmo nível do `build.gradle.kts`)
2. Adicione as seguintes linhas (substitua pelos seus valores):

```properties
supabase.url=https://seu-projeto.supabase.co
supabase.key=sua-chave-anon-key-aqui
```

**Exemplo:**
```properties
supabase.url=https://abcdefghijklmnop.supabase.co
supabase.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFiY2RlZmdoaWprbG1ub3AiLCJyb2xlIjoiYW5vbiIsImlhdCI6MTYxNjIzOTAyMiwiZXhwIjoxOTMxODE1MDIyfQ.xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

⚠️ **IMPORTANTE**: O arquivo `local.properties` já está no `.gitignore`, então suas credenciais não serão commitadas.

### 4. Criar Tabelas no Supabase

1. No painel do Supabase, vá em **SQL Editor** (menu lateral)
2. Clique em **"New query"**
3. Abra o arquivo `supabase_setup.sql` deste projeto
4. Copie todo o conteúdo e cole no editor SQL
5. Clique em **"Run"** (ou pressione Ctrl+Enter)
6. Você deve ver a mensagem "Success. No rows returned"

### 5. Verificar Tabelas Criadas

1. No painel do Supabase, vá em **Table Editor** (menu lateral)
2. Você deve ver as tabelas:
   - `produtos`
   - `usuarios`
   - `estabelecimentos`
   - `precos_produtos`

### 6. (Opcional) Inserir Dados de Exemplo

1. No SQL Editor, descomente a seção de dados de exemplo no arquivo `supabase_setup.sql`
2. Execute novamente apenas a parte de INSERT
3. Ou use o Table Editor para inserir dados manualmente

### 7. Sincronizar Projeto Android

1. No Android Studio, clique em **File > Sync Project with Gradle Files**
2. Aguarde a sincronização terminar
3. Se houver erros, verifique se:
   - As credenciais no `local.properties` estão corretas
   - O arquivo `local.properties` está na raiz do projeto
   - Você fez o sync do Gradle

### 8. Testar Conexão

1. Execute o app
2. O app deve conectar ao Supabase automaticamente
3. Se houver erro, verifique o Logcat para mensagens de erro

## 🔄 Migrar de Room para Supabase

Para usar o Supabase ao invés do Room:

1. **No `CestaViewModelFactory`**, substitua:
```kotlin
// ANTES
val repository = CestaRepository(DatabaseHelper.getInstance(context))

// DEPOIS
val repository = SupabaseRepository()
```

2. **Remova a inicialização do Room** nos ecrãs:
```kotlin
// Remover estas linhas:
LaunchedEffect(Unit) {
    scope.launch {
        DatabaseInitializer.initialize(context)
    }
}
```

3. **Certifique-se de que o Supabase está configurado**:
```kotlin
if (!SupabaseClient.isConfigured()) {
    // Mostrar erro ou usar Room como fallback
}
```

## 🛠️ Troubleshooting

### Erro: "Supabase não está configurado"
- Verifique se o `local.properties` tem as credenciais corretas
- Verifique se fez o sync do Gradle após adicionar as credenciais
- Limpe e reconstrua o projeto (Build > Clean Project)

### Erro: "Failed to connect"
- Verifique se a URL do Supabase está correta
- Verifique se a chave anon está correta
- Verifique sua conexão com a internet
- Verifique se o projeto no Supabase está ativo

### Erro: "Table does not exist"
- Execute o script SQL `supabase_setup.sql` novamente
- Verifique se as tabelas foram criadas no Table Editor

### Erro de BuildConfig
- Certifique-se de que `buildConfig = true` está no `build.gradle.kts`
- Faça Clean Project e Rebuild

## 📚 Recursos Úteis

- [Documentação Supabase](https://supabase.com/docs)
- [Supabase Kotlin SDK](https://github.com/supabase/supabase-kt)
- [PostgreSQL Tutorial](https://www.postgresql.org/docs/)

## 🔐 Segurança

- ✅ Nunca commite o arquivo `local.properties`
- ✅ Use a chave **anon** (pública) no app
- ✅ Configure Row Level Security (RLS) no Supabase
- ✅ Use autenticação para operações sensíveis
- ⚠️ Para produção, considere usar variáveis de ambiente ou um servidor intermediário

## ✅ Checklist

- [ ] Projeto criado no Supabase
- [ ] Credenciais adicionadas no `local.properties`
- [ ] Tabelas criadas via SQL script
- [ ] Gradle sincronizado
- [ ] App testado e funcionando
- [ ] Dados de exemplo inseridos (opcional)

