package com.example.controledevendas.features.produto.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.controledevendas.R
import com.example.controledevendas.core.data.relations.ProdutoMovimentacao
import com.example.controledevendas.databinding.ProdutoComponentItemBinding
import com.example.controledevendas.features.produto.data.Produto
import java.io.File

class ProdutoAdapter(private val onProdutoClicked: ((Produto) -> Unit)? = null,
    private val onDeleteClicked: ((Produto) -> Unit),
    private val onEditClicked: ((Produto) -> Unit),
    private val onUpdateEstoqueClicked: ((Produto) -> Unit)):
    ListAdapter<ProdutoMovimentacao, ProdutoAdapter.ProdutoViewHolder>(DiffCallback()) {

    class ProdutoViewHolder(private val binding: ProdutoComponentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(relation: ProdutoMovimentacao, clickListener: ((Produto) -> Unit)?, onDelete: (Produto) -> Unit, onEdit: (Produto) -> Unit, onUpdateEstoque: (Produto) -> Unit) {
            binding.produto = relation.produto
            var sum = 0
            relation.movimentacoes.forEach { movimentacao ->
                sum += movimentacao.movimento
            }
            binding.textViewQtd.text = "${sum} unidades"

            if(clickListener != null){
                binding.root.setOnClickListener {
                    clickListener(relation.produto)
                }
            }else {
                // Garanta que não há listener (para o caso de uma View reciclada ter um)
                binding.root.setOnClickListener(null)
                binding.root.isClickable = false
                binding.buttonOptions.setOnClickListener { clickedView ->
                    showPopUpMenu(clickedView, relation.produto, onDelete, onEdit, onUpdateEstoque)
                }
                loadProdutoImage(relation.produto)
            }

            binding.executePendingBindings()
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

        private fun showPopUpMenu(clickedView: View, produto: Produto, onDelete: (Produto) -> Unit, onEdit: (Produto) -> Unit, onUpdateEstoque: (Produto) -> Unit){
            val popup = PopupMenu(clickedView.context, clickedView)

            popup.inflate(R.menu.product_menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_editar -> {
                        onEdit(produto)
                        true
                    }
                    R.id.menu_excluir -> {
                        onDelete(produto)
                        true
                    }
                    R.id.menu_atualizar_estoque -> {
                        onUpdateEstoque(produto)
                        true
                    }
                    else -> false
                }
            }

            popup.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        // Infla o layout usando o Data Binding
        val binding = ProdutoComponentItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProdutoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        // Pega o Produto da posição atual
        val produto = getItem(position)
        holder.bind(produto, onProdutoClicked, onDeleteClicked, onEditClicked, onUpdateEstoqueClicked)
    }

    private class DiffCallback : DiffUtil.ItemCallback<ProdutoMovimentacao>() {
        override fun areItemsTheSame(oldItem: ProdutoMovimentacao, newItem: ProdutoMovimentacao): Boolean {
            return oldItem.produto.idProduto == newItem.produto.idProduto
        }

        // Verifica se o conteúdo mudou (para atualizar a UI do item)
        override fun areContentsTheSame(oldItem: ProdutoMovimentacao, newItem: ProdutoMovimentacao): Boolean {
            return oldItem == newItem
        }
    }
}