package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Locale

enum class PomodoroSessionType(
    val title: String,
    val durationSeconds: Int,
    val primaryColor: Color,
    val iconDescription: String
) {
    FOCUS_25("25m Focus", 25 * 60, EduRoyalBlue, "Focus Session"),
    SHORT_BREAK("5m Break", 5 * 60, EduTealGreen, "Short Recovery Break"),
    LONG_BREAK("15m Review", 15 * 60, EduSkyCyan, "Deep Review Break")
}

@Composable
fun PomodoroTimerWidget(
    modifier: Modifier = Modifier,
    completedSessions: Int = 4,
    onSessionCompleted: (sessionType: PomodoroSessionType) -> Unit = {}
) {
    var selectedType by remember { mutableStateOf(PomodoroSessionType.FOCUS_25) }
    var remainingSeconds by remember { mutableIntStateOf(selectedType.durationSeconds) }
    var isRunning by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }
    var isCollapsed by remember { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current

    // Countdown Coroutine Engine
    LaunchedEffect(isRunning, isPaused, remainingSeconds) {
        if (isRunning && !isPaused && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds -= 1
            if (remainingSeconds == 0) {
                isRunning = false
                isPaused = false
                isCompleted = true
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onSessionCompleted(selectedType)
            }
        }
    }

    // Reset when user selects another session type while idle
    fun selectSessionType(type: PomodoroSessionType) {
        selectedType = type
        remainingSeconds = type.durationSeconds
        isRunning = false
        isPaused = false
        isCompleted = false
    }

    fun startTimer() {
        if (remainingSeconds == 0) {
            remainingSeconds = selectedType.durationSeconds
        }
        isRunning = true
        isPaused = false
        isCompleted = false
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    fun pauseTimer() {
        isPaused = true
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    fun resumeTimer() {
        isPaused = false
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    fun resetTimer() {
        isRunning = false
        isPaused = false
        isCompleted = false
        remainingSeconds = selectedType.durationSeconds
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    val totalDuration = selectedType.durationSeconds
    val progress = if (totalDuration > 0) {
        (remainingSeconds.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 400),
        label = "PomodoroProgressAnimation"
    )

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    Card(
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, SlateBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("pomodoro_timer_card")
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Header: Title, Streak Badge, Collapse Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(selectedType.primaryColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = "Pomodoro Study Timer",
                            tint = selectedType.primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "UNEB Pomodoro Focus Timer",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = EduTextPrimary
                            )
                        }
                        Text(
                            text = if (isRunning) "Deep focus session in progress" else "Timed intervals for maximum retention",
                            fontSize = 11.sp,
                            color = EduTextSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Session count pill
                    Surface(
                        color = EduSunnyAmber.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "🔥 $completedSessions Sessions",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EduSunnyAmberContainer
                            )
                        }
                    }

                    IconButton(
                        onClick = { isCollapsed = !isCollapsed },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("pomodoro_collapse_toggle")
                    ) {
                        Icon(
                            imageVector = if (isCollapsed) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                            contentDescription = if (isCollapsed) "Expand Timer" else "Collapse Timer",
                            tint = EduTextSecondary
                        )
                    }
                }
            }

            // Compact View when collapsed
            if (isCollapsed) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SlateSurfaceLight)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = selectedType.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = selectedType.primaryColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = timeFormatted,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            color = EduTextPrimary,
                            modifier = Modifier.testTag("pomodoro_time_display_compact")
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            color = selectedType.primaryColor,
                            trackColor = SlateBorder,
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!isRunning || isPaused) {
                            IconButton(
                                onClick = {
                                    if (isPaused) resumeTimer() else startTimer()
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("pomodoro_compact_start_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Start",
                                    tint = EduRoyalBlue
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { pauseTimer() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("pomodoro_compact_pause_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Pause,
                                    contentDescription = "Pause",
                                    tint = EduCoralRose
                                )
                            }
                        }

                        IconButton(
                            onClick = { resetTimer() },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("pomodoro_compact_reset_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset",
                                tint = EduTextSecondary
                            )
                        }
                    }
                }
            } else {
                // Expanded View with Full Circular Countdown
                Spacer(modifier = Modifier.height(14.dp))

                // Mode Selector Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PomodoroSessionType.values().forEach { sessionType ->
                        val isSelected = selectedType == sessionType
                        val chipTag = when (sessionType) {
                            PomodoroSessionType.FOCUS_25 -> "pomodoro_preset_25"
                            PomodoroSessionType.SHORT_BREAK -> "pomodoro_preset_5"
                            PomodoroSessionType.LONG_BREAK -> "pomodoro_preset_15"
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) sessionType.primaryColor.copy(alpha = 0.12f) else SlateSurfaceLight,
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) sessionType.primaryColor else SlateBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(enabled = !isRunning || isPaused) {
                                    selectSessionType(sessionType)
                                }
                                .testTag(chipTag)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = sessionType.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) sessionType.primaryColor else EduTextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Circular Progress Dial
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val strokeWidth = 10.dp
                    val ringColor = selectedType.primaryColor
                    val trackColor = SlateBorder.copy(alpha = 0.6f)

                    Canvas(modifier = Modifier.size(150.dp)) {
                        val strokePx = strokeWidth.toPx()
                        // Track background circle
                        drawCircle(
                            color = trackColor,
                            style = Stroke(width = strokePx)
                        )

                        // Animated progress arc
                        drawArc(
                            color = ringColor,
                            startAngle = -90f,
                            sweepAngle = animatedProgress * 360f,
                            useCenter = false,
                            style = Stroke(width = strokePx, cap = StrokeCap.Round)
                        )
                    }

                    // Center Content: Time & Status
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            color = selectedType.primaryColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = when {
                                    isCompleted -> "COMPLETED 🎉"
                                    isRunning && !isPaused -> "FOCUSING ⚡"
                                    isPaused -> "PAUSED ⏸️"
                                    else -> "READY 🎯"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = selectedType.primaryColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = timeFormatted,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            color = EduTextPrimary,
                            letterSpacing = 1.sp,
                            modifier = Modifier.testTag("pomodoro_time_display")
                        )

                        Text(
                            text = selectedType.iconDescription,
                            fontSize = 11.sp,
                            color = EduTextMuted
                        )
                    }
                }

                // Completion Celebration State
                if (isCompleted) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = EduTealGreen.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, EduTealGreen.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EduTealGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Bravo! ${selectedType.title} Finished 🎉",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EduTealDark
                                )
                                Text(
                                    text = if (selectedType == PomodoroSessionType.FOCUS_25)
                                        "Focus session recorded to your streak! Ready for a 5m break?"
                                    else
                                        "Break time over. Ready to start another focus block?",
                                    fontSize = 11.sp,
                                    color = EduTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (selectedType == PomodoroSessionType.FOCUS_25) {
                            Button(
                                onClick = {
                                    selectSessionType(PomodoroSessionType.SHORT_BREAK)
                                    startTimer()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EduTealGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("pomodoro_break_button")
                            ) {
                                Text("Start 5m Break ☕", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        Button(
                            onClick = {
                                selectSessionType(PomodoroSessionType.FOCUS_25)
                                startTimer()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EduRoyalBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("pomodoro_new_session_button")
                        ) {
                            Text("New 25m Focus 🎯", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                } else {
                    // Standard Controls
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Start / Pause / Resume Button
                        if (!isRunning) {
                            Button(
                                onClick = { startTimer() },
                                colors = ButtonDefaults.buttonColors(containerColor = selectedType.primaryColor),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("pomodoro_start_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Start ${selectedType.title}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        } else if (!isPaused) {
                            Button(
                                onClick = { pauseTimer() },
                                colors = ButtonDefaults.buttonColors(containerColor = EduCoralRose),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("pomodoro_pause_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Pause,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Pause Session",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        } else {
                            Button(
                                onClick = { resumeTimer() },
                                colors = ButtonDefaults.buttonColors(containerColor = selectedType.primaryColor),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("pomodoro_resume_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Resume",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        // Reset Button
                        OutlinedButton(
                            onClick = { resetTimer() },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, SlateBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EduTextPrimary),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("pomodoro_reset_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Timer",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Study encouragement footer
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateSurfaceLight)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = selectedType.primaryColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pro Tip: Silence phones and study one topic intensely without multitasking.",
                        fontSize = 11.sp,
                        color = EduTextSecondary
                    )
                }
            }
        }
    }
}
