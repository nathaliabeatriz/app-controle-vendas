package com.example.controledevendas.features.movimentacao.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovimentacaoRepository @Inject constructor(private val movimentacaoDao: MovimentacaoDao) {
    suspend fun insert(movimentacao: Movimentacao) {
        movimentacaoDao.insert(movimentacao)
    }

    suspend fun delete(movimentacao: Movimentacao) {
        movimentacaoDao.delete(movimentacao)
    }
}