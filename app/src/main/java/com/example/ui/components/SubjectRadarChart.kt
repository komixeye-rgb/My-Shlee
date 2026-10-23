package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SubjectScore
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SubjectRadarChartSection(
    subjectScores: List<SubjectScore>,
    onPracticeSubject: (SubjectScore) -> Unit,
    onAskAiTutor: (SubjectScore) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubject by remember { mutableStateOf<SubjectScore?>(null) }
    // Radar Chart is hidden from UI by default as requested, but retained in code/state
    var isRadarChartVisible by remember { mutableStateOf(false) }
    val focusSubjects = subjectScores.filter { it.needsRevision }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("subject_radar_chart_section")
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Subject Performance Diagnostic",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "UNEB Distinction 1 Baseline (80%) Analysis",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Toggle Show / Hide Radar Button
                OutlinedButton(
                    onClick = { isRadarChartVisible = !isRadarChartVisible },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isRadarChartVisible) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (isRadarChartVisible) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("toggle_radar_visibility_button")
                ) {
                    Icon(
                        imageVector = if (isRadarChartVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Radar",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isRadarChartVisible) "Hide Radar" else "Show Radar",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Animated Radar Chart Canvas (hidden by default)
        AnimatedVisibility(visible = isRadarChartVisible) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    contentAlignment = Alignment.Center
                ) {
                    RadarChartCanvas(
                        subjectScores = subjectScores,
                        selectedSubject = selectedSubject,
                        onSubjectSelected = { selectedSubject = it }
                    )
                }

                // Legend row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Your Competence", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("UNEB D1 Target (80%)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Focus Areas Header & List
        Text(
            text = "Revision Focus Radar (<65%)",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Subjects requiring urgent attention to achieve distinction aggregate",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (focusSubjects.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🌟", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Outstanding! All subjects are above the 65% benchmark line.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                focusSubjects.forEach { subject ->
                    FocusSubjectCard(
                        subject = subject,
                        onPractice = { onPracticeSubject(subject) },
                        onAskAi = { onAskAiTutor(subject) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RadarChartCanvas(
    subjectScores: List<SubjectScore>,
    selectedSubject: SubjectScore?,
    onSubjectSelected: (SubjectScore) -> Unit
) {
    val count = subjectScores.size
    if (count == 0) return

    val angleStep = (2 * Math.PI / count).toFloat()

    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val errorColor = MaterialTheme.colorScheme.error
    val primaryColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurface

    val outlineColor = MaterialTheme.colorScheme.outline

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(subjectScores) {
                detectTapGestures { tapOffset ->
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f
                    val radius = (minOf(size.width, size.height) / 2f) * 0.72f

                    var closest: SubjectScore? = null
                    var minDist = Float.MAX_VALUE

                    subjectScores.forEachIndexed { idx, sub ->
                        val angle = idx * angleStep - Math.PI.toFloat() / 2f
                        val vx = centerX + radius * cos(angle)
                        val vy = centerY + radius * sin(angle)
                        val dist = (tapOffset.x - vx) * (tapOffset.x - vx) + (tapOffset.y - vy) * (tapOffset.y - vy)
                        if (dist < minDist && dist < 4000f) {
                            minDist = dist
                            closest = sub
                        }
                    }
                    closest?.let { onSubjectSelected(it) }
                }
            }
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = (minOf(size.width, size.height) / 2f) * 0.70f

        val gridLevels = listOf(0.2f, 0.4f, 0.6f, 0.8f, 1.0f)
        val outlineColorCapt = outlineColor // outlineColor is already in scope from the outer function but let's be safe

        gridLevels.forEach { level ->
            val gridPath = Path()
            for (i in 0 until count) {
                val angle = i * angleStep - Math.PI.toFloat() / 2f
                val r = radius * level
                val x = centerX + r * cos(angle)
                val y = centerY + r * sin(angle)
                if (i == 0) gridPath.moveTo(x, y) else gridPath.lineTo(x, y)
            }
            gridPath.close()

            drawPath(
                path = gridPath,
                color = outlineColorCapt.copy(alpha = if (level == 0.8f) 0.9f else 0.4f),
                style = Stroke(
                    width = if (level == 0.8f) 1.5f else 1f,
                    pathEffect = if (level == 0.8f) PathEffect.dashPathEffect(floatArrayOf(10f, 6f)) else null
                )
            )
        }

        for (i in 0 until count) {
            val angle = i * angleStep - Math.PI.toFloat() / 2f
            val endX = centerX + radius * cos(angle)
            val endY = centerY + radius * sin(angle)
            drawLine(
                color = outlineColorCapt.copy(alpha = 0.5f),
                start = Offset(centerX, centerY),
                end = Offset(endX, endY),
                strokeWidth = 1f
            )
        }

        val benchmarkPath = Path()
        for (i in 0 until count) {
            val angle = i * angleStep - Math.PI.toFloat() / 2f
            val r = radius * 0.80f
            val x = centerX + r * cos(angle)
            val y = centerY + r * sin(angle)
            if (i == 0) benchmarkPath.moveTo(x, y) else benchmarkPath.lineTo(x, y)
        }
        benchmarkPath.close()

        drawPath(
            path = benchmarkPath,
            color = secondaryColor.copy(alpha = 0.15f)
        )
        drawPath(
            path = benchmarkPath,
            color = secondaryColor.copy(alpha = 0.8f),
            style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 6f)))
        )

        val studentPath = Path()
        val vertexPoints = mutableListOf<Offset>()

        subjectScores.forEachIndexed { i, subject ->
            val angle = i * angleStep - Math.PI.toFloat() / 2f
            val scoreNormalized = (subject.scorePercentage / 100f).coerceIn(0f, 1f)
            val r = radius * scoreNormalized
            val x = centerX + r * cos(angle)
            val y = centerY + r * sin(angle)

            val pt = Offset(x, y)
            vertexPoints.add(pt)
            if (i == 0) studentPath.moveTo(x, y) else studentPath.lineTo(x, y)
        }
        studentPath.close()

        drawPath(
            path = studentPath,
            color = tertiaryColor.copy(alpha = 0.30f)
        )
        drawPath(
            path = studentPath,
            color = tertiaryColor,
            style = Stroke(width = 3f)
        )

        subjectScores.forEachIndexed { i, subject ->
            val angle = i * angleStep - Math.PI.toFloat() / 2f
            val pt = vertexPoints[i]

            val isSelected = selectedSubject?.id == subject.id
            val isWeak = subject.needsRevision

            drawCircle(
                color = if (isWeak) errorColor else primaryColor,
                radius = if (isSelected) 8.dp.toPx() else 5.dp.toPx(),
                center = pt
            )
            drawCircle(
                color = Color.White,
                radius = if (isSelected) 4.dp.toPx() else 2.5.dp.toPx(),
                center = pt
            )

            val labelR = radius + 24.dp.toPx()
            val lx = centerX + labelR * cos(angle)
            val ly = centerY + labelR * sin(angle)

            val paint = android.graphics.Paint().apply {
                color = if (isWeak) {
                    android.graphics.Color.argb(
                        (errorColor.alpha * 255).toInt(),
                        (errorColor.red * 255).toInt(),
                        (errorColor.green * 255).toInt(),
                        (errorColor.blue * 255).toInt()
                    )
                } else {
                    android.graphics.Color.argb(
                        (textColor.alpha * 255).toInt(),
                        (textColor.red * 255).toInt(),
                        (textColor.green * 255).toInt(),
                        (textColor.blue * 255).toInt()
                    )
                }
                textSize = 12.sp.toPx()
                textAlign = when {
                    cos(angle) > 0.2f -> android.graphics.Paint.Align.LEFT
                    cos(angle) < -0.2f -> android.graphics.Paint.Align.RIGHT
                    else -> android.graphics.Paint.Align.CENTER
                }
                isFakeBoldText = true
                isAntiAlias = true
            }

            drawContext.canvas.nativeCanvas.drawText(
                "${subject.subjectName} (${subject.scorePercentage.toInt()}%)",
                lx,
                ly + 4.dp.toPx(),
                paint
            )
        }
    }
}

@Composable
private fun FocusSubjectCard(
    subject: SubjectScore,
    onPractice: () -> Unit,
    onAskAi: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("focus_subject_card_${subject.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Needs Revision",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = subject.subjectName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${subject.scorePercentage.toInt()}% / 80% D1 Target",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Diagnostic: ${subject.diagnosticTag}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onAskAi,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("ask_ai_button_${subject.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Ask AI",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AI Tutor Breakdown", style = MaterialTheme.typography.labelSmall)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onPractice,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("practice_now_button_${subject.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Practice Now",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Practice Now ➜", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
