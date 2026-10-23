package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.SubscriptionPlan
import com.example.ui.theme.*

@Composable
fun LandingPricingSection(
    plans: List<SubscriptionPlan>,
    onSelectPlan: (SubscriptionPlan) -> Unit,
    onAuthClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLevelTab by remember { mutableIntStateOf(0) } // 0 = Primary (PLE), 1 = Secondary (UCE/UACE)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SlateSurface)
            .border(1.dp, SlateBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("landing_pricing_section")
            .animateContentSize()
    ) {
        // Hero Header & Study Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(14.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.students_studying_revision_1792698460199),
                contentDescription = "Students doing exam revision",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, SlateCanvas.copy(alpha = 0.92f)),
                            startY = 40f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Surface(
                    color = AmberPrimary,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "PLE, UCE & UACE 2026",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Master UNEB Exams with Shlee",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Uganda's premier candidate preparation ecosystem",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Auth / Signup Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateCanvas),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Candidate Account & Access",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Join 15,000+ candidates • Start with 7-Day Free Trial",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = onAuthClick,
                    colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("landing_signup_button")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sign In / Sign Up", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Level Overview Header: Primary vs Secondary
        Text(
            text = "📚 What Shlee Offers by Level",
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Tailored curriculum breakdowns for Ugandan candidate classes",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Level Tabs (Primary vs Secondary)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SlateCanvas, RoundedCornerShape(10.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedLevelTab == 0) AmberPrimary else Color.Transparent)
                    .clickable { selectedLevelTab = 0 }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Primary (PLE: P.5-P.7)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedLevelTab == 0) Color.White else TextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selectedLevelTab == 1) AmberPrimary else Color.Transparent)
                    .clickable { selectedLevelTab = 1 }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Secondary (UCE & UACE)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedLevelTab == 1) Color.White else TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Level Specific Content Cards
        if (selectedLevelTab == 0) {
            // Primary (PLE) Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCanvas),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = EmeraldLight.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🎒", fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Primary Leaving Examination (PLE) Hub",
                            style = MaterialTheme.typography.titleSmall,
                            color = EmeraldLight,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Designed for P.5, P.6, and P.7 candidates aiming for Aggregate 4 (Distinction 1 in all four papers).",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val pleFeatures = listOf(
                        "Four Core UNEB Subjects: Mathematics, Integrated Science, English & Social Studies (SST/RE)",
                        "Child-Friendly AI Tutor: Simplified step-by-step explanations with pictorial aids",
                        "Ugandan Top School Mocks: Kampala Parents, Greenhill, Namagunga Junior, Kitante Primary",
                        "Parent SMS / WhatsApp Linking: Instant notifications on child's daily quizzes & streak",
                        "Instant PLE Aggregate Calculator: Automatic grading from Distinction 1 to Pass 8"
                    )

                    pleFeatures.forEach { feat ->
                        Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = feat, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontSize = 11.sp)
                        }
                    }
                }
            }
        } else {
            // Secondary (UCE & UACE) Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCanvas),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = AmberLight.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🎓", fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Secondary Examination (UCE & UACE) Hub",
                            style = MaterialTheme.typography.titleSmall,
                            color = AmberLight,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Comprehensive revision for O-Level (S.1-S.4 New Curriculum) and A-Level (S.5-S.6 Combinations).",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val uceFeatures = listOf(
                        "All Sciences & Humanities: Physics, Chemistry, Biology, Mathematics, Geography, History, Literature",
                        "New Lower Secondary Curriculum: Competency-based scenarios & Activity of Integration breakdown",
                        "High-Thinking Gemini AI Tutor: Step-by-step solutions for 20-mark UNEB essay & practical apparatus",
                        "Subject Radar Competency: Interactive radar chart benchmarking against the 80% D1 line",
                        "National Rivalry Leaderboard: Compete with Budo, Kitende, Gayaza, SMACK, and Namagunga"
                    )

                    uceFeatures.forEach { feat ->
                        Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = AmberLight, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = feat, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Transparent: What Stays Free Forever After Free Trial
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateCanvas),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🎁 What We Offer Free Forever (Post-Trial)",
                        style = MaterialTheme.typography.titleSmall,
                        color = EmeraldLight,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Even if you do not subscribe after your 7-day free trial, you NEVER lose access to essential revision tools:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Free column
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(SlateSurface, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text("✅ 100% Free Forever", fontWeight = FontWeight.Bold, color = EmeraldLight, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• 1 Daily Quiz per subject", color = TextSecondary, fontSize = 10.sp)
                        Text("• National Leaderboard viewing", color = TextSecondary, fontSize = 10.sp)
                        Text("• Pomodoro Study Timer", color = TextSecondary, fontSize = 10.sp)
                        Text("• UNEB Exam Countdown", color = TextSecondary, fontSize = 10.sp)
                        Text("• Formula & Definition sheets", color = TextSecondary, fontSize = 10.sp)
                    }

                    // Premium column
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(SlateSurface, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text("⭐ Premium Upgrades", fontWeight = FontWeight.Bold, color = AmberLight, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• Unlimited High-Thinking AI Tutor", color = TextSecondary, fontSize = 10.sp)
                        Text("• Past Papers & Marking Schemes", color = TextSecondary, fontSize = 10.sp)
                        Text("• Radar Weak Spot Diagnostics", color = TextSecondary, fontSize = 10.sp)
                        Text("• Predicted UNEB Mock Papers", color = TextSecondary, fontSize = 10.sp)
                        Text("• Printable Revision Passports", color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Pricing Tiers Section
        Text(
            text = "💳 Candidate Subscription Tiers (UGX)",
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Affordable pricing for every Ugandan family • Instant Mobile Money Activation",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Pricing Tiers List
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            plans.forEach { plan ->
                LandingPlanCard(plan = plan, onSelect = { onSelectPlan(plan) })
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Payment Methods Card (MTN & Airtel)
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateCanvas),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "📱 Ugandan Mobile Money Accepted",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Instant USSD Push prompt directly to your phone. Enter your PIN to activate immediately:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        color = Color(0xFFFECA0A).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECA0A)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("MTN Mobile Money", fontWeight = FontWeight.Bold, color = Color(0xFFFECA0A), fontSize = 11.sp)
                            Text("Merchant: 0775551996", color = TextPrimary, fontSize = 10.sp)
                            Text("Dial *165# for prompt", color = TextSecondary, fontSize = 9.sp)
                        }
                    }

                    Surface(
                        color = Color(0xFFEF4444).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Airtel Money", fontWeight = FontWeight.Bold, color = Color(0xFFEF4444), fontSize = 11.sp)
                            Text("Merchant: 0704556555", color = TextPrimary, fontSize = 10.sp)
                            Text("Dial *185# for prompt", color = TextSecondary, fontSize = 9.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LandingPlanCard(
    plan: SubscriptionPlan,
    onSelect: () -> Unit
) {
    val isFree = plan.priceUgx == "UGX 0"

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (plan.isPopular) SlateSurfaceLight else SlateCanvas
        ),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (plan.isPopular) 1.5.dp else 1.dp,
            color = if (plan.isPopular) AmberPrimary else SlateBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pricing_plan_${plan.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = plan.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    if (plan.isPopular) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = AmberPrimary,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "MOST POPULAR",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontSize = 8.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = plan.priceUgx,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isFree) EmeraldLight else AmberLight,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = plan.period,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
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
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isFree) EmeraldLight else AmberLight,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = feature, style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onSelect,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFree) SlateSurfaceLight else AmberPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isFree) "Start 7-Day Free Trial ➜" else "Subscribe for ${plan.priceUgx} ➜",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
