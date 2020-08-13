package com.pedro.schwarz.goalstracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AppViewModel : ViewModel() {

    private val _components = MutableLiveData<Components>().also { it.value = setComponents }
    val components: LiveData<Components> get() = _components

    var setComponents = Components()
        set(value) {
            field = value
            _components.value = value
        }
}

class Components(val appBar: AppBar = AppBar(), val bottomNav: Boolean = false)

class AppBar(val set: Boolean = false, val elevation: Float = 11.0f)