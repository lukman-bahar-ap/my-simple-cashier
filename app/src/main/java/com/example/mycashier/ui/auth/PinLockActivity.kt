package com.example.mycashier.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.mycashier.MainActivity
import com.example.mycashier.R
import com.example.mycashier.databinding.ActivityPinLockBinding

class PinLockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPinLockBinding
    private val CORRECT_PIN = "1234"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_pin_lock)
        binding.buttonEnter.setOnClickListener {
            checkPinAndProceed()
        }

        binding.editTextPin.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkPinAndProceed()
                true
            } else {
                false
            }
        }
    }

    private fun checkPinAndProceed() {
        val enteredPin = binding.editTextPin.text.toString()

        if (enteredPin == CORRECT_PIN) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            binding.inputLayoutPin.error = "PIN salah. Silakan coba lagi."
            binding.editTextPin.text?.clear()
            Toast.makeText(this, "PIN salah!", Toast.LENGTH_SHORT).show()
        }
    }
}