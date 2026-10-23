package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.model.UserProfile
import com.example.ui.theme.*
import com.example.util.QrCodeGenerator

@Composable
fun ProfileShareModal(
    userProfile: UserProfile,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Generate scannable QR payload containing student's unique index number and current study streak
    val qrPayload = remember(userProfile.studentId, userProfile.currentStreak, userProfile.name) {
        QrCodeGenerator.buildStudentVerificationPayload(
            studentIndex = userProfile.studentId,
            streakDays = userProfile.currentStreak,
            studentName = userProfile.name
        )
    }

    // Generate real scannable QR Bitmap using ZXing library
    val qrBitmap = remember(qrPayload) {
        QrCodeGenerator.generateQrBitmap(qrPayload, sizePx = 600)
    }

    Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("profile_share_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Modal Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Student Pass & QR Code",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_qr_modal")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                }

                Text(
                    text = "Scannable student badge with unique Index & Study Streak",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // High-Contrast ZXing-Generated QR Matrix
                Box(
                    modifier = Modifier
                        .size(210.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(2.dp, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                        .testTag("student_qr_code_image"),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "Candidate Verification QR Code (Index: ${userProfile.studentId}, Streak: ${userProfile.currentStreak} Days)",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Candidate Details Card with Index & Streak
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_shulee_logo),
                                contentDescription = "Graduation Cap Logo",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = userProfile.name,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.Verified, contentDescription = "Verified", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = "${userProfile.school} • ${userProfile.candidateLevel}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            BadgeStatPill("INDEX NO.", userProfile.studentId, MaterialTheme.colorScheme.tertiary)
                            BadgeStatPill("CURRENT STREAK", "🔥 ${userProfile.currentStreak} Days", MaterialTheme.colorScheme.error)
                            BadgeStatPill("TARGET", userProfile.projectedAggregate, MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scan Instruction Note
                Text(
                    text = "Scan with any phone camera or barcode app to view verified candidate credentials and streak.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            copyToClipboard(context, qrPayload)
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_share_link_button")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Info", style = MaterialTheme.typography.labelMedium)
                    }

                    Button(
                        onClick = {
                            shareQrCode(context, userProfile, qrPayload)
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("download_share_qr_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Badge", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeStatPill(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), fontSize = 9.sp)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = valueColor, fontWeight = FontWeight.Bold)
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("SHLEE Candidate Badge", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "📋 Candidate index & streak copied to clipboard!", Toast.LENGTH_SHORT).show()
}

private fun shareQrCode(context: Context, profile: UserProfile, content: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "Official UNEB Candidate Revision Pass\nIndex No: ${profile.studentId}\nCandidate: ${profile.name}\nStreak: 🔥 ${profile.currentStreak} Days\n\n$content"
        )
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share Student Profile & QR Pass")
    context.startActivity(shareIntent)
}
