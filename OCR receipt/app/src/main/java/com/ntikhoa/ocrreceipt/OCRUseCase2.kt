package com.ntikhoa.ocrreceipt

import android.graphics.Rect
import androidx.core.text.isDigitsOnly
import com.google.mlkit.vision.text.Text

class OCRUseCase2 {
    operator fun invoke(visionText: Text): String {
        println(visionText.text)

        val textBlocks = visionText.textBlocks
        val croppedTextBlocks = croppedData(textBlocks)
        val leftTextBlocks = removeRightData(croppedTextBlocks)
        val productNames = getProductName(leftTextBlocks)

        return productNames.joinToString {
            it + "\n\n"
        }

//        return leftTextBlocks.joinToString {
//            it.text + "                    " +
//            it.boundingBox!!.top + " " + it.boundingBox!!.left +
//                    "\n\n"
//        }
    }

    fun getProductName(textBlocks: List<Text.TextBlock>): List<String> {
        val lines = textBlocks.flatMap {
            it.text.split("\n")
        }.toMutableList()

        for (i in lines.indices.reversed()) {
            if (lines[i].isDigitsOnly()) {
                lines.removeAt(i)
            } else if (lines[i].replace(".", "").isDigitsOnly()) {
                lines.removeAt(i)
            } else if (lines[i].contains("KHUYEN") || lines[i].contains("MAI")) {
                lines.removeAt(i)
            }
        }
        return lines
    }

    fun removeRightData(textBlocks: List<Text.TextBlock>): List<Text.TextBlock> {
        val sortedTextBlock = textBlocks.sortedBy {
            it.boundingBox!!.left
        }

        val leftPosBoxes = sortedTextBlock.map {
            it.boundingBox!!.left
        }
        val dist = ArrayList<Int>()
        for (i in 0 until leftPosBoxes.size - 1) {
            dist.add(leftPosBoxes[i + 1] - leftPosBoxes[i])
        }

        println(leftPosBoxes.toString())
        println(dist.toString())
        val maxDist = dist.maxOf { it }
        val maxDistIndex = dist.indexOf(maxDist) + 1
        return sortedTextBlock.subList(0, maxDistIndex)
    }

    fun croppedData(textBlocks: List<Text.TextBlock>): List<Text.TextBlock> {

        val mapTextBlock = textBlocks.map {
            it.text
        }.toMutableList()

        mapTextBlock.add("====--")

        var start = 0
        var end = textBlocks.size

        // flag for making sure start and end is only set one time
        var startFlag = false
        var endFlag = false
        for (i in mapTextBlock.indices) {
            val tempText = mapTextBlock[i]

            if (!startFlag && tempText.contains("[*|=|:]([*|=|:])+".toRegex()) && i < end) {
                start = i + 1
                startFlag = true
                continue
            }
            if (!endFlag) {
                if (tempText.uppercase().contains("SO LUONG")
                    || tempText.uppercase().contains("S0 LUONG")
                    || tempText.uppercase().contains("MAT HANG")
                    || tempText.uppercase().contains("PHUONG THUC")
                    || tempText.uppercase().contains("THANH TOAN")
                ) {
                    if (i > start) {
                        end = i
                        endFlag = true
                        continue
                    }
                }
            }
        }
        return textBlocks.subList(start, end)
    }
}