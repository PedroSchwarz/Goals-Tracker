package com.pedro.schwarz.goalstracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.pedro.schwarz.goalstracker.repository.CheckpointRepository

class CheckpointDetailsViewModel(private val checkpointRepository: CheckpointRepository) :
    ViewModel() {

    fun fetchCheckpoint(checkpointId: Long, goalId: Long) =
        checkpointRepository.fetchCheckpoint(checkpointId, goalId)
}