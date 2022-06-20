package com.ntikhoa.ocrreceipt.presentation.extractreceipt

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ntikhoa.ocrreceipt.business.domain.utils.Constants
import com.ntikhoa.ocrreceipt.business.repeatLifecycleFlow
import com.ntikhoa.ocrreceipt.databinding.ActivityExtractReceiptBinding
import com.ntikhoa.ocrreceipt.presentation.auth.LoginActivity
import com.ntikhoa.ocrreceipt.presentation.chooseimage.ChooseImageActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.lang.Exception

@AndroidEntryPoint
class ExtractReceiptActivity : AppCompatActivity() {

    private var _binding: ActivityExtractReceiptBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ExtractReceiptViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityExtractReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repeatLifecycleFlow {
            viewModel.state.collectLatest { dataState ->


                dataState.text?.let {
                    binding.tvOcrResult.text = it
                }
            }
        }

        binding.apply {
            btnChooseImage.setOnClickListener {
                val chooseImageIntent = Intent(applicationContext, ChooseImageActivity::class.java)
                chooseImageActivityResLauncher.launch(chooseImageIntent)
            }

            btnLogout.setOnClickListener {
                val loginIntent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(loginIntent)
                finish()
            }
        }
    }

    private val chooseImageActivityResLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                try {
                    it?.data?.getParcelableExtra<Uri>(Constants.EXTRA_IMAGE_URI)?.let { imageUri ->
                        getTextFromImage(imageUri)
                    }
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "You haven't picked an image", Toast.LENGTH_LONG).show()
            }
        }

    private fun getTextFromImage(imageUri: Uri) {
        viewModel.onTriggerEvent(ExtractReceiptEvent.ExtractReceiptImage(imageUri))
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelJobs()
        _binding = null
    }
}