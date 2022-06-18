package com.ntikhoa.ocrreceipt.presentation.extractreceipt

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.ntikhoa.ocrreceipt.business.domain.utils.Constants
import com.ntikhoa.ocrreceipt.business.repeatLifecycleFlow
import com.ntikhoa.ocrreceipt.business.usecase.ExtractReceiptUC
import com.ntikhoa.ocrreceipt.databinding.ActivityExtractReceiptBinding
import com.ntikhoa.ocrreceipt.presentation.chooseimage.ChooseImageActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import kotlin.coroutines.suspendCoroutine

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

        binding.btnChooseImage.setOnClickListener {
            val chooseImageIntent = Intent(applicationContext, ChooseImageActivity::class.java)
            chooseImageActivityResLauncher.launch(chooseImageIntent)
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
        _binding = null
    }
}