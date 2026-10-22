package com.example.service

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiRevisionService {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Calls Gemini gemini-4-argon with thinkingLevel = ThinkingLevel.HIGH
     * using standard Android JSONObject to deliver deep UNEB examination diagnostics.
     */
    suspend fun getHighThinkingRevisionAdvice(
        subject: String,
        currentScore: Float,
        topicOrQuestion: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineDiagnosticAdvice(subject, currentScore, topicOrQuestion)
        }

        val promptText = """
            You are a Senior UNEB (Uganda National Examinations Board) Master Examiner and AI Tutor.
            Subject: $subject
            Candidate's Current Performance: $currentScore% (Benchmark Distinction 1 is 80%)
            Topic / Question of Focus: $topicOrQuestion
            
            Please provide a deep, high-thinking step-by-step revision breakdown for this candidate:
            1. Key UNEB Concept & Principle Breakdown.
            2. Common UNEB Examination Pitfalls to avoid (e.g. key formula errors, spelling in biology, step presentation in math).
            3. Step-by-Step Sample Worked Question with clear explanation.
            4. Quick Practice Question for the student to solve now.
        """.trimIndent()

        try {
            // Build JSON request manually with Android's org.json
            val partObj = JSONObject().apply {
                put("text", promptText)
            }
            val contentObj = JSONObject().apply {
                put("parts", JSONArray().put(partObj))
            }
            val contentsArray = JSONArray().put(contentObj)

            val thinkingConfigObj = JSONObject().apply {
                put("thinkingLevel", "HIGH")
            }
            val generationConfigObj = JSONObject().apply {
                put("temperature", 0.3)
                put("thinkingConfig", thinkingConfigObj)
            }

            val rootReq = JSONObject().apply {
                put("contents", contentsArray)
                put("generationConfig", generationConfigObj)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = rootReq.toString().toRequestBody(mediaType)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-4-argon:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            val bodyString = response.body?.string() ?: ""

            if (!response.isSuccessful || bodyString.isBlank()) {
                return@withContext getOfflineDiagnosticAdvice(subject, currentScore, topicOrQuestion)
            }

            val responseJson = JSONObject(bodyString)
            val candidates = responseJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val firstPart = parts?.optJSONObject(0)
            val outputText = firstPart?.optString("text")

            if (!outputText.isNullOrBlank()) {
                outputText
            } else {
                getOfflineDiagnosticAdvice(subject, currentScore, topicOrQuestion)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            getOfflineDiagnosticAdvice(subject, currentScore, topicOrQuestion)
        }
    }

    private fun getOfflineDiagnosticAdvice(subject: String, score: Float, topic: String): String {
        return """
            🧠 **SHULEE UNEB Diagnostic Thinking Mode ($subject)**
            
            **1. UNEB Core Principle Analysis:**
            • $subject requires strict compliance with UNEB mark scheme phrasing. For $topic, always state standard definitions and show complete working.
            
            **2. Examiner's Common Pitfalls:**
            • Failing to state SI units in numerical answers.
            • Skipping intermediate derivation steps.
            • Confusing key terminology during short answer questions.
            
            **3. Step-by-Step Worked Exam Pattern:**
            • **Step 1:** Identify given variables and required UNEB target outcome.
            • **Step 2:** Apply fundamental formula / standard principle.
            • **Step 3:** Perform precise substitution & verify units.
            
            **4. High-Yield Revision Action:**
            Review 2018–2023 UNEB Past Paper Section B questions on $topic. Complete 3 practice questions today to boost your $subject score from ${score.toInt()}% toward the 80% D1 distinction line!
        """.trimIndent()
    }
}
