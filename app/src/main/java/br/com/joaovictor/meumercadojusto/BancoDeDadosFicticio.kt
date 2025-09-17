package br.com.joaovictor.meumercadojusto

// Este objeto vai guardar nossa lista de dados falsos (mock)
object BancoDeDadosFicticio {

    // Esta função cria e retorna a lista de produtos que vamos usar para testar o app
    fun gerarDadosFicticios(): List<ItemEncontrado> {
        return listOf(
            // --- Produtos no Supermercado Preço Bom ---
            ItemEncontrado(
                nomeProduto = "Arroz Tipo 1 (5kg)",
                precoProduto = 25.50,
                nomeEstabelecimento = "Supermercado Preço Bom",
                enderecoEstabelecimento = "Av. Principal, 123"
            ),
            ItemEncontrado(
                nomeProduto = "Feijão Carioca (1kg)",
                precoProduto = 8.90,
                nomeEstabelecimento = "Supermercado Preço Bom",
                enderecoEstabelecimento = "Av. Principal, 123"
            ),
            ItemEncontrado(
                nomeProduto = "Óleo de Soja (900ml)",
                precoProduto = 7.80,
                nomeEstabelecimento = "Supermercado Preço Bom",
                enderecoEstabelecimento = "Av. Principal, 123"
            ),
            ItemEncontrado(
                nomeProduto = "Açúcar Refinado (1kg)",
                precoProduto = 5.20,
                nomeEstabelecimento = "Supermercado Preço Bom",
                enderecoEstabelecimento = "Av. Principal, 123"
            ),
            ItemEncontrado(
                nomeProduto = "Café em Pó (500g)",
                precoProduto = 15.00,
                nomeEstabelecimento = "Supermercado Preço Bom",
                enderecoEstabelecimento = "Av. Principal, 123"
            ),

            // --- Produtos no Atacadão da Esquina ---
            ItemEncontrado(
                nomeProduto = "Arroz Tipo 1 (5kg)",
                precoProduto = 24.90,
                nomeEstabelecimento = "Atacadão da Esquina",
                enderecoEstabelecimento = "Rua Secundária, 456"
            ),
            ItemEncontrado(
                nomeProduto = "Feijão Carioca (1kg)",
                precoProduto = 9.20,
                nomeEstabelecimento = "Atacadão da Esquina",
                enderecoEstabelecimento = "Rua Secundária, 456"
            ),
            ItemEncontrado(
                nomeProduto = "Óleo de Soja (900ml)",
                precoProduto = 7.50,
                nomeEstabelecimento = "Atacadão da Esquina",
                enderecoEstabelecimento = "Rua Secundária, 456"
            ),
            ItemEncontrado(
                nomeProduto = "Açúcar Refinado (1kg)",
                precoProduto = 4.99,
                nomeEstabelecimento = "Atacadão da Esquina",
                enderecoEstabelecimento = "Rua Secundária, 456"
            ),
            ItemEncontrado(
                nomeProduto = "Café em Pó (500g)",
                precoProduto = 14.80,
                nomeEstabelecimento = "Atacadão da Esquina",
                enderecoEstabelecimento = "Rua Secundária, 456"
            )
        )
    }
}