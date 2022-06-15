package com.ntikhoa.ocrreceipt.presentation.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.ntikhoa.ocrreceipt.databinding.ActivityLoginBinding
import com.ntikhoa.ocrreceipt.presentation.extractreceipt.ExtractReceiptActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.onTriggerEvent(LoginEvent.Login("ntikhoa", "khoa"))

        binding.btnLogin.setOnClickListener {
            val intent = Intent(applicationContext, ExtractReceiptActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}