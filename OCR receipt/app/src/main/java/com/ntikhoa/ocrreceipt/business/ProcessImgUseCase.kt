package com.ntikhoa.ocrreceipt.business

import android.graphics.Bitmap
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class ProcessImgUseCase {

    init {
        OpenCVLoader.initDebug()
    }

    suspend operator fun invoke(bitmap: Bitmap): Bitmap {
        val mat = bitmapToMat(bitmap)

        val grayMat = Mat()
        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_RGB2GRAY)

        val bwMat = binarization(grayMat)

        return convertToBitmap(bwMat)
    }

    private suspend fun bitmapToMat(bitmap: Bitmap): Mat {
        val mat = Mat()
        val bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Utils.bitmapToMat(bmp32, mat)
        return mat
    }

    private suspend fun binarization(graySrc: Mat): Mat {
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

    private suspend fun removeShadow(mat: Mat): Mat {

        val rgbPlanes = ArrayList<Mat>()
        Core.split(mat, rgbPlanes)
        val normPlanes = ArrayList<Mat>()
        for (plane in rgbPlanes) {
            val dilatedImg = Mat()
            Imgproc.dilate(
                plane, dilatedImg,
                Imgproc.getStructuringElement(
                    Imgproc.MORPH_CROSS, Size(7.0, 7.0)
                )
            )
            val bgImg = Mat()
            Imgproc.medianBlur(dilatedImg, bgImg, 21)
            val absDiffMat = Mat()
            Core.absdiff(plane, bgImg, absDiffMat)

            val diffImg = Mat()
            Core.subtract(
                Mat(absDiffMat.rows(), absDiffMat.cols(), CvType.CV_8U, Scalar.all(255.0)),
                absDiffMat,
                diffImg
            )
            val normImg = Mat()
            Core.normalize(diffImg, normImg, 0.0, 0.0, Core.NORM_MINMAX, CvType.CV_8UC1)
            normPlanes.add(normImg)
        }
        val result = Mat()
        Core.merge(normPlanes, result)
        return result
    }
}