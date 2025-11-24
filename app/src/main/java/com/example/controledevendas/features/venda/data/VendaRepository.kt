package com.example.controledevendas.features.venda.data

import javax.inject.Inject

class VendaRepository @Inject constructor(private val vendaDao: VendaDao) {
    suspend fun insert(venda: Venda) {
        vendaDao.insert(venda)
    }

    suspend fun delete(venda: Venda) {
        vendaDao.delete(venda)
    }
}