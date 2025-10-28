package br.com.joaovictor.meumercadojusto.db

import androidx.room.*
import br.com.joaovictor.meumercadojusto.model.Produto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProdutoDao {
    @Query("SELECT * FROM produtos ORDER BY categoria, nome")
    fun getAllProdutos(): Flow<List<Produto>>
    
    @Query("SELECT * FROM produtos WHERE categoria = :categoria ORDER BY nome")
    fun getProdutosPorCategoria(categoria: String): Flow<List<Produto>>
    
    @Query("SELECT * FROM produtos WHERE id = :id")
    suspend fun getProdutoPorId(id: Int): Produto?
    
    @Query("SELECT DISTINCT categoria FROM produtos ORDER BY categoria")
    fun getAllCategorias(): Flow<List<String>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduto(produto: Produto)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProdutos(produtos: List<Produto>)
    
    @Update
    suspend fun updateProduto(produto: Produto)
    
    @Delete
    suspend fun deleteProduto(produto: Produto)
    
    @Query("DELETE FROM produtos")
    suspend fun deleteAllProdutos()
}
