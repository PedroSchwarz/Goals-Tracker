package com.pedro.schwarz.goalstracker.service

import com.google.firebase.auth.FirebaseAuth

class AuthService {
    companion object {
        private val auth = FirebaseAuth.getInstance()

        fun createUserWithEmailAndPassword(
            email: String,
            password: String,
            onSuccess: (data: String) -> Unit,
            onFailure: (error: String) -> Unit
        ) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.let { result ->
                            result.user?.let { user ->
                                onSuccess(user.uid)
                            }
                        }
                    } else {
                        task.exception?.let { error ->
                            error.message?.let { message ->
                                onFailure(message)
                            }
                        }
                    }
                }
        }

        fun signInUserWithEmailAndPassword(
            email: String,
            password: String,
            onSuccess: () -> Unit,
            onFailure: (error: String) -> Unit
        ) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onSuccess()
                    } else {
                        task.exception?.let { error ->
                            error.message?.let { message ->
                                onFailure(message)
                            }
                        }
                    }
                }
        }

        fun listenUserStateChanges(onSuccess: () -> Unit, onFailure: () -> Unit) {
            auth.addAuthStateListener { firebaseUser ->
                if (firebaseUser.currentUser != null) {
                    onSuccess()
                } else {
                    onFailure()
                }
            }
        }
    }
}