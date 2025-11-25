package com.example.controledevendas.features.venda.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface VendaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(venda: Venda): Long

    @Delete
    suspend fun delete(venda: Venda)
}