package com.example.controledevendas.core.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.controledevendas.features.forma_pagamento.data.FormaPagamento
import com.example.controledevendas.features.meio_pagamento.data.MeioPagamento
import com.example.controledevendas.features.pagamento.data.Pagamento
import com.example.controledevendas.features.venda.data.Venda

data class PagamentoDetalhes (
    @Embedded
    val pagamento: Pagamento,

    @Relation(
        parentColumn = "idVenda",
        entityColumn = "idVenda"
    )
    val venda: Venda,
    @Relation(
        parentColumn = "idForma",
        entityColumn = "idForma"
    )
    val formaPagamento: FormaPagamento
)