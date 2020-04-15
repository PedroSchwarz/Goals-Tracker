package com.pedro.schwarz.goalstracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.pedro.schwarz.goalstracker.repository.CheckpointRepository

class CheckpointsViewModel(private val checkpointRepository: CheckpointRepository) : ViewModel() {

    fun fetchCheckpoints(goalId: Long) = checkpointRepository.fetchCheckpoints(goalId)
}
