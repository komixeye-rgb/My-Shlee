package com.example.model

import androidx.compose.ui.graphics.Color

data class SubjectScore(
    val id: String,
    val subjectName: String,
    val scorePercentage: Float, // 0.0 to 100.0 (Latest Mock Exam score)
    val distinctionBenchmark: Float = 80.0f, // UNEB D1 Distinction Line
    val totalQuizzesTaken: Int,
    val diagnosticTag: String,
    val iconName: String,
    val colorHex: Long = 0xFFD97706,
    val mockHistory: List<Float> = listOf(scorePercentage - 10f, scorePercentage - 4f, scorePercentage)
) {
    val needsRevision: Boolean get() = scorePercentage < 65.0f

    val unebGrade: String
        get() = when {
            scorePercentage >= 80.0f -> "D1 (Distinction 1)"
            scorePercentage >= 75.0f -> "D2 (Distinction 2)"
            scorePercentage >= 70.0f -> "C3 (Credit 3)"
            scorePercentage >= 65.0f -> "C4 (Credit 4)"
            scorePercentage >= 60.0f -> "C5 (Credit 5)"
            scorePercentage >= 55.0f -> "C6 (Credit 6)"
            scorePercentage >= 50.0f -> "P7 (Pass 7)"
            scorePercentage >= 45.0f -> "P8 (Pass 8)"
            else -> "F9 (Fail 9)"
        }

    val unebGradeShort: String
        get() = unebGrade.split(" ").first()

    val masteryLevelLabel: String
        get() = when {
            scorePercentage >= 80.0f -> "Mastered 🌟"
            scorePercentage >= 70.0f -> "Proficient 📈"
            scorePercentage >= 60.0f -> "Developing ⚡"
            else -> "Needs Revision 🚨"
        }

    val masteryColor: Color
        get() = when {
            scorePercentage >= 80.0f -> Color(0xFF10B981) // Emerald
            scorePercentage >= 70.0f -> Color(0xFF3B82F6) // Royal Blue
            scorePercentage >= 60.0f -> Color(0xFFF59E0B) // Amber
            else -> Color(0xFFEF4444) // Red/Flame
        }
}

data class FocusArea(
    val subjectId: String,
    val subjectName: String,
    val scorePercentage: Float,
    val benchmark: Float = 80.0f,
    val diagnosticTag: String,
    val priority: FocusPriority,
    val recommendedTopics: List<String>
)

enum class FocusPriority(val label: String, val color: Color) {
    URGENT("Urgent Revision", Color(0xFFEF4444)),
    MODERATE("Needs Practice", Color(0xFFF59E0B)),
    GOOD("On Track", Color(0xFF10B981))
}

data class LeaderboardCandidate(
    val rank: Int,
    val candidateName: String,
    val schoolName: String,
    val candidateLevel: String, // PLE, UCE, UACE
    val quizAccuracyPct: Float,
    val streakDays: Int,
    val compositePoints: Int,
    val rankShift: Int, // +2, -1, 0
    val isCurrentUser: Boolean = false,
    val avatarInitials: String
)

data class UserProfile(
    val studentId: String = "UG-2026-8842",
    val name: String = "Kato Ivan",
    val school: String = "King's College Budo",
    val candidateLevel: String = "UCE Candidate (Senior 4)",
    val projectedAggregate: String = "Agg 8 (Distinction)",
    val currentStreak: Int = 14,
    val totalQuizzesTaken: Int = 42,
    val averageScore: Float = 73.5f,
    val dailyQuizGoal: Int = 5,
    val dailyQuizCompleted: Int = 3,
    val notificationEnabled: Boolean = true,
    val reminderTime: String = "17:00"
)

data class QuizQuestion(
    val id: String,
    val subject: String,
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val pastPaperRef: String
)
