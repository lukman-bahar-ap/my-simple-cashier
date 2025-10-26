package com.example.mycashier.ui.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mycashier.data.Product
import com.example.mycashier.databinding.ListItemMenuBinding

class MenuListAdapter(
    private val onAddClick: (Product) -> Unit,
    private val onRemoveClick: (Product) -> Unit,
    // Map untuk menyimpan status jumlah order (ID Produk -> Jumlah)
    var orderQuantities: Map<Long, Int>
) : ListAdapter<Product, MenuListAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ListItemMenuBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = getItem(position)
        // Dapatkan jumlah order dari map yang di-inject
        val quantity = orderQuantities[item.id] ?: 0
        holder.bind(item, quantity, onAddClick, onRemoveClick)
    }

    class ProductViewHolder(private val binding: ListItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            product: Product,
            quantity: Int,
            onAddClick: (Product) -> Unit,
            onRemoveClick: (Product) -> Unit
        ) {
            binding.product = product
            binding.orderQuantity = quantity // Set Data Binding untuk quantity

            binding.buttonAdd.setOnClickListener { onAddClick(product) }
            binding.buttonRemove.setOnClickListener { onRemoveClick(product) }

            binding.executePendingBindings() // Update UI segera
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            // Kita hanya perlu cek apakah data Product berubah
            return oldItem == newItem
        }
    }
}