package com.example.mycashier

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mycashier.data.CartItem
import com.example.mycashier.databinding.ListItemCartBinding

class CartAdapter(
    private val onAddClick: (CartItem) -> Unit,
    private val onRemoveClick: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ListItemCartBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onAddClick, onRemoveClick)
    }

    class CartViewHolder(private val binding: ListItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            cartItem: CartItem,
            onAddClick: (CartItem) -> Unit,
            onRemoveClick: (CartItem) -> Unit
        ) {
            binding.cartItem = cartItem
            binding.buttonAddCart.setOnClickListener { onAddClick(cartItem) }
            binding.buttonRemoveCart.setOnClickListener { onRemoveClick(cartItem) }
            binding.executePendingBindings()
        }
    }

    class CartItemDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            // Membandingkan data secara keseluruhan
            return oldItem == newItem
        }
    }
}
