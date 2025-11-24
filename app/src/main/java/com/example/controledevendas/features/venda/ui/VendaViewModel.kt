package com.example.controledevendas.features.venda.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controledevendas.features.cliente.data.Cliente
import com.example.controledevendas.features.cliente.data.ClienteRepository
import com.example.controledevendas.features.itemVenda.data.ItemVendaDto
import com.example.controledevendas.features.produto.data.Produto
import com.example.controledevendas.features.produto.data.ProdutoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

@HiltViewModel
class VendaViewModel @Inject constructor(private val clienteRepository: ClienteRepository, private val produtoRepository: ProdutoRepository) : ViewModel(){
    private val saveMutex = Mutex()
    private val _itensVenda = MutableStateFlow<List<ItemVendaDto>>(emptyList())
    val itensVenda: StateFlow<List<ItemVendaDto>> = _itensVenda.asStateFlow()

    private val _total = MutableStateFlow<Double>(0.0)
    val total: StateFlow<Double> = _total.asStateFlow()

    val allClientes: StateFlow<List<Cliente>> = clienteRepository.allClientes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _cliente = MutableStateFlow<Cliente?>(null)
    val cliente: StateFlow<Cliente?> = _cliente

    init {
        calculateEstoque()
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
}