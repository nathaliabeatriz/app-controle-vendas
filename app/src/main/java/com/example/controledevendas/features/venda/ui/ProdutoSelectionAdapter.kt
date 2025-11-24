package com.example.controledevendas.features.venda.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.controledevendas.R
import com.example.controledevendas.databinding.VendaComponentSelecaoProdutoBinding
import com.example.controledevendas.features.itemVenda.data.ItemVendaDto
import com.example.controledevendas.features.produto.data.Produto
import java.io.File

class ProdutoSelectionAdapter(private val onQtdAlterada: (Produto, Int) -> Unit) : ListAdapter<ItemVendaDto, ProdutoSelectionAdapter.ViewHolder>(DiffCallback()) {
    inner class ViewHolder(private val binding: VendaComponentSelecaoProdutoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(itemVendaDto: ItemVendaDto) {
            binding.produto = itemVendaDto.produtoMovimentacao.produto
            binding.textViewQtd.text = "${itemVendaDto.quantidadeEstoque} unidades"
            binding.textViewUnidades.text = itemVendaDto.quantidadeItens.toString()
            binding.root.setOnClickListener(null)
            binding.root.isClickable = false
            loadProdutoImage(itemVendaDto.produtoMovimentacao.produto)
            binding.executePendingBindings()

            insertListeners(itemVendaDto)
        }

        private fun loadProdutoImage(produto: Produto) {
            if (produto.urlImg != null) {
                // Pega o diretório de arquivos
                val context = binding.root.context
                val file = File(context.filesDir, produto.urlImg)

                // O Glide cuida de carregar o arquivo
                Glide.with(context)
                    .load(file) // Carrega o ARQUIVO
                    .placeholder(R.drawable.ic_placeholder)
                    .into(binding.imgViewProduto)

            } else {
                // Se não houver imagem, mostre um placeholder
                binding.imgViewProduto.setImageResource(R.drawable.ic_placeholder)
            }
        }

        private fun insertListeners(itemVendaDto: ItemVendaDto){
            binding.btnIncrease.setOnClickListener {
                if(itemVendaDto.quantidadeEstoque > 0){
                    onQtdAlterada(itemVendaDto.produtoMovimentacao.produto, 1)
                }
            }

            binding.btnDecrease.setOnClickListener {
                if(itemVendaDto.quantidadeItens > 0){
                    onQtdAlterada(itemVendaDto.produtoMovimentacao.produto, - 1)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Infla o layout usando o Data Binding
        val binding = VendaComponentSelecaoProdutoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Pega o Produto da posição atual
        val produto = getItem(position)
        holder.bind(produto)
    }

    private class DiffCallback : DiffUtil.ItemCallback<ItemVendaDto>() {
        override fun areItemsTheSame(oldItem: ItemVendaDto, newItem: ItemVendaDto): Boolean {
            return oldItem.produtoMovimentacao.produto.idProduto == newItem.produtoMovimentacao.produto.idProduto
        }

        // Verifica se o conteúdo mudou (para atualizar a UI do item)
        override fun areContentsTheSame(oldItem: ItemVendaDto, newItem: ItemVendaDto): Boolean {
            return oldItem == newItem
        }
    }
}