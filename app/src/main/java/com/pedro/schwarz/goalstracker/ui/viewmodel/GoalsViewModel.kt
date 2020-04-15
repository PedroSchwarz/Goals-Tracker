package com.pedro.schwarz.goalstracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pedro.schwarz.goalstracker.model.Goal
import com.pedro.schwarz.goalstracker.repository.GoalRepository
import kotlinx.coroutines.Job

class GoalsViewModel(private val goalRepository: GoalRepository) : ViewModel() {

    private val job: Job = Job()

    private val _isEmpty = MutableLiveData<Boolean>().also { it.value = setIsEmpty }

    val isEmpty: LiveData<Boolean> get() = _isEmpty

    var setIsEmpty: Boolean = false
        set(value) {
            field = value
            _isEmpty.value = value
        }

    fun fetchGoals() = goalRepository.fetchGoals()

    fun deleteGoal(goal: Goal) = goalRepository.deleteGoal(goal, job)

    fun saveGoal(goal: Goal) = goalRepository.insertGoal(goal, job)

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
