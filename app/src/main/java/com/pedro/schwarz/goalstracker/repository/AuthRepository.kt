package com.pedro.schwarz.goalstracker.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.pedro.schwarz.goalstracker.model.User
import com.pedro.schwarz.goalstracker.service.AuthService
import com.pedro.schwarz.goalstracker.service.FirestoreService
import com.pedro.schwarz.goalstracker.service.StorageService
import com.pedro.schwarz.goalstracker.service.UserSharedPreferences

private const val USER_COLLECTION = "users"
private const val IMAGE_PATH = "users_images"

class AuthRepository(private val userSharedPreferences: UserSharedPreferences) {

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
                        val updatedUser = user.copy(id = id, password = "", imageUrl = imageUrl)
                        FirestoreService.insertDocument(
                            USER_COLLECTION,
                            id,
                            updatedUser,
                            onComplete = { result ->
                                userSharedPreferences.setUserData(updatedUser)
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
                setUserData()
                liveData.postValue(Success())
            },
            onFailure = { error ->
                liveData.postValue(Failure(error = error))
            })
        return liveData
    }

    private fun setUserData() {
        auth.currentUser?.let { firebaseUser ->
            FirestoreService.fetchDocument<User>(
                USER_COLLECTION,
                firebaseUser.uid,
                onSuccess = { user ->
                    userSharedPreferences.setUserData(user)
                })
        }
    }

    fun getUserData(): LiveData<User> {
        val liveData = MutableLiveData<User>()
        liveData.value = userSharedPreferences.getUserData()
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
        userSharedPreferences.clearUserData()
    }

    companion object {
        private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    }
}