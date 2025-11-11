package br.com.joaovictor.meumercadojusto.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import br.com.joaovictor.meumercadojusto.model.Estabelecimento
import br.com.joaovictor.meumercadojusto.model.PrecoProduto
import br.com.joaovictor.meumercadojusto.model.Produto
import br.com.joaovictor.meumercadojusto.model.Usuario

@Database(
    version = 2, 
    entities = [
        Produto::class, 
        Usuario::class,
        Estabelecimento::class,
        PrecoProduto::class
    ],
    exportSchema = false
)
abstract class DatabaseHelper : RoomDatabase() {

    abstract fun produtoDao(): ProdutoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun estabelecimentoDao(): EstabelecimentoDao
    abstract fun precoProdutoDao(): PrecoProdutoDao

    companion object {
        @Volatile
        private var INSTANCE: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    DatabaseHelper::class.java,
                    "meumercadojusto.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Criar tabela estabelecimentos
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS estabelecimentos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nome TEXT NOT NULL,
                        endereco TEXT NOT NULL,
                        cidade TEXT NOT NULL,
                        estado TEXT NOT NULL,
                        cep TEXT NOT NULL,
                        telefone TEXT NOT NULL,
                        latitude REAL,
                        longitude REAL,
                        ativo INTEGER NOT NULL DEFAULT 1
                    )
                """.trimIndent())

                // Criar tabela precos_produtos
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS precos_produtos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        produtoId INTEGER NOT NULL,
                        estabelecimentoId INTEGER NOT NULL,
                        preco REAL NOT NULL,
                        dataAtualizacao INTEGER NOT NULL,
                        disponivel INTEGER NOT NULL DEFAULT 1,
                        FOREIGN KEY(produtoId) REFERENCES produtos(id) ON DELETE CASCADE,
                        FOREIGN KEY(estabelecimentoId) REFERENCES estabelecimentos(id) ON DELETE CASCADE
                    )
                """.trimIndent())

                // Criar índices
                database.execSQL("CREATE INDEX IF NOT EXISTS index_precos_produtos_produtoId ON precos_produtos(produtoId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_precos_produtos_estabelecimentoId ON precos_produtos(estabelecimentoId)")
                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_precos_produtos_produtoId_estabelecimentoId ON precos_produtos(produtoId, estabelecimentoId)")
            }
        }
    }
}
