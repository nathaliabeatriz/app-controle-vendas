package com.example.controledevendas.features.venda.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.example.controledevendas.features.cliente.data.Cliente
import com.example.controledevendas.features.cliente.data.ClienteRepository
import com.example.controledevendas.features.movimentacao.data.Movimentacao
import com.example.controledevendas.features.movimentacao.data.MovimentacaoRepository
import com.example.controledevendas.features.venda.data.ItemVendaDto
import com.example.controledevendas.features.produto.data.Produto
import com.example.controledevendas.features.produto.data.ProdutoRepository
import com.example.controledevendas.features.venda.data.Venda
import com.example.controledevendas.features.venda.data.VendaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class VendaViewModel @Inject constructor(
        private val vendaRepository: VendaRepository,
        private val clienteRepository: ClienteRepository,
        private val produtoRepository: ProdutoRepository,
        private val movimentacaoRepository: MovimentacaoRepository) : ViewModel(){

    private val _savedStatus = MutableStateFlow(false)
    val savedStatus: StateFlow<Boolean> = _savedStatus.asStateFlow()
    private val _itensVenda = MutableStateFlow<List<ItemVendaDto>>(emptyList())
    val itensVenda: StateFlow<List<ItemVendaDto>> = _itensVenda.asStateFlow()

    private val _total = MutableStateFlow<Double>(0.0)
    val total: StateFlow<Double> = _total.asStateFlow()

    private val _desconto = MutableStateFlow<Double>(0.0)
    val desconto: StateFlow<Double> = _desconto.asStateFlow()

    val allClientes: StateFlow<List<Cliente>> = clienteRepository.allClientes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _cliente = MutableStateFlow<Cliente?>(null)
    val cliente: StateFlow<Cliente?> = _cliente

    val continuarVenda: StateFlow<Boolean> = _itensVenda.map { lista ->
        lista.sumOf { it.quantidadeItens } > 0
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val totalComDesconto: StateFlow<Double> = combine(_total, _desconto) { total, desconto ->
        total - desconto
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    init {
        calculateEstoque()
    }

    fun setDesconto(desconto: Double){
        _desconto.value = desconto
    }

    fun setCliente(cliente: Cliente){
        _cliente.value = cliente
    }

    fun calculateEstoque(){
        viewModelScope.launch {
            val lstRelations = produtoRepository.movimentacoesByProduto.first()

            val mappedLst = lstRelations.map { relation ->
                val totalEstoque = relation!!.movimentacoes.sumOf { it.movimento }

                ItemVendaDto(
                    produtoMovimentacao = relation,
                    quantidadeEstoque = totalEstoque,
                    quantidadeItens = 0
                )
            }
            _itensVenda.value = mappedLst
        }
    }

    fun updateEstoque(produto: Produto, value: Int) {
        _itensVenda.update{ currentList ->
            currentList.map { item ->
                if (item.produtoMovimentacao.produto.idProduto == produto.idProduto && item.quantidadeEstoque - value >= 0 && item.quantidadeItens + value >= 0) {
                    _total.value = _total.value + (produto.preco * value)
                    item.copy(
                        quantidadeItens = item.quantidadeItens + value,
                        quantidadeEstoque = item.quantidadeEstoque - value
                    )
                } else {
                    item
                }
            }
        }
    }

    @Transaction
    fun saveVenda(dataVenda: Date){
        viewModelScope.launch {
            try{
                withContext(Dispatchers.IO){
                    val venda = Venda(idCliente = cliente.value?.idCliente, dataVenda = dataVenda, valorTotal = totalComDesconto.value, valorItens = total.value)
                    val idVenda: Long = vendaRepository.insert(venda)

                    if(idVenda > 0){
                        _itensVenda.value.forEach { item ->
                            if(item.quantidadeItens > 0){
                                val movimentacao = Movimentacao(movimento = item.quantidadeItens * -1, dataMovimentacao = dataVenda, idProduto = item.produtoMovimentacao.produto.idProduto, idVenda = idVenda)
                                movimentacaoRepository.insert(movimentacao)
                            }
                        }
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