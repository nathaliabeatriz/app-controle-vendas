package com.example.controledevendas.features.cliente.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clientes")
data class Cliente (
    @PrimaryKey(autoGenerate = true)
    val idCliente: Long = 0,
    val nome: String,
    val telefone: String
)