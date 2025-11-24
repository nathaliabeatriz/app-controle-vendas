package com.example.controledevendas.features.venda.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.controledevendas.databinding.VendaComponentSelecaoClienteBinding
import com.example.controledevendas.features.cliente.data.Cliente

class ClienteSelectionAdapter(private val onClienteSelected: (Cliente) -> Unit): ListAdapter<Cliente, ClienteSelectionAdapter.ViewHolder>(DiffCallback()) {
    private var selectedPosition = -1

    inner class ViewHolder(private val binding: VendaComponentSelecaoClienteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cliente: Cliente, position: Int) {
            binding.cliente = cliente

            binding.radioButtonSelect.isChecked = (position == selectedPosition)

            // Configura o clique no item inteiro
            binding.root.setOnClickListener {
                val previousItem = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousItem)
                notifyItemChanged(selectedPosition)
                onClienteSelected(cliente)
            }

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = VendaComponentSelecaoClienteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cliente = getItem(position)
        holder.bind(cliente, position)
    }

    class DiffCallback : DiffUtil.ItemCallback<Cliente>() {
        override fun areItemsTheSame(oldItem: Cliente, newItem: Cliente) = oldItem.idCliente == newItem.idCliente
        override fun areContentsTheSame(oldItem: Cliente, newItem: Cliente) = oldItem == newItem
    }
}