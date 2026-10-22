package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SubjectScore
import com.example.ui.theme.*

@Composable
fun SubjectMasterySection(
    subjectScores: List<SubjectScore>,
    onPracticeSubject: (SubjectScore) -> Unit,
    onAskAiTutor: (SubjectScore) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubject by remember { mutableStateOf(subjectScores.firstOrNull()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SlateSurface)
            .border(1.dp, SlateBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("subject_mastery_section")
    ) {
        // Card Title & Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        tint = AmberLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Subject Mastery & Mock Exam Diagnostics",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Official UNEB Grading Scale (D1 to F9) based on Mock Exam Results",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Subject Proficiency Level Bars
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            subjectScores.forEach { subject ->
                val isSelected = selectedSubject?.id == subject.id
                SubjectProficiencyRow(
                    subject = subject,
                    isSelected = isSelected,
                    onClick = { selectedSubject = subject },
                    onPractice = { onPracticeSubject(subject) },
                    onAskAi = { onAskAiTutor(subject) }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Divider(color = SlateBorder)

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Subject Mock Exam Trend Chart (Mock 1 -> Mock 2 -> Mock 3)
        selectedSubject?.let { sub ->
            MockExamTrendChartCard(subject = sub)
        }
    }
}

@Composable
private fun SubjectProficiencyRow(
    subject: SubjectScore,
    isSelected: Boolean,
    onClick: () -> Unit,
    onPractice: () -> Unit,
    onAskAi: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SlateSurfaceLight else SlateSurface
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isSelected) AmberPrimary else SlateBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("subject_row_${subject.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = subject.subjectName,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = subject.masteryColor.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = subject.masteryLevelLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = subject.masteryColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "UNEB Grade: ${subject.unebGrade}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "${subject.scorePercentage.toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = subject.masteryColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar with UNEB 80% D1 Benchmark Marker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(SlateSurfaceLight)
            ) {
                // Progress Fill
                val progressFraction = (subject.scorePercentage / 100f).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressFraction)
                        .clip(CircleShape)
                        .background(subject.masteryColor)
                )

                // D1 Distinction Benchmark Line (at 80%)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .align(Alignment.CenterStart)
                        .offset(x = 240.dp) // Visual marker offset
                        .background(ShuleeBlueLight)
                )
            }
        }
    }
}

@Composable
private fun MockExamTrendChartCard(subject: SubjectScore) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SlateCanvas)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = AmberLight,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${subject.subjectName} Mock Exam Progression",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Target: 80% D1",
                style = MaterialTheme.typography.labelSmall,
                color = ShuleeBlueLight,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Canvas Line Chart for Mock 1, Mock 2, Mock 3
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Draw horizontal benchmark line (80%)
                val benchmarkY = height * (1f - 0.80f)
                drawLine(
                    color = ShuleeBlueLight.copy(alpha = 0.6f),
                    start = Offset(0f, benchmarkY),
                    end = Offset(width, benchmarkY),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f))
                )

                // Draw mock points
                val history = subject.mockHistory
                if (history.size >= 3) {
                    val p1 = Offset(width * 0.15f, height * (1f - history[0] / 100f))
                    val p2 = Offset(width * 0.50f, height * (1f - history[1] / 100f))
                    val p3 = Offset(width * 0.85f, height * (1f - history[2] / 100f))

                    val linePath = Path().apply {
                        moveTo(p1.x, p1.y)
                        lineTo(p2.x, p2.y)
                        lineTo(p3.x, p3.y)
                    }

                    drawPath(
                        path = linePath,
                        color = subject.masteryColor,
                        style = Stroke(width = 4f)
                    )

                    // Draw points
                    listOf(p1, p2, p3).forEachIndexed { index, pt ->
                        drawCircle(color = subject.masteryColor, radius = 7.dp.toPx(), center = pt)
                        drawCircle(color = Color.White, radius = 3.5.dp.toPx(), center = pt)
                    }
                }
            }
        }

        // Labels below chart
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Mock 1 (${subject.mockHistory.getOrElse(0) { 0f }.toInt()}%)", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            Text("Mock 2 (${subject.mockHistory.getOrElse(1) { 0f }.toInt()}%)", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            Text("Mock 3 (${subject.mockHistory.getOrElse(2) { 0f }.toInt()}%)", style = MaterialTheme.typography.labelSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
        }
    }
}
