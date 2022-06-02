package com.ntikhoa.ocrreceipt.business

import androidx.core.text.isDigitsOnly
import com.google.mlkit.vision.text.Text

class OCRUseCase2 {
    operator fun invoke(visionText: Text): String {
        println(visionText.text)

        val textBlocks = visionText.textBlocks
        val croppedTextBlocks = croppedData(textBlocks)
        val leftTextBlocks = removeRightData(croppedTextBlocks)
        val productNames = getProductName(leftTextBlocks)

        return productNames.reduce { acc, s ->
            acc + "\n" + s
        }
    }

    private fun getProductName(textBlocks: List<Text.TextBlock>): List<String> {
        val lines = textBlocks.flatMap {
            it.text.split("\n")
        }.toMutableList()

        for (i in lines.indices.reversed()) {
            var line = lines[i]
            line = Regex("[^A-Za-z0-9]").replace(line, "")

            if (line.isDigitsOnly()) {
                lines.removeAt(i)
            } else if (line.contains("KHUYEN") || lines.contains("MAI")) {
                println(line)
                lines.removeAt(i)
            }
        }
        return lines
    }

    private fun removeRightData(textBlocks: List<Text.TextBlock>): List<Text.TextBlock> {
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

        val maxDist = dist.maxOf { it }
        val maxDistIndex = dist.indexOf(maxDist) + 1
        return sortedTextBlock.subList(0, maxDistIndex)
    }

    private fun croppedData(textBlocks: List<Text.TextBlock>): List<Text.TextBlock> {

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

            if (!startFlag && tempText.contains("[*|=|:]([*|=|:])+".toRegex())) {
                start = i + 1
                startFlag = true
                endFlag = false
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