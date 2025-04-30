package com.example.fittyfit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fittyfit.R
import com.example.fittyfit.models.ChatMessage
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*

class ChatMessageAdapter : RecyclerView.Adapter<ChatMessageAdapter.ChatMessageViewHolder>() {

    private val messages = mutableListOf<ChatMessage>()
    private val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    init {
        // Add dummy data
        messages.addAll(listOf(
            ChatMessage(
                id = "1",
                senderId = "user1",
                senderName = "John Doe",
                message = "Hey everyone! Ready to crush this challenge? 💪",
                timestamp = Date(System.currentTimeMillis() - 3600000), // 1 hour ago
                challengeId = "challenge1"
            ),
            ChatMessage(
                id = "2",
                senderId = "user2",
                senderName = "Jane Smith",
                message = "Absolutely! I've been training for this!",
                timestamp = Date(System.currentTimeMillis() - 1800000), // 30 mins ago
                challengeId = "challenge1"
            ),
            ChatMessage(
                id = "3",
                senderId = "user3",
                senderName = "Mike Johnson",
                message = "Let's do this! Who's tracking their progress?",
                timestamp = Date(System.currentTimeMillis() - 900000), // 15 mins ago
                challengeId = "challenge1"
            ),
            ChatMessage(
                id = "4",
                senderId = "user1",
                senderName = "John Doe",
                message = "I am! Already logged my first workout!",
                timestamp = Date(System.currentTimeMillis() - 300000), // 5 mins ago
                challengeId = "challenge1"
            )
        ))
    }

    class ChatMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderName: TextView = itemView.findViewById(R.id.senderName)
        val messageText: TextView = itemView.findViewById(R.id.messageText)
        val timestamp: TextView = itemView.findViewById(R.id.timestamp)
        val messageCard: MaterialCardView = itemView.findViewById(R.id.messageCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatMessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        val message = messages[position]
        
        holder.senderName.text = message.senderName
        holder.messageText.text = message.message
        holder.timestamp.text = dateFormat.format(message.timestamp)
    }

    override fun getItemCount() = messages.size

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
} 