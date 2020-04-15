package com.pedro.schwarz.goalstracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.pedro.schwarz.goalstracker.model.Goal
import com.pedro.schwarz.goalstracker.repository.CheckpointRepository
import com.pedro.schwarz.goalstracker.repository.GoalRepository
import com.pedro.schwarz.goalstracker.repository.MilestoneRepository
import kotlinx.coroutines.Job

class GoalsViewModel(
    private val goalRepository: GoalRepository,
    private val milestoneRepository: MilestoneRepository,
    private val checkpointRepository: CheckpointRepository
) : ViewModel() {

    private val job: Job = Job()

    private val _isEmpty = MutableLiveData<Boolean>().also { it.value = setIsEmpty }

    val isEmpty: LiveData<Boolean> get() = _isEmpty

    var setIsEmpty: Boolean = false
        set(value) {
            field = value
            _isEmpty.value = value
        }

    private val _isRefreshing = MutableLiveData<Boolean>().also { it.value = setIsRefreshing }

    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    var setIsRefreshing: Boolean = false
        set(value) {
            field = value
            _isRefreshing.value = value
        }

    fun fetchGoals(completed: Boolean = false): LiveData<PagedList<Goal>> =
        goalRepository.fetchGoals(completed)

    fun deleteGoal(goal: Goal) = goalRepository.deleteGoal(goal, job)

    fun saveGoal(goal: Goal) = goalRepository.insertGoal(goal, job)

    fun fetchGoalsNetwork() = goalRepository.fetchGoalsNetwork()

    fun fetchGoalsNetworkChildren() {
        milestoneRepository.fetchMilestonesNetwork()
        checkpointRepository.fetchCheckpointsNetwork()
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
