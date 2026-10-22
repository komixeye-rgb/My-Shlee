package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.model.SubjectScore
import com.example.model.SubscriptionPlan
import com.example.model.PaymentTransaction
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(
    viewModel: StudentDashboardViewModel,
    modifier: Modifier = Modifier
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val subjectScores by viewModel.subjectScores.collectAsStateWithLifecycle()
    val leaderboardCandidates by viewModel.leaderboardCandidates.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val subscriptionPlans by viewModel.subscriptionPlans.collectAsStateWithLifecycle()
    val paymentTransactions by viewModel.paymentTransactions.collectAsStateWithLifecycle()
    val isSupabaseConnected by viewModel.isSupabaseConnected.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val syncStatusMessage by viewModel.syncStatusMessage.collectAsStateWithLifecycle()

    var activeTab by remember { mutableIntStateOf(0) } // 0 = Radar, 1 = Leaderboard, 2 = Daily Goals, 3 = Payments
    var showShareModal by remember { mutableStateOf(false) }
    var showAuthModal by remember { mutableStateOf(false) }
    var selectedAiSubject by remember { mutableStateOf<SubjectScore?>(null) }
    var selectedPracticeSubject by remember { mutableStateOf<SubjectScore?>(null) }
    var selectedPlanForCheckout by remember { mutableStateOf<SubscriptionPlan?>(null) }

    val mainScrollState = rememberScrollState()

    Scaffold(
        topBar = {
            // Expanded Upper Control Panel with lowered controls and vertical padding
            Surface(
                color = SlateSurface,
                tonalElevation = 6.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .animateContentSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Logo & Shlee Dashboard
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_shulee_logo),
                            contentDescription = "Shlee Logo",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Shlee Dashboard",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Right Controls: Share Badge + Login / Signup Button
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { showShareModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("share_profile_header_button")
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = "Share Profile", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share Badge", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }

                        // Signup / Login Control Button
                        OutlinedButton(
                            onClick = { showAuthModal = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isLoggedIn) SlateSurfaceLight else AmberPrimary.copy(alpha = 0.15f),
                                contentColor = if (isLoggedIn) EmeraldLight else AmberLight
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = if (isLoggedIn) EmeraldDistinct else AmberPrimary
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("auth_login_header_button")
                        ) {
                            Icon(
                                imageVector = if (isLoggedIn) Icons.Default.AccountCircle else Icons.Default.Login,
                                contentDescription = "Auth",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isLoggedIn) "Account" else "Sign In",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            val weakestSubject = remember(subjectScores) {
                subjectScores.minByOrNull { it.scorePercentage }
            }
            LowerControlPanel(
                activeTab = activeTab,
                onTabSelected = { activeTab = it },
                userProfile = userProfile,
                weakestSubject = weakestSubject,
                onQuickPracticeClick = {
                    selectedPracticeSubject = weakestSubject ?: subjectScores.firstOrNull()
                },
                onQuickAiTutorClick = {
                    selectedAiSubject = weakestSubject ?: subjectScores.firstOrNull()
                }
            )
        },
        containerColor = SlateCanvas,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .drawRightScrollbar(mainScrollState)
                .verticalScroll(mainScrollState)
                .padding(16.dp)
                .animateContentSize()
        ) {
            // Welcome Header & Streak Banner with Supabase Cloud Status
            WelcomeHeaderCard(
                userProfile = userProfile,
                isSupabaseConnected = isSupabaseConnected,
                isSyncing = isSyncing,
                syncStatusMessage = syncStatusMessage,
                onRefreshSupabase = { viewModel.refreshFromSupabase() },
                onShareClick = { showShareModal = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Tabs with smooth selection transitions (4 Tabs)
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                containerColor = SlateSurface,
                contentColor = AmberPrimary,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    if (activeTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                            height = 3.dp,
                            color = AmberPrimary
                        )
                    }
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PieChart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Radar & Mastery", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    },
                    selectedContentColor = AmberPrimary,
                    unselectedContentColor = TextSecondary,
                    modifier = Modifier.testTag("tab_radar_overview")
                )

                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Leaderboard", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    },
                    selectedContentColor = AmberPrimary,
                    unselectedContentColor = TextSecondary,
                    modifier = Modifier.testTag("tab_leaderboard")
                )

                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Alarm, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Daily Goals", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    },
                    selectedContentColor = AmberPrimary,
                    unselectedContentColor = TextSecondary,
                    modifier = Modifier.testTag("tab_daily_goals")
                )

                Tab(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Payments", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    },
                    selectedContentColor = AmberPrimary,
                    unselectedContentColor = TextSecondary,
                    modifier = Modifier.testTag("tab_payments")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Smooth Animated Tab Content Transition
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        )
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut()
                        )
                    }.using(SizeTransform(clip = false))
                },
                label = "TabContentAnimation"
            ) { targetTab ->
                when (targetTab) {
                    0 -> {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            SubjectRadarChartSection(
                                subjectScores = subjectScores,
                                onPracticeSubject = { selectedPracticeSubject = it },
                                onAskAiTutor = { selectedAiSubject = it }
                            )

                            SubjectMasterySection(
                                subjectScores = subjectScores,
                                onPracticeSubject = { selectedPracticeSubject = it },
                                onAskAiTutor = { selectedAiSubject = it }
                            )
                        }
                    }

                    1 -> {
                        NationalLeaderboardSection(
                            candidates = leaderboardCandidates,
                            selectedLevelFilter = selectedFilter,
                            onFilterChanged = { viewModel.updateLevelFilter(it) }
                        )
                    }

                    2 -> {
                        NotificationSettingsCard(
                            userProfile = userProfile,
                            onToggleNotification = { viewModel.toggleNotification(it) },
                            onReminderTimeChanged = { viewModel.updateReminderTime(it) }
                        )
                    }

                    3 -> {
                        LandingPricingSection(
                            plans = subscriptionPlans,
                            onSelectPlan = { plan ->
                                selectedPlanForCheckout = plan
                            },
                            onAuthClick = {
                                showAuthModal = true
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Modals & Dialogs
    selectedPlanForCheckout?.let { plan ->
        PaymentCheckoutDialog(
            plan = plan,
            onPaymentSuccess = { subscribedPlan ->
                viewModel.addPaymentTransaction(subscribedPlan)
            },
            onDismiss = { selectedPlanForCheckout = null }
        )
    }

    if (showAuthModal) {
        AuthModal(
            isLoggedIn = isLoggedIn,
            currentStudentName = userProfile.name,
            onLoginSuccess = { name -> viewModel.loginUser(name) },
            onLogout = { viewModel.logoutUser() },
            onDismiss = { showAuthModal = false }
        )
    }

    if (showShareModal) {
        ProfileShareModal(
            userProfile = userProfile,
            onDismiss = { showShareModal = false }
        )
    }

    selectedAiSubject?.let { subject ->
        AiThinkingTutorDialog(
            subject = subject,
            onDismiss = { selectedAiSubject = null }
        )
    }

    selectedPracticeSubject?.let { subject ->
        PracticeQuizDialog(
            subject = subject,
            questions = viewModel.getSampleQuestionsForSubject(subject.subjectName),
            onQuizCompleted = { subjectId, newScore ->
                viewModel.updateSubjectScore(subjectId, newScore)
            },
            onDismiss = { selectedPracticeSubject = null }
        )
    }
}

/**
 * Draws an explicit right vertical scrollbar thumb on the right edge of the screen as the user scrolls up and down.
 */
private fun Modifier.drawRightScrollbar(
    scrollState: ScrollState,
    color: Color = AmberPrimary,
    width: Dp = 5.dp
): Modifier = this.drawWithContent {
    drawContent()
    if (scrollState.maxValue > 0) {
        val totalHeight = size.height
        val thumbHeight = (totalHeight * (totalHeight / (scrollState.maxValue + totalHeight))).coerceIn(40f, totalHeight)
        val scrollPercent = scrollState.value.toFloat() / scrollState.maxValue.toFloat()
        val thumbY = scrollPercent * (totalHeight - thumbHeight)

        drawRoundRect(
            color = color,
            topLeft = Offset(size.width - width.toPx() - 3.dp.toPx(), thumbY),
            size = Size(width.toPx(), thumbHeight),
            cornerRadius = CornerRadius(width.toPx() / 2, width.toPx() / 2),
            alpha = 0.85f
        )
    }
}

@Composable
private fun WelcomeHeaderCard(
    userProfile: com.example.model.UserProfile,
    isSupabaseConnected: Boolean?,
    isSyncing: Boolean,
    syncStatusMessage: String,
    onRefreshSupabase: () -> Unit,
    onShareClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Candidate ${userProfile.name}",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(color = EmeraldDistinct.copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                            Text(
                                text = userProfile.projectedAggregate,
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldLight,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "ID: ${userProfile.studentId} • UNEB Center 2026",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                // Streak Pill
                Surface(
                    color = FlameOrange.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FlameOrange.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔥", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${userProfile.currentStreak} Days",
                                style = MaterialTheme.typography.titleSmall,
                                color = FlameLight,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Streak", fontSize = 9.sp, color = TextSecondary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Cloud Sync Indicator Pill (Supabase SHLEE Project)
            Surface(
                onClick = onRefreshSupabase,
                color = when (isSupabaseConnected) {
                    true -> EmeraldDistinct.copy(alpha = 0.15f)
                    false -> FlameOrange.copy(alpha = 0.15f)
                    else -> SlateSurfaceLight
                },
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    when (isSupabaseConnected) {
                        true -> EmeraldLight.copy(alpha = 0.35f)
                        false -> FlameOrange.copy(alpha = 0.35f)
                        else -> SlateBorder
                    }
                ),
                modifier = Modifier.testTag("supabase_sync_status_chip")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(12.dp),
                            strokeWidth = 2.dp,
                            color = AmberPrimary
                        )
                    } else {
                        Icon(
                            imageVector = when (isSupabaseConnected) {
                                true -> Icons.Default.CloudDone
                                false -> Icons.Default.CloudOff
                                else -> Icons.Default.Sync
                            },
                            contentDescription = "Supabase Status",
                            tint = when (isSupabaseConnected) {
                                true -> EmeraldLight
                                false -> FlameLight
                                else -> TextSecondary
                            },
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSyncing) "Syncing with Supabase..." else syncStatusMessage,
                        style = MaterialTheme.typography.labelSmall,
                        color = when (isSupabaseConnected) {
                            true -> EmeraldLight
                            false -> FlameLight
                            else -> TextSecondary
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• Tap to sync",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary.copy(alpha = 0.7f),
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}
