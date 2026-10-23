package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
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
import com.example.model.PaymentTransaction
import com.example.model.SubscriptionPlan
import com.example.ui.theme.*

@Composable
fun PaymentSection(
    plans: List<SubscriptionPlan>,
    transactions: List<PaymentTransaction>,
    onSelectPlan: (SubscriptionPlan) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("payment_section")
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Shlee Premium & Payments",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Unlock Unlimited AI Tutor, Past Papers & Leaderboard Priority via Mobile Money",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Subscription Plans List
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            plans.forEach { plan ->
                SubscriptionPlanCard(
                    plan = plan,
                    onSubscribe = { onSelectPlan(plan) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

        Spacer(modifier = Modifier.height(16.dp))

        // Payment History Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Transaction History",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (transactions.isEmpty()) {
            Text("No recent payment transactions.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                transactions.forEach { tx ->
                    TransactionRow(transaction = tx)
                }
            }
        }
    }
}

@Composable
private fun SubscriptionPlanCard(
    plan: SubscriptionPlan,
    onSubscribe: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (plan.isPopular) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) else MaterialTheme.colorScheme.background
        ),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (plan.isPopular) 1.5.dp else 1.dp,
            color = if (plan.isPopular) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("plan_card_${plan.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = plan.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    if (plan.isPopular) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "POPULAR",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = plan.priceUgx,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = plan.period,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            plan.features.forEach { feature ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = feature, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSubscribe,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (plan.isPopular) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Subscribe via Mobile Money ➜",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (plan.isPopular) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun TransactionRow(transaction: PaymentTransaction) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = transaction.planName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${transaction.paymentMethod} • ${transaction.date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = transaction.amountUgx,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = transaction.status,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}
