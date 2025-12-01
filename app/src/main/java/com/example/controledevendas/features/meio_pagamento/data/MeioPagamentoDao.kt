package com.example.controledevendas.features.meio_pagamento.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MeioPagamentoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(meioPagamento: MeioPagamento)

    @Delete
    suspend fun delete(meioPagamento: MeioPagamento)

    @Query("SELECT * FROM meios_pagamento")
    fun getAllMeiosPagamento(): Flow<List<MeioPagamento>>

    @Query("SELECT * FROM meios_pagamento WHERE descricao = :descricao")
    fun getMeioByDescricao(descricao: String): Flow<MeioPagamento?>
}