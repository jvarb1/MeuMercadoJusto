package br.com.joaovictor.meumercadojusto.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "precos_produtos",
    foreignKeys = [
        ForeignKey(
            entity = Produto::class,
            parentColumns = ["id"],
            childColumns = ["produtoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Estabelecimento::class,
            parentColumns = ["id"],
            childColumns = ["estabelecimentoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["produtoId"]),
        Index(value = ["estabelecimentoId"]),
        Index(value = ["produtoId", "estabelecimentoId"], unique = true)
    ]
)
data class PrecoProduto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val produtoId: Int,
    val estabelecimentoId: Int,
    val preco: Double,
    val dataAtualizacao: Long = System.currentTimeMillis(),
    val disponivel: Boolean = true
)

