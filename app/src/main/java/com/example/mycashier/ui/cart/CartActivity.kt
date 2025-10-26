package com.example.mycashier.ui.cart
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mycashier.CartAdapter
import com.example.mycashier.KasirViewModel
import com.example.mycashier.R
import com.example.mycashier.data.CartItem
import com.example.mycashier.databinding.ActivityCartBinding
import com.example.mycashier.ui.payment.PaymentActivity

class CartActivity : AppCompatActivity() {

    private val viewModel: KasirViewModel by viewModels()
    private lateinit var binding: ActivityCartBinding

    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupRecyclerView()
        toPaymentActivity()
        toMainActivity()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onAddClick = { cartItem: CartItem ->
                viewModel.incrementCartItemQuantity(cartItem)
            },
            onRemoveClick = { cartItem: CartItem ->
                viewModel.decrementCartItemQuantity(cartItem)
            }
        )

        binding.recyclerCart.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(this@CartActivity)
        }

        viewModel.cartItems.observe(this) { cartItems ->
            cartAdapter.submitList(cartItems)
        }
    }

    private fun toPaymentActivity(){
        binding.buttonCheckout.setOnClickListener {
            val intent = Intent(this, PaymentActivity::class.java)
            startActivity(intent)
        }
    }

    private fun toMainActivity(){
        binding.fabBackmenu.setOnClickListener{
            //back preveous activity
            finish()
        }
    }

}
