package com.example.fittyfit.utils

import com.example.fittyfit.models.Challenge
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.GenericTypeIndicator

class FirebaseDatabaseHelper {
    private val database: DatabaseReference = Firebase.database.reference

    // Challenges
    private val challengesRef: DatabaseReference = database.child("challenges")
    private val userChallengesRef: DatabaseReference = database.child("user_challenges")
    private val groupChallengesRef: DatabaseReference = userChallengesRef.child("group_challenges")

    // Callback interface for challenges
    interface ChallengesCallback {
        fun onChallengesLoaded(challenges: List<Challenge>)
        fun onError(error: String)
    }

    // Get personal challenges
    fun getPersonalChallenges(callback: ChallengesCallback) {
        userChallengesRef.get()
            .addOnSuccessListener { snapshot ->
                val challenges = mutableListOf<Challenge>()
                snapshot.children.forEach { child ->
                    try {
                        val challengeId = child.key ?: ""
                        challengesRef.child(challengeId).get()
                            .addOnSuccessListener { challengeSnapshot ->
                                val challenge = challengeSnapshot.getValue(Challenge::class.java)
                                challenge?.let { 
                                    if (it.type == "personal") {
                                        challenges.add(it)
                                    }
                                }
                                if (challenges.size == snapshot.childrenCount.toInt()) {
                                    callback.onChallengesLoaded(challenges)
                                }
                            }
                            .addOnFailureListener { e ->
                                callback.onError("Error loading challenge: ${e.message}")
                            }
                    } catch (e: Exception) {
                        callback.onError("Error parsing challenge: ${e.message}")
                    }
                }
            }
            .addOnFailureListener { e ->
                callback.onError("Error loading challenges: ${e.message}")
            }
    }

    // Get all group challenges
    fun getGroupChallenges(callback: ChallengesCallback) {
        println("Fetching group challenges from: ${groupChallengesRef.toString()}")
        groupChallengesRef.get()
            .addOnSuccessListener { snapshot ->
                println("Successfully retrieved group challenges snapshot")
                val challenges = mutableListOf<Challenge>()
                snapshot.children.forEach { child ->
                    try {
                        val challengeId = child.key ?: ""
                        println("Processing challenge with ID: $challengeId")
                        val challengeData = child.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})
                        
                        challengeData?.let { data ->
                            println("Challenge data: $data")
                            // Get participants data
                            val participants = data["participants"] as? Map<String, Map<String, Any>>
                            val totalParticipants = data["totalParticipants"] as? Number ?: (participants?.size ?: 0)
                            
                            // Format prize string
                            val prizeMap = data["prize"] as? Map<*, *>
                            val prizeString = prizeMap?.let { prize ->
                                "${prize["type"]} - ${prize["value"]}"
                            } ?: ""

                            val challenge = Challenge(
                                id = challengeId,
                                title = data["title"] as? String ?: "",
                                description = data["description"] as? String ?: "",
                                target = "", // Not used for group challenges
                                progress = (data["progress"] as? Number)?.toInt() ?: 0,
                                type = "group",
                                startDate = data["startDate"] as? String ?: "",
                                endDate = data["endDate"] as? String ?: "",
                                participants = participants ?: mapOf(),
                                createdBy = data["createdBy"] as? String ?: "",
                                createdAt = (data["createdAt"] as? String)?.let { 
                                    java.time.Instant.parse(it).toEpochMilli() 
                                } ?: System.currentTimeMillis(),
                                prize = prizeString,
                                status = data["status"] as? String ?: "active",
                                totalParticipants = totalParticipants.toInt()
                            )
                            challenges.add(challenge)
                            println("Added challenge: ${challenge.title}")
                        }
                    } catch (e: Exception) {
                        println("Error parsing challenge: ${e.message}")
                        e.printStackTrace()
                        callback.onError("Error parsing challenge: ${e.message}")
                    }
                }
                println("Total challenges loaded: ${challenges.size}")
                callback.onChallengesLoaded(challenges)
            }
            .addOnFailureListener { e ->
                println("Error loading group challenges: ${e.message}")
                e.printStackTrace()
                callback.onError("Error loading group challenges: ${e.message}")
            }
    }

    // Save a new challenge
    fun saveChallenge(challenge: Challenge, onComplete: (Boolean, String?) -> Unit) {
        val challengeRef = challengesRef.push()
        val challengeId = challengeRef.key ?: ""
        val challengeWithId = challenge.copy(id = challengeId)
        
        challengeRef.setValue(challengeWithId)
            .addOnSuccessListener {
                // Also save reference in user_challenges
                if (challenge.type == "personal") {
                    userChallengesRef.child(challenge.createdBy)
                        .child(challengeId)
                        .setValue(true)
                        .addOnSuccessListener {
                            onComplete(true, challengeId)
                        }
                        .addOnFailureListener {
                            onComplete(false, null)
                        }
                } else {
                    // For group challenges, add to all participants
                    val updates = challenge.participants.keys.associate { userId ->
                        "user_challenges/$userId/$challengeId" to true
                    }
                    database.updateChildren(updates)
                        .addOnSuccessListener {
                            onComplete(true, challengeId)
                        }
                        .addOnFailureListener {
                            onComplete(false, null)
                        }
                }
            }
            .addOnFailureListener {
                onComplete(false, null)
            }
    }

    // Update challenge progress
    fun updateChallengeProgress(challengeId: String, progress: Int, onComplete: (Boolean) -> Unit) {
        challengesRef.child(challengeId)
            .child("progress")
            .setValue(progress)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Get challenge details
    fun getChallengeDetails(challengeId: String, onDataLoaded: (Challenge?) -> Unit) {
        groupChallengesRef.child(challengeId).get()
            .addOnSuccessListener { snapshot ->
                val challengeData = snapshot.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})
                if (challengeData != null) {
                    // Get participants data
                    val participants = challengeData["participants"] as? Map<String, Map<String, Any>>
                    val totalParticipants = challengeData["totalParticipants"] as? Number ?: (participants?.size ?: 0)
                    
                    // Format prize string
                    val prizeMap = challengeData["prize"] as? Map<*, *>
                    val prizeString = prizeMap?.let { prize ->
                        "${prize["type"]} - ${prize["value"]}"
                    } ?: ""

                    val challenge = Challenge(
                        id = challengeId,
                        title = challengeData["title"] as? String ?: "",
                        description = challengeData["description"] as? String ?: "",
                        target = "", // Not used for group challenges
                        progress = (challengeData["progress"] as? Number)?.toInt() ?: 0,
                        type = "group",
                        startDate = challengeData["startDate"] as? String ?: "",
                        endDate = challengeData["endDate"] as? String ?: "",
                        participants = participants ?: mapOf(),
                        createdBy = challengeData["createdBy"] as? String ?: "",
                        createdAt = (challengeData["createdAt"] as? String)?.let { 
                            java.time.Instant.parse(it).toEpochMilli() 
                        } ?: System.currentTimeMillis(),
                        prize = prizeString,
                        status = challengeData["status"] as? String ?: "active",
                        totalParticipants = totalParticipants.toInt()
                    )
                    onDataLoaded(challenge)
                } else {
                    onDataLoaded(null)
                }
            }
            .addOnFailureListener {
                onDataLoaded(null)
            }
    }
} 