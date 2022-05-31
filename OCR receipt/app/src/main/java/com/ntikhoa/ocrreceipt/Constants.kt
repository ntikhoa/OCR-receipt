package com.ntikhoa.ocrreceipt

import android.Manifest


object Constants {

    const val FILE_NAME_FORMAT = "yy-MM-dd-HH-mm-ss-SSS"
    const val REQUEST_CODE_PERMISSIONS = 100
    val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
}