package com.example.mycashier.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PosDao {

    @Insert
    suspend fun insertProduct(product: Product)

    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): LiveData<List<Product>>

    @Insert
    suspend fun insertCartItem(cartItem: CartItem)

    @Update
    suspend fun updateCartItem(cartItem: CartItem)

    @Query("SELECT * FROM cart_items WHERE productId = :productId LIMIT 1")
    suspend fun findCartItemByProductId(productId: Long): CartItem?

    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): LiveData<List<CartItem>>

    //select by category
    @Query("SELECT * FROM products WHERE category = :category")
    fun getProductsByCategory(category: String): LiveData<List<Product>>

    @Delete // BARU: Fungsi untuk menghapus item
    suspend fun deleteCartItem(cartItem: CartItem)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}