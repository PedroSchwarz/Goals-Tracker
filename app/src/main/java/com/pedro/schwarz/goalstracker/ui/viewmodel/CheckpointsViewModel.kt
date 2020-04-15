package com.pedro.schwarz.goalstracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pedro.schwarz.goalstracker.repository.CheckpointRepository

class CheckpointsViewModel(private val checkpointRepository: CheckpointRepository) : ViewModel() {

    private val _isEmpty = MutableLiveData<Boolean>().also { it.value = setIsEmpty }

    val isEmpty: LiveData<Boolean> get() = _isEmpty

    var setIsEmpty: Boolean = false
        set(value) {
            field = value
            _isEmpty.value = value
        }

    fun fetchCheckpoints(goalId: Long) = checkpointRepository.fetchCheckpoints(goalId)
}
