package com.example.controledevendas.core.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.controledevendas.features.cliente.data.Cliente
import com.example.controledevendas.features.venda.data.Venda

data class ClienteVenda(
    @Embedded
    val cliente: Cliente,
    @Relation(
        parentColumn = "idCliente",
        entityColumn = "idCliente"
    )
    val vendas: List<Venda>
)
