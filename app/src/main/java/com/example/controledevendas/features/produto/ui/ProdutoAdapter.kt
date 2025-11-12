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
import com.example.controledevendas.databinding.ProdutoComponentItemBinding
import com.example.controledevendas.features.produto.data.Produto
import java.io.File

class ProdutoAdapter(private val onProdutoClicked: ((Produto) -> Unit)? = null,
    private val onDeleteClicked: ((Produto) -> Unit),
    private val onEditClicked: ((Produto) -> Unit)): 
    ListAdapter<Produto, ProdutoAdapter.ProdutoViewHolder>(ProdutoDiffCallback()) {

    class ProdutoViewHolder(private val binding: ProdutoComponentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(produto: Produto, clickListener: ((Produto) -> Unit)?, onDelete: (Produto) -> Unit, onEdit: (Produto) -> Unit) {
            binding.produto = produto

            if(clickListener != null){
                binding.root.setOnClickListener {
                    clickListener(produto)
                }
            }else {
                // Garanta que não há listener (para o caso de uma View reciclada ter um)
                binding.root.setOnClickListener(null)
                binding.root.isClickable = false
                binding.buttonOptions.setOnClickListener { clickedView ->
                    showPopUpMenu(clickedView, produto, onDelete, onEdit)
                }
                loadProdutoImage(produto)
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

        private fun showPopUpMenu(clickedView: View, produto: Produto, onDelete: (Produto) -> Unit, onEdit: (Produto) -> Unit){
            val popup = PopupMenu(clickedView.context, clickedView)

            popup.inflate(R.menu.client_menu)

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
        holder.bind(produto, onProdutoClicked, onDeleteClicked, onEditClicked)
    }

    private class ProdutoDiffCallback : DiffUtil.ItemCallback<Produto>() {
        override fun areItemsTheSame(oldItem: Produto, newItem: Produto): Boolean {
            return oldItem.idProduto == newItem.idProduto
        }

        // Verifica se o conteúdo mudou (para atualizar a UI do item)
        override fun areContentsTheSame(oldItem: Produto, newItem: Produto): Boolean {
            return oldItem == newItem
        }
    }
}