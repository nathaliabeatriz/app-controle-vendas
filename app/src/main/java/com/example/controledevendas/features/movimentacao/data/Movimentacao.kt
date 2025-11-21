package com.example.controledevendas.features.movimentacao.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.controledevendas.features.produto.data.Produto
import java.util.Date

@Entity(tableName = "movimentacoes", foreignKeys = [
    ForeignKey(
        entity = Produto::class,
        parentColumns = ["idProduto"],
        childColumns = ["idProduto"],
        onDelete = ForeignKey.SET_NULL
    )
],
    indices = [Index(value = ["idProduto"])]
)

data class Movimentacao(
    @PrimaryKey(autoGenerate = true)
    val idMovimentacao: Long = 0,
    val movimento: Int,
    val dataMovimentacao: Date,
    val custoUnidade: Double,
    val idProduto: Long?,
)