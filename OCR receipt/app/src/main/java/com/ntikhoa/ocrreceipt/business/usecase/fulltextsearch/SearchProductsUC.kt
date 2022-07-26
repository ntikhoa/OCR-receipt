package com.ntikhoa.ocrreceipt.business.usecase.fulltextsearch

import com.ntikhoa.ocrreceipt.business.domain.model.ProductSearch
import com.ntikhoa.ocrreceipt.business.domain.utils.DataState
import com.ntikhoa.ocrreceipt.business.usecase.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlin.math.acos
import kotlin.math.sqrt

class SearchProductsUC {
    suspend operator fun invoke(
        queries: List<String>,
        docTermMatrix: Map<String, Map<String, Float>>
    ): Flow<DataState<List<ProductSearch>>> =
        flow {
            emit(DataState.loading())

            val result = mutableListOf<ProductSearch>()
            for (query in queries) {
                val rankings = search(query, docTermMatrix)
                result.add(
                    ProductSearch(
                        productName = query,
                        suggestion = rankings
                    )
                )
            }

            emit(DataState.data(data = result.toList()))

        }.catch {
            emit(handleUseCaseException(it))
        }

    private suspend fun search(
        query: String,
        docTermMatrix: Map<String, Map<String, Float>>
    ): List<String> {
        val queries = query.split(" ")
        val queryVector = ArrayList<Float>()
        val docVectors = mutableMapOf<String, ArrayList<Float>>()

        getVectors(queries, docTermMatrix, queryVector, docVectors)

        val docAngles = calculateAngles(queryVector, docVectors)
        return docAngles.sortedBy { it.angle }.map { it.doc }
    }

    private fun calculateAngles(
        queryVector: MutableList<Float>,
        docVectors: MutableMap<String, ArrayList<Float>>
    ): List<DocAngle> {
        val angles = arrayListOf<DocAngle>()
        val queryUnitVector = getUnitVector(queryVector)
        docVectors.onEach {
            val docUnitVector = getUnitVector(it.value)
            val dot = dotProduct(docUnitVector, queryUnitVector)
            val angle = acos(dot)
            angles.add(DocAngle(it.key, angle))
        }
        return angles
    }

    private fun getVectors(
        queries: List<String>,
        docTermMatrix: Map<String, Map<String, Float>>,
        queryVector: MutableList<Float>,
        docVectors: MutableMap<String, ArrayList<Float>>
    ) {
        for (i in queries.indices) {
            docTermMatrix[queries[i]]?.let {
                queryVector.add(1.0f)
                it.forEach { entry ->
                    if (queryVector.size - 1 > 0) {
                        while (docVectors[entry.key] == null || docVectors[entry.key]!!.size < queryVector.size - 1) {
                            if (docVectors.contains(entry.key)) {
                                docVectors[entry.key]?.add(0.0f)
                            } else docVectors[entry.key] = arrayListOf(0.0f)
                        }
                        docVectors[entry.key]!!.add(entry.value)
                    } else {
                        docVectors[entry.key] = arrayListOf(entry.value)
                    }
                }
            }
        }

        for (entry in docVectors.entries.iterator()) {
            while (docVectors[entry.key]!!.size < queryVector.size)
                docVectors[entry.key]!!.add(0.0f)
        }
    }

    private fun getUnitVector(vector: List<Float>): List<Float> {
        var distance = 0.0f
        for (element in vector) {
            distance += element * element
        }
        distance = sqrt(distance)

        return vector.map { it / distance }
    }

    private fun dotProduct(vector1: List<Float>, vector2: List<Float>): Float {
        var result = 0.0f
        for (i in vector1.indices) {
            result += vector1[i] * vector2[i]
        }
        return result
    }

    inner class DocAngle(val doc: String, val angle: Float)
}