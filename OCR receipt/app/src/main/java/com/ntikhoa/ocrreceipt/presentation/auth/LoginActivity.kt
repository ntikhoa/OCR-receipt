package com.ntikhoa.ocrreceipt.presentation.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.ntikhoa.ocrreceipt.business.repeatLifecycleFlow
import com.ntikhoa.ocrreceipt.databinding.ActivityLoginBinding
import com.ntikhoa.ocrreceipt.presentation.extractreceipt.ExtractReceiptActivity
import com.ntikhoa.ocrreceipt.presentation.setVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        collectData()

        binding.apply {
            btnLogin.setOnClickListener {
                val username = etUsername.text.toString()
                val password = etPassword.text.toString()
                viewModel.onTriggerEvent(LoginEvent.Login(username, password))
            }
        }
    }

    private fun collectData() {
        repeatLifecycleFlow {
            viewModel.state.collectLatest {
                binding.progressBar.setVisibility(it.isLoading)

                when (it) {
                    is LoginState.LoginSuccess -> {
                        val intent = Intent(applicationContext, ExtractReceiptActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    is LoginState.LoginFailed -> {
                        Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}