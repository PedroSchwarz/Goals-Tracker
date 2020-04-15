package com.pedro.schwarz.goalstracker.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.pedro.schwarz.goalstracker.model.User
import com.pedro.schwarz.goalstracker.service.FirestoreService
import com.pedro.schwarz.goalstracker.service.StorageService

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
                            val id = firebaseUser.uid
                            StorageService.storeImage(
                                "$IMAGE_PATH/$id",
                                user.imageUrl,
                                onComplete = { result ->
                                    when (result) {
                                        is Success -> {
                                            result.data?.let { imageUrl ->
                                                FirestoreService.insertDocument(
                                                    USER_COLLECTION,
                                                    id,
                                                    user.copy(id = id, imageUrl = imageUrl),
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
        auth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                liveData.postValue(Success())
            } else {
                liveData.postValue(Failure())
            }
        }
    }

    fun signOutUser() {
        auth.signOut()
    }

    companion object {
        private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    }
}