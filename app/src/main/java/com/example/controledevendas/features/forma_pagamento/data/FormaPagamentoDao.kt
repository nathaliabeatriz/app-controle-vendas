package com.example.controledevendas.features.forma_pagamento.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FormaPagamentoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(formaPagamento: FormaPagamento)

    @Delete
    suspend fun delete(formaPagamento: FormaPagamento)

    @Query("SELECT * FROM formas_pagamento")
    fun getAllFormasPagamento(): Flow<List<FormaPagamento>>

    @Query("SELECT * FROM formas_pagamento WHERE descricao = :descricao")
    fun getFormaPagamentoByDescricao(descricao: String): Flow<FormaPagamento?>
}