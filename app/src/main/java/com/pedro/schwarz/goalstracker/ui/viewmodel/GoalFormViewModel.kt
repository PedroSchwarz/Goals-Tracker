package com.pedro.schwarz.goalstracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.pedro.schwarz.goalstracker.model.Goal
import com.pedro.schwarz.goalstracker.repository.GoalRepository
import com.pedro.schwarz.goalstracker.repository.Resource
import kotlinx.coroutines.Job

class GoalFormViewModel(private val goalRepository: GoalRepository) : ViewModel() {

    private val job = Job()

    private val _isLoading = MutableLiveData<Boolean>().also { it.value = setIsLoading }
    val isLoading: LiveData<Boolean> get() = _isLoading

    var setIsLoading: Boolean = false
        set(value) {
            field = value
            _isLoading.value = value
        }

    fun saveGoal(goal: Goal): LiveData<Resource<Unit>> {
        return if (goal.id > 0L) goalRepository.updateGoal(goal, job)
        else goalRepository.insertGoal(goal.copy(userId = auth.currentUser!!.uid), job)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    companion object {
        private val auth = FirebaseAuth.getInstance()
    }
}
