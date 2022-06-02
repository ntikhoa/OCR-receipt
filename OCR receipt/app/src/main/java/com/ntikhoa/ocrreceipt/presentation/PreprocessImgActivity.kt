package com.ntikhoa.ocrreceipt.presentation

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.databinding.ActivityPreprocessImgBinding
import kotlinx.coroutines.*
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.*


class PreprocessImgActivity : AppCompatActivity() {

    private var _binding: ActivityPreprocessImgBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPreprocessImgBinding.inflate(layoutInflater)
        setContentView(binding.root)

        OpenCVLoader.initDebug()

        val mat = Utils.loadResource(applicationContext, R.drawable.ic_launcher_background)
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            val bitmap = withContext(Dispatchers.Default) {
                val grayMat = Mat()
                Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_RGB2GRAY)

//                val cc = removeShadow(grayMat)
//                val dstMat = removeBorder(bwMat)
                val bwMat = binarization(grayMat)

                convertToBitmap(bwMat)
            }
            binding.llPreprocessing.visibility = View.GONE
            binding.ivReceipt.setImageBitmap(bitmap)
            delay(2000)

            val savedUri = getImageUri(applicationContext, bitmap)
            val resultIntent = Intent()
            resultIntent.putExtra("uri", savedUri)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun binarization(graySrc: Mat): Mat {
        val dstMat = Mat()

//        Imgproc.adaptiveThreshold(
//            graySrc,
//            dstMat,
//            255.0,
//            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
//            Imgproc.THRESH_BINARY,
//            11,
//            2.0
//        )

        //127.0
        Imgproc.threshold(graySrc, dstMat, 127.0, 255.0, Imgproc.THRESH_BINARY)
        return dstMat
    }

    private fun convertToBitmap(mat: Mat): Bitmap {
        val bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(mat, bitmap)
        return bitmap
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(
                inContext.contentResolver,
                inImage,
                getString(R.string.app_name),
                null
            )
        return Uri.parse(path)
    }
}