package com.example.fittyfit.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fittyfit.R
import com.example.fittyfit.adapters.ChatMessageAdapter
import com.example.fittyfit.databinding.ActivityChallengeChatBinding
import com.example.fittyfit.models.ChatMessage
import java.util.*

class ChallengeChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChallengeChatBinding
    private lateinit var chatAdapter: ChatMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChallengeChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSendButton()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatMessageAdapter()
        binding.chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChallengeChatActivity).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }
    }

    private fun setupSendButton() {
        binding.sendButton.setOnClickListener {
            val messageText = binding.messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                // Create a new message with dummy user data
                val newMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    senderId = "current_user", // This would be the actual user ID in a real app
                    senderName = "You", // This would be the actual user name in a real app
                    message = messageText,
                    timestamp = Date(),
                    challengeId = "challenge1" // This would come from the intent in a real app
                )
                
                chatAdapter.addMessage(newMessage)
                binding.messageInput.text.clear()
                
                // Scroll to the bottom to show the new message
                binding.chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 