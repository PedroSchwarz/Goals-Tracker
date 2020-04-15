package com.pedro.schwarz.goalstracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pedro.schwarz.goalstracker.model.Checkpoint
import com.pedro.schwarz.goalstracker.repository.CheckpointRepository
import kotlinx.coroutines.Job

class CheckpointFormViewModel(private val checkpointRepository: CheckpointRepository) :
    ViewModel() {

    private val job: Job = Job()

    private val _isSaving = MutableLiveData<Boolean>().also { it.value = setIsSaving }
    val isSaving: LiveData<Boolean> get() = _isSaving

    var setIsSaving: Boolean = false
        set(value) {
            field = value
            _isSaving.value = value
        }

    private val _isFetchingLocation =
        MutableLiveData<Boolean>().also { it.value = setIsFetchingLocation }
    val isFetchingLocation: LiveData<Boolean> get() = _isFetchingLocation

    var setIsFetchingLocation: Boolean = false
        set(value) {
            field = value
            _isFetchingLocation.value = value
        }

    fun saveCheckpoint(checkpoint: Checkpoint) =
        checkpointRepository.saveCheckpoint(checkpoint, job)

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
