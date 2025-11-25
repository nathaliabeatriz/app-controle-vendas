package com.example.controledevendas.core.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.controledevendas.core.utils.DateConverter
import com.example.controledevendas.features.cliente.data.Cliente
import com.example.controledevendas.features.cliente.data.ClienteDao
import com.example.controledevendas.features.movimentacao.data.Movimentacao
import com.example.controledevendas.features.movimentacao.data.MovimentacaoDao
import com.example.controledevendas.features.produto.data.Produto
import com.example.controledevendas.features.produto.data.ProdutoDao
import com.example.controledevendas.features.venda.data.Venda
import com.example.controledevendas.features.venda.data.VendaDao

@Database(entities = [Cliente::class, Produto::class, Movimentacao::class, Venda::class], version = 5, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clienteDao(): ClienteDao
    abstract fun produtoDao(): ProdutoDao
    abstract fun movimentacaoDao(): MovimentacaoDao
    abstract fun vendaDao(): VendaDao
}