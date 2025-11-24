package com.example.controledevendas.core.di

import android.content.Context
import androidx.room.Room
import com.example.controledevendas.core.data.AppDatabase
import com.example.controledevendas.features.cliente.data.ClienteDao
import com.example.controledevendas.features.cliente.data.ClienteRepository
import com.example.controledevendas.features.movimentacao.data.MovimentacaoDao
import com.example.controledevendas.features.movimentacao.data.MovimentacaoRepository
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
}
