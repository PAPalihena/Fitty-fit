package com.example.fittyfit.models

import java.util.Date

data class ChatMessage(
    val id: String,
    val senderId: String,
    val senderName: String,
    val message: String,
    val timestamp: Date,
    val challengeId: String
) 