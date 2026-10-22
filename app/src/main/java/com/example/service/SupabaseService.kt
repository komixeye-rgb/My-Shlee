package com.example.service

import com.example.BuildConfig
import com.example.model.LeaderboardCandidate
import com.example.model.PaymentTransaction
import com.example.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object SupabaseService {

    private const val FALLBACK_URL = "https://ezrrixubfgpxvxcakfrr.supabase.co/rest/v1/"
    private const val FALLBACK_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImV6cnJpeHViZmdweHZ4Y2FrZnJyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTI2ODQzMDksImV4cCI6MjEwODI2MDMwOX0.4RbLVzGWcEV_7MeKJsrX_2V__IXjoOXt-JcnosQpgqY"

    val baseUrl: String
        get() = try {
            val key = BuildConfig.SUPABASE_URL
            if (key.isNotBlank()) key else FALLBACK_URL
        } catch (_: Exception) {
            FALLBACK_URL
        }

    val apiKey: String
        get() = try {
            val key = BuildConfig.SUPABASE_KEY
            if (key.isNotBlank()) key else FALLBACK_KEY
        } catch (_: Exception) {
            FALLBACK_KEY
        }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Pings Supabase PostgREST root endpoint to verify authorization & connectivity.
     */
    suspend fun checkConnection(): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(baseUrl)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer $apiKey")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            // PostgREST returns 200 with OpenAPI spec when key is valid
            response.isSuccessful || response.code == 200 || response.code == 204
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Fetches candidates from Supabase `candidates` table if present.
     */
    suspend fun fetchLeaderboardFromSupabase(): List<LeaderboardCandidate>? = withContext(Dispatchers.IO) {
        try {
            val url = "${baseUrl}candidates?select=*&order=composite_points.desc"
            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer $apiKey")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val body = response.body?.string() ?: return@withContext null
            val array = JSONArray(body)
            val list = mutableListOf<LeaderboardCandidate>()

            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    LeaderboardCandidate(
                        rank = obj.optInt("rank", i + 1),
                        candidateName = obj.optString("candidate_name", "Student"),
                        schoolName = obj.optString("school_name", "National School"),
                        candidateLevel = obj.optString("candidate_level", "UCE"),
                        quizAccuracyPct = obj.optDouble("quiz_accuracy_pct", 80.0).toFloat(),
                        streakDays = obj.optInt("streak_days", 1),
                        compositePoints = obj.optInt("composite_points", 500),
                        rankShift = obj.optInt("rank_shift", 0),
                        isCurrentUser = obj.optBoolean("is_current_user", false),
                        avatarInitials = obj.optString("avatar_initials", "ST")
                    )
                )
            }
            if (list.isNotEmpty()) list else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Upserts candidate record into Supabase `candidates` table.
     */
    suspend fun syncCandidate(candidate: LeaderboardCandidate): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("rank", candidate.rank)
                put("candidate_name", candidate.candidateName)
                put("school_name", candidate.schoolName)
                put("candidate_level", candidate.candidateLevel)
                put("quiz_accuracy_pct", candidate.quizAccuracyPct)
                put("streak_days", candidate.streakDays)
                put("composite_points", candidate.compositePoints)
                put("rank_shift", candidate.rankShift)
                put("is_current_user", candidate.isCurrentUser)
                put("avatar_initials", candidate.avatarInitials)
            }

            val requestBody = json.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url("${baseUrl}candidates")
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Prefer", "resolution=merge-duplicates")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Saves user profile to Supabase `profiles` table.
     */
    suspend fun syncUserProfile(profile: UserProfile): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("student_id", profile.studentId)
                put("name", profile.name)
                put("school", profile.school)
                put("candidate_level", profile.candidateLevel)
                put("projected_aggregate", profile.projectedAggregate)
                put("current_streak", profile.currentStreak)
                put("total_quizzes_taken", profile.totalQuizzesTaken)
                put("average_score", profile.averageScore)
                put("daily_quiz_goal", profile.dailyQuizGoal)
                put("daily_quiz_completed", profile.dailyQuizCompleted)
                put("notification_enabled", profile.notificationEnabled)
                put("reminder_time", profile.reminderTime)
            }

            val requestBody = json.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url("${baseUrl}profiles")
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Prefer", "resolution=merge-duplicates")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Inserts transaction into Supabase `payment_transactions` table.
     */
    suspend fun syncTransaction(transaction: PaymentTransaction): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("transaction_id", transaction.id)
                put("plan_name", transaction.planName)
                put("amount_ugx", transaction.amountUgx)
                put("payment_method", transaction.paymentMethod)
                put("date", transaction.date)
                put("status", transaction.status)
            }

            val requestBody = json.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url("${baseUrl}payment_transactions")
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer $apiKey")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
