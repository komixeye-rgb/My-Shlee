package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LeaderboardCandidate
import com.example.ui.theme.*

@Composable
fun NationalLeaderboardSection(
    candidates: List<LeaderboardCandidate>,
    selectedLevelFilter: String,
    onFilterChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val levels = listOf("All Levels", "PLE (P7)", "UCE (S4)", "UACE (S6)")

    val filteredCandidates = remember(candidates, selectedLevelFilter) {
        if (selectedLevelFilter == "All Levels") {
            candidates
        } else {
            val levelCode = selectedLevelFilter.split(" ").first()
            candidates.filter { it.candidateLevel == levelCode }
        }
    }

    val topThree = filteredCandidates.take(3)
    val remainingCandidates = filteredCandidates.drop(3)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Section Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = AmberPrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ugandan National Leaderboard",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Ranked by UNEB Accuracy + Continuous Study Streak",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Level Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            levels.forEach { level ->
                val isSelected = selectedLevelFilter == level
                FilterChip(
                    selected = isSelected,
                    onClick = { onFilterChanged(level) },
                    label = { Text(level, style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AmberPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = MaterialTheme.colorScheme.outline,
                        selectedBorderColor = AmberPrimary
                    ),
                    modifier = Modifier.testTag("filter_chip_$level")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Top 3 Podium
        if (topThree.size >= 3) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // 2nd Place (Silver)
                PodiumCard(candidate = topThree[1], rankIndex = 2, badgeColor = SilverBadge, heightDp = 120)
                // 1st Place (Gold)
                PodiumCard(candidate = topThree[0], rankIndex = 1, badgeColor = GoldBadge, heightDp = 145)
                // 3rd Place (Bronze)
                PodiumCard(candidate = topThree[2], rankIndex = 3, badgeColor = BronzeBadge, heightDp = 105)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Leaderboard List Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Rank & Candidate", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            Text("Accuracy / Streak / Points", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)

        // Scrollable Rankings List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 380.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(remainingCandidates, key = { it.candidateName + it.rank }) { candidate ->
                CandidateRankRow(candidate = candidate)
            }
        }
    }
}

@Composable
private fun PodiumCard(
    candidate: LeaderboardCandidate,
    rankIndex: Int,
    badgeColor: Color,
    heightDp: Int
) {
    val medalEmoji = when (rankIndex) {
        1 -> "🥇"
        2 -> "🥈"
        else -> "🥉"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        // Avatar circle
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(badgeColor.copy(alpha = 0.2f))
                .border(2.dp, badgeColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = candidate.avatarInitials,
                style = MaterialTheme.typography.titleSmall,
                color = badgeColor,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = candidate.candidateName,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        Text(
            text = candidate.schoolName,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            fontSize = 9.sp,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Podium Block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(heightDp.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(badgeColor.copy(alpha = 0.15f))
                .border(1.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(medalEmoji, fontSize = 22.sp)
                Text(
                    text = "${candidate.compositePoints} pts",
                    style = MaterialTheme.typography.titleSmall,
                    color = badgeColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "🔥 ${candidate.streakDays}d streak",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
private fun CandidateRankRow(candidate: LeaderboardCandidate) {
    val rowBg = if (candidate.isCurrentUser) AmberPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
    val borderStroke = if (candidate.isCurrentUser) androidx.compose.foundation.BorderStroke(1.5.dp, AmberPrimary) else null

    Card(
        colors = CardDefaults.cardColors(containerColor = rowBg),
        shape = RoundedCornerShape(10.dp),
        border = borderStroke,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("candidate_row_${candidate.rank}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Rank, Movement, & Avatar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "#${candidate.rank}",
                    style = MaterialTheme.typography.titleSmall,
                    color = if (candidate.isCurrentUser) AmberLight else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(32.dp)
                )

                // Shift Indicator
                when {
                    candidate.rankShift > 0 -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(12.dp))
                            Text("${candidate.rankShift}", fontSize = 10.sp, color = EmeraldLight)
                        }
                    }
                    candidate.rankShift < 0 -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = FlameOrange, modifier = Modifier.size(12.dp))
                            Text("${candidate.rankShift}", fontSize = 10.sp, color = FlameOrange)
                        }
                    }
                    else -> {
                        Icon(Icons.Default.Remove, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(12.dp))
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Avatar initials
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (candidate.isCurrentUser) AmberPrimary else MaterialTheme.colorScheme.outline),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = candidate.avatarInitials,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = candidate.candidateName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        if (candidate.isCurrentUser) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(color = AmberPrimary, shape = RoundedCornerShape(4.dp)) {
                                Text(
                                    "YOU",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "${candidate.schoolName} (${candidate.candidateLevel})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            // Stats
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${candidate.compositePoints} pts",
                    style = MaterialTheme.typography.titleSmall,
                    color = AmberLight,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${candidate.quizAccuracyPct.toInt()}% Acc • 🔥${candidate.streakDays}d",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }
        }
    }
}
