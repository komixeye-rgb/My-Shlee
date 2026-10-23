package com.example.util

import android.content.Context
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.File
import java.io.FileOutputStream

object QrCodeGenerator {

    /**
     * Generates a certified, scannable QR Code Bitmap using the industry-standard ZXing library.
     * Encodes the student's unique Index number and study streak with Level-H error correction
     * so it can be reliably scanned by any physical barcode/QR reader or smartphone camera.
     */
    fun generateQrBitmap(content: String, sizePx: Int = 512): Bitmap {
        val hints = mapOf(
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
            EncodeHintType.MARGIN to 2,
            EncodeHintType.CHARACTER_SET to "UTF-8"
        )

        val bitMatrix = QRCodeWriter().encode(
            content,
            BarcodeFormat.QR_CODE,
            sizePx,
            sizePx,
            hints
        )

        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Slate 900 for dark modules, crisp white for quiet zone & background
        val darkColor = android.graphics.Color.parseColor("#0F172A")
        val lightColor = android.graphics.Color.WHITE

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) darkColor else lightColor)
            }
        }

        return bitmap
    }

    /**
     * Helper to build a standard, structured verification payload string
     * containing the student's unique index number, streak count, and verification URL.
     */
    fun buildStudentVerificationPayload(studentIndex: String, streakDays: Int, studentName: String): String {
        return """
            SHLEE CANDIDATE PASS
            Index No: $studentIndex
            Candidate: $studentName
            Study Streak: $streakDays Days
            Verified UNEB Prep: https://shulee.ug/verify?index=$studentIndex&streak=$streakDays
        """.trimIndent()
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
