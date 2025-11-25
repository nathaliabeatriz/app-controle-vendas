package com.example.controledevendas.features.movimentacao.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.controledevendas.features.produto.data.Produto
import com.example.controledevendas.features.venda.data.Venda
import java.util.Date

@Entity(tableName = "movimentacoes", foreignKeys = [
    ForeignKey(
        entity = Produto::class,
        parentColumns = ["idProduto"],
        childColumns = ["idProduto"],
        onDelete = ForeignKey.SET_NULL
    ),
    ForeignKey(
        entity = Venda::class,
        parentColumns = ["idVenda"],
        childColumns = ["idVenda"],
        onDelete = ForeignKey.SET_NULL
    )
],
    indices = [Index(value = ["idProduto"]), Index(value = ["idVenda"])]
)

data class Movimentacao(
    @PrimaryKey(autoGenerate = true)
    val idMovimentacao: Long = 0,
    val movimento: Int,
    val dataMovimentacao: Date,
    val custoUnidade: Double? = null,
    val idProduto: Long? = null,
    val idVenda: Long? = null
)