package com.example.controledevendas.features.forma_pagamento.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FormaPagamentoRepository @Inject constructor(private val formaPagamentoDao: FormaPagamentoDao) {
    val allFormasPagamento = formaPagamentoDao.getAllFormasPagamento()

    suspend fun insert(formaPagamento: FormaPagamento) {
        formaPagamentoDao.insert(formaPagamento)
    }

    suspend fun delete(formaPagamento: FormaPagamento){
        formaPagamentoDao.delete(formaPagamento)
    }

    suspend fun getFormaPagamentoByDescricao(descricao: String): Flow<FormaPagamento?>{
        return formaPagamentoDao.getFormaPagamentoByDescricao(descricao)
    }
}