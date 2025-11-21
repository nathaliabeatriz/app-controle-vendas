package com.example.controledevendas.features.movimentacao.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.controledevendas.core.data.relations.ProdutoMovimentacao
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimentacaoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(movimentacao: Movimentacao)

    @Delete
    suspend fun delete(movimentacao: Movimentacao)

}