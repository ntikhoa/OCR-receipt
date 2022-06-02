package com.ntikhoa.ocrreceipt

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.ntikhoa.ocrreceipt.databinding.ActivityTakePhotoBinding
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.imgproc.Imgproc
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class TakePhotoActivity : AppCompatActivity() {
    private val TAG = "TakePhotoActivity"

    var _binding: ActivityTakePhotoBinding? = null
    val binding get() = _binding!!

    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var outputDir: File
    private var cameraProvider: ProcessCameraProvider? = null
    private var preview: Preview? = null
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTakePhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        OpenCVLoader.initDebug()
        cameraExecutor = Executors.newSingleThreadExecutor()
        outputDir = getOutputDir()

        if (allPermissionGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, Constants.REQUIRED_PERMISSIONS,
                Constants.REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnTakePhoto.setOnClickListener {
            takePhoto()
            bitmap?.let {
                val resultIntent = Intent()
                resultIntent.putExtra("bitmap", bitmap)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun getOutputDir(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let { file ->
            return@let File(file, resources.getString(R.string.app_name)).apply {
                mkdirs()
            }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(
            outputDir,
            SimpleDateFormat(
                Constants.FILE_NAME_FORMAT,
                Locale.getDefault()
            )
                .format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOption = ImageCapture
            .OutputFileOptions
            .Builder(photoFile)
            .build()

        imageCapture.takePicture(cameraExecutor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val bitmapImage = imageProxyToBitmap(image)
                    val mat = Mat()
                    Utils.bitmapToMat(bitmapImage, mat)

                    val grayMat = Mat()
                    Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_RGB2GRAY)
                    val bwMat = binarization(grayMat)

//                    val dstMat = removeBorder(bwMat)
//
//                    Imgproc.dilate(
//                        dstMat, dstMat,
//                        Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, Size(3.0, 3.0))
//                    )

                    val bitmap = convertToBitmap(bwMat)

                    runOnUiThread {
                        binding.ivReceipt.setImageBitmap(bitmap)
                        binding.clTakePhoto.visibility = View.GONE
                        binding.ivReceipt.visibility = View.VISIBLE
//                        val resultIntent = Intent()
//                        setResult(RESULT_OK, resultIntent)
//                        finish()
//
//                        val savedUri = Uri.fromFile(photoFile)
//                        val resultIntent = Intent()
//                        resultIntent.putExtra("uri", savedUri)
//                        setResult(RESULT_OK, resultIntent)
//                        finish()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                }
            })
    }

    private fun binarization(graySrc: Mat): Mat {
        val dstMat = Mat()
        //127.0
//        Imgproc.adaptiveThreshold(
//            graySrc,
//            dstMat,
//            255.0,
//            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
//            Imgproc.THRESH_BINARY,
//            11,
//            2.0
//        )
        Imgproc.threshold(graySrc, dstMat, 127.0, 255.0, Imgproc.THRESH_BINARY)
        return dstMat
    }

    private fun removeBorder(graySrc: Mat): Mat {
        val contours = ArrayList<MatOfPoint>()
        Imgproc.findContours(
            graySrc,
            contours,
            Mat(),
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_SIMPLE
        )
        contours.sortByDescending {
            Imgproc.contourArea(it)
        }
        val croppedRect = Imgproc.boundingRect(contours[0])
        return Mat(graySrc, croppedRect)
    }

    private fun convertToBitmap(mat: Mat): Bitmap {
        val bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(mat, bitmap)
        return bitmap
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val planeProxy = image.planes[0]
        val buffer: ByteBuffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.REQUEST_CODE_PERMISSIONS) {
            if (allPermissionGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({

            cameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder()
                .build()
            preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    this as LifecycleOwner, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                Log.d(TAG, "startCamera: Fail:", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionGranted() =
        Constants.REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                baseContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        _binding = null
    }
}