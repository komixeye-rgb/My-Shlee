package com.example.model

data class SubscriptionPlan(
    val id: String,
    val name: String,
    val priceUgx: String,
    val period: String,
    val features: List<String>,
    val isPopular: Boolean = false
)

data class PaymentTransaction(
    val id: String,
    val planName: String,
    val amountUgx: String,
    val paymentMethod: String,
    val date: String,
    val status: String // "SUCCESS", "PENDING"
)
