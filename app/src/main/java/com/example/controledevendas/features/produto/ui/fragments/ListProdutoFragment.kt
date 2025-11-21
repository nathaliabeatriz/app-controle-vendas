package com.example.controledevendas.features.produto.ui.fragments

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
import com.example.controledevendas.databinding.ProdutoFragmentListBinding
import com.example.controledevendas.features.produto.ui.ProdutoAdapter
import com.example.controledevendas.features.produto.ui.ProdutoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class ListProdutoFragment: Fragment() {
    private val viewModel: ProdutoViewModel by viewModels()
    private var _binding: ProdutoFragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var produtoAdapter: ProdutoAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.produto_fragment_list, container, false)
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
        binding.buttonAddProduto.setOnClickListener {
            val action = ListProdutoFragmentDirections.actionListProductsToSaveProduct(
                idProduto = 0L
            )
            findNavController().navigate(action)
        }
    }

    private fun setupAdapter(){
        produtoAdapter = ProdutoAdapter(
            onDeleteClicked = {produto ->
                viewModel.delete(produto)
            }, onEditClicked = { produto ->
                val action = ListProdutoFragmentDirections.actionListProductsToSaveProduct(
                    idProduto = produto.idProduto
                )
                findNavController().navigate(action)
            },
            onUpdateEstoqueClicked = { produto ->
                val action = ListProdutoFragmentDirections.actionListProductsToSaveEntrada(
                    idProduto = produto.idProduto
                )
                findNavController().navigate(action)
            }
        )
    }

    private fun setupRecyclerView() {
        binding.recyclerViewClients.adapter = produtoAdapter
        binding.recyclerViewClients.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.produtoComMovimentacoes.collect { allProdutos ->
                    produtoAdapter.submitList(allProdutos)
                }
            }
        }
    }
}