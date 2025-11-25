package com.example.controledevendas.features.venda.data

import com.example.controledevendas.core.data.relations.ProdutoMovimentacao

data class ItemVendaDto(
    var produtoMovimentacao: ProdutoMovimentacao,
    var quantidadeItens: Int = 0,
    var quantidadeEstoque: Int
)