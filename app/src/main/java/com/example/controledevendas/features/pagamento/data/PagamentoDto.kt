package com.example.controledevendas.features.pagamento.data

import com.example.controledevendas.core.data.relations.PagamentoDetalhes

data class PagamentoDto(
    val pagamentoDetalhes: PagamentoDetalhes,
    val nomeCliente: String?,
    val valorPendente: Double
)