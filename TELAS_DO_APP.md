# 📱 Telas do App - Meu Mercado Justo

## 📋 Visão Geral

O app possui **3 telas principais** conectadas através de navegação:

```
LoginScreen → MainScreen → SearchScreen
```

---

## 1. 🔐 LoginScreen (Tela de Login)

**Arquivo:** `screens/LoginScreen.kt`  
**Rota:** `"login"` (tela inicial)  
**Função:** Autenticação de usuários

### Funcionalidades:

✅ **Campos de Entrada:**
- Campo de **Email** (com ícone de email)
- Campo de **Senha** (com opção de mostrar/ocultar senha)
- Validação de campos obrigatórios

✅ **Autenticação:**
- Validação de credenciais no banco de dados Room
- Busca usuário por email e senha
- Exibe mensagens de erro se login falhar
- Loading state durante autenticação

✅ **Ações Disponíveis:**
- **Botão "Entrar"** - Realiza login
- **"Esqueci a senha"** - (TODO: não implementado)
- **"Cadastre-se"** - (TODO: não implementado)

✅ **Inicialização:**
- Carrega usuários de teste no banco de dados automaticamente
- Exibe card com credenciais de teste:
  - `joao@email.com / senha123`
  - `jamison@email.com / senha123`

✅ **Navegação:**
- Após login bem-sucedido, navega para `MainScreen`
- Remove a tela de login do back stack

---

## 2. 🏠 MainScreen (Tela Principal / Home)

**Arquivo:** `screens/MainScreen.kt`  
**Rota:** `"main"`  
**Função:** Exibir comparação de custo-benefício e dashboard

### Funcionalidades:

✅ **TopBar:**
- Título: "Meu Mercado Justo"
- Botão de busca (ícone de lupa) - navega para SearchScreen

✅ **Card: Melhor Custo-Benefício** 🏆
- Exibe o estabelecimento com a **cesta mais barata**
- Mostra:
  - Nome do estabelecimento
  - Valor total da cesta
  - Economia em relação ao mais caro (se houver)
- Cor: Primary Container (verde/azul claro)
- Dados vêm do `CestaViewModel` (calculado automaticamente)

✅ **Card: Pior Custo-Benefício** ⚠️
- Exibe o estabelecimento com a **cesta mais cara**
- Mostra:
  - Nome do estabelecimento
  - Valor total da cesta
  - Diferença em relação ao mais barato
- Cor: Error Container (vermelho claro)
- Dados vêm do `CestaViewModel`

✅ **Botão "Buscar Produtos":**
- Navega para `SearchScreen`
- Ícone de busca + texto

✅ **Card de Informações:**
- Explica como funciona a comparação
- Informa que preços são atualizados diariamente
- Dados coletados de supermercados locais

✅ **Inicialização Automática:**
- Inicializa banco de dados com dados de exemplo
- Calcula automaticamente a cesta mais barata ao carregar
- Usa `CestaViewModel` para gerenciar estado

---

## 3. 🔍 SearchScreen (Tela de Busca)

**Arquivo:** `screens/SearchScreen.kt`  
**Rota:** `"search"`  
**Função:** Buscar produtos e calcular cestas

### Funcionalidades:

✅ **TopBar:**
- Título: "Buscar Produtos"
- Botão voltar (seta) - retorna para MainScreen

✅ **Botão "Qual a Cesta Mais Barata?":**
- Calcula e exibe lista de estabelecimentos ordenados por preço
- Mostra todos os estabelecimentos com preços da cesta completa
- Ordena do mais barato para o mais caro
- Exibe loading durante cálculo

✅ **Campo de Busca de Produto:**
- Campo de texto para digitar nome do produto
- Ícone de busca
- Limpa resultados se campo ficar vazio

✅ **Botão "Buscar Produto":**
- Busca produtos por nome
- Retorna lista de produtos encontrados em diferentes estabelecimentos
- Ordena por preço (mais barato primeiro)
- Exibe loading durante busca

✅ **Exibição de Resultados:**

**Resultados de Cesta:**
- Lista de estabelecimentos com:
  - Nome do estabelecimento
  - Preço total da cesta
  - Quantidade de itens
  - Endereço
  - Economia (se houver)

**Resultados de Produtos:**
- Lista de produtos encontrados com:
  - Preço do produto
  - Nome do produto
  - Nome do estabelecimento
  - Endereço do estabelecimento

✅ **Tratamento de Erros:**
- Exibe mensagens de erro em card vermelho
- Loading states durante operações

✅ **Inicialização:**
- Inicializa banco de dados na primeira vez
- Usa `CestaViewModel` para gerenciar estado

---

## 🔄 Fluxo de Navegação

```
┌─────────────┐
│ LoginScreen │ (Tela Inicial)
└──────┬──────┘
       │ Login bem-sucedido
       ▼
┌─────────────┐
│ MainScreen  │ (Dashboard)
└──────┬──────┘
       │ Clica em busca
       ▼
┌─────────────┐
│SearchScreen │ (Busca)
└──────┬──────┘
       │ Volta
       ▼
┌─────────────┐
│ MainScreen  │
└─────────────┘
```

---

## 🎨 Componentes Reutilizáveis

### CestaItem
- Exibe resultado de cálculo de cesta
- Mostra estabelecimento, preço total, quantidade de itens, endereço e economia

### ProdutoItem
- Exibe resultado de busca de produto
- Mostra preço, nome do produto, estabelecimento e endereço

---

## 📊 Integração com Dados

### Banco de Dados (Room)
- **LoginScreen:** Usa `UsuarioDao` para autenticação
- **MainScreen:** Usa `CestaRepository` → `CestaViewModel`
- **SearchScreen:** Usa `CestaRepository` → `CestaViewModel`

### ViewModel
- **CestaViewModel:** Gerencia estado de:
  - Resultados de cesta
  - Resultados de produtos
  - Loading states
  - Mensagens de erro

### Repositório
- **CestaRepository:** Lógica de negócio:
  - Calcula cesta mais barata
  - Busca produtos por nome
  - Ordena resultados por preço

---

## 🚀 Funcionalidades Futuras (TODO)

### LoginScreen:
- [ ] Implementar "Esqueci a senha"
- [ ] Implementar "Cadastre-se"
- [ ] Integração com Supabase Auth (futuro)

### MainScreen:
- [ ] Atualizar dados da API Economiza Alagoas
- [ ] Filtros por cidade/região
- [ ] Histórico de comparações

### SearchScreen:
- [ ] Busca por código de barras (GTIN)
- [ ] Filtros avançados (categoria, preço, etc)
- [ ] Favoritar produtos
- [ ] Compartilhar resultados

---

## 📝 Notas Técnicas

- Todas as telas usam **Jetpack Compose**
- Navegação com **Navigation Compose**
- Estado gerenciado com **ViewModel + StateFlow**
- Dados locais com **Room Database**
- Preparado para **Supabase** (repositório alternativo criado)
- Preparado para **API Economiza Alagoas** (repositório criado)

---

## ✅ Status Atual

| Tela | Status | Funcionalidades |
|------|--------|----------------|
| LoginScreen | ✅ Completa | Login, validação, loading |
| MainScreen | ✅ Completa | Dashboard, comparação, navegação |
| SearchScreen | ✅ Completa | Busca produtos, cálculo cesta, resultados |

**Todas as telas principais estão funcionais!** 🎉

