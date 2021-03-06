package com.ntikhoa.ocrreceipt.business.usecase.scanreceipt

import android.graphics.Bitmap
import com.ntikhoa.ocrreceipt.business.domain.utils.DataState
import com.ntikhoa.ocrreceipt.business.usecase.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class ProcessImageUC {

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

    suspend operator fun invoke(bitmap: Bitmap): Flow<DataState<Bitmap>> = flow {
        emit(DataState.loading())

        var mat = bitmapToMat(bitmap)
        val grayMat = Mat()
        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_RGB2GRAY)
        division(grayMat, mat)
        sharp(mat, mat)
        binarization(mat, mat)
        dilation(mat, mat)

        Core.copyMakeBorder(
            mat,
            mat,
            150,
            150,
            150,
            150,
            Core.BORDER_CONSTANT,
            Scalar(1.0, 1.0, 1.0)
        )

        emit(DataState(data = convertToBitmap(mat)))
    }.catch {
        emit(handleUseCaseException(it))
    }

    suspend operator fun invoke(mat: Mat): Flow<DataState<Bitmap>> = flow {
        emit(DataState.loading())

        val grayMat = Mat()
        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_RGB2GRAY)
        division(grayMat, mat)
        sharp(mat, mat)
        binarization(mat, mat)
        dilation(mat, mat)

        Core.copyMakeBorder(
            mat,
            mat,
            150,
            150,
            150,
            150,
            Core.BORDER_CONSTANT,
            Scalar(1.0, 1.0, 1.0)
        )

        emit(DataState(data = convertToBitmap(mat)))
    }.catch {
        emit(handleUseCaseException(it))
    }

    private suspend fun bitmapToMat(bitmap: Bitmap): Mat {
        val mat = Mat()
        val bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Utils.bitmapToMat(bmp32, mat)
        return mat
    }

    //remove shadow
    private suspend fun division(graySrc: Mat, dst: Mat) {
        Imgproc.GaussianBlur(graySrc, dst, Size(95.0, 95.0), 0.0)
        Core.divide(graySrc, dst, dst, 255.0)
    }

    private suspend fun sharp(graySrc: Mat, dst: Mat) {
        val radius = 5.0
        val amount = 2.0

        val blurred = Mat()
        Imgproc.GaussianBlur(graySrc, blurred, Size(0.0, 0.0), radius)
        Core.addWeighted(graySrc, amount + 1, blurred, -amount, 0.0, dst)
    }

    private suspend fun binarization(graySrc: Mat, dst: Mat) {
        //127.0
        Imgproc.threshold(
            graySrc,
            dst,
            127.0,
            255.0,
            Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU
        )
    }

    //thicc font
    private suspend fun dilation(graySrc: Mat, dst: Mat) {
        Core.bitwise_not(graySrc, dst)
        //2, 2
        val kernal = Mat(2, 2, CvType.CV_8UC1, Scalar(1.0))
        Imgproc.dilate(dst, dst, kernal, Point(-1.0, -1.0), 1)
        Core.bitwise_not(dst, dst)
    }

    private suspend fun convertToBitmap(mat: Mat): Bitmap {
        val bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(mat, bitmap)
        return bitmap
    }
}