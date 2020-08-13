package com.pedro.schwarz.goalstracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pedro.schwarz.goalstracker.repository.GoalRepository

class ProfileViewModel(private val goalRepository: GoalRepository) : ViewModel() {

    private val _uncompletedCount = MutableLiveData<Int>().also { it.value = setUncompletedCount }
    val uncompletedCount: LiveData<Int> = _uncompletedCount

    var setUncompletedCount: Int = 0
        set(value) {
            field = value
            _uncompletedCount.value = value
        }

    private val _completedCount = MutableLiveData<Int>().also { it.value = setCompletedCount }
    val completedCount: LiveData<Int> = _completedCount

    var setCompletedCount: Int = 0
        set(value) {
            field = value
            _completedCount.value = value
        }

    fun fetchUncompletedGoalsCount() = goalRepository.fetchUncompletedGoalsCount()

    fun fetchCompletedGoalsCount() = goalRepository.fetchCompletedGoalsCount()
}