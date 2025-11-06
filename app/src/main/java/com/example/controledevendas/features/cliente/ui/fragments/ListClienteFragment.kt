package com.example.controledevendas.features.cliente.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.controledevendas.R
import com.example.controledevendas.databinding.FragmentListClientsBinding
import com.example.controledevendas.features.cliente.ui.ClienteAdapter
import com.example.controledevendas.features.cliente.ui.ClienteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class ListClienteFragment: Fragment() {
    private val viewModel: ClienteViewModel by activityViewModels()
    private var _binding: FragmentListClientsBinding? = null
    private val binding get() = _binding!!
    private lateinit var clienteAdapter: ClienteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_clients, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addButtonClick()
        setupAdapter()
        setupRecyclerView()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addButtonClick(){
        binding.buttonAddClient.setOnClickListener {
            val action = ListClienteFragmentDirections.actionListClientsToAddClient(
                idCliente = 0L
            )
            findNavController().navigate(action)
        }
    }

    private fun setupAdapter(){
        clienteAdapter = ClienteAdapter(
            onDeleteClicked = {cliente ->
                viewModel.delete(cliente)
            }, onEditClicked = { cliente ->
                val action = ListClienteFragmentDirections.actionListClientsToAddClient(
                    idCliente = cliente.idCliente
                )
                findNavController().navigate(action)
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
}