package com.example.controledevendas.features.cliente.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ClienteRepository @Inject constructor(private val clienteDao: ClienteDao) {
    val allClientes: Flow<List<Cliente>> = clienteDao.getAllClientes()

    suspend fun insert(cliente: Cliente){
        clienteDao.insert(cliente)
    }

    suspend fun delete(cliente: Cliente){
        clienteDao.delete(cliente)
    }

    suspend fun update(cliente: Cliente){
        clienteDao.update(cliente)
    }

    fun getClienteById(id: Long): Flow<Cliente?> {
        return clienteDao.getClienteById(id)
    }

}