package com.example.controledevendas.features.pagamento.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.controledevendas.core.data.enums.StatusPagamento
import com.example.controledevendas.core.utils.formatDate
import com.example.controledevendas.databinding.PagamentoComponentItemBinding
import com.example.controledevendas.R
import com.example.controledevendas.features.cliente.data.Cliente
import com.example.controledevendas.features.pagamento.data.Pagamento
import com.example.controledevendas.features.pagamento.data.PagamentoDto
import java.text.NumberFormat
import java.util.Locale

class PagamentoAdapter(private val onUpdatePagamentoClicked: ((Pagamento) -> Unit)): ListAdapter<PagamentoDto, PagamentoAdapter.PagamentoViewHolder>(PagamentoDiffCallback()) {
    inner class PagamentoViewHolder(private val binding: PagamentoComponentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pagamentoDto: PagamentoDto, onUpdatePagamentoClicked: (Pagamento) -> Unit) {
            
            binding.textViewDataVenda.text = formatDate(pagamentoDto.pagamentoDetalhes.venda.dataVenda)
            if(pagamentoDto.nomeCliente == null){
                binding.textViewCliente.text = "Desconhecido"
            } else {
                binding.textViewCliente.text = pagamentoDto.nomeCliente
            }

            binding.textViewForma.text = pagamentoDto.pagamentoDetalhes.formaPagamento.descricao
            val locale = Locale("pt", "BR")
            val currencyFormat = NumberFormat.getCurrencyInstance(locale)
            val formattedValorTotal = currencyFormat.format(pagamentoDto.pagamentoDetalhes.venda.valorTotal)
            val formattedValorPendente = currencyFormat.format(pagamentoDto.valorPendente)
            binding.textViewValorTotal.text = formattedValorTotal
            binding.textViewValorPendente.text = formattedValorPendente

            when(pagamentoDto.pagamentoDetalhes.pagamento.status){
                StatusPagamento.PENDENTE -> {
                    binding.textViewStatus.text = "Pendente"
                    binding.textViewStatus.setTextColor(binding.root.context.getColor(R.color.red_A90C0C))
                }
                StatusPagamento.CONCLUIDO -> {
                    binding.textViewStatus.text = "Concluído"
                    binding.textViewStatus.setTextColor(binding.root.context.getColor(R.color.green_5f8829))
                }
                StatusPagamento.CANCELADO -> {
                    binding.textViewStatus.text = "Cancelado"
                    binding.textViewStatus.setTextColor(binding.root.context.getColor(R.color.gray_4a484f))
                }
            }

            binding.buttonOptions.setOnClickListener { clickedView ->
                showPopUpMenu(clickedView, pagamentoDto.pagamentoDetalhes.pagamento, onUpdatePagamentoClicked)
            }

            binding.executePendingBindings()
        }

        private fun showPopUpMenu(clickedView: View, pagamento: Pagamento, onUpdatePagamentoClicked: (Pagamento) -> Unit){
            val popup = PopupMenu(clickedView.context, clickedView)

            popup.inflate(R.menu.pagamento_menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_atualizar_pagamentor -> {
                        onUpdatePagamentoClicked(pagamento)
                        true
                    }
                    else -> false
                }
            }

            popup.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagamentoViewHolder {
        // Infla o layout usando o Data Binding
        val binding = PagamentoComponentItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PagamentoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PagamentoViewHolder, position: Int) {
        val pagamento = getItem(position)
        holder.bind(pagamento, onUpdatePagamentoClicked)
    }

    private class PagamentoDiffCallback : DiffUtil.ItemCallback<PagamentoDto>() {
        override fun areItemsTheSame(oldItem: PagamentoDto, newItem: PagamentoDto): Boolean {
            return oldItem.pagamentoDetalhes.pagamento.idPagamento == newItem.pagamentoDetalhes.pagamento.idPagamento
        }
        override fun areContentsTheSame(oldItem: PagamentoDto, newItem: PagamentoDto): Boolean {
            return oldItem == newItem
        }
    }
}