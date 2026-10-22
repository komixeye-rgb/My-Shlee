package com.example.ui.components

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("notification_settings_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Daily Quiz Goal Progress Ring Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daily Study Goal & Reminders",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Automated candidate goal tracking & notification alarms",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                // Quiz Progress Ring
                Box(
                    modifier = Modifier.size(52.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val progress = (userProfile.dailyQuizCompleted.toFloat() / userProfile.dailyQuizGoal.toFloat()).coerceIn(0f, 1f)
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        color = AmberPrimary,
                        strokeWidth = 5.dp,
                        trackColor = SlateSurfaceLight
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${userProfile.dailyQuizCompleted}/${userProfile.dailyQuizGoal}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Quizzes", fontSize = 8.sp, color = TextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Divider(color = SlateBorder)

            Spacer(modifier = Modifier.height(14.dp))

            // Enable Notifications Toggle Row
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
                            .background(AmberPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = AmberLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Daily Revision Alarm",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Notify me if daily quiz goal is incomplete",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
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
                        checkedTrackColor = AmberPrimary,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = SlateSurfaceLight
                    ),
                    modifier = Modifier.testTag("notification_toggle_switch")
                )
            }

            // Reminder Time Options & Test Trigger
            AnimatedVisibility(visible = userProfile.notificationEnabled) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = "Preferred Reminder Time",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
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
                                    containerColor = if (isSelected) AmberPrimary.copy(alpha = 0.2f) else SlateSurfaceLight,
                                    contentColor = if (isSelected) AmberLight else TextSecondary
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) AmberPrimary else SlateBorder
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

                    // Send Test Notification Button
                    Button(
                        onClick = {
                            NotificationHelper.sendInstantTestNotification(
                                context = context,
                                completedGoal = userProfile.dailyQuizCompleted,
                                totalGoal = userProfile.dailyQuizGoal,
                                streakDays = userProfile.currentStreak
                            )
                            Toast.makeText(context, "🚀 Test Notification dispatched to status bar!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SlateSurfaceLight),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("test_notification_button")
                    ) {
                        Icon(Icons.Default.Alarm, contentDescription = null, tint = AmberLight, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Test System Notification Banner",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
