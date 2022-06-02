package com.ntikhoa.ocrreceipt.business

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.ImageProxy
import com.ntikhoa.ocrreceipt.R
import org.opencv.android.Utils
import org.opencv.core.Mat
import java.io.File
import java.nio.ByteBuffer

fun convertToBitmap(mat: Mat): Bitmap {
    val bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(mat, bitmap)
    return bitmap
}

fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val planeProxy = image.planes[0]
    val buffer: ByteBuffer = planeProxy.buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

fun Activity.getOutputDir(): File {
    val mediaDir = externalMediaDirs.firstOrNull()?.let { file ->
        return@let File(file, resources.getString(R.string.app_name)).apply {
            mkdirs()
        }
    }
    return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
}