package com.example.mycashier.ui.payment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Toast
import android.util.TypedValue
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.mycashier.KasirViewModel
import com.example.mycashier.R
import com.example.mycashier.databinding.ActivityPaymentBinding
import com.example.mycashier.databinding.DialogCashInputBinding
import android.widget.EditText
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToLong
import androidx.core.content.ContextCompat

class PaymentActivity : AppCompatActivity() {

    private val viewModel: KasirViewModel by viewModels()
    private lateinit var binding: ActivityPaymentBinding
    private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    private var totalAmount: Double = 0.0
    private var paidAmount: Double = 0.0
    private var customerName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.buttonCustomerInfo.setOnClickListener {
            showCustomerNameDialog()
        }

        viewModel.totalAmount.observe(this) { total ->
            totalAmount = total
            binding.buttonProcessPayment.isEnabled = total > 0.0
            binding.radioGroupPayment.check(R.id.radio_cash)
            updatePaymentButtonState()
        }

        binding.radioGroupPayment.setOnCheckedChangeListener { _, checkedId ->
            updatePaymentButtonState()
        }

        binding.buttonProcessPayment.setOnClickListener {
            processPayment()
        }
    }

    private fun updateCustomerNameDisplay() {
        binding.buttonCustomerInfo.text = customerName
    }


    private fun showCustomerNameDialog() {
        val inputEditText = EditText(this).apply {
            setText(customerName)
            hint = "Masukkan Nama Pelanggan atau Nomor Meja"
            val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics).toInt()
            setPadding(padding, padding, padding, padding)
        }

        AlertDialog.Builder(this)
            .setTitle("Input Nama Pelanggan / Meja")
            .setView(inputEditText)
            .setPositiveButton("Simpan") { _, _ ->
                val newName = inputEditText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    customerName = newName
                } else {
                    customerName = "Pelanggan Umum"
                }
                updateCustomerNameDisplay()
                Toast.makeText(this, "Nama pelanggan diatur ke: $customerName", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Batal", null)
            .create()
            .show()
    }

    private fun updatePaymentButtonState() {
        val button = binding.buttonProcessPayment
        when (binding.radioGroupPayment.checkedRadioButtonId) {
            R.id.radio_cash -> {
                button.text = "INPUT TUNAI & PROSES"
                button.isEnabled = totalAmount > 0.0
            }
            R.id.radio_transfer -> {
                button.text = "PROSES TRANSFER BANK"
                button.isEnabled = totalAmount > 0.0
            }
            R.id.radio_qris -> {
                button.text = "GENERATE QRIS"
                button.isEnabled = totalAmount > 0.0
            }
            else -> button.isEnabled = false
        }
    }

    private fun processPayment() {
        when (binding.radioGroupPayment.checkedRadioButtonId) {
            R.id.radio_cash -> showCashInputDialog()
            R.id.radio_transfer -> simulatePayment("Transfer Bank")
            R.id.radio_qris -> simulatePayment("QRIS")
        }
    }

    private fun showCashInputDialog() {
        val dialogBinding: DialogCashInputBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this), R.layout.dialog_cash_input, null, false
        )
        dialogBinding.textChangeAmount.text = currencyFormat.format(0.0)

        //kembalian
        dialogBinding.editPaidAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                paidAmount = s.toString().toDoubleOrNull() ?: 0.0
                calculateChange(dialogBinding)
            }
        })

        // Dialog
        val dialog = AlertDialog.Builder(this)
            .setTitle("Pembayaran Tunai")
            .setView(dialogBinding.root)
            .setPositiveButton("Bayar") { _, _ ->
                if (paidAmount >= totalAmount) {
                    simulatePayment("Tunai")
                } else {
                    Toast.makeText(this, "Jumlah pembayaran kurang. Coba lagi.", Toast.LENGTH_SHORT).show()
                    showCashInputDialog()
                }
            }
            .setNegativeButton("Batal", null)
            .create()

        dialog.show()
    }

    private fun calculateChange(dialogBinding: DialogCashInputBinding) {
        val change = paidAmount - totalAmount

        val displayChange = if (change < 0) 0.0 else change
        dialogBinding.textChangeAmount.text = currencyFormat.format(displayChange)

        val color: Int
        if (change < 0) {
            color = ContextCompat.getColor(this, android.R.color.holo_red_dark)
        } else {
            color = ContextCompat.getColor(this, android.R.color.black)
        }
        dialogBinding.textChangeAmount.setTextColor(color)
    }

    private fun simulatePayment(method: String) {
        Toast.makeText(this, "Transaksi untuk $customerName. Pembayaran $method Berhasil! Transaksi Selesai.", Toast.LENGTH_LONG).show()
        viewModel.clearCart()
        finish()
    }
}
