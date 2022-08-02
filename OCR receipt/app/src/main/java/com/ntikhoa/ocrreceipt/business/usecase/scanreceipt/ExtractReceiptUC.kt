package com.ntikhoa.ocrreceipt.business.usecase.scanreceipt

import androidx.core.text.isDigitsOnly
import com.google.mlkit.vision.text.Text
import com.ntikhoa.ocrreceipt.business.domain.model.Receipt
import com.ntikhoa.ocrreceipt.business.domain.utils.DataState
import com.ntikhoa.ocrreceipt.business.usecase.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class ExtractReceiptUC {
    suspend operator fun invoke(visionText: Text): Flow<DataState<Receipt>> = flow {
        emit(DataState.loading())

        val max = visionText.textBlocks.maxOf { it.boundingBox!!.right }
        val min = visionText.textBlocks.minOf { it.boundingBox!!.left }
        val middle = (max + min) / 2

        println("Value: $min $max $middle")

        var msg: String? = null
        val textBlocksLeft = mutableListOf<Text.TextBlock>()
        val textBlocksRight = mutableListOf<Text.TextBlock>()
        val textBlocksMiddle = mutableListOf<Text.TextBlock>()
        val textBlocks = visionText.textBlocks

        splitData(textBlocks, textBlocksLeft, textBlocksRight, middle)
//        try {
//            splitData(textBlocksRight, textBlocksMiddle, textBlocksRight)
//        } catch (e: Exception) {
//            msg = "There is exception"
//        }

        println("LEFT: ${textBlocksLeft.map { it.text }}")
        println("RIGHT: ${textBlocksRight.map { it.text }}")

        val products = getProductName(textBlocksLeft)
        val prices = getProductPrices(textBlocksRight)

        emit(
            DataState(
                data = Receipt(
                    products = products,
                    prices = prices,
                    visionText = visionText.text
                ), message = msg
            )
        )
    }.catch {
        val dataState = handleUseCaseException<Receipt>(it)
        emit(
            dataState.copy(
                data =
                Receipt(
                    visionText = visionText.text,
                    products = mutableListOf(),
                    prices = mutableListOf()
                ),
                message = "Cannot extracted"
            )
        )
    }

    private fun splitData(
        textBlocks: List<Text.TextBlock>,
        textBlocksLeft: MutableList<Text.TextBlock>,
        textBlocksRight: MutableList<Text.TextBlock>,
        middleValue: Int
    ) {

        val textBlocksLeftTemp =
            textBlocks.filter { it.boundingBox!!.left < middleValue }
        val textBlocksRightTemp =
            textBlocks.filter { it.boundingBox!!.left > middleValue }

        textBlocksLeft.clear()
        textBlocksRight.clear()
        textBlocksLeft.addAll(textBlocksLeftTemp)
        textBlocksRight.addAll(textBlocksRightTemp)
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
            var line = lines[i]
            line = line.uppercase()

            if (lines[i].contains("[*|=|:]([*|=|:])+".toRegex())) {
                lines.removeAt(i)
            } else if (!lines[i].contains("[a-zA-Z]+".toRegex())) {
                lines.removeAt(i)
            } else if (line.replace(".", "")
                    .replace("O", "0")
                    .replace("Q", "0")
                    .trim()
                    .isDigitsOnly()
            ) {
                lines.removeAt(i)
            }
        }

        return lines
    }

    private fun getProductPrices(textBlocks: List<Text.TextBlock>): MutableList<String> {
        val lines = textBlocks.flatMap {
            it.text.split("\n")
        }

        val prices = mutableListOf<String>()

        for (text in lines) {
            if (text.contains(".") && text.contains("[0-9]".toRegex())) {
                prices.add(text)
            }
        }
        return prices
    }
}