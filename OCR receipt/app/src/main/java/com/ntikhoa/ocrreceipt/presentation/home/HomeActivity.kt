package com.ntikhoa.ocrreceipt.presentation.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.databinding.ActivityHomeBinding
import com.ntikhoa.ocrreceipt.presentation.SessionManager
import com.ntikhoa.ocrreceipt.presentation.TakePhotoActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            tvHello.text = viewModel.getFirstName()

            cardPhoto.setOnClickListener {
                val intent = Intent(applicationContext, TakePhotoActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}