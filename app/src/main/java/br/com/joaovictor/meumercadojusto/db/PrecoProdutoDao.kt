package br.com.joaovictor.meumercadojusto.db

import androidx.room.*
import br.com.joaovictor.meumercadojusto.model.PrecoProduto
import kotlinx.coroutines.flow.Flow

@Dao
interface PrecoProdutoDao {
    @Query("SELECT * FROM precos_produtos WHERE produtoId = :produtoId AND disponivel = 1 ORDER BY preco ASC")
    fun getPrecosPorProduto(produtoId: Int): Flow<List<PrecoProduto>>
    
    @Query("SELECT * FROM precos_produtos WHERE estabelecimentoId = :estabelecimentoId AND disponivel = 1")
    fun getPrecosPorEstabelecimento(estabelecimentoId: Int): Flow<List<PrecoProduto>>
    
    @Query("SELECT * FROM precos_produtos WHERE produtoId = :produtoId AND estabelecimentoId = :estabelecimentoId")
    suspend fun getPreco(produtoId: Int, estabelecimentoId: Int): PrecoProduto?
    
    @Query("SELECT * FROM precos_produtos WHERE produtoId = :produtoId AND disponivel = 1 ORDER BY preco ASC LIMIT 1")
    suspend fun getMenorPreco(produtoId: Int): PrecoProduto?
    
    @Query("SELECT * FROM precos_produtos WHERE produtoId IN (:produtoIds) AND disponivel = 1")
    suspend fun getPrecosPorProdutos(produtoIds: List<Int>): List<PrecoProduto>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreco(precoProduto: PrecoProduto)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrecos(precos: List<PrecoProduto>)
    
    @Update
    suspend fun updatePreco(precoProduto: PrecoProduto)
    
    @Delete
    suspend fun deletePreco(precoProduto: PrecoProduto)
    
    @Query("DELETE FROM precos_produtos")
    suspend fun deleteAllPrecos()
}

