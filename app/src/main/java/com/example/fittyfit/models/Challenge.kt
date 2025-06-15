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
    val participants: Map<String, Map<String, Any>> = mapOf(), // Map of participant data
    val createdBy: String = "", // User ID of the creator
    val createdAt: Long = System.currentTimeMillis(),
    val prize: String = "", // Prize for group challenges
    val status: String = "active", // active, completed, cancelled
    val totalParticipants: Int = 0 // Total number of participants for group challenges
) {
    fun getLeadingParticipant(): String {
        return participants.entries
            .filter { (_, data) -> data["isLeader"] == true }
            .map { (_, data) -> data["name"] as? String ?: "" }
            .firstOrNull() ?: ""
    }

    fun getPrizeType(): String {
        return prize.split(" - ").firstOrNull() ?: ""
    }

    fun getPrizeValue(): String {
        return prize.split(" - ").getOrNull(1) ?: ""
    }
} 