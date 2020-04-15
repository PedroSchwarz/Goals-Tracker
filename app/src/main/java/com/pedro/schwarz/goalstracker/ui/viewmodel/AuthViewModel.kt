package com.pedro.schwarz.goalstracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pedro.schwarz.goalstracker.model.User
import com.pedro.schwarz.goalstracker.repository.AuthRepository

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>().also { it.value = setIsLoading }
    val isLoading: LiveData<Boolean> get() = _isLoading

    var setIsLoading: Boolean = false
        set(value) {
            field = value
            _isLoading.value = value
        }

    fun registerUser(user: User) = authRepository.registerUser(user)

    fun signInUser(email: String, password: String) = authRepository.signInUser(email, password)

    fun checkUserState() = authRepository.checkUserState()

    fun signOutUser() = authRepository.signOutUser()
}