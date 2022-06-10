package com.ntikhoa.ocrreceipt.business.usecase

import android.graphics.Rect
import androidx.core.text.isDigitsOnly
import com.google.mlkit.vision.text.Text
import com.ntikhoa.ocrreceipt.MyText

class OCRUseCase {
    operator fun invoke(visionText: Text): String {
        println(visionText.text)

        val predefineSize = 8

        val mutableTextBlocks = visionText.textBlocks.toMutableList()
        val myTexts = normalize1(mutableTextBlocks)

        val products = ArrayList<MyText>()
        val unitPrices = ArrayList<String>()
        val quantities = ArrayList<String>()

        for (text in myTexts) {
            if (text.text.isDigitsOnly()) {
                quantities.add(text.text)
            } else {
                val temp = text.text.replace(".", "")
                if (temp.isDigitsOnly()) {
                    unitPrices.add(text.text)
                } else {
                    products.add(text)
                }
            }
        }

        if (products.size > predefineSize) {
            var mergeIndex = 0
            var minSpace = products[1].rect.top - products[0].rect.top
            for (i in 0 until products.size - 2) {
                val space = products[i + 1].rect.top - products[i].rect.top
                if (space < minSpace) {
                    minSpace = space
                    mergeIndex = i
                }
            }
            products[mergeIndex].text += " " + products[mergeIndex + 1].text
            products.removeAt(mergeIndex + 1)
        }

        var text = ""
//        val listText = visionText.text.lines().toMutableList()
//        for (i in listText.indices) {
//            text += i.toString() + ":\t" + listText[i] + "\n"
//        }
        text += "Product" + "                      " + "Quantity" + "    " + "Unit Price" + "\n\n"
        for (i in unitPrices.indices) {
            text += products[i].text + "             " + quantities[i] + "    " + unitPrices[i] + "\n"
        }

        return text
    }

    private fun normalize1(textBlocks: MutableList<Text.TextBlock>): List<MyText> {

        val myTexts = ArrayList<MyText>()

        textBlocks.sortBy {
            it.boundingBox!!.top
        }
        //textBlocks.removeAt(1)
        if (textBlocks[0].text.contains("=")) {
            textBlocks.removeAt(0)
        }

        val height = textBlocks.filter {
            it.text.lines().count() == 1
        }.map {
            it.boundingBox!!.height()
        }
        val minHeight = height.minOf {
            it
        }
        val maxHeight = height.maxOf {
            it
        }
        val avgHeight = (maxHeight + minHeight) / 2

        for (textBlock in textBlocks) {
            val lines = textBlock.text.lines().toMutableList()
            if (lines.count() > 1) {
                val avgSpace =
                    (textBlock.boundingBox!!.height() - (avgHeight * lines.count())) /
                            (avgHeight * (lines.count() - 1))
                var currentTop = textBlock.boundingBox!!.top
                for (line in lines) {
                    if (!line.contains("KHUYEN") && !line.contains("MAI")) {
                        myTexts.add(
                            MyText(
                                line,
                                Rect(
                                    textBlock.boundingBox!!.left,
                                    currentTop,
                                    textBlock.boundingBox!!.right,
                                    currentTop + avgHeight
                                )
                            )
                        )
                    }
                    currentTop += avgHeight + avgSpace
                }
            } else {
                myTexts.add(
                    MyText(
                        textBlock.text,
                        Rect(textBlock.boundingBox!!)
                    )
                )
            }
        }

        myTexts.sortBy {
            it.rect.top
        }

        for (text in myTexts) {
            println(text.toString())
        }

        return myTexts
    }

    private fun normalize(listText: MutableList<String>) {
        for (i in listText.indices.reversed()) {
            if (listText[i].isBlank()) {
                listText.removeAt(i)
                continue
            }
            if (listText[i] == "=" || listText[i] == "==" || listText[i].contains("=(=)+".toRegex())) {
                listText.removeAt(i)
                continue
            }
            if (listText[i].contains("KHUYEN MAI")) {
                listText.removeAt(i)
                continue
            }
            listText[i].trim()
        }
    }

    private fun getBoundingInfo(boundingBox: Rect) {
        println(
            "" +
                    boundingBox.top + " " +
                    boundingBox.bottom + " " +
                    boundingBox.width() + " " +
                    boundingBox.height()
        )
    }
}