package com.ntikhoa.ocrreceipt.presentation

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.ntikhoa.ocrreceipt.Constants
import com.ntikhoa.ocrreceipt.business.ProcessImgUseCase
import com.ntikhoa.ocrreceipt.business.getOutputDir
import com.ntikhoa.ocrreceipt.databinding.ActivityChooseImageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class ChooseImageActivity : AppCompatActivity() {

    private var _binding: ActivityChooseImageBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null

    private val processImg = ProcessImgUseCase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChooseImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnLoadImage.setOnClickListener {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                loadImageActivityResLauncher.launch(photoPickerIntent)
            }

            btnTakePhoto.setOnClickListener {
                val cameraIntent = Intent(applicationContext, TakePhotoActivity::class.java)
                takePhotoActivityResLauncher.launch(cameraIntent)
            }

            btnProcessImage.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    val bitmap = withContext(Dispatchers.IO) {
                        lateinit var bitmap: Bitmap
                        if (Build.VERSION.SDK_INT < 28) {
                            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                        } else {
                            val source: ImageDecoder.Source =
                                ImageDecoder.createSource(contentResolver, imageUri!!)
                            bitmap = ImageDecoder.decodeBitmap(source)
                        }
                        bitmap
                    }

                    val img = withContext(Dispatchers.Default) {
                        processImg(bitmap)
                    }
                    withContext(Dispatchers.IO) {
                        imageUri = getImageUri(applicationContext, img)
                    }

                    binding.ivReceipt.setImageBitmap(img)
                }
            }

            btnDone.setOnClickListener {
                val resIntent = Intent()
                resIntent.putExtra(Constants.EXTRA_IMAGE_URI, imageUri)
                setResult(RESULT_OK, resIntent)
                finish()
            }
        }
    }

    private val loadImageActivityResLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.data?.let { imageUri ->
                    onGetImageRes(imageUri)
                }
            }
        }

    private val takePhotoActivityResLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                it.data?.getParcelableExtra<Uri>(Constants.EXTRA_IMAGE_URI)?.let { imageUri ->
                    onGetImageRes(imageUri)
                }
            }
        }

    private fun onGetImageRes(imageUri: Uri) {
        this.imageUri = imageUri
        binding.ivReceipt.setImageURI(imageUri)
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {

        val path = File(
            getOutputDir(),
            SimpleDateFormat(
                Constants.FILE_NAME_FORMAT,
                Locale.getDefault()
            ).format(System.currentTimeMillis()) + ".jpg"
        )
        try {
            val out = FileOutputStream(path)
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace();
        }

        return path.toUri()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}