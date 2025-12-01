package com.example.controledevendas.features.parcela.data

import javax.inject.Inject

class ParcelaRepository @Inject constructor(private val parcelaDao: ParcelaDao) {
    suspend fun insert(parcela: Parcela) {
        parcelaDao.insert(parcela)
    }
    suspend fun delete(parcela: Parcela) {
        parcelaDao.delete(parcela)
    }
}