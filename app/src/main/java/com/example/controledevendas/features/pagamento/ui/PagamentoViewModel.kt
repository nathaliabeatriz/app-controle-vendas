package com.example.controledevendas.features.pagamento.ui

import androidx.lifecycle.ViewModel
import com.example.controledevendas.features.pagamento.data.PagamentoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PagamentoViewModel @Inject constructor(private val pagamentoRepository: PagamentoRepository) : ViewModel() {

}