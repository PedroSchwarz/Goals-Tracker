package com.pedro.schwarz.goalstracker.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pedro.schwarz.goalstracker.model.User

private const val USER_COLLECTION = "users"
private const val IMAGE_PATH = "users_images"

class AuthRepository {

    fun registerUser(user: User): LiveData<Resource<Unit>> {
        val liveData = MutableLiveData<Resource<Unit>>()
        auth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.let { result ->
                        result.user?.let { firebaseUser ->
                            val updatedUser = user.copy(id = firebaseUser.uid)
                            storeUserImage(updatedUser, onComplete = { result ->
                                when (result) {
                                    is Success -> {
                                        result.data?.let { imageUrl ->
                                            createUser(
                                                updatedUser.copy(imageUrl = imageUrl),
                                                onComplete = { result ->
                                                    liveData.postValue(result)
                                                })
                                        }
                                    }
                                    is Failure -> {
                                        liveData.postValue(Failure(error = result.error))
                                    }
                                }
                            })
                        }
                    }
                } else {
                    task.exception?.let { error ->
                        liveData.postValue(Failure(error = error.message))
                    }
                }
            }
        return liveData
    }

    private fun storeUserImage(user: User, onComplete: (result: Resource<String>) -> Unit) {
        val path = storage.reference.child("${IMAGE_PATH}/${user.id}.jpg")
        val uploadTask = path.putFile(Uri.parse(user.imageUrl))
        val urlTask = uploadTask.continueWith { task ->
            if (!task.isSuccessful) {
                task.exception?.let { error ->
                    onComplete(Failure(error = error.message))
                }
            }
            path.downloadUrl
        }
        urlTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onComplete(Success(data = task.result.toString()))
            } else {
                task.exception?.let { error ->
                    onComplete(Failure(error = error.message))
                }
            }
        }
    }

    private fun createUser(user: User, onComplete: (result: Resource<Unit>) -> Unit) {
        database.collection(USER_COLLECTION).document(user.id).set(user)
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

    fun signInUser(email: String, password: String): LiveData<Resource<Unit>> {
        val liveData = MutableLiveData<Resource<Unit>>()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    liveData.postValue(Success())
                } else {
                    task.exception?.let { error ->
                        liveData.postValue(Failure(error = error.message))
                    }
                }
            }
        return liveData
    }

    fun checkUserState() = MutableLiveData<Resource<Unit>>().also { liveData ->
        if (auth.currentUser != null) {
            liveData.postValue(Success())
        } else {
            liveData.postValue(Failure())
        }
    }

    companion object {
        private val auth: FirebaseAuth = FirebaseAuth.getInstance()
        private val storage: FirebaseStorage = FirebaseStorage.getInstance()
        private val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    }
}