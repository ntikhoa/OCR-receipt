package com.ntikhoa.ocrreceipt.business.domain.utils

import android.Manifest


object Constants {

    const val FILE_NAME_FORMAT = "yy-MM-dd-HH-mm-ss-SSS"
    const val REQUEST_CODE_PERMISSIONS = 100
    val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    const val EXTRA_IMAGE_URI = "extra_image_uri"
    const val BASE_URL = "https://rpa-voucher-exchange.herokuapp.com"
    const val DATASTORE_NAME = "ocr_receipt_auth"
    const val UNKNOWN_ERROR = "Unknown Error"
    const val DATASTORE_VALUE_NOT_FOUND = "datastore_value_not_found"
    const val DATASTORE_TOKEN_KEY = "datastore_token_key"
    const val DATASTORE_NAME_KEY = "datastore_name_key"
    const val INVALID_TOKEN = "invalid_token"
}