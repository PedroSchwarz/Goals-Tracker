package com.pedro.schwarz.goalstracker.ui.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.pedro.schwarz.goalstracker.model.Goal
import com.pedro.schwarz.goalstracker.repository.GoalRepository

class GoalsViewModel(private val goalRepository: GoalRepository) : ViewModel() {

    val goalsList = MediatorLiveData<List<Goal>>()

    fun fetchGoals() {
        auth.currentUser?.let { user ->
            goalsList.addSource(goalRepository.fetchGoals(user.uid), Observer { result ->
                goalsList.postValue(result)
            })
        }
    }

    companion object {
        private val auth = FirebaseAuth.getInstance()
    }
}
