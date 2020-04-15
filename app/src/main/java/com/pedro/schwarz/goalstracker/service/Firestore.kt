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
            onComplete: (result: Resource<Unit>) -> Unit = {}
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

        fun deleteDocument(
            collection: String,
            document: String
        ) {
            database.collection(collection).document(document).delete()
        }

        fun deleteChildren(
            collection: String,
            parentField: String,
            parentId: Long,
            userField: String,
            userId: String
        ) {
            val batch = database.batch()
            database.collection(collection).whereEqualTo(parentField, parentId)
                .whereEqualTo(userField, userId).get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.let {
                            it.forEach { result ->
                                batch.delete(result.reference)
                            }
                            batch.commit()
                        }
                    }
                }
        }
    }
}