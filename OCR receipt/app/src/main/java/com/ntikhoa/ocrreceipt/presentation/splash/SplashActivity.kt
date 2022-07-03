package com.ntikhoa.ocrreceipt.presentation.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.ntikhoa.ocrreceipt.business.repeatLifecycleFlow
import com.ntikhoa.ocrreceipt.databinding.ActivitySplashBinding
import com.ntikhoa.ocrreceipt.presentation.auth.LoginActivity
import com.ntikhoa.ocrreceipt.presentation.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private var _binding: ActivitySplashBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repeatLifecycleFlow {
            viewModel.state.collectLatest {
                when (it) {
                    is SplashState.AutoLoginSuccess -> {
                        val mainIntent = Intent(applicationContext, HomeActivity::class.java)
                        startActivity(mainIntent)
                        finish()
                    }
                    is SplashState.AutoLoginFail -> {
                        val loginIntent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(loginIntent)
                        finish()
                    }
                    else -> {}
                }
            }
        }

        viewModel.onTriggerEvent(SplashEvent.AutoLogin)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelJobs()
        _binding = null
    }
}