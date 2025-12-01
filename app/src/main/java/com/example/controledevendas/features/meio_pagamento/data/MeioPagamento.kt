package com.example.controledevendas.features.meio_pagamento.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meios_pagamento")
data class MeioPagamento(
    @PrimaryKey(autoGenerate = true)
    val idMeio: Long = 0,
    val descricao: String
)
