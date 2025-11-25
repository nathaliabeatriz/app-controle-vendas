package com.example.controledevendas.features.venda.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.controledevendas.R
import com.example.controledevendas.databinding.VendaFragmentSelecionarProdutosBinding
import com.example.controledevendas.features.venda.ui.ProdutoSelectionAdapter
import com.example.controledevendas.features.venda.ui.VendaViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.getValue

@AndroidEntryPoint
class SelecionarProdutosVendaFragment: Fragment() {
    private val viewModel: VendaViewModel by hiltNavGraphViewModels(R.id.navigation_make_sale)
    private lateinit var produtoAdapter: ProdutoSelectionAdapter
    private var _binding: VendaFragmentSelecionarProdutosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, R.layout.venda_fragment_selecionar_produtos, container, false)
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
        produtoAdapter = ProdutoSelectionAdapter{ produto, value ->
            viewModel.updateEstoque(produto, value)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewProducts.adapter = produtoAdapter
        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch{
                    viewModel.itensVenda.collect { allProdutos ->
                        produtoAdapter.submitList(allProdutos)
                    }
                }
                launch {
                    viewModel.total.collect {
                        val locale = Locale("pt", "BR")
                        val currencyFormat = NumberFormat.getCurrencyInstance(locale)
                        val formattedPreco = currencyFormat.format(it)
                        binding.textViewTotal.text = formattedPreco
                    }
                }
                launch {
                    viewModel.continuarVenda.collect { flag ->
                        binding.buttonContinue.isEnabled = flag
                        binding.buttonContinue.alpha = if (flag) 1.0f else 0.5f
                    }
                }
            }
        }
    }

    private fun insertVendaListeners(){
        binding.buttonContinue.setOnClickListener {
            val action = SelecionarProdutosVendaFragmentDirections.actionVendaSelectProductToVendaInfoPagamento()
            findNavController().navigate(action)
        }
    }
}