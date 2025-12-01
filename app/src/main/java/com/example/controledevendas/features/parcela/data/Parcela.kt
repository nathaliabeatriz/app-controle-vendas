package com.example.controledevendas.features.parcela.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.controledevendas.features.meio_pagamento.data.MeioPagamento
import com.example.controledevendas.features.pagamento.data.Pagamento
import java.util.Date

@Entity(tableName = "parcelas", foreignKeys = [
    ForeignKey(
        entity = Pagamento::class,
        parentColumns = ["idPagamento"],
        childColumns = ["idPagamento"],
        onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(
        entity = MeioPagamento::class,
        parentColumns = ["idMeio"],
        childColumns = ["idMeio"],
        onDelete = ForeignKey.SET_NULL
    )
],
    indices = [Index(value = ["idPagamento"]), Index(value = ["idMeio"])])

data class Parcela(
    @PrimaryKey(autoGenerate = true)
    val idParcela: Long = 0,
    val idPagamento: Long,
    val idMeio: Long,
    val valor: Double,
    val dataPagamento: Date
)
