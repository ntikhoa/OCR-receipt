package com.ntikhoa.ocrreceipt

import androidx.core.text.isDigitsOnly
import com.google.mlkit.vision.text.Text


class OCRUseCase1 {
    operator fun invoke(visionText: Text): String {
        val lines = visionText.text.lines().toTypedArray()
        val blackList = ArrayList<String>()

        val copyLines = lines.copyOf()
        val lines1 = lines.toMutableList()

        for (i in lines1.indices.reversed()) {
            if (copyLines[i] == "=" || copyLines[i].contains("=(=)+".toRegex())) {
                lines1.removeAt(i)
                continue
            }
            if (copyLines[i] == "-" || copyLines[i].contains("-(-)+".toRegex())) {
                lines1.removeAt(i)
                continue
            }
            if (copyLines[i].contains("KHUYEN") || copyLines[i].contains("MAI")) {
                lines1.removeAt(i)
                continue
            }
            if (copyLines[i].replace(" ", "").isDigitsOnly()
                || copyLines[i].replace("O", "0")
                    .isDigitsOnly()
            ) {
                lines1.removeAt(i)
                continue
            }
            if (copyLines[i].replace(".", "").isDigitsOnly()) {
                lines1.removeAt(i)
                continue
            }
            var lowerCaseFlag = false
            for (c in copyLines[i]) {
                if (c.isLowerCase()) {
                    val str = lines1[i]
                    println(str)
                    blackList.add(str)
                    lines1.removeAt(i)
                    lowerCaseFlag = true
                    break
                }
            }
            if (lowerCaseFlag) {
                continue
            }
        }

        return lines1.joinToString {
            it + "\n"
        } + "\n" + blackList.joinToString {
            it + "\n"
        }
    }
}