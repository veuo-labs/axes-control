package com.example.vision

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.abs
import kotlin.math.sqrt

data class ObjectFingerprint(
    val luminanceGrid: FloatArray, // 8x8 = 64
    val hueHistogram: FloatArray,   // 16 bins
    val saturationHistogram: FloatArray, // 4 bins
    val edgeEnergy: Float
) {
    fun serialize(): String {
        val lumStr = luminanceGrid.joinToString(",") { "%.4f".format(it) }
        val hueStr = hueHistogram.joinToString(",") { "%.4f".format(it) }
        val satStr = saturationHistogram.joinToString(",") { "%.4f".format(it) }
        return "$lumStr|$hueStr|$satStr|%.4f".format(edgeEnergy)
    }

    companion object {
        fun deserialize(serialized: String): ObjectFingerprint? {
            return try {
                val parts = serialized.split("|")
                if (parts.size < 4) return null
                val lum = parts[0].split(",").map { it.toFloat() }.toFloatArray()
                val hue = parts[1].split(",").map { it.toFloat() }.toFloatArray()
                val sat = parts[2].split(",").map { it.toFloat() }.toFloatArray()
                val edge = parts[3].toFloat()
                ObjectFingerprint(lum, hue, sat, edge)
            } catch (e: Exception) {
                null
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ObjectFingerprint
        return luminanceGrid.contentEquals(other.luminanceGrid) &&
                hueHistogram.contentEquals(other.hueHistogram) &&
                saturationHistogram.contentEquals(other.saturationHistogram) &&
                abs(edgeEnergy - other.edgeEnergy) < 0.001f
    }

    override fun hashCode(): Int {
        var result = luminanceGrid.contentHashCode()
        result = 31 * result + hueHistogram.contentHashCode()
        result = 31 * result + saturationHistogram.contentHashCode()
        result = 31 * result + edgeEnergy.hashCode()
        return result
    }
}

object OfflineVisualEngine {

    /**
     * Extracts an offline visual fingerprint from a captured object bitmap.
     * Computes a normalized 8x8 luminance grid, 16-bin hue histogram, 4-bin saturation,
     * and high-frequency edge energy. Zero cloud AI / 100% on-device.
     */
    fun extractObjectFingerprint(bitmap: Bitmap): ObjectFingerprint {
        val scaled = Bitmap.createScaledBitmap(bitmap, 64, 64, true)
        val lumGrid = FloatArray(64)
        val hueHisto = FloatArray(16)
        val satHisto = FloatArray(4)
        var totalEdgeEnergy = 0f

        val hsv = FloatArray(3)
        val blockSize = 8
        var sampleCount = 0

        for (by in 0 until 8) {
            for (bx in 0 until 8) {
                var blockLumSum = 0f
                for (y in (by * blockSize) until ((by + 1) * blockSize)) {
                    for (x in (bx * blockSize) until ((bx + 1) * blockSize)) {
                        val pixel = scaled.getPixel(x, y)
                        val r = Color.red(pixel)
                        val g = Color.green(pixel)
                        val b = Color.blue(pixel)
                        val lum = (0.299f * r + 0.587f * g + 0.114f * b) / 255f
                        blockLumSum += lum

                        Color.RGBToHSV(r, g, b, hsv)
                        val hue = hsv[0]
                        val sat = hsv[1]
                        val hueBin = ((hue / 360f) * 16).toInt().coerceIn(0, 15)
                        val satBin = (sat * 4).toInt().coerceIn(0, 3)
                        hueHisto[hueBin] += 1f
                        satHisto[satBin] += 1f
                        sampleCount++

                        if (x > 0 && y > 0) {
                            val prevX = scaled.getPixel(x - 1, y)
                            val prevY = scaled.getPixel(x, y - 1)
                            val lumX = (0.299f * Color.red(prevX) + 0.587f * Color.green(prevX) + 0.114f * Color.blue(prevX)) / 255f
                            val lumY = (0.299f * Color.red(prevY) + 0.587f * Color.green(prevY) + 0.114f * Color.blue(prevY)) / 255f
                            totalEdgeEnergy += abs(lum - lumX) + abs(lum - lumY)
                        }
                    }
                }
                lumGrid[by * 8 + bx] = blockLumSum / (blockSize * blockSize)
            }
        }

        if (sampleCount > 0) {
            for (i in hueHisto.indices) hueHisto[i] /= sampleCount
            for (i in satHisto.indices) satHisto[i] /= sampleCount
            totalEdgeEnergy /= sampleCount
        }

        return ObjectFingerprint(lumGrid, hueHisto, satHisto, totalEdgeEnergy)
    }

    /**
     * Compares live candidate object with stored fingerprint.
     * Returns match similarity in [0.0..1.0].
     */
    fun matchObject(candidateBitmap: Bitmap, registered: ObjectFingerprint): Float {
        val candidate = extractObjectFingerprint(candidateBitmap)

        // 1. Luminance correlation
        var dotLum = 0f
        var normCand = 0f
        var normReg = 0f
        for (i in 0 until 64) {
            dotLum += candidate.luminanceGrid[i] * registered.luminanceGrid[i]
            normCand += candidate.luminanceGrid[i] * candidate.luminanceGrid[i]
            normReg += registered.luminanceGrid[i] * registered.luminanceGrid[i]
        }
        val lumSim = when {
            normCand == 0f && normReg == 0f -> 1.0f
            normCand > 0f && normReg > 0f -> {
                (dotLum / (sqrt(normCand.toDouble()) * sqrt(normReg.toDouble()))).toFloat().coerceIn(0f, 1f)
            }
            else -> 0f
        }

        // 2. Color Histogram intersection (Hue)
        var hueIntersection = 0f
        for (i in 0 until 16) {
            hueIntersection += kotlin.math.min(candidate.hueHistogram[i], registered.hueHistogram[i])
        }

        // 3. Saturation intersection
        var satIntersection = 0f
        for (i in 0 until 4) {
            satIntersection += kotlin.math.min(candidate.saturationHistogram[i], registered.saturationHistogram[i])
        }

        // 4. Edge energy difference
        val edgeDiff = abs(candidate.edgeEnergy - registered.edgeEnergy)
        val edgeSim = (1f - edgeDiff * 2f).coerceIn(0f, 1f)

        // Weighted blend
        val totalScore = (lumSim * 0.40f) + (hueIntersection * 0.35f) + (satIntersection * 0.15f) + (edgeSim * 0.10f)
        return totalScore.coerceIn(0f, 1f)
    }
}
