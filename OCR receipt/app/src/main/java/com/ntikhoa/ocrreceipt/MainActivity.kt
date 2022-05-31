package com.ntikhoa.ocrreceipt

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.ntikhoa.ocrreceipt.databinding.ActivityMainBinding
import java.io.FileNotFoundException


class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    val RESULT_LOAD_IMG = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCapture.setOnClickListener {
//            val photoPickerIntent = Intent(Intent.ACTION_PICK)
//            photoPickerIntent.type = "image/*"
//            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG)


            val cameraIntent = Intent(applicationContext, TakePhotoActivity::class.java)
            startActivityForResult(cameraIntent, RESULT_LOAD_IMG)
        }
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            try {
//                data?.data?.let {
//                    getTextFromImage(it)
//                }


                data?.getParcelableExtra<Uri>("uri")?.let {
                    getTextFromImage(it)
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show()
        }
    }

    private fun getTextFromImage(imageUri: Uri) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val inputImage = InputImage.fromFilePath(this, imageUri)

        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->

                val ocr = OCRUseCase1()
                val text = ocr(visionText)
                binding.tvOcrResult.text = text

//                val ocr = OCRUseCase()
//                val text = ocr(visionText)
//                binding.tvOcrResult.text = text


//                var textString = ""
//                for (textBlock in visionText.textBlocks) {
//                    val text = textBlock.text + " " +
//                            textBlock.boundingBox?.top + " " +
//                            textBlock.boundingBox?.left + " " +
//                            textBlock.boundingBox?.width() + " " +
//                            textBlock.boundingBox?.height()
//                    textString += text + "\n\n"
//                }
//                binding.tvOcrResult.text = visionText.text.replace("\n", "\n\n")
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