package com.example.controledevendas.features.parcela.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ParcelaRepository @Inject constructor(private val parcelaDao: ParcelaDao) {
    suspend fun insert(parcela: Parcela) {
        parcelaDao.insert(parcela)
    }
    suspend fun delete(parcela: Parcela) {
        parcelaDao.delete(parcela)
    }

    suspend fun getValorTotalParcelas(idPagamento: Long): Double {
        return parcelaDao.getValorTotalParcelas(idPagamento)
    }

    fun getAllParcelas(): Flow<List<Parcela>> {
        return parcelaDao.getAllParcelas()
    }
}