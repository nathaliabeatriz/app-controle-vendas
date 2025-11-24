package com.example.controledevendas.features.venda.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.controledevendas.R
import com.example.controledevendas.databinding.VendaFragmentSelecionarClienteBinding
import com.example.controledevendas.features.produto.ui.ProdutoAdapter
import com.example.controledevendas.features.produto.ui.fragments.ListProdutoFragmentDirections
import com.example.controledevendas.features.venda.ui.ClienteSelectionAdapter
import com.example.controledevendas.features.venda.ui.VendaViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelecionarClienteVendaFragment: Fragment() {
    private val viewModel: VendaViewModel by hiltNavGraphViewModels(R.id.navigation_make_sale)
    private lateinit var clienteAdapter: ClienteSelectionAdapter
    private var _binding: VendaFragmentSelecionarClienteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.venda_fragment_selecionar_cliente, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupRecyclerView()
        observeViewModel()
        insertVendaListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAdapter(){
        clienteAdapter = ClienteSelectionAdapter(
            onClienteSelected = { cliente ->
                viewModel.setCliente(cliente)
            }
        )
    }

    private fun setupRecyclerView() {
        binding.recyclerViewClients.adapter = clienteAdapter
        binding.recyclerViewClients.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allClientes.collect { allClientes ->
                    clienteAdapter.submitList(allClientes)
                }
            }
        }
    }

    private fun insertVendaListeners(){
        binding.buttonContinue.setOnClickListener {
            val action = SelecionarClienteVendaFragmentDirections.actionVendaSelectClientToVendaSelectProduct()
            findNavController().navigate(action)
        }
    }
}