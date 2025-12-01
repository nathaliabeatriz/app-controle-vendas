package com.example.controledevendas.features.pagamento.data

import javax.inject.Inject

class PagamentoRepository @Inject constructor(private val pagamentoDao: PagamentoDao) {
    val allPagamentos = pagamentoDao.getAllPagamentosComDetalhes()

    suspend fun insert(pagamento: Pagamento): Long{
        return pagamentoDao.insert(pagamento)
    }
    suspend fun update(pagamento: Pagamento){
        pagamentoDao.update(pagamento)
    }
    suspend fun delete(pagamento: Pagamento) {
        pagamentoDao.delete(pagamento)
    }
}