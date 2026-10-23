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

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }

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

    fun updateCandidateLevel(level: String) {
        val updated = _userProfile.value.copy(candidateLevel = level)
        _userProfile.value = updated
        viewModelScope.launch {
            SupabaseService.syncUserProfile(updated)
        }
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

    fun recordCompletedPomodoroSession(durationMinutes: Int = 25) {
        val profile = _userProfile.value
        val newSessions = profile.focusSessionsCompleted + 1
        val updated = profile.copy(
            focusSessionsCompleted = newSessions
        )
        _userProfile.value = updated
        viewModelScope.launch {
            SupabaseService.syncUserProfile(updated)
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

    fun getMilestonesForLevel(level: String): List<ExamMilestone> {
        return when {
            level.contains("PLE", ignoreCase = true) || level.contains("Primary", ignoreCase = true) -> listOf(
                ExamMilestone(
                    id = "ple_01",
                    title = "Term 1 Diagnostic & Syllabus Mapping",
                    dateLabel = "15 March 2026",
                    status = MilestoneStatus.COMPLETED,
                    daysRemaining = null,
                    phase = "Term 1 Foundation",
                    description = "Comprehensive review of P.5 - P.7 topics across Mathematics, English, SST, and Science.",
                    keyTips = listOf("Master long division & fraction word problems", "Create personal SST map summaries"),
                    targetLevel = "PLE",
                    unebRefCode = "PLE-2026-F1"
                ),
                ExamMilestone(
                    id = "ple_02",
                    title = "District Joint Pre-PLE Mock Exams",
                    dateLabel = "10 August 2026",
                    status = MilestoneStatus.IN_PROGRESS,
                    daysRemaining = 24,
                    phase = "Mock Assessment",
                    description = "District-wide timed examinations replicating the official UNEB test center conditions.",
                    keyTips = listOf("Time management: 2 hrs 15 mins per paper", "Eliminate careless spelling errors in English & Science"),
                    targetLevel = "PLE",
                    unebRefCode = "MOCK-KLA-PLE"
                ),
                ExamMilestone(
                    id = "ple_03",
                    title = "Final 5-Year Past Papers Speed Drill",
                    dateLabel = "25 September 2026",
                    status = MilestoneStatus.UPCOMING,
                    daysRemaining = 70,
                    phase = "Intensive Revision",
                    description = "Full simulated past paper papers (2020-2025) with step-by-step marking schemes.",
                    keyTips = listOf("Review diagrams of the human digestive and circulatory systems", "Memorize key historical dates for East Africa"),
                    targetLevel = "PLE",
                    unebRefCode = "SPRINT-PLE"
                ),
                ExamMilestone(
                    id = "ple_04",
                    title = "Official PLE Candidate Briefing",
                    dateLabel = "02 November 2026",
                    status = MilestoneStatus.UPCOMING,
                    daysRemaining = 108,
                    phase = "Pre-Exam Readiness",
                    description = "Headteacher & invigilator briefing on candidate index verification and examination room rules.",
                    keyTips = listOf("Check registration index number slip", "Prepare transparent geometry set & non-smudge pens"),
                    targetLevel = "PLE",
                    unebRefCode = "BRIEF-PLE-2026"
                ),
                ExamMilestone(
                    id = "ple_05",
                    title = "National PLE Examinations D-Day",
                    dateLabel = "04 - 05 November 2026",
                    status = MilestoneStatus.CRITICAL,
                    daysRemaining = 110,
                    phase = "National UNEB Exam",
                    description = "Official national sitting: Mathematics & Social Studies (Day 1), Integrated Science & English (Day 2).",
                    keyTips = listOf("Read each question twice before attempting", "Keep calm and stay focused for aggregate 4 distinction"),
                    targetLevel = "PLE",
                    unebRefCode = "UNEB-PLE-MAIN"
                )
            )
            level.contains("UACE", ignoreCase = true) || level.contains("Senior 6", ignoreCase = true) -> listOf(
                ExamMilestone(
                    id = "uace_01",
                    title = "Principal Subject Audit & Paper 3 Practicals",
                    dateLabel = "28 February 2026",
                    status = MilestoneStatus.COMPLETED,
                    daysRemaining = null,
                    phase = "Term 1 Foundation",
                    description = "Laboratory skills audit for Biology, Chemistry, and Physics alongside Subsidiary ICT/Maths.",
                    keyTips = listOf("Verify titration and food tests procedures", "Review General Paper essay structure"),
                    targetLevel = "UACE",
                    unebRefCode = "UACE-AUDIT"
                ),
                ExamMilestone(
                    id = "uace_02",
                    title = "National Joint Mock Examinations",
                    dateLabel = "01 August 2026",
                    status = MilestoneStatus.IN_PROGRESS,
                    daysRemaining = 15,
                    phase = "Joint Mock Trials",
                    description = "Comprehensive multi-school joint mocks covering both Paper 1, Paper 2, and practical components.",
                    keyTips = listOf("Focus on 20-mark essay articulation", "Maintain numerical precision in Calculus and Mechanics"),
                    targetLevel = "UACE",
                    unebRefCode = "MOCK-UACE-2026"
                ),
                ExamMilestone(
                    id = "uace_03",
                    title = "University Cut-Off Benchmark Sprint",
                    dateLabel = "15 September 2026",
                    status = MilestoneStatus.UPCOMING,
                    daysRemaining = 60,
                    phase = "Competitive Distinction",
                    description = "Focus on securing 20 points for Government Merit scholarship admissions at Makerere, Kyambogo, and Mbarara.",
                    keyTips = listOf("Target 'A' grades in all three principal subjects", "Practice 2.5 hour time splits for lengthy calculations"),
                    targetLevel = "UACE",
                    unebRefCode = "SPRINT-UACE"
                ),
                ExamMilestone(
                    id = "uace_04",
                    title = "National UACE Examinations Commencement",
                    dateLabel = "09 November 2026",
                    status = MilestoneStatus.CRITICAL,
                    daysRemaining = 115,
                    phase = "National UNEB Exam",
                    description = "Commencement of A-Level national papers nationwide across all registered examination centers.",
                    keyTips = listOf("Ensure proper labeling of answer booklets", "Review past paper examiners' reports for common pitfalls"),
                    targetLevel = "UACE",
                    unebRefCode = "UNEB-UACE-MAIN"
                )
            )
            else -> listOf( // Default UCE (Senior 4 / O-Level)
                ExamMilestone(
                    id = "uce_01",
                    title = "Term 1 Syllabus Completion & Diagnostic Test",
                    dateLabel = "20 March 2026",
                    status = MilestoneStatus.COMPLETED,
                    daysRemaining = null,
                    phase = "Foundation & Diagnosis",
                    description = "Baseline assessment of core O-Level syllabus across 8 registered subjects.",
                    keyTips = listOf("Identify weakest topics in Physics and Biology", "Consolidate Mathematics Paper 1 formula notes"),
                    targetLevel = "UCE",
                    unebRefCode = "UCE-TERM1-DIAG"
                ),
                ExamMilestone(
                    id = "uce_02",
                    title = "Regional Joint Mock Examinations (Wakiso & Kampala)",
                    dateLabel = "14 July 2026",
                    status = MilestoneStatus.IN_PROGRESS,
                    daysRemaining = 12,
                    phase = "Regional Joint Mocks",
                    description = "Rigorous multi-school mock exams benchmarked against the distinction 1 standard (80%+).",
                    keyTips = listOf("Practice Section B structured essay formats", "Double check chemical equation balancing in Chemistry"),
                    targetLevel = "UCE",
                    unebRefCode = "WAKISHA-MOCK-2026"
                ),
                ExamMilestone(
                    id = "uce_03",
                    title = "10-Year UNEB Past Papers Intensive Sprint",
                    dateLabel = "01 September 2026",
                    status = MilestoneStatus.UPCOMING,
                    daysRemaining = 46,
                    phase = "Intensive Past Papers",
                    description = "Daily timed drills analyzing repeating UNEB question patterns from 2014 to 2025.",
                    keyTips = listOf("Time yourself: 2.5 hours strictly without phone distractions", "Consult AI Revision Tutor for step-by-step math proofs"),
                    targetLevel = "UCE",
                    unebRefCode = "PASTPAPERS-UCE-10Y"
                ),
                ExamMilestone(
                    id = "uce_04",
                    title = "Official UNEB Candidate Briefing Day",
                    dateLabel = "09 October 2026",
                    status = MilestoneStatus.UPCOMING,
                    daysRemaining = 84,
                    phase = "Examination Readiness",
                    description = "Mandatory in-person briefing by Chief Invigilators covering timetable, index numbers, and exam ethics.",
                    keyTips = listOf("Verify your photograph and name on the UNEB center register", "Inspect authorized calculator models"),
                    targetLevel = "UCE",
                    unebRefCode = "BRIEF-UCE-2026"
                ),
                ExamMilestone(
                    id = "uce_05",
                    title = "National UCE Examinations Commencement (D-Day)",
                    dateLabel = "12 October 2026",
                    status = MilestoneStatus.CRITICAL,
                    daysRemaining = 87,
                    phase = "National UNEB Exam",
                    description = "Commencement of main national papers (Physics, Mathematics, Biology, Chemistry, English, Geography).",
                    keyTips = listOf("Arrive at the examination hall 30 minutes early", "Aim for Distinction 1 in all 8 compulsory subjects for Aggregate 8"),
                    targetLevel = "UCE",
                    unebRefCode = "UNEB-UCE-MAIN"
                ),
                ExamMilestone(
                    id = "uce_06",
                    title = "National Grading & Division 1 Release",
                    dateLabel = "18 January 2027",
                    status = MilestoneStatus.UPCOMING,
                    daysRemaining = 185,
                    phase = "Results & Admissions",
                    description = "Official release of UNEB results by the Minister of Education and Senior 5 merit selections.",
                    keyTips = listOf("Check SMS verification via 6600", "Prepare academic transcripts for A-Level school placement"),
                    targetLevel = "UCE",
                    unebRefCode = "UNEB-RESULTS-2026"
                )
            )
        }
    }
}
