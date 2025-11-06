package com.example.controledevendas.features.cliente.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(cliente: Cliente)

    @Delete
    suspend fun delete(cliente: Cliente)

    @Update
    suspend fun update(cliente: Cliente)

    @Query("SELECT * FROM clientes ORDER BY nome ASC")
    fun getAllClientes(): Flow<List<Cliente>>

    @Query("SELECT * FROM clientes WHERE idCliente = :id")
    fun getClienteById(id: Long): Flow<Cliente?>
}