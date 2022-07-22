package com.ntikhoa.ocrreceipt.presentation

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.extensions.ExtensionMode
import androidx.camera.extensions.ExtensionsManager
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.ntikhoa.ocrreceipt.business.domain.utils.Constants
import com.ntikhoa.ocrreceipt.business.getOutputDir
import com.ntikhoa.ocrreceipt.databinding.ActivityTakePhotoBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class TakePhotoActivity : AppCompatActivity() {

    private var _binding: ActivityTakePhotoBinding? = null
    private val binding get() = _binding!!

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var outputDir: File
    private lateinit var cameraProvider: ProcessCameraProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTakePhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        }
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

        imageCapture.takePicture(outputOption,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    runOnUiThread {
                        cameraProvider.unbindAll()
                        val savedUri = Uri.fromFile(photoFile)
                        val resultIntent = Intent()
                        resultIntent.putExtra(Constants.EXTRA_IMAGE_URI, savedUri)
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(applicationContext, exception.message, Toast.LENGTH_SHORT).show()
                }
            })
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
        val cameraProviderFuture = ProcessCameraProvider.getInstance(applicationContext)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val extensionsManagerFuture =
                ExtensionsManager.getInstanceAsync(applicationContext, cameraProvider)
            extensionsManagerFuture.addListener({

                val extensionsManager = extensionsManagerFuture.get()
                val preview = Preview.Builder()
                    .build()
                preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

                imageCapture = ImageCapture.Builder().build()
                var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraSelector = setCameraMode(extensionsManager, cameraSelector, ExtensionMode.HDR)
                cameraSelector =
                    setCameraMode(extensionsManager, cameraSelector, ExtensionMode.NIGHT)

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        this as LifecycleOwner, cameraSelector, preview, imageCapture
                    )
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                    println(e.message)
                }

            }, mainExecutor)
        }, mainExecutor)
    }

    private fun setCameraMode(
        extensionsManager: ExtensionsManager,
        cameraSelector: CameraSelector,
        mode: Int
    ): CameraSelector {
        if (extensionsManager.isExtensionAvailable(
                cameraSelector,
                mode
            )
        ) {
            cameraProvider.unbindAll()
            // Retrieve extension enabled camera selector
            return extensionsManager.getExtensionEnabledCameraSelector(
                cameraSelector,
                mode
            )
        }
        return cameraSelector
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