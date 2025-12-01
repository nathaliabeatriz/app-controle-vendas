package com.example.controledevendas.core.di

import android.content.Context
import androidx.room.Room
import com.example.controledevendas.core.data.AppDatabase
import com.example.controledevendas.features.cliente.data.ClienteDao
import com.example.controledevendas.features.cliente.data.ClienteRepository
import com.example.controledevendas.features.forma_pagamento.data.FormaPagamentoDao
import com.example.controledevendas.features.forma_pagamento.data.FormaPagamentoRepository
import com.example.controledevendas.features.meio_pagamento.data.MeioPagamento
import com.example.controledevendas.features.meio_pagamento.data.MeioPagamentoDao
import com.example.controledevendas.features.meio_pagamento.data.MeioPagamentoRepository
import com.example.controledevendas.features.movimentacao.data.MovimentacaoDao
import com.example.controledevendas.features.movimentacao.data.MovimentacaoRepository
import com.example.controledevendas.features.pagamento.data.PagamentoDao
import com.example.controledevendas.features.pagamento.data.PagamentoRepository
import com.example.controledevendas.features.parcela.data.ParcelaDao
import com.example.controledevendas.features.parcela.data.ParcelaRepository
import com.example.controledevendas.features.produto.data.ProdutoDao
import com.example.controledevendas.features.produto.data.ProdutoRepository
import com.example.controledevendas.features.venda.data.VendaDao
import com.example.controledevendas.features.venda.data.VendaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "controle_de_vendas_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    @Provides
    @Singleton
    fun provideClienteDao(database: AppDatabase): ClienteDao {
        return database.clienteDao()
    }
    @Provides
    @Singleton
    fun provideClienteRepository(clienteDao: ClienteDao): ClienteRepository {
        return ClienteRepository(clienteDao)
    }
    @Provides
    @Singleton
    fun provideProdutoDao(database: AppDatabase): ProdutoDao {
        return database.produtoDao()
    }
    @Provides
    @Singleton
    fun provideProdutoRepository(produtoDao: ProdutoDao, @ApplicationContext context: Context): ProdutoRepository {
        return ProdutoRepository(produtoDao, context)
    }
    @Provides
    @Singleton
    fun provideMovimentacaoDao(database: AppDatabase): MovimentacaoDao {
        return database.movimentacaoDao()
    }
    @Provides
    @Singleton
    fun provideMovimentacaoRepository(movimentacaoDao: MovimentacaoDao): MovimentacaoRepository {
        return MovimentacaoRepository(movimentacaoDao)
    }
    @Provides
    @Singleton
    fun provideVendaDao(database: AppDatabase): VendaDao {
        return database.vendaDao()
    }
    @Provides
    @Singleton
    fun provideVendaRepository(vendaDao: VendaDao): VendaRepository {
        return VendaRepository(vendaDao)
    }
    @Provides
    @Singleton
    fun provideFormaPagamentoDao(database: AppDatabase): FormaPagamentoDao{
        return database.formaPagamentoDao()
    }
    @Provides
    @Singleton
    fun provideFormaPagamentoRepository(formaPagamentoDao: FormaPagamentoDao): FormaPagamentoRepository {
        return FormaPagamentoRepository(formaPagamentoDao)
    }
    @Provides
    @Singleton
    fun provideMeioPagamentoDao(database: AppDatabase): MeioPagamentoDao{
        return database.meioPagamentoDao()
    }
    @Provides
    @Singleton
    fun provideMeioPagamentoRepository(meioPagamentoDao: MeioPagamentoDao): MeioPagamentoRepository {
        return MeioPagamentoRepository(meioPagamentoDao)
    }
    @Provides
    @Singleton
    fun providePagamentoDao(database: AppDatabase): PagamentoDao{
        return database.pagamentoDao()
    }
    @Provides
    @Singleton
    fun providePagamentoRepository(pagamentoDao: PagamentoDao): PagamentoRepository {
        return PagamentoRepository(pagamentoDao)
    }
    @Provides
    @Singleton
    fun provideParcelaDao(database: AppDatabase): ParcelaDao{
        return database.parcelaDao()
    }
    @Provides
    @Singleton
    fun provideParcelaRepository(parcelaDao: ParcelaDao): ParcelaRepository {
        return ParcelaRepository(parcelaDao)
    }
}
