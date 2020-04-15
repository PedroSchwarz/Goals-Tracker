package com.pedro.schwarz.goalstracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.pedro.schwarz.goalstracker.model.Checkpoint
import com.pedro.schwarz.goalstracker.repository.CheckpointRepository
import kotlinx.coroutines.Job

class CheckpointsViewModel(private val checkpointRepository: CheckpointRepository) : ViewModel() {

    private val job: Job = Job()

    private val _isEmpty = MutableLiveData<Boolean>().also { it.value = setIsEmpty }

    val isEmpty: LiveData<Boolean> get() = _isEmpty

    var setIsEmpty: Boolean = false
        set(value) {
            field = value
            _isEmpty.value = value
        }

    fun fetchCheckpoints(goalId: Long): LiveData<PagedList<Checkpoint>> =
        checkpointRepository.fetchCheckpoints(goalId)

    fun saveCheckpoint(checkpoint: Checkpoint) = checkpointRepository.saveCheckpoint(checkpoint)

    fun deleteCheckpoint(checkpoint: Checkpoint) = checkpointRepository.deleteCheckpoint(checkpoint)

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
