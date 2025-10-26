package com.example.mycashier

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
// import androidx.lifecycle.Transformations TIDAK DIPERLUKAN LAGI
import androidx.lifecycle.map // Import ekstensi fungsi map
import androidx.lifecycle.viewModelScope
import com.example.mycashier.data.AppDatabase
import com.example.mycashier.data.CartItem
import com.example.mycashier.data.PosDao
import com.example.mycashier.data.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class KasirViewModel(application: Application) : AndroidViewModel(application) {

    private val posDao: PosDao

    val allProducts: LiveData<List<Product>>
    val cartItems: LiveData<List<CartItem>>
    val totalAmount: LiveData<Double>

    init {
        posDao = AppDatabase.getDatabase(application).posDao()
        allProducts = posDao.getAllProducts()
        cartItems = posDao.getAllCartItems()

        // use LIVE DATA EXTENSION FUNCTION (.map)
        totalAmount = cartItems.map { items ->
            items.sumOf { it.price * it.quantity }
        }
    }

    fun incrementCartItemQuantity(cartItem: CartItem) {
        viewModelScope.launch(Dispatchers.IO) {
            val newQuantity = cartItem.quantity + 1
            val updatedItem = cartItem.copy(quantity = newQuantity)
            posDao.updateCartItem(updatedItem)
        }
    }

    fun decrementCartItemQuantity(cartItem: CartItem) {
        viewModelScope.launch(Dispatchers.IO) {
            if (cartItem.quantity > 1) {
                val newQuantity = cartItem.quantity - 1
                val updatedItem = cartItem.copy(quantity = newQuantity)
                posDao.updateCartItem(updatedItem)
            } else {
                posDao.deleteCartItem(cartItem)
            }
        }
    }

    fun addItemToCart(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingItem = posDao.findCartItemByProductId(product.id)
            if (existingItem != null) {
                existingItem.quantity++
                posDao.updateCartItem(existingItem)
            } else {
                val newItem = CartItem(
                    productId = product.id,
                    productName = product.name,
                    category = product.category,
                    quantity = 1,
                    price = product.price
                )
                posDao.insertCartItem(newItem)
            }
        }
    }

    fun removeItemFromCart(product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingItem = posDao.findCartItemByProductId(product.id)

            if (existingItem != null) {
                if (existingItem.quantity > 1) {
                    existingItem.quantity--
                    posDao.updateCartItem(existingItem)
                } else {
                    posDao.deleteCartItem(existingItem)
                }
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch(Dispatchers.IO) {
            posDao.clearCart()
        }
    }
}
