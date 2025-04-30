package com.example.fittyfit.utils

import com.example.fittyfit.models.Challenge
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseDatabaseHelper {
    private val database: DatabaseReference = Firebase.database.reference

    // Challenges
    private val challengesRef: DatabaseReference = database.child("challenges")
    private val userChallengesRef: DatabaseReference = database.child("user_challenges")

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
                } else {
                    // For group challenges, add to all participants
                    challenge.participants.forEach { userId ->
                        userChallengesRef.child(userId)
                            .child(challengeId)
                            .setValue(true)
                    }
                }
                onComplete(true, challengeId)
            }
            .addOnFailureListener {
                onComplete(false, it.message)
            }
    }

    // Get all challenges for a user
    fun getUserChallenges(userId: String, onDataLoaded: (List<Challenge>) -> Unit) {
        userChallengesRef.child(userId).get()
            .addOnSuccessListener { snapshot ->
                val challengeIds = snapshot.children.mapNotNull { it.key }
                val challenges = mutableListOf<Challenge>()
                
                challengeIds.forEach { challengeId ->
                    challengesRef.child(challengeId).get()
                        .addOnSuccessListener { challengeSnapshot ->
                            val challenge = challengeSnapshot.getValue(Challenge::class.java)
                            challenge?.let { challenges.add(it) }
                            if (challenges.size == challengeIds.size) {
                                onDataLoaded(challenges)
                            }
                        }
                }
            }
    }

    // Get all group challenges (public access)
    fun getGroupChallenges(onDataLoaded: (List<Challenge>) -> Unit) {
        challengesRef.get()
            .addOnSuccessListener { snapshot ->
                val challenges = mutableListOf<Challenge>()
                snapshot.children.forEach { child ->
                    val challenge = child.getValue(Challenge::class.java)
                    if (challenge?.type == "group") {
                        challenges.add(challenge)
                    }
                }
                onDataLoaded(challenges)
            }
            .addOnFailureListener {
                onDataLoaded(emptyList())
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
        challengesRef.child(challengeId).get()
            .addOnSuccessListener { snapshot ->
                val challenge = snapshot.getValue(Challenge::class.java)
                onDataLoaded(challenge)
            }
            .addOnFailureListener {
                onDataLoaded(null)
            }
    }
} 