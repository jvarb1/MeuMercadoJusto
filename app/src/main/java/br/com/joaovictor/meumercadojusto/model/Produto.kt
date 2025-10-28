package br.com.joaovictor.meumercadojusto.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "produtos")
data class Produto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val categoria: String,
    val preco: Double,
    val unidade: String, // kg, litro, unidade, etc.
    val descricao: String = "",
    val imagemUrl: String = "",
    val emEstoque: Boolean = true
)
