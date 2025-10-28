package br.com.joaovictor.meumercadojusto.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.joaovictor.meumercadojusto.model.Produto
import br.com.joaovictor.meumercadojusto.model.Usuario

@Database(
    version = 1, 
    entities = [Produto::class, Usuario::class]
)
abstract class DatabaseHelper : RoomDatabase() {

    abstract fun produtoDao(): ProdutoDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        fun getInstance(context: Context): DatabaseHelper {
            return Room.databaseBuilder(
                context,
                DatabaseHelper::class.java,
                "meumercadojusto.db"
            ).build()
        }
    }
}
