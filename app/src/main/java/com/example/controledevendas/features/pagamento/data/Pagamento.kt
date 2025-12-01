package com.example.controledevendas.features.pagamento.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.controledevendas.core.data.enums.StatusPagamento
import com.example.controledevendas.features.forma_pagamento.data.FormaPagamento
import com.example.controledevendas.features.meio_pagamento.data.MeioPagamento
import com.example.controledevendas.features.venda.data.Venda

@Entity(tableName = "pagamentos", foreignKeys = [
    ForeignKey(
        entity = Venda::class,
        parentColumns = ["idVenda"],
        childColumns = ["idVenda"],
        onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
        entity = FormaPagamento::class,
        parentColumns = ["idForma"],
        childColumns = ["idForma"],
        onDelete = ForeignKey.SET_NULL
    )
],
    indices = [Index(value = ["idVenda"]), Index(value = ["idForma"])]
)
data class Pagamento(
    @PrimaryKey(autoGenerate = true)
    val idPagamento: Long = 0,
    val idVenda: Long,
    val idForma: Long,
    val status: StatusPagamento = StatusPagamento.PENDENTE
)
