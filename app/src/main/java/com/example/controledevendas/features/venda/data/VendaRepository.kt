package com.example.controledevendas.features.venda.data

import com.example.controledevendas.core.data.relations.VendaCliente
import javax.inject.Inject

class VendaRepository @Inject constructor(private val vendaDao: VendaDao) {
    suspend fun insert(venda: Venda): Long {
        return vendaDao.insert(venda)
    }

    suspend fun delete(venda: Venda) {
        vendaDao.delete(venda)
    }
    suspend fun getVendaComCliente(idVenda: Long): VendaCliente?{
        return vendaDao.getVendaComCliente(idVenda)
    }
}