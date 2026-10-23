package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.window.Dialog
import com.example.model.SubscriptionPlan
import com.example.ui.theme.*

@Composable
fun PaymentCheckoutDialog(
    plan: SubscriptionPlan,
    onPaymentSuccess: (SubscriptionPlan) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedProvider by remember { mutableStateOf("MTN Mobile Money") }
    var phoneNumber by remember { mutableStateOf("0775551996") }
    var isProcessing by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }

    val providers = listOf("MTN Mobile Money", "Airtel Money", "Visa / Card")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("payment_checkout_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SHLEE Secure Checkout",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Instant UNEB Premium Activation",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_checkout_dialog")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isSuccess) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Payment Successful!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "You are now subscribed to ${plan.name} (${plan.priceUgx}). Full access unlocked!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            onPaymentSuccess(plan)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Start Studying Now ➜", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                } else {
                    // Plan Summary Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = plan.name, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                                Text(text = plan.priceUgx, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Billing Period: ${plan.period}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Select Mobile Money Provider", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.Start))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        providers.forEach { provider ->
                            val isSelected = selectedProvider == provider
                            OutlinedButton(
                                onClick = { 
                                    selectedProvider = provider
                                    phoneNumber = when {
                                        provider.contains("MTN") -> "0775551996"
                                        provider.contains("Airtel") -> "0704556555"
                                        else -> "Merchant Card"
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(provider.split(" ").first(), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Merchant Number Instructions Box
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            val merchantNum = if (selectedProvider.contains("MTN")) "0775551996" else if (selectedProvider.contains("Airtel")) "0704556555" else "Merchant Gateway"
                            val merchantName = if (selectedProvider.contains("MTN")) "MTN Mobile Money" else if (selectedProvider.contains("Airtel")) "Airtel Money" else "Secure Card Gateway"
                            
                            Text(
                                text = "Merchant: $merchantName",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Send ${plan.priceUgx} to: $merchantNum (SHLEE Education)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (selectedProvider.contains("MTN")) "Dial *165# or use MTN Momo App" else if (selectedProvider.contains("Airtel")) "Dial *185# or use Airtel Money App" else "Enter card details below",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Your Phone / Account Number") },
                        leadingIcon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            isProcessing = true
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                isProcessing = false
                                isSuccess = true
                                Toast.makeText(context, "UGX ${plan.priceUgx} payment verified via $selectedProvider ($phoneNumber)", Toast.LENGTH_LONG).show()
                            }, 1800)
                        },
                        enabled = !isProcessing,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Authorizing USSD Prompt...", color = Color.White)
                        } else {
                            Text("Pay ${plan.priceUgx} Now", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

