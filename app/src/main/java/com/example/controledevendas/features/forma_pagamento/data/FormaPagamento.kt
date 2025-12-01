package com.example.controledevendas.features.forma_pagamento.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "formas_pagamento")
data class FormaPagamento(
    @PrimaryKey(autoGenerate = true)
    val idForma: Long = 0,
    val descricao: String
)