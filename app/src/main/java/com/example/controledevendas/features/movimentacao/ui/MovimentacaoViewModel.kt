package com.example.controledevendas.features.movimentacao.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controledevendas.features.movimentacao.data.Movimentacao
import com.example.controledevendas.features.movimentacao.data.MovimentacaoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MovimentacaoViewModel @Inject constructor(private val movimentacaoRepository: MovimentacaoRepository): ViewModel() {

    fun saveMovimentacao(movimento: Int, dataMovimentacao: Date, custoUnidade: Double, idProduto: Long?){
        viewModelScope.launch {
            try{
                val newMovimentacao = Movimentacao(movimento = movimento, dataMovimentacao = dataMovimentacao, custoUnidade = custoUnidade, idProduto = idProduto)
                movimentacaoRepository.insert(newMovimentacao)
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}