package br.com.joaovictor.meumercadojusto.db

import androidx.room.*
import br.com.joaovictor.meumercadojusto.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios WHERE email = :email AND senha = :senha")
    suspend fun login(email: String, senha: String): Usuario?
    
    @Query("SELECT * FROM usuarios WHERE email = :email")
    suspend fun getUsuarioPorEmail(email: String): Usuario?
    
    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun getUsuarioPorId(id: Int): Usuario?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(usuario: Usuario)
    
    @Update
    suspend fun updateUsuario(usuario: Usuario)
    
    @Delete
    suspend fun deleteUsuario(usuario: Usuario)
    
    @Query("DELETE FROM usuarios")
    suspend fun deleteAllUsuarios()
}
