package com.example.mycashier.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val productName: String,
    val category: String,
    var quantity: Int,
    val price: Double // Harga satuan saat ditambahkan
){
    // Properti turunan untuk menghitung subtotal
    val subtotal: Double
        get() = quantity * price
}
