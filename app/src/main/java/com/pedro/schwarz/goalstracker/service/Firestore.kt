package com.pedro.schwarz.goalstracker.service

import com.google.firebase.firestore.FirebaseFirestore
import com.pedro.schwarz.goalstracker.model.Checkpoint
import com.pedro.schwarz.goalstracker.model.Goal
import com.pedro.schwarz.goalstracker.model.Milestone
import com.pedro.schwarz.goalstracker.model.User
import com.pedro.schwarz.goalstracker.repository.Failure
import com.pedro.schwarz.goalstracker.repository.Resource
import com.pedro.schwarz.goalstracker.repository.Success

private const val USER_COLLECTION = "users"
private const val GOAL_COLLECTION = "goals"
private const val MILESTONE_COLLECTION = "milestones"
private const val CHECKPOINT_COLLECTION = "checkpoints"
private const val IMAGE_PATH = "checkpoints_images"

class FirestoreService {
    companion object {
        private val database = FirebaseFirestore.getInstance()

        fun <T> fetchDocument(
            collection: String,
            document: String,
            onSuccess: (result: T) -> Unit,
            onFailure: (error: String) -> Unit = {}
        ) {
            database.collection(collection).document(document).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.let { result ->
                            val item = when (collection) {
                                USER_COLLECTION -> result.toObject(User::class.java) as T
                                GOAL_COLLECTION -> result.toObject(Goal::class.java) as T
                                MILESTONE_COLLECTION -> result.toObject(Milestone::class.java) as T
                                else -> result.toObject(Checkpoint::class.java) as T
                            }
                            onSuccess(item)
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

        fun <T> fetchDocuments(
            collection: String,
            userField: String,
            userId: String,
            onSuccess: (result: List<T>) -> Unit,
            onFailure: (error: String) -> Unit = {}
        ) {
            database.collection(collection).whereEqualTo(userField, userId).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.let { result ->
                            val list = mutableListOf<T>()
                            result.forEach { child ->
                                val document = when (collection) {
                                    GOAL_COLLECTION -> child.toObject(Goal::class.java) as T
                                    MILESTONE_COLLECTION -> child.toObject(Milestone::class.java) as T
                                    else -> child.toObject(Checkpoint::class.java) as T
                                }
                                list.add(document)
                            }
                            onSuccess(list)
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
                            if (collection == CHECKPOINT_COLLECTION) {
                                it.forEach { result ->
                                    val checkpoint = result.toObject(Checkpoint::class.java)
                                    StorageService.deleteImage("$IMAGE_PATH/${checkpoint.userId}-${checkpoint.createdAt}")
                                }
                            }
                            batch.commit()
                        }
                    }
                }
        }
    }
}