package com.example.controledevendas.features.produto.ui

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controledevendas.features.produto.data.Produto
import com.example.controledevendas.features.produto.data.ProdutoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProdutoViewModel @Inject constructor(private val repository: ProdutoRepository): ViewModel(){
    private val _produto = MutableLiveData<Produto?>()
    val produto: LiveData<Produto?> = _produto
    private var idProduto: Long = 0L
    // LiveData para "segurar" a Uri que o Fragment nos envia
    private val _imagemSelecionada = MutableLiveData<Uri?>(null)

    fun setImagemSelecionada(uri: Uri) {
        _imagemSelecionada.value = uri
    }

    val allProdutos: StateFlow<List<Produto>> = repository.allProdutos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun loadProduto(id: Long){
        if (id > 0L) {
            idProduto = id
            viewModelScope.launch {
                repository.getProdutoById(id).collect { loadedProduto ->
                    _produto.value = loadedProduto
                }
            }
        } else {
            _produto.value = null
        }
    }

    fun saveProduto(nome: String, preco: Double, descricao: String?){
        viewModelScope.launch(Dispatchers.IO) {
            var newUrlImg: String? = produto.value?.urlImg
            _imagemSelecionada.value?.let { uri ->
                // Converte a Uri em Bitmap
                val bitmap = repository.uriToBitmap(uri)
                // Salva o bitmap no armazenamento interno
                newUrlImg = repository.saveImage(bitmap, nome)
            }

            //se está no modo de edição
            if(idProduto > 0L){
                repository.deleteImage(produto.value?.urlImg)
                val updatedProduto = Produto(idProduto, nome, preco, descricao, newUrlImg)
                repository.update(updatedProduto)
            }else{
                val newProduto = Produto(nome = nome, preco = preco, descricao = descricao, urlImg = newUrlImg)
                repository.insert(newProduto)
            }
        }
    }

    fun delete(produto: Produto) {
        viewModelScope.launch {
            repository.deleteImage(produto.urlImg)
            repository.delete(produto)
        }
    }
}