package com.example.controledevendas.features.meio_pagamento.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MeioPagamentoRepository @Inject constructor(private val meioPagamentoDao: MeioPagamentoDao) {
    val allMeiosPagamento = meioPagamentoDao.getAllMeiosPagamento()

    suspend fun insert(meioPagamento: MeioPagamento) {
        meioPagamentoDao.insert(meioPagamento)
    }

    suspend fun delete(meioPagamento: MeioPagamento){
        meioPagamentoDao.delete(meioPagamento)
    }

    fun getMeioPagamentoByDescricao(descricao: String): Flow<MeioPagamento?> {
        return meioPagamentoDao.getMeioByDescricao(descricao)
    }
}