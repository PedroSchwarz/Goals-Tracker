package com.pedro.schwarz.goalstracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.pedro.schwarz.goalstracker.repository.GoalRepository

class GoalDetailsViewModel(private val goalRepository: GoalRepository) : ViewModel() {

    fun fetchGoal(goalId: Long) = goalRepository.fetchGoal(goalId, auth.currentUser!!.uid)

    companion object {
        private val auth = FirebaseAuth.getInstance()
    }
}
