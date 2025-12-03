package com.example.controledevendas.features.venda.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.controledevendas.core.data.relations.VendaCliente

@Dao
interface VendaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(venda: Venda): Long

    @Delete
    suspend fun delete(venda: Venda)

    @Transaction
    @Query("SELECT * FROM vendas WHERE idVenda = :idVenda")
    suspend fun getVendaComCliente(idVenda: Long): VendaCliente?
}