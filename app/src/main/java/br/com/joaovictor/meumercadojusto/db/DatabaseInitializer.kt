package br.com.joaovictor.meumercadojusto.db

import android.content.Context
import br.com.joaovictor.meumercadojusto.model.PrecoProduto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * Inicializa o banco de dados com dados de exemplo
 */
object DatabaseInitializer {
    
    suspend fun initialize(context: Context) = withContext(Dispatchers.IO) {
        val db = DatabaseHelper.getInstance(context)
        
        // Limpar dados existentes
        db.produtoDao().deleteAllProdutos()
        db.estabelecimentoDao().deleteAllEstabelecimentos()
        db.precoProdutoDao().deleteAllPrecos()
        
        // Inserir produtos
        db.produtoDao().insertProdutos(produtoList)
        
        // Inserir estabelecimentos
        db.estabelecimentoDao().insertEstabelecimentos(estabelecimentoList)
        
        // Inserir preços (criar variações de preço para cada produto em cada estabelecimento)
        val produtos = db.produtoDao().getAllProdutos().first()
        val estabelecimentos = db.estabelecimentoDao().getAllEstabelecimentos().first()
        
        val precos = mutableListOf<PrecoProduto>()
        
        produtos.forEach { produto ->
            estabelecimentos.forEachIndexed { index, estabelecimento ->
                // Criar variação de preço (primeiro estabelecimento mais barato, último mais caro)
                val variacao = 1.0 + (index * 0.1) // 10% de diferença entre estabelecimentos
                val preco = produto.preco * variacao
                
                precos.add(
                    PrecoProduto(
                        produtoId = produto.id,
                        estabelecimentoId = estabelecimento.id,
                        preco = preco,
                        dataAtualizacao = System.currentTimeMillis(),
                        disponivel = true
                    )
                )
            }
        }
        
        db.precoProdutoDao().insertPrecos(precos)
    }
}

