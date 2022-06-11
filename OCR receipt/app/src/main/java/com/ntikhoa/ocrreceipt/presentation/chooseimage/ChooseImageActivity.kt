package com.ntikhoa.ocrreceipt.presentation.chooseimage

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.ntikhoa.ocrreceipt.business.domain.utils.Constants
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.business.usecase.ProcessImageUseCase
import com.ntikhoa.ocrreceipt.business.getOutputDir
import com.ntikhoa.ocrreceipt.databinding.ActivityChooseImageBinding
import com.ntikhoa.ocrreceipt.presentation.TakePhotoActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.opencv.android.Utils
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ChooseImageActivity : AppCompatActivity() {

    private var _binding: ActivityChooseImageBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null

    private val processImg = ProcessImageUseCase()

    private val viewModel by viewModels<ChooseImageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChooseImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.state.observe(this) { dataState ->
            dataState.bitmap?.let {
                imageUri = getImageUri(applicationContext, it)
                binding.ivReceipt.setImageBitmap(it)
            }
        }

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

            fabDummy.setOnClickListener {
                val mat = Utils.loadResource(applicationContext, R.drawable.receipt2)
                viewModel.onTriggerEvent(ChooseImageEvent.ProcessMatImageEvent(mat))

//                CoroutineScope(Dispatchers.Main).launch {
//                    val mat = Utils.loadResource(applicationContext, R.drawable.receipt2)
//                    viewModel.onTriggerEvent(ChooseImageEvent.ProcessMatImageEvent)
//
//                    binding.cvLoading.visibility = View.VISIBLE
//
//                    val mat = withContext(Dispatchers.IO) {
//                        Utils.loadResource(applicationContext, R.drawable.receipt2)
//                    }
//                    val img = withContext(Dispatchers.Default) {
//                        processImg(mat)
//                    }
//                    //imageUri = getImageUri(applicationContext, img)
//                    //binding.ivReceipt.setImageBitmap(img)
//                    binding.cvLoading.visibility = View.GONE
//                }
            }

            btnProcessImage.setOnClickListener {
                lateinit var bitmap: Bitmap
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                } else {
                    val source: ImageDecoder.Source =
                        ImageDecoder.createSource(contentResolver, imageUri!!)
                    bitmap = ImageDecoder.decodeBitmap(source)
                }
                viewModel.onTriggerEvent(ChooseImageEvent.ProcessBitmapImageEvent(bitmap))

//                CoroutineScope(Dispatchers.Main).launch {
//                binding.cvLoading.visibility = View.VISIBLE
//
//                val bitmap = withContext(Dispatchers.IO) {
//                    lateinit var bitmap: Bitmap
//                    if (Build.VERSION.SDK_INT < 28) {
//                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
//                    } else {
//                        val source: ImageDecoder.Source =
//                            ImageDecoder.createSource(contentResolver, imageUri!!)
//                        bitmap = ImageDecoder.decodeBitmap(source)
//                    }
//                    bitmap
//                }
//
//                val img = withContext(Dispatchers.Default) {
//                    processImg(bitmap)
//                }
//                //imageUri = getImageUri(applicationContext, img)
//                //binding.ivReceipt.setImageBitmap(img)
//                binding.cvLoading.visibility = View.GONE
//            }
//        }
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
            e.printStackTrace()
        }

        return path.toUri()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}