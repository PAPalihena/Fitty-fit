package com.example.fittyfit.models

data class Challenge(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val target: String = "",
    val progress: Int = 0,
    val type: String = "", // "personal" or "group"
    val startDate: String = "",
    val endDate: String = "",
    val participants: List<String> = listOf(), // List of user IDs for group challenges
    val createdBy: String = "", // User ID of the creator
    val createdAt: Long = System.currentTimeMillis()
) 