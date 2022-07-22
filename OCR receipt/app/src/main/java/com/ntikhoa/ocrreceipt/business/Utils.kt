package com.ntikhoa.ocrreceipt.business

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageProxy
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.business.domain.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*

fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val planeProxy = image.planes[0]
    val buffer: ByteBuffer = planeProxy.buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)

    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    return Bitmap.createBitmap(
        bitmap,
        0,
        0,
        bitmap.width,
        bitmap.height,
        Matrix().apply { postRotate(image.imageInfo.rotationDegrees.toFloat()) },
        true
    )
}

fun Activity.getOutputDir(): File {
    val mediaDir = externalMediaDirs.firstOrNull()?.let { file ->
        return@let File(file, resources.getString(R.string.app_name)).apply {
            mkdirs()
        }
    }
    return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
}

fun ViewModel.getImageUri(outputDir: File, image: Bitmap): File {
    val path = File(
        outputDir,
        SimpleDateFormat(
            Constants.FILE_NAME_FORMAT,
            Locale.getDefault()
        ).format(System.currentTimeMillis()) + ".jpg"
    )
    try {
        val out = FileOutputStream(path)
        image.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.flush()
        out.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return path
}

fun AppCompatActivity.repeatLifecycleFlow(flowCollect: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED, flowCollect)
    }
}

fun Fragment.repeatLifecycleFlow(flowCollect: suspend CoroutineScope.() -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED, flowCollect)
    }
}