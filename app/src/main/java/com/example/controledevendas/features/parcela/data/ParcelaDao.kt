package com.example.controledevendas.features.parcela.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ParcelaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(parcela: Parcela)
    @Delete
    suspend fun delete(parcela: Parcela)

    @Query("SELECT * FROM parcelas")
    fun getAllParcelas(): Flow<List<Parcela>>

    @Query("SELECT COALESCE(SUM(valor), 0) FROM parcelas WHERE idPagamento = :idPagamento")
    suspend fun getValorTotalParcelas(idPagamento: Long): Double
}