package com.example.controledevendas.features.cliente.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.controledevendas.features.cliente.data.Cliente
import com.example.controledevendas.features.cliente.data.ClienteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

//o ViewModel existe principalmente para evitar perca de dados ao girar a tela por exemplo
@HiltViewModel
class ClienteViewModel @Inject constructor(private val repository: ClienteRepository) : ViewModel() {
    //observar o objeto que está sendo modificado
    private val _cliente = MutableLiveData<Cliente?>()
    val cliente: LiveData<Cliente?> = _cliente

    private var idCliente: Long = 0L

    val allClientes: StateFlow<List<Cliente>> = repository.allClientes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun loadCliente(id: Long){
        if (id > 0L) {
            idCliente = id
            viewModelScope.launch {
                repository.getClienteById(id).collect { loadedCliente ->
                    _cliente.value = loadedCliente
                }
            }
        } else {
            _cliente.value = null
        }
    }

    fun saveCliente(nome: String, telefone: String){
        viewModelScope.launch {
            if(idCliente > 0L){
                val updatedCliente = Cliente(idCliente, nome, telefone)
                repository.update(updatedCliente)
            }else{
                val newCliente = Cliente(nome = nome, telefone = telefone)
                repository.insert(newCliente)
            }
        }
    }

    fun delete(cliente: Cliente) {
        viewModelScope.launch {
            repository.delete(cliente)
        }
    }
}