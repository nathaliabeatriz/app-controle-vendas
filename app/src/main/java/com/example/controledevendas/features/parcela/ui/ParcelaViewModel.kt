package com.example.controledevendas.features.parcela.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.example.controledevendas.core.data.enums.StatusPagamento
import com.example.controledevendas.features.meio_pagamento.data.MeioPagamento
import com.example.controledevendas.features.meio_pagamento.data.MeioPagamentoRepository
import com.example.controledevendas.features.pagamento.data.Pagamento
import com.example.controledevendas.features.pagamento.data.PagamentoRepository
import com.example.controledevendas.features.parcela.data.Parcela
import com.example.controledevendas.features.parcela.data.ParcelaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ParcelaViewModel @Inject constructor(private val parcelaRepository: ParcelaRepository,
    private val meioPagamentoRepository: MeioPagamentoRepository,
    private val pagamentoRepository: PagamentoRepository) : ViewModel() {
    private val _valorPendente = MutableStateFlow<Double>(0.0)
    val valorPendente: StateFlow<Double> = _valorPendente.asStateFlow()
    private val _valorPendenteUi = MutableStateFlow<Double>(0.0)
    val valorPendenteUi: StateFlow<Double> = _valorPendenteUi.asStateFlow()
    private val _idPagamento = MutableStateFlow<Long>(0)

    val meiosPagamento: StateFlow<List<MeioPagamento>> = meioPagamentoRepository.allMeiosPagamento.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    private val _savedStatus = MutableStateFlow(false)
    val savedStatus: StateFlow<Boolean> = _savedStatus.asStateFlow()

    fun setIdPagamento(idPagamento: Long){
        _idPagamento.value = idPagamento
    }
    fun setValorPendenteUi(valorPago: Double){
        _valorPendenteUi.value = _valorPendente.value - valorPago
    }

    fun loadValorPendente(idPagamento: Long){
        viewModelScope.launch {
            val sumParcelas = parcelaRepository.getValorTotalParcelas(idPagamento)
            val pagamentoDetalhes = pagamentoRepository.getPagamentoById(idPagamento)
            _valorPendente.value = pagamentoDetalhes!!.venda.valorTotal - sumParcelas
            _valorPendenteUi.value = _valorPendente.value
        }
    }

    @Transaction
    fun saveParcela(idPagamento: Long, data: Date, valor: Double, meioPagamento: String){
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO){
                    val meioPagamento = meioPagamentoRepository.getMeioPagamentoByDescricao(meioPagamento).first()
                    val parcela = Parcela(idPagamento = idPagamento, idMeio = meioPagamento!!.idMeio, dataPagamento = data, valor = valor)
                    parcelaRepository.insert(parcela)
                    val pagamentoDetalhes = pagamentoRepository.getPagamentoById(idPagamento)
                    val sumParcelas = parcelaRepository.getValorTotalParcelas(idPagamento)
                    if(sumParcelas >= pagamentoDetalhes!!.venda.valorTotal){
                        val updatedPagamento = Pagamento(idPagamento = idPagamento, idVenda = pagamentoDetalhes.venda.idVenda, idForma = pagamentoDetalhes.pagamento.idForma, status = StatusPagamento.CONCLUIDO)
                        pagamentoRepository.update(updatedPagamento)
                    }
                }
                _savedStatus.value = true
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun navigationConcluded(){
        _savedStatus.value = false
    }
}