package com.pedro.schwarz.goalstracker.model

import com.google.firebase.firestore.Exclude

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    @Exclude val password: String = "",
    val imageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)