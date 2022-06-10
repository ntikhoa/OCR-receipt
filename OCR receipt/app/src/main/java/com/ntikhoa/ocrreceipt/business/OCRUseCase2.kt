package com.ntikhoa.ocrreceipt.business

import androidx.core.text.isDigitsOnly
import com.google.mlkit.vision.text.Text

class OCRUseCase2 {
    suspend operator fun invoke(visionText: Text): String {
        val textBlocksLeft = mutableListOf<Text.TextBlock>()
        val textBlocksRight = mutableListOf<Text.TextBlock>()
        val textBlocksMiddle = mutableListOf<Text.TextBlock>()
        val textBlocks = visionText.textBlocks

        splitData(textBlocks, textBlocksLeft, textBlocksRight)
        splitData(textBlocksRight, textBlocksMiddle, textBlocksRight)

        val productName = getProductName(textBlocksLeft)
        val prices = getProductPrices(textBlocksRight)

        val pricesStr = prices.map {
            it.toString()
        }

//        val textsLeft = textBlocksLeft.map {
//            it.text
//        }
//        val textsRight = textBlocksRight.map {
//            it.text
//        }
//        val textsMiddle = textBlocksMiddle.map {
//            it.text
//        }

        return productName.reduce { acc, s ->
            acc + "\n" + s
        } +
                "\n\n" +
                pricesStr.reduce { acc, s ->
                    acc + "\n" + s
                }
    }

    private fun splitData(
        textBlocks: List<Text.TextBlock>,
        textBlocksLeft: MutableList<Text.TextBlock>,
        textBlocksRight: MutableList<Text.TextBlock>
    ) {
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

        var textBlocksLeftTemp = sortedTextBlock.subList(0, maxDistIndex)
        var textBlocksRightTemp = sortedTextBlock.subList(maxDistIndex, sortedTextBlock.size)

        textBlocksLeftTemp = textBlocksLeftTemp.sortedBy {
            it.boundingBox!!.top
        }
        textBlocksRightTemp = textBlocksRightTemp.sortedBy {
            it.boundingBox!!.top
        }

        textBlocksLeft.clear()
        textBlocksRight.clear()
        textBlocksLeft.addAll(textBlocksLeftTemp)
        textBlocksRight.addAll(textBlocksRightTemp)
    }

    private fun getProductName(textBlocks: List<Text.TextBlock>): MutableList<String> {
        val lines = textBlocks.flatMap {
            it.text.split("\n")
        }.toMutableList()

        for (i in lines.indices.reversed()) {
//            var line = lines[i]
//            line = Regex("[^A-Za-z0-9]").replace(line, "")

            if (lines[i].contains("[*|=|:]([*|=|:])+".toRegex())) {
                lines.removeAt(i)
            } else if (lines[i].isDigitsOnly()) {
                lines.removeAt(i)
            } else if (lines[i].contains("KHUYEN MAI")) {
                lines.removeAt(i)
            }
        }
        return lines
    }

    private fun getProductPrices(textBlocks: List<Text.TextBlock>): MutableList<Int> {
        val lines = textBlocks.flatMap {
            it.text.split("\n")
        }

        val prices = mutableListOf<Int>()

        for (text in lines) {
            if (text.contains(".")) {
                val priceStr = text.replace(".", "")
                prices.add(priceStr.toInt())
            }
        }
        return prices
    }
}