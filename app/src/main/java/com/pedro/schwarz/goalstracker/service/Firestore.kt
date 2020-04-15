package com.pedro.schwarz.goalstracker.service

import com.google.firebase.firestore.FirebaseFirestore
import com.pedro.schwarz.goalstracker.repository.Failure
import com.pedro.schwarz.goalstracker.repository.Resource
import com.pedro.schwarz.goalstracker.repository.Success

class FirestoreService {
    companion object {
        private val database = FirebaseFirestore.getInstance()

        fun <T> insertDocument(
            collection: String,
            document: String,
            data: T,
            onComplete: (result: Resource<Unit>) -> Unit
        ) {
            database.collection(collection).document(document).set(data as Any)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onComplete(Success())
                    } else {
                        task.exception?.let { error ->
                            onComplete(Failure(error = error.message))
                        }
                    }
                }
        }
    }
}