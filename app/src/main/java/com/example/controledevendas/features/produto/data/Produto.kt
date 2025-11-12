package com.example.controledevendas.features.produto.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "produtos")
data class Produto (
    @PrimaryKey(autoGenerate = true)
    var idProduto: Long = 0,
    var nome: String,
    var preco: Double,
    var descricao: String?,
    var urlImg: String?
)