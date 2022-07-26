package com.ntikhoa.ocrreceipt.business.process

import kotlin.math.log2
import kotlin.math.roundToInt

class VectorSpaceModel {
    suspend operator fun invoke(docs: List<String>): Map<String, Map<String, Float>> {
        val docTermMatrix = mutableMapOf<String, MutableMap<String, Float>>()

        val terms = docs.flatMap { it.split(" ") }
            .filter { it.isNotBlank() }
            .distinct()

        calculateTF(terms, docs, docTermMatrix)
        calculateIDF(docs, docTermMatrix)

        return docTermMatrix
    }

    private suspend fun calculateTF(
        terms: List<String>,
        docs: List<String>,
        docTermMatrix: MutableMap<String, MutableMap<String, Float>>
    ) {
        for (term in terms) {
            for (i in docs.indices) {
                val word = docs[i].split(" ")
                if (word.contains(term))

                    if (docTermMatrix.contains(term)) {
                        docTermMatrix[term]?.set(docs[i], 1.0f / word.size)
                    } else docTermMatrix[term] = mutableMapOf(docs[i] to 1.0f / word.size)
            }
        }
    }

    private suspend fun calculateIDF(
        docs: List<String>,
        docTermMatrix: MutableMap<String, MutableMap<String, Float>>
    ) {
        docTermMatrix.forEach { entry ->
            docTermMatrix[entry.key]?.let {
                var idf = docs.size.toFloat() / it.size.toFloat()
                idf = 1 + log2(idf)
                it.forEach { entryTerm ->
                    val roundOff = (entryTerm.value * idf * 100000.0).roundToInt() / 100000.0f
                    docTermMatrix[entry.key]!![entryTerm.key] = roundOff
                }
            }
        }
    }
}