package com.example.controledevendas.features.parcela.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface ParcelaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(parcela: Parcela)
    @Delete
    suspend fun delete(parcela: Parcela)
}