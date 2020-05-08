package com.pedro.schwarz.goalstracker.service

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.pedro.schwarz.goalstracker.model.User

class UserSharedPreferences(private val context: Context, private val name: String) {

    private lateinit var instance: SharedPreferences

    private fun getInstance(): SharedPreferences {
        if (!::instance.isInitialized) {
            instance = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        }
        return instance
    }

    fun setUserData(user: User) {
        getInstance().edit {
            putString("name", user.name)
            putString("avatar", user.imageUrl)
            putString("email", user.email)
        }
    }

    fun getUserData(): User {
        val name = getInstance().getString("name", "")
        val avatar = getInstance().getString("avatar", "")
        val email = getInstance().getString("email", "")
        return User(name = name!!, imageUrl = avatar!!, email = email!!)
    }

    fun clearUserData() {
        getInstance().edit {
            clear()
        }
    }
}