package com.ntikhoa.ocrreceipt

import android.graphics.Rect

class MyText(
    var text: String,
    var rect: Rect
) {
    override fun toString(): String {
        return (text + "    :    " + rect.top)
    }
}