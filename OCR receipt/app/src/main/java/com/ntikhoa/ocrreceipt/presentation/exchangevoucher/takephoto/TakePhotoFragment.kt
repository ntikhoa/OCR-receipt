package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.takephoto

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.ImageCapture
import androidx.camera.extensions.ExtensionMode
import androidx.camera.extensions.ExtensionsManager
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.business.domain.utils.Constants
import com.ntikhoa.ocrreceipt.business.getOutputDir
import com.ntikhoa.ocrreceipt.business.imageProxyToBitmap
import com.ntikhoa.ocrreceipt.databinding.FragmentTakePhotoBinding
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.ExchangeVoucherViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class TakePhotoFragment : Fragment(R.layout.fragment_take_photo) {

    private var _binding: FragmentTakePhotoBinding? = null
    private val binding get() = _binding!!

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var outputDir: File
    private lateinit var cameraProvider: ProcessCameraProvider

    private val viewModel by activityViewModels<ExchangeVoucherViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTakePhotoBinding.bind(view)

        cameraExecutor = Executors.newSingleThreadExecutor()
        outputDir = requireActivity().getOutputDir()

        if (allPermissionGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), Constants.REQUIRED_PERMISSIONS,
                Constants.REQUEST_CODE_PERMISSIONS
            )
        }

        binding.apply {
            btnBack.setOnClickListener {
                requireActivity().onBackPressed()
            }

            btnTakePhoto.setOnClickListener {
                takePhoto()
            }

            fabDev.setOnClickListener {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                loadImageActivityResLauncher.launch(photoPickerIntent)
            }
        }
    }

    private val loadImageActivityResLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                it.data?.data?.let { imageUri ->
                    val bitmap = if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(
                            requireActivity().contentResolver,
                            imageUri
                        )
                    } else {
                        val source: ImageDecoder.Source =
                            ImageDecoder.createSource(requireActivity().contentResolver, imageUri)
                        ImageDecoder.decodeBitmap(source)
                    }

                    viewModel.image = bitmap
                    findNavController().navigate(R.id.action_takePhotoFragment_to_cropImageFragment)
                }
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

        imageCapture.takePicture(
            requireActivity().mainExecutor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    cameraProvider.unbindAll()
                    val bitmap = imageProxyToBitmap(image)
                    viewModel.image = bitmap
                    findNavController().navigate(R.id.action_takePhotoFragment_to_cropImageFragment)
                }
            })

//        imageCapture.takePicture(outputOption,
//            ContextCompat.getMainExecutor(requireContext()),
//            object : ImageCapture.OnImageSavedCallback {
//                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                    requireActivity().runOnUiThread {
//                        cameraProvider.unbindAll()
//                        val savedUri = Uri.fromFile(photoFile)
//                        val resultIntent = Intent()
//                        resultIntent.putExtra(Constants.EXTRA_IMAGE_URI, savedUri)
//                        //TODO Add Navigation
////                        setResult(AppCompatActivity.RESULT_OK, resultIntent)
////                        finish()
//                    }
//                }
//
//                override fun onError(exception: ImageCaptureException) {
//                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
//                }
//            })
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val extensionsManagerFuture =
                ExtensionsManager.getInstanceAsync(requireContext(), cameraProvider)
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
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    println(e.message)
                }

            }, requireActivity().mainExecutor)
        }, requireActivity().mainExecutor)
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

    fun allPermissionGranted() =
        Constants.REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}