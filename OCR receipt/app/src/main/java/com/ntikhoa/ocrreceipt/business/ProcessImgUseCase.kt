package com.ntikhoa.ocrreceipt.business

import android.graphics.Bitmap
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class ProcessImgUseCase {

    //Init opencv library
    init {
        OpenCVLoader.initDebug()
    }

    /**
     * Remove shadow (Division)
     * Sharp image
     * Binarization
     * Dilation (thicc font)
     * Rotation and deskewing (optional)
     */

    suspend operator fun invoke(bitmap: Bitmap): Bitmap {
        var mat = bitmapToMat(bitmap)
        val grayMat = Mat()
        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_RGB2GRAY)
        mat = division(grayMat)
        mat = sharp(mat)
        mat = binarization(mat)
        mat = dilation(mat)
        return convertToBitmap(mat)
    }

    suspend operator fun invoke(mat: Mat): Bitmap {
//        val mat = bitmapToMat(bitmap)

        val grayMat = Mat()
        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_RGB2GRAY)
        var mat = division(grayMat)
        mat = sharp(mat)
        mat = binarization(mat)
        mat = dilation(mat)
        return convertToBitmap(mat)
    }

    private suspend fun bitmapToMat(bitmap: Bitmap): Mat {
        val mat = Mat()
        val bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Utils.bitmapToMat(bmp32, mat)
        return mat
    }

    //remove shadow
    private suspend fun division(graySrc: Mat): Mat {
        val mat = Mat()
        Imgproc.GaussianBlur(graySrc, mat, Size(95.0, 95.0), 0.0)
        Core.divide(graySrc, mat, mat, 255.0)
        return mat
    }

    private suspend fun sharp(graySrc: Mat): Mat {
        val radius = 5.0
        val amount = 2.0

        val mat = Mat()
        val blurred = Mat()
        Imgproc.GaussianBlur(graySrc, blurred, Size(0.0, 0.0), radius)
        Core.addWeighted(graySrc, amount + 1, blurred, -amount, 0.0, mat)
        return mat
    }

    private suspend fun binarization(graySrc: Mat): Mat {
        val dstMat = Mat()
        //127.0
        Imgproc.threshold(
            graySrc,
            dstMat,
            127.0,
            255.0,
            Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU
        )
        return dstMat
    }

    //thicc font
    private suspend fun dilation(graySrc: Mat): Mat {
        val mat = Mat()
        Core.bitwise_not(graySrc, mat)
        val kernal = Mat(2, 2, CvType.CV_8UC1, Scalar(1.0))
        Imgproc.dilate(mat, mat, kernal, Point(-1.0, -1.0), 1)
        Core.bitwise_not(mat, mat)
        return mat
    }

    private suspend fun convertToBitmap(mat: Mat): Bitmap {
        val bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(mat, bitmap)
        return bitmap
    }

    private suspend fun removeBorder(graySrc: Mat): Mat {
        val contours = ArrayList<MatOfPoint>()
        Imgproc.findContours(
            graySrc,
            contours,
            Mat(),
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_SIMPLE
        )
        println(contours.joinToString {
            Imgproc.contourArea(it).toString()
        })
        contours.sortByDescending {
            Imgproc.contourArea(it)
        }
        val croppedRect = Imgproc.boundingRect(contours[0])
        println(contours.joinToString {
            Imgproc.contourArea(it).toString()
        })
        return Mat(graySrc, croppedRect)
    }
}

//fun cc() {
//    val matFirst = Mat()
//    Core.multiply(graySrc, Scalar(1.5 + 1), matFirst)
//    val matSecond = Mat()
//    Core.multiply(smooth, Scalar(-1.5), matSecond)
//    Core.add(matFirst, matSecond, mat)
//    for (i in 0 until mat.rows()) {
//        for (j in 0 until mat.cols()) {
//            if (mat.get(i, j)[0] < 0) {
//                mat.get(i, j)[0] = 0.0
//            }
//        }
//    }
//    for (i in 0 until mat.rows()) {
//        for (j in 0 until mat.cols()) {
//            if (mat.get(i, j)[0] > 255) {
//                mat.get(i, j)[0] = 255.0
//            }
//        }
//    }
//}