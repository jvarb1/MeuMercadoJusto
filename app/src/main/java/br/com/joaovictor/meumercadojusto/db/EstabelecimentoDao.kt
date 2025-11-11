package br.com.joaovictor.meumercadojusto.db

import androidx.room.*
import br.com.joaovictor.meumercadojusto.model.Estabelecimento
import kotlinx.coroutines.flow.Flow

@Dao
interface EstabelecimentoDao {
    @Query("SELECT * FROM estabelecimentos WHERE ativo = 1 ORDER BY nome")
    fun getAllEstabelecimentos(): Flow<List<Estabelecimento>>
    
    @Query("SELECT * FROM estabelecimentos WHERE id = :id")
    suspend fun getEstabelecimentoPorId(id: Int): Estabelecimento?
    
    @Query("SELECT * FROM estabelecimentos WHERE cidade = :cidade AND estado = :estado AND ativo = 1")
    fun getEstabelecimentosPorCidade(cidade: String, estado: String): Flow<List<Estabelecimento>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstabelecimento(estabelecimento: Estabelecimento)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstabelecimentos(estabelecimentos: List<Estabelecimento>)
    
    @Update
    suspend fun updateEstabelecimento(estabelecimento: Estabelecimento)
    
    @Delete
    suspend fun deleteEstabelecimento(estabelecimento: Estabelecimento)
    
    @Query("DELETE FROM estabelecimentos")
    suspend fun deleteAllEstabelecimentos()
}

