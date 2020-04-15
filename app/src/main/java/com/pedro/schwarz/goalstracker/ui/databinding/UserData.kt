package com.pedro.schwarz.goalstracker.ui.databinding

import androidx.lifecycle.MutableLiveData
import com.pedro.schwarz.goalstracker.model.User

class UserData(
    private var user: User = User(),
    val name: MutableLiveData<String> = MutableLiveData<String>().also {
        it.value = user.name
    },
    val email: MutableLiveData<String> = MutableLiveData<String>().also {
        it.value = user.email
    },
    val password: MutableLiveData<String> = MutableLiveData<String>().also {
        it.value = user.password
    },
    val imageUrl: MutableLiveData<String> = MutableLiveData<String>().also {
        it.value = user.imageUrl
    }
) {

    fun setUser(user: User) {
        this.user = user
        this.name.postValue(this.user.name)
        this.email.postValue(this.user.email)
        this.password.postValue(this.user.password)
        this.imageUrl.postValue(this.user.imageUrl)
    }

    fun toUser(): User? {
        return this.user.copy(
            name = this.name.value ?: return null,
            email = this.email.value ?: return null,
            password = this.password.value ?: return null,
            imageUrl = this.imageUrl.value ?: return null
        )
    }
}