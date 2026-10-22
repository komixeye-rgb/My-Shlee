package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import com.example.service.SupabaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentDashboardViewModel : ViewModel() {

    private val _isSupabaseConnected = MutableStateFlow<Boolean?>(null)
    val isSupabaseConnected: StateFlow<Boolean?> = _isSupabaseConnected.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncStatusMessage = MutableStateFlow("Initializing Supabase...")
    val syncStatusMessage: StateFlow<String> = _syncStatusMessage.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userProfile = MutableStateFlow(
        UserProfile(
            studentId = "UG-2026-8842",
            name = "Kato Ivan",
            school = "King's College Budo",
            candidateLevel = "UCE (Senior 4)",
            projectedAggregate = "Agg 8 D1",
            currentStreak = 14,
            totalQuizzesTaken = 42,
            averageScore = 68.5f,
            dailyQuizGoal = 5,
            dailyQuizCompleted = 3,
            notificationEnabled = true,
            reminderTime = "5:00 PM"
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    init {
        refreshFromSupabase()
    }

    fun refreshFromSupabase() {
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                val connected = SupabaseService.checkConnection()
                _isSupabaseConnected.value = connected
                if (connected) {
                    _syncStatusMessage.value = "⚡ Supabase Active (SHLEE)"
                    val remoteCandidates = SupabaseService.fetchLeaderboardFromSupabase()
                    if (!remoteCandidates.isNullOrEmpty()) {
                        _leaderboardCandidates.value = remoteCandidates
                    } else {
                        // Seed candidates to Supabase if table is fresh
                        _leaderboardCandidates.value.forEach { candidate ->
                            SupabaseService.syncCandidate(candidate)
                        }
                    }
                    SupabaseService.syncUserProfile(_userProfile.value)
                } else {
                    _syncStatusMessage.value = "Supabase Standby (Offline Mode)"
                }
            } catch (e: Exception) {
                _isSupabaseConnected.value = false
                _syncStatusMessage.value = "Supabase Standby: ${e.localizedMessage ?: "Offline"}"
            } finally {
                _isSyncing.value = false
            }
        }
    }

    private val _subjectScores = MutableStateFlow(
        listOf(
            SubjectScore("math", "Mathematics", 78.0f, 80.0f, 12, "Calculus & Algebra Steps", "calculate", 0xFFD97706),
            SubjectScore("sst", "SST (Social Studies)", 58.0f, 80.0f, 8, "East African Constitution & Climate", "public", 0xFFEF4444),
            SubjectScore("sci", "Integrated Science", 74.0f, 80.0f, 10, "Human Physiology & Magnetism", "science", 0xFF059669),
            SubjectScore("bio", "Biology", 55.0f, 80.0f, 7, "Photosynthesis Chemical Equations", "biotech", 0xFFF97316),
            SubjectScore("phy", "Physics", 62.0f, 80.0f, 9, "Newton's Laws & Circuit Calculations", "bolt", 0xFFEAB308),
            SubjectScore("eng", "English Language", 84.0f, 80.0f, 15, "Grammar & Summary Writing", "menu_book", 0xFF3B82F6)
        )
    )
    val subjectScores: StateFlow<List<SubjectScore>> = _subjectScores.asStateFlow()

    private val _leaderboardCandidates = MutableStateFlow(
        listOf(
            LeaderboardCandidate(1, "Nalwanga Sarah", "King's College Budo", "UCE", 96.5f, 28, 920, 0, false, "NS"),
            LeaderboardCandidate(2, "Okello David", "St. Mary's Kitende", "UCE", 94.0f, 24, 885, 1, false, "OD"),
            LeaderboardCandidate(3, "Akello Grace", "Gayaza High School", "UCE", 93.2f, 22, 860, -1, false, "AG"),
            LeaderboardCandidate(4, "Kato Ivan", "King's College Budo", "UCE", 88.5f, 14, 736, 2, true, "KI"),
            LeaderboardCandidate(5, "Muwanguzi Joel", "Mt. St. Mary's Namagunga", "UCE", 87.0f, 18, 720, 0, false, "MJ"),
            LeaderboardCandidate(6, "Chebet Brenda", "Ntare School", "UCE", 85.4f, 15, 695, -1, false, "CB"),
            LeaderboardCandidate(7, "Ochieng Emmanuel", "Uganda Martyrs Namugongo", "UCE", 84.1f, 12, 660, 3, false, "OE"),
            LeaderboardCandidate(8, "Nabirye Sandra", "Nabisunsa Girls", "UCE", 82.0f, 10, 620, -2, false, "NS")
        )
    )
    val leaderboardCandidates: StateFlow<List<LeaderboardCandidate>> = _leaderboardCandidates.asStateFlow()

    private val _subscriptionPlans = MutableStateFlow(
        listOf(
            SubscriptionPlan(
                id = "plan_basic",
                name = "Shlee Free Revision",
                priceUgx = "UGX 0",
                period = "Forever",
                features = listOf("Basic UNEB Quizzes", "Standard Radar Analytics", "Community Leaderboard Access"),
                isPopular = false
            ),
            SubscriptionPlan(
                id = "plan_premium",
                name = "Shlee UNEB Distinction Pass",
                priceUgx = "UGX 10,000",
                period = "Per Month",
                features = listOf("Unlimited High-Thinking AI Tutor (gemini-4-argon)", "All UNEB Past Papers (2010–2026)", "Leaderboard Verified Badge & Priority Rank", "Offline PDF Exam Downloads"),
                isPopular = true
            ),
            SubscriptionPlan(
                id = "plan_term",
                name = "Shlee Term Masterclass",
                priceUgx = "UGX 35,000",
                period = "Full Term",
                features = listOf("All Distinction Pass Features", "1-on-1 Master Examiner Live Q&A", "Personalized Weakness Remediation Plan"),
                isPopular = false
            )
        )
    )
    val subscriptionPlans: StateFlow<List<SubscriptionPlan>> = _subscriptionPlans.asStateFlow()

    private val _paymentTransactions = MutableStateFlow(
        listOf(
            PaymentTransaction("tx_01", "Shlee UNEB Distinction Pass", "UGX 10,000", "MTN Mobile Money", "12 Oct 2026", "SUCCESS")
        )
    )
    val paymentTransactions: StateFlow<List<PaymentTransaction>> = _paymentTransactions.asStateFlow()

    fun addPaymentTransaction(plan: SubscriptionPlan) {
        val newTx = PaymentTransaction(
            id = "tx_${System.currentTimeMillis()}",
            planName = plan.name,
            amountUgx = plan.priceUgx,
            paymentMethod = "MTN Mobile Money",
            date = "Today",
            status = "SUCCESS"
        )
        val current = _paymentTransactions.value.toMutableList()
        current.add(0, newTx)
        _paymentTransactions.value = current

        // Sync transaction to Supabase
        viewModelScope.launch {
            SupabaseService.syncTransaction(newTx)
        }
    }

    private val _selectedFilter = MutableStateFlow("All Levels")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    fun loginUser(name: String) {
        _isLoggedIn.value = true
        val updated = _userProfile.value.copy(name = name)
        _userProfile.value = updated
        viewModelScope.launch {
            SupabaseService.syncUserProfile(updated)
        }
    }

    fun logoutUser() {
        _isLoggedIn.value = false
    }

    fun updateLevelFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun toggleNotification(enabled: Boolean) {
        val updated = _userProfile.value.copy(notificationEnabled = enabled)
        _userProfile.value = updated
        viewModelScope.launch {
            SupabaseService.syncUserProfile(updated)
        }
    }

    fun updateReminderTime(timeStr: String) {
        val updated = _userProfile.value.copy(reminderTime = timeStr)
        _userProfile.value = updated
        viewModelScope.launch {
            SupabaseService.syncUserProfile(updated)
        }
    }

    fun updateSubjectScore(subjectId: String, newScorePct: Float) {
        val currentList = _subjectScores.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == subjectId }
        if (index != -1) {
            val oldSubject = currentList[index]
            currentList[index] = oldSubject.copy(
                scorePercentage = newScorePct,
                totalQuizzesTaken = oldSubject.totalQuizzesTaken + 1
            )
            _subjectScores.value = currentList

            // Increment daily quiz count & streak
            val profile = _userProfile.value
            val newCompleted = (profile.dailyQuizCompleted + 1).coerceAtMost(profile.dailyQuizGoal)
            val newAverage = currentList.map { it.scorePercentage }.average().toFloat()
            val updatedProfile = profile.copy(
                dailyQuizCompleted = newCompleted,
                averageScore = newAverage,
                totalQuizzesTaken = profile.totalQuizzesTaken + 1
            )
            _userProfile.value = updatedProfile

            viewModelScope.launch {
                SupabaseService.syncUserProfile(updatedProfile)
            }
        }
    }

    fun getSampleQuestionsForSubject(subjectName: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = "q1",
                subject = subjectName,
                questionText = "Which of the following represents the correct UNEB standard equation for kinetic energy?",
                options = listOf("Ek = 1/2 m v²", "Ek = m g h", "Ek = F × d", "Ek = m / v"),
                correctIndex = 0,
                explanation = "Kinetic energy is calculated using 1/2 mass × velocity squared.",
                pastPaperRef = "UNEB Past Paper 2022 Q4"
            ),
            QuizQuestion(
                id = "q2",
                subject = subjectName,
                questionText = "In UNEB examination diagrams, what is the primary organelle responsible for aerobic respiration?",
                options = listOf("Chloroplast", "Mitochondrion", "Ribosome", "Golgi Apparatus"),
                correctIndex = 1,
                explanation = "Mitochondria are the powerhouses where ATP energy is released.",
                pastPaperRef = "UNEB Biology 2021 Section A"
            ),
            QuizQuestion(
                id = "q3",
                subject = subjectName,
                questionText = "Solve for x in the UNEB linear equation: 3x - 7 = 14",
                options = listOf("x = 5", "x = 7", "x = 6", "x = 8"),
                correctIndex = 1,
                explanation = "3x = 14 + 7 = 21, so x = 21 / 3 = 7.",
                pastPaperRef = "UNEB Mathematics Paper 1 2023"
            )
        )
    }
}
