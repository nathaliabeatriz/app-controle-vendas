package com.example.controledevendas.core.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.controledevendas.features.movimentacao.data.Movimentacao
import com.example.controledevendas.features.produto.data.Produto
import kotlinx.coroutines.flow.Flow

data class ProdutoMovimentacao(
    @Embedded
    val produto: Produto,
    @Relation(
        parentColumn = "idProduto",
        entityColumn = "idProduto"
    )
    val movimentacoes: List<Movimentacao>
)
