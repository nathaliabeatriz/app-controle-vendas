package com.example.controledevendas.features.pagamento.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.controledevendas.R
import com.example.controledevendas.databinding.PagamentoFragmentListBinding
import com.example.controledevendas.features.pagamento.ui.PagamentoAdapter
import com.example.controledevendas.features.pagamento.ui.PagamentoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class ListPagamentoFragment: Fragment() {
    private val viewModel: PagamentoViewModel by viewModels()
    private var _binding: PagamentoFragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var pagamentoAdapter: PagamentoAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.pagamento_fragment_list, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupRecyclerView()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAdapter(){
        pagamentoAdapter = PagamentoAdapter(
            onUpdatePagamentoClicked = {
                val action = ListPagamentoFragmentDirections.actionListPagamentosToUpdatePagamento(it.idPagamento)
                findNavController().navigate(action)
            }
        )
    }

    private fun setupRecyclerView() {
        binding.recyclerViewPagamentos.adapter = pagamentoAdapter
        binding.recyclerViewPagamentos.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch{
                    viewModel.pagamentosComDetalhes.collect { allPagamentos ->
                        pagamentoAdapter.submitList(allPagamentos)
                    }
                }
                launch {
                    viewModel.allParcelas.collect {
                        viewModel.loadPagamentos()
                    }
                }
            }
        }
    }
}