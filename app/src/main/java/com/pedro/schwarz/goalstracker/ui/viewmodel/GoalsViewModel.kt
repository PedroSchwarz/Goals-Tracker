package com.pedro.schwarz.goalstracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.pedro.schwarz.goalstracker.repository.GoalRepository

class GoalsViewModel(private val goalRepository: GoalRepository) : ViewModel() {

    fun fetchGoals() = goalRepository.fetchGoals()
}
