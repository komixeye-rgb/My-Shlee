package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.window.Dialog
import com.example.model.QuizQuestion
import com.example.model.SubjectScore
import com.example.ui.theme.*

@Composable
fun PracticeQuizDialog(
    subject: SubjectScore,
    questions: List<QuizQuestion>,
    onQuizCompleted: (subjectId: String, newScorePercentage: Float) -> Unit,
    onDismiss: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedAnswers by remember { mutableStateOf(mutableMapOf<Int, Int>()) }
    var showResults by remember { mutableStateOf(false) }

    val currentQuestion = questions.getOrNull(currentIndex)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("practice_quiz_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${subject.subjectName} Revision Quiz",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "UNEB Standard Practice • Question ${currentIndex + 1}/${questions.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_quiz_dialog")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (!showResults && currentQuestion != null) {
                    // Question Reference Pill
                    Surface(
                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = currentQuestion.pastPaperRef,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = currentQuestion.questionText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Options
                    currentQuestion.options.forEachIndexed { optionIndex, optionText ->
                        val isSelected = selectedAnswers[currentIndex] == optionIndex
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    val newMap = selectedAnswers.toMutableMap()
                                    newMap[currentIndex] = optionIndex
                                    selectedAnswers = newMap
                                }
                                .testTag("quiz_option_${currentIndex}_$optionIndex")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        val newMap = selectedAnswers.toMutableMap()
                                        newMap[currentIndex] = optionIndex
                                        selectedAnswers = newMap
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.tertiary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = optionText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Navigation buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { if (currentIndex > 0) currentIndex-- },
                            enabled = currentIndex > 0,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Previous")
                        }

                        Button(
                            onClick = {
                                if (currentIndex < questions.size - 1) {
                                    currentIndex++
                                } else {
                                    showResults = true
                                }
                            },
                            enabled = selectedAnswers.containsKey(currentIndex),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("quiz_next_submit_button")
                        ) {
                            Text(if (currentIndex == questions.size - 1) "Submit Quiz" else "Next Question", color = Color.White)
                        }
                    }
                } else {
                    // Results View
                    var correctCount = 0
                    questions.forEachIndexed { idx, q ->
                        if (selectedAnswers[idx] == q.correctIndex) {
                            correctCount++
                        }
                    }

                    val quizPct = (correctCount.toFloat() / questions.size.toFloat()) * 100f
                    val updatedScore = ((subject.scorePercentage * 0.7f) + (quizPct * 0.3f)).coerceIn(0f, 100f)

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎉 Practice Quiz Completed!", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Score: $correctCount / ${questions.size} (${quizPct.toInt()}%)",
                            style = MaterialTheme.typography.titleLarge,
                            color = if (quizPct >= 66f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Your ${subject.subjectName} competence score updated to ${updatedScore.toInt()}% on the Radar Chart!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                onQuizCompleted(subject.id, updatedScore)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("finish_quiz_button")
                        ) {
                            Text("Update Radar & Finish ➜", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
