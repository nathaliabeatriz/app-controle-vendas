package com.example.controledevendas.features.produto.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import com.example.controledevendas.core.data.relations.ProdutoMovimentacao
import com.example.controledevendas.features.produto.data.Produto
import com.example.controledevendas.features.produto.data.ProdutoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProdutoViewModel @Inject constructor(private val produtoRepository: ProdutoRepository): ViewModel(){
    private val _produto = MutableStateFlow<Produto?>(null)
    val produto: StateFlow<Produto?> = _produto
    private var idProduto: Long = 0L
    // LiveData para "segurar" a Uri que o Fragment nos envia
    private val _imagemSelecionada = MutableStateFlow<Uri?>(null)
    private val _savedStatus = MutableStateFlow(false)
    val savedStatus: StateFlow<Boolean> = _savedStatus.asStateFlow()

    val produtoComMovimentacoes: StateFlow<List<ProdutoMovimentacao?>> =
        produtoRepository.movimentacoesByProduto.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setImagemSelecionada(uri: Uri) {
        _imagemSelecionada.value = uri
    }
    fun loadProduto(id: Long){
        if (id > 0L) {
            idProduto = id
            viewModelScope.launch {
                produtoRepository.getProdutoById(id).collect { loadedProduto ->
                    _produto.value = loadedProduto
                }
            }
        } else {
            _produto.value = null
        }
    }

    @Transaction
    fun saveProduto(nome: String, preco: Double, descricao: String?){
        viewModelScope.launch {
            try{
                withContext(Dispatchers.IO){
                    var newUrlImg: String? = produto.value?.urlImg
                    _imagemSelecionada.value?.let { uri ->
                        // Converte a Uri em Bitmap
                        val bitmap = produtoRepository.uriToBitmap(uri)
                        // Salva o bitmap no armazenamento interno
                        newUrlImg = produtoRepository.saveImage(bitmap, nome)
                    }

                    //se está no modo de edição
                    if(idProduto > 0L){
                        produtoRepository.deleteImage(produto.value?.urlImg)
                        val updatedProduto = Produto(idProduto, nome, preco, descricao, newUrlImg)
                        produtoRepository.update(updatedProduto)
                    }else{
                        val newProduto = Produto(nome = nome, preco = preco, descricao = descricao, urlImg = newUrlImg)
                        val newId: Long = produtoRepository.insert(newProduto)
                        _produto.value = newProduto.copy(idProduto = newId)
                    }
                }
                _savedStatus.value = true

            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun delete(produto: Produto) {
        viewModelScope.launch {
            produtoRepository.deleteImage(produto.urlImg)
            produtoRepository.delete(produto)
        }
    }

    fun navigationConcluded(){
        _savedStatus.value = false
    }
}