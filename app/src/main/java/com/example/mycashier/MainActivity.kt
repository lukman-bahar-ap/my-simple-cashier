package com.example.mycashier

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mycashier.data.Product
import com.example.mycashier.databinding.ActivityMainBinding
import com.example.mycashier.ui.cart.CartActivity
import com.example.mycashier.ui.menu.MenuListAdapter


class MainActivity : AppCompatActivity() {

    private val viewModel: KasirViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var productSelectionAdapter: MenuListAdapter
    private var currentSearchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupProductSelectionRecyclerView()
        setupSearchListener()
        toCartActivity()
    }

    private fun setupProductSelectionRecyclerView() {
        productSelectionAdapter = MenuListAdapter(
            onAddClick = { product -> viewModel.addItemToCart(product) },
            onRemoveClick = { product -> viewModel.removeItemFromCart(product) },
            orderQuantities = emptyMap()
        )

        binding.recyclerProductSelection.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = productSelectionAdapter
        }

        viewModel.cartItems.observe(this) { cartItems ->
            val orderQuantitiesMap = cartItems.associate { it.productId to it.quantity }
            productSelectionAdapter.orderQuantities = orderQuantitiesMap
            filterProducts(currentSearchQuery)

            productSelectionAdapter.notifyDataSetChanged()
        }

        // Observasi daftar semua produk
        viewModel.allProducts.observe(this) {
            filterProducts(currentSearchQuery)
        }
    }

    private fun setupSearchListener() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s.toString().trim()
                filterProducts(currentSearchQuery)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterProducts(query: String) {
        val allProducts = viewModel.allProducts.value ?: emptyList()
        val lowerCaseQuery = query.lowercase()

        //filter
        val filteredList = if (query.isEmpty()) {
            allProducts
        } else {
            allProducts.filter { product ->
                product.name.lowercase().contains(lowerCaseQuery)
            }
        }

        productSelectionAdapter.submitList(filteredList)
    }

    private fun toCartActivity(){
        binding.fabCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }
}
