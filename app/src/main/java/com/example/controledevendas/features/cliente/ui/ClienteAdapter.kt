package com.example.controledevendas.features.cliente.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.controledevendas.databinding.ClienteComponentItemBinding
import com.example.controledevendas.features.cliente.data.Cliente
import com.example.controledevendas.R


class ClienteAdapter(private val onClienteClicked: ((Cliente) -> Unit)? = null,
    private val onDeleteClicked: ((Cliente) -> Unit),
    private val onEditClicked: ((Cliente) -> Unit)):
    ListAdapter<Cliente, ClienteAdapter.ClienteViewHolder>(ClienteDiffCallback()){
    class ClienteViewHolder(private val binding: ClienteComponentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cliente: Cliente, clickListener: ((Cliente) -> Unit)?, onDelete: (Cliente) -> Unit, onEdit: (Cliente) -> Unit) {
            binding.cliente = cliente

            if(clickListener != null){
                binding.root.setOnClickListener {
                    clickListener(cliente)
                }
            }else {
                // Garanta que não há listener (para o caso de uma View reciclada ter um)
                binding.root.setOnClickListener(null)
                binding.root.isClickable = false
                binding.buttonOptions.setOnClickListener { clickedView ->
                    showPopUpMenu(clickedView, cliente, onDelete, onEdit)
                }
            }
            binding.executePendingBindings()
        }

        private fun showPopUpMenu(clickedView: View, cliente: Cliente, onDelete: (Cliente) -> Unit, onEdit: (Cliente) -> Unit){
            val popup = PopupMenu(clickedView.context, clickedView)

            popup.inflate(R.menu.client_menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_editar -> {
                        onEdit(cliente)
                        true
                    }
                    R.id.menu_excluir -> {
                        onDelete(cliente)
                        true
                    }
                    else -> false
                }
            }

            popup.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        // Infla o layout usando o Data Binding
        val binding = ClienteComponentItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ClienteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        // Pega o cliente da posição atual
        val cliente = getItem(position)
        holder.bind(cliente, onClienteClicked, onDeleteClicked, onEditClicked)
    }

    private class ClienteDiffCallback : DiffUtil.ItemCallback<Cliente>() {
        override fun areItemsTheSame(oldItem: Cliente, newItem: Cliente): Boolean {
            return oldItem.idCliente == newItem.idCliente
        }

        // Verifica se o conteúdo mudou (para atualizar a UI do item)
        override fun areContentsTheSame(oldItem: Cliente, newItem: Cliente): Boolean {
            return oldItem == newItem
        }
    }
}