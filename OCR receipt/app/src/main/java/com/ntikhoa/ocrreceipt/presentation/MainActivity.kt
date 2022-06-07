package com.ntikhoa.ocrreceipt.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.ntikhoa.ocrreceipt.Constants
import com.ntikhoa.ocrreceipt.business.OCRUseCase2
import com.ntikhoa.ocrreceipt.databinding.ActivityMainBinding
import java.io.FileNotFoundException
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val inputImage = InputImage.fromFilePath(this, imageUri)

        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
//                val ocr = OCRUseCase2()
//                val text = ocr(visionText)
//                val resText = "Processed OCR:\n" +
//                        text +
//                        "\n\n" +
//                        "Raw OCR:\n" +
//                        visionText.text
                binding.tvOcrResult.setText(visionText.text)
            }
            .addOnFailureListener { e ->
                println(e.message)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}