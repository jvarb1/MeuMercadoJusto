package br.com.joaovictor.meumercadojusto.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val senha: String,
    val nome: String,
    val telefone: String = "",
    val endereco: String = ""
)
