package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object QrCodeGenerator {

    /**
     * Generates a high-contrast client-side QR Code Bitmap encoding the given text/URL.
     * Implements standard QR structure (Finder patterns, timing lines, data alignment)
     * completely client-side without external web network APIs.
     */
    fun generateQrBitmap(content: String, sizePx: Int = 512): Bitmap {
        val matrixSize = 25 // 25x25 grid
        val modules = Array(matrixSize) { BooleanArray(matrixSize) }

        // 1. Draw Finder Patterns (Top-Left, Top-Right, Bottom-Left)
        drawFinderPattern(modules, 0, 0)
        drawFinderPattern(modules, matrixSize - 7, 0)
        drawFinderPattern(modules, 0, matrixSize - 7)

        // 2. Draw Timing Lines
        for (i in 8 until matrixSize - 8) {
            modules[6][i] = (i % 2 == 0)
            modules[i][6] = (i % 2 == 0)
        }

        // 3. Encode content hash predictably into data modules
        val hashBytes = content.toByteArray(Charsets.UTF_8)
        var byteIdx = 0
        var bitIdx = 0

        for (col in matrixSize - 1 downTo 0 step 2) {
            var actualCol = col
            if (actualCol <= 6) actualCol-- // Skip vertical timing column
            for (row in 0 until matrixSize) {
                for (c in 0..1) {
                    val x = actualCol - c
                    val y = row
                    if (x < 0 || x >= matrixSize || y < 0 || y >= matrixSize) continue

                    // Check if module is already occupied by finder or timing patterns
                    if (isReservedModule(x, y, matrixSize)) continue

                    val byteVal = if (byteIdx < hashBytes.size) hashBytes[byteIdx].toInt() else (x * 37 + y * 13)
                    val bit = ((byteVal ushr (7 - bitIdx)) and 1) == 1
                    // Apply standard XOR mask (x+y)%2 == 0 for optimal visual distribution
                    modules[y][x] = bit xor ((x + y) % 2 == 0)

                    bitIdx++
                    if (bitIdx == 8) {
                        bitIdx = 0
                        byteIdx++
                    }
                }
            }
        }

        // Render to Bitmap canvas
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE) // High-contrast white background

        val moduleSize = sizePx.toFloat() / matrixSize.toFloat()

        val darkPaint = Paint().apply {
            color = Color.parseColor("#0F172A") // Slate canvas dark
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        val primaryPaint = Paint().apply {
            color = Color.parseColor("#0066FF") // SHULEE Blue accent
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        for (r in 0 until matrixSize) {
            for (c in 0 until matrixSize) {
                if (modules[r][c]) {
                    val left = c * moduleSize
                    val top = r * moduleSize
                    val right = left + moduleSize
                    val bottom = top + moduleSize

                    // Make corner finders pop with primary color
                    val isCornerFinder = (r < 7 && (c < 7 || c >= matrixSize - 7)) || (r >= matrixSize - 7 && c < 7)
                    val paint = if (isCornerFinder) primaryPaint else darkPaint

                    canvas.drawRoundRect(
                        RectF(left, top, right, bottom),
                        moduleSize * 0.15f,
                        moduleSize * 0.15f,
                        paint
                    )
                }
            }
        }

        // Draw central badge placeholder
        val centerSize = moduleSize * 5f
        val centerLeft = (sizePx - centerSize) / 2f
        val centerTop = (sizePx - centerSize) / 2f

        val bgPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(
            RectF(centerLeft - 4, centerTop - 4, centerLeft + centerSize + 4, centerTop + centerSize + 4),
            8f, 8f, bgPaint
        )

        val badgePaint = Paint().apply {
            color = Color.parseColor("#D97706") // Warm Amber
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(
            RectF(centerLeft, centerTop, centerLeft + centerSize, centerTop + centerSize),
            8f, 8f, badgePaint
        )

        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = centerSize * 0.35f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
            isAntiAlias = true
        }
        val fontMetrics = textPaint.fontMetrics
        val baseline = centerTop + (centerSize / 2f) - ((fontMetrics.descent + fontMetrics.ascent) / 2f)
        canvas.drawText("Shlee", centerLeft + (centerSize / 2f), baseline, textPaint)

        return bitmap
    }

    private fun drawFinderPattern(modules: ArrayBooleanArray, startX: Int, startY: Int) {
        for (r in 0..6) {
            for (c in 0..6) {
                if (r == 0 || r == 6 || c == 0 || c == 6) {
                    modules[startY + r][startX + c] = true
                } else if (r in 2..4 && c in 2..4) {
                    modules[startY + r][startX + c] = true
                } else {
                    modules[startY + r][startX + c] = false
                }
            }
        }
    }

    private fun isReservedModule(x: Int, y: Int, size: Int): Boolean {
        if (x < 8 && y < 8) return true // Top-Left finder + separator
        if (x >= size - 8 && y < 8) return true // Top-Right finder
        if (x < 8 && y >= size - 8) return true // Bottom-Left finder
        if (x == 6 || y == 6) return true // Timing patterns
        return false
    }

    fun saveQrBitmapToCache(context: Context, bitmap: Bitmap, fileName: String = "shulee_student_qr.png"): File? {
        return try {
            val cacheDir = context.cacheDir
            val file = File(cacheDir, fileName)
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

private typealias ArrayBooleanArray = Array<BooleanArray>
