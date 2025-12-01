package com.example.controledevendas.core.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.controledevendas.core.utils.DateConverter
import com.example.controledevendas.core.utils.EnumStatusPagamentoConverter
import com.example.controledevendas.features.cliente.data.Cliente
import com.example.controledevendas.features.cliente.data.ClienteDao
import com.example.controledevendas.features.forma_pagamento.data.FormaPagamento
import com.example.controledevendas.features.forma_pagamento.data.FormaPagamentoDao
import com.example.controledevendas.features.meio_pagamento.data.MeioPagamento
import com.example.controledevendas.features.meio_pagamento.data.MeioPagamentoDao
import com.example.controledevendas.features.movimentacao.data.Movimentacao
import com.example.controledevendas.features.movimentacao.data.MovimentacaoDao
import com.example.controledevendas.features.pagamento.data.Pagamento
import com.example.controledevendas.features.pagamento.data.PagamentoDao
import com.example.controledevendas.features.parcela.data.Parcela
import com.example.controledevendas.features.parcela.data.ParcelaDao
import com.example.controledevendas.features.produto.data.Produto
import com.example.controledevendas.features.produto.data.ProdutoDao
import com.example.controledevendas.features.venda.data.Venda
import com.example.controledevendas.features.venda.data.VendaDao

@Database(entities = [Cliente::class, Produto::class, Movimentacao::class, Venda::class, FormaPagamento::class, MeioPagamento::class, Pagamento::class, Parcela::class], version = 6, exportSchema = false)
@TypeConverters(DateConverter::class, EnumStatusPagamentoConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clienteDao(): ClienteDao
    abstract fun produtoDao(): ProdutoDao
    abstract fun movimentacaoDao(): MovimentacaoDao
    abstract fun vendaDao(): VendaDao
    abstract fun formaPagamentoDao(): FormaPagamentoDao
    abstract fun meioPagamentoDao(): MeioPagamentoDao
    abstract fun pagamentoDao(): PagamentoDao
    abstract fun parcelaDao(): ParcelaDao
}