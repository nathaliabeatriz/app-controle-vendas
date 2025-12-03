package com.example.controledevendas.features.pagamento.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controledevendas.core.data.relations.PagamentoDetalhes
import com.example.controledevendas.core.data.relations.VendaCliente
import com.example.controledevendas.features.pagamento.data.PagamentoDto
import com.example.controledevendas.features.pagamento.data.PagamentoRepository
import com.example.controledevendas.features.parcela.data.Parcela
import com.example.controledevendas.features.parcela.data.ParcelaRepository
import com.example.controledevendas.features.venda.data.VendaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PagamentoViewModel @Inject constructor(
    private val pagamentoRepository: PagamentoRepository,
    private val vendaRepository: VendaRepository,
    private val parcelaRepository: ParcelaRepository) : ViewModel() {
    private val _pagamentosComDetalhes = MutableStateFlow<List<PagamentoDto>>(emptyList())
    val pagamentosComDetalhes: StateFlow<List<PagamentoDto>> = _pagamentosComDetalhes

    val allParcelas: StateFlow<List<Parcela>> = parcelaRepository.getAllParcelas().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadPagamentos()
    }

    fun loadPagamentos(){
        viewModelScope.launch {
            pagamentoRepository.allPagamentos.collect { detalhesList ->
                val uiModelList = detalhesList.map { detalhe ->
                    val vendaCliente = vendaRepository.getVendaComCliente(detalhe.venda.idVenda)
                    val sumParcelas = parcelaRepository.getValorTotalParcelas(detalhe.pagamento.idPagamento)
                    var valorPendente = detalhe.venda.valorTotal - sumParcelas
                    if(valorPendente < 0) valorPendente = 0.0
                    PagamentoDto(
                        pagamentoDetalhes = detalhe,
                        nomeCliente = vendaCliente?.cliente?.nome,
                        valorPendente = valorPendente
                    )
                }
                _pagamentosComDetalhes.value = uiModelList
            }
        }
    }
}