package br.com.joaovictor.meumercadojusto.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "estabelecimentos")
data class Estabelecimento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val endereco: String,
    val cidade: String,
    val estado: String,
    val cep: String = "",
    val telefone: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val ativo: Boolean = true
)

