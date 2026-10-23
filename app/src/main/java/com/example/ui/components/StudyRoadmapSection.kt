package com.example.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExamMilestone
import com.example.model.MilestoneStatus
import com.example.model.SubjectScore
import com.example.model.UserProfile
import com.example.ui.theme.*

@Composable
fun StudyRoadmapSection(
    userProfile: UserProfile,
    milestones: List<ExamMilestone>,
    selectedLevel: String,
    onLevelSelected: (String) -> Unit,
    onPracticeClick: () -> Unit,
    onAskAiClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val levels = listOf("UCE (Senior 4)", "PLE (Primary 7)", "UACE (Senior 6)")

    val completedCount = milestones.count { it.status == MilestoneStatus.COMPLETED }
    val totalCount = milestones.size
    val progressPct = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    val nextExamMilestone = milestones.firstOrNull { it.status == MilestoneStatus.IN_PROGRESS || it.status == MilestoneStatus.CRITICAL }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .animateContentSize()
            .testTag("study_roadmap_section")
    ) {
        // Section Title & Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RoyalIndigo.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = "Study Roadmap",
                        tint = RoyalIndigo,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "UNEB Study Roadmap",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "National Exam Timetable & Revision Milestones",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            // Target Badge
            Surface(
                color = AmberPrimary.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.35f))
            ) {
                Text(
                    text = userProfile.projectedAggregate,
                    style = MaterialTheme.typography.labelSmall,
                    color = AmberLight,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Level Filter Chips (UCE, PLE, UACE)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            levels.forEach { levelOption ->
                val isSelected = selectedLevel.contains(levelOption.take(3), ignoreCase = true)
                Surface(
                    onClick = { onLevelSelected(levelOption) },
                    color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) EmeraldLight else MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("level_chip_${levelOption.take(4)}")
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = levelOption.split(" ").first(),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Roadmap Progress & Countdown Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, RoyalIndigo.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Revision Progress",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "$completedCount of $totalCount Milestones Cleared",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (nextExamMilestone?.daysRemaining != null) {
                        Surface(
                            color = CoralSalmon.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CoralSalmon.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = CoralSalmon, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${nextExamMilestone.daysRemaining} Days to Exam",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CoralSalmon,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { progressPct },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = EmeraldLight,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Visual Connected Timeline
        Column(modifier = Modifier.fillMaxWidth()) {
            milestones.forEachIndexed { index, milestone ->
                val isLast = index == milestones.lastIndex
                TimelineNodeItem(
                    milestone = milestone,
                    isLast = isLast,
                    onPracticeClick = onPracticeClick,
                    onAskAiClick = onAskAiClick
                )
            }
        }
    }
}

@Composable
private fun TimelineNodeItem(
    milestone: ExamMilestone,
    isLast: Boolean,
    onPracticeClick: () -> Unit,
    onAskAiClick: () -> Unit
) {
    val nodeColor = Color(milestone.status.colorHex)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("timeline_node_${milestone.id}")
    ) {
        // Vertical timeline marker column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            // Node Icon / Circle
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(nodeColor.copy(alpha = 0.2f))
                    .border(2.dp, nodeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                when (milestone.status) {
                    MilestoneStatus.COMPLETED -> Icon(Icons.Default.Check, contentDescription = null, tint = nodeColor, modifier = Modifier.size(14.dp))
                    MilestoneStatus.IN_PROGRESS -> Icon(Icons.Default.Flag, contentDescription = null, tint = nodeColor, modifier = Modifier.size(14.dp))
                    MilestoneStatus.CRITICAL -> Icon(Icons.Default.School, contentDescription = null, tint = nodeColor, modifier = Modifier.size(14.dp))
                    MilestoneStatus.UPCOMING -> Icon(Icons.Default.CalendarToday, contentDescription = null, tint = nodeColor, modifier = Modifier.size(12.dp))
                }
            }

            // Connecting Vertical Line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(150.dp)
                        .background(
                            if (milestone.status == MilestoneStatus.COMPLETED) EmeraldLight.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Milestone Details Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = when (milestone.status) {
                    MilestoneStatus.IN_PROGRESS -> MaterialTheme.colorScheme.surfaceVariant
                    MilestoneStatus.CRITICAL -> MaterialTheme.colorScheme.surfaceVariant
                    else -> MaterialTheme.colorScheme.background
                }
            ),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(
                width = if (milestone.status == MilestoneStatus.IN_PROGRESS) 1.5.dp else 1.dp,
                color = if (milestone.status == MilestoneStatus.IN_PROGRESS) AmberPrimary else MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (isLast) 0.dp else 16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Phase & Status Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = nodeColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = milestone.phase.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = nodeColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (milestone.daysRemaining != null) {
                            Text(
                                text = "⏳ ${milestone.daysRemaining}d left",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (milestone.daysRemaining <= 15) CoralSalmon else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = milestone.status.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = nodeColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Title & Date
                Text(
                    text = milestone.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = milestone.dateLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = AmberLight,
                        fontSize = 11.sp
                    )
                    if (milestone.unebRefCode != null) {
                        Text(text = " • ", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        Text(
                            text = milestone.unebRefCode,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = milestone.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )

                if (milestone.keyTips.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "💡 Key Revision Strategies:",
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                        milestone.keyTips.forEach { tip ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text("• ", color = AmberLight, fontSize = 11.sp)
                                Text(
                                    text = tip,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                if (milestone.status == MilestoneStatus.IN_PROGRESS || milestone.status == MilestoneStatus.UPCOMING) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onPracticeClick,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberLight),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Practice Quiz", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onAskAiClick,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalIndigo),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ask AI Tutor", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
