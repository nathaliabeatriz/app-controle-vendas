package com.example.controledevendas.features.itemVenda.data

import com.example.controledevendas.core.data.relations.ProdutoMovimentacao

data class ItemVendaDto(
    var produtoMovimentacao: ProdutoMovimentacao,
    var quantidadeItens: Int = 0,
    var quantidadeEstoque: Int
)