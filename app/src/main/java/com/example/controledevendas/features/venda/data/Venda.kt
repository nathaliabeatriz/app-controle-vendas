package com.example.controledevendas.features.venda.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.controledevendas.features.cliente.data.Cliente
import java.util.Date

@Entity(tableName = "vendas", foreignKeys = [
    ForeignKey(
        entity = Cliente::class,
        parentColumns = ["idCliente"],
        childColumns = ["idCliente"],
        onDelete = ForeignKey.SET_NULL
    )
],
    indices = [Index(value = ["idCliente"])]
)

data class Venda(
    @PrimaryKey(autoGenerate = true)
    val idVenda: Long = 0,
    val idCliente: Long?,
    val dataVenda: Date,
    val valorTotal: Double,
    val desconto: Double
)