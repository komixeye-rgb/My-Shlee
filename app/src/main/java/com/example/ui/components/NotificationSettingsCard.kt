package com.example.ui.components

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyStudyTask
import com.example.model.UserProfile
import com.example.ui.theme.*
import com.example.util.NotificationHelper

@Composable
fun NotificationSettingsCard(
    userProfile: UserProfile,
    onToggleNotification: (Boolean) -> Unit,
    onReminderTimeChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val timeOptions = listOf("5:00 PM", "7:00 PM", "8:00 PM", "9:00 PM")

    // Interactive Daily Study Tasks List
    var dailyTasks by remember {
        mutableStateOf(
            listOf(
                DailyStudyTask("t1", "Complete 1 Mathematics UNEB Practice Quiz", "Math", true),
                DailyStudyTask("t2", "Review 1 Weak Biology Topic via AI Tutor", "Biology", true),
                DailyStudyTask("t3", "Solve 5 Section B Physics Numerical Questions", "Physics", true),
                DailyStudyTask("t4", "Attempt 1 SST Climate Map Reading Exercise", "SST", false),
                DailyStudyTask("t5", "Maintain 14-day Leaderboard Study Streak", "General", false)
            )
        )
    }

    val completedCount = dailyTasks.count { it.isCompleted }
    val totalCount = dailyTasks.size
    val targetProgress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    // Animated Circular Progress Indicator
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 800),
        label = "CircularProgressAnimation"
    )

    // Android 13+ Notification Permission Request Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onToggleNotification(true)
            Toast.makeText(context, "🔔 Study reminders enabled!", Toast.LENGTH_SHORT).show()
        } else {
            onToggleNotification(false)
            Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_study_goals_section")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TaskAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Daily Study Goals",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Track and complete your daily UNEB revision tasks",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "🔥 ${userProfile.currentStreak}d Streak",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Prominent Circular Progress Indicator & Stats Summary
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // Circular Progress Gauge
                    Box(
                        modifier = Modifier.size(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.fillMaxSize(),
                            color = if (animatedProgress >= 1f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                            strokeWidth = 9.dp,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${(animatedProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (animatedProgress >= 1f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$completedCount of $totalCount Done",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(
                            text = if (animatedProgress >= 1f) "🎉 Daily Target Reached!" else "Keep Going Candidate!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (animatedProgress >= 1f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${totalCount - completedCount} tasks remaining today",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Target: UNEB Aggregate 8 D1",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Interactive Daily Task Checklist
            Text(
                text = "Today's Checklist",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                dailyTasks.forEachIndexed { index, task ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = if (task.isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val updated = dailyTasks.toMutableList()
                                updated[index] = task.copy(isCompleted = !task.isCompleted)
                                dailyTasks = updated
                            }
                            .testTag("task_checkbox_$index")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                modifier = Modifier.weight(1f)
                            )

                            Surface(
                                color = MaterialTheme.colorScheme.background,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = task.subjectTag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(color = SlateBorder)

            Spacer(modifier = Modifier.height(16.dp))

            // Enable Notifications & Reminder Settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Daily Revision Alarm",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Remind me if daily goal is incomplete",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                Switch(
                    checked = userProfile.notificationEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            onToggleNotification(enabled)
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.testTag("notification_toggle_switch")
                )
            }

            // Time Selector & Test Button
            AnimatedVisibility(visible = userProfile.notificationEnabled) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = "Preferred Reminder Time",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        timeOptions.forEach { timeStr ->
                            val isSelected = userProfile.reminderTime == timeStr
                            OutlinedButton(
                                onClick = { onReminderTimeChanged(timeStr) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("time_option_$timeStr")
                            ) {
                                Text(timeStr, style = MaterialTheme.typography.labelSmall, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            NotificationHelper.sendInstantTestNotification(
                                context = context,
                                completedGoal = completedCount,
                                totalGoal = totalCount,
                                streakDays = userProfile.currentStreak
                            )
                            Toast.makeText(context, "🚀 Test Notification dispatched to status bar!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("test_notification_button")
                    ) {
                        Icon(Icons.Default.Alarm, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Send Test System Notification",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
