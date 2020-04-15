package com.pedro.schwarz.goalstracker.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.pedro.schwarz.goalstracker.model.User
import com.pedro.schwarz.goalstracker.service.AuthService
import com.pedro.schwarz.goalstracker.service.FirestoreService
import com.pedro.schwarz.goalstracker.service.StorageService

private const val USER_COLLECTION = "users"
private const val IMAGE_PATH = "users_images"

class AuthRepository {

    fun registerUser(user: User): LiveData<Resource<Unit>> {
        val liveData = MutableLiveData<Resource<Unit>>()
        AuthService.createUserWithEmailAndPassword(
            user.email,
            user.password,
            onSuccess = { id ->
                StorageService.storeImage(
                    "$IMAGE_PATH/$id",
                    user.imageUrl,
                    onSuccess = { imageUrl ->
                        FirestoreService.insertDocument(
                            USER_COLLECTION,
                            id,
                            user.copy(id = id, imageUrl = imageUrl),
                            onComplete = { result ->
                                liveData.postValue(result)
                            })
                    },
                    onFailure = { error ->
                        liveData.postValue(Failure(error = error))
                    })
            },
            onFailure = { error ->
                liveData.postValue(Failure(error = error))
            })
        return liveData
    }

    fun signInUser(email: String, password: String): LiveData<Resource<Unit>> {
        val liveData = MutableLiveData<Resource<Unit>>()
        AuthService.signInUserWithEmailAndPassword(email, password,
            onSuccess = {
                liveData.postValue(Success())
            },
            onFailure = { error ->
                liveData.postValue(Failure(error = error))
            })
        return liveData
    }

    fun checkUserState() = MutableLiveData<Resource<Unit>>().also { liveData ->
        AuthService.listenUserStateChanges(onSuccess = {
            liveData.postValue(Success())
        }, onFailure = {
            liveData.postValue(Failure())
        })
    }

    fun signOutUser() {
        auth.signOut()
    }

    companion object {
        private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    }
}