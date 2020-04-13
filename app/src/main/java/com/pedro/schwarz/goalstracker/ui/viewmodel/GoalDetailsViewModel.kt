package com.pedro.schwarz.goalstracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pedro.schwarz.goalstracker.model.Goal
import com.pedro.schwarz.goalstracker.model.Milestone
import com.pedro.schwarz.goalstracker.repository.GoalRepository
import com.pedro.schwarz.goalstracker.repository.MilestoneRepository
import com.pedro.schwarz.goalstracker.repository.Resource
import kotlinx.coroutines.Job

class GoalDetailsViewModel(
    private val goalRepository: GoalRepository,
    private val milestoneRepository: MilestoneRepository
) : ViewModel() {

    private val job: Job = Job()

    private val _hide = MutableLiveData<Boolean>().also { it.value = setHide }

    val hide: LiveData<Boolean> get() = _hide

    var setHide: Boolean = true
        set(value) {
            field = value
            _hide.value = value
        }

    private val _scrollDown = MutableLiveData<Boolean>().also { it.value = setScrollDown }

    val scrollDown: LiveData<Boolean> get() = _scrollDown

    var setScrollDown: Boolean = false
        set(value) {
            field = value
            _scrollDown.value = value
        }

    fun fetchGoal(goalId: Long) = goalRepository.fetchGoal(goalId)

    fun fetchMilestones(goalId: Long) = milestoneRepository.fetchMilestones(goalId)

    fun saveMilestone(
        milestone: Milestone,
        goal: Goal = Goal(),
        toggle: Boolean = false
    ): LiveData<Resource<Unit>> {
        return if (milestone.id > 0L) {
            if (toggle) {
                goalRepository.updateGoal(
                    goal.copy(
                        completedMilestones = getUpdatedMilestones(
                            milestone.completed,
                            goal.completedMilestones
                        )
                    )
                )
            }
            milestoneRepository.updateMilestone(milestone, job)
        } else {
            goalRepository.updateGoal(goal.copy(milestones = goal.milestones + 1))
            milestoneRepository.insertMilestone(milestone, job)
        }
    }

    private fun getUpdatedMilestones(completed: Boolean, completedMilestones: Int): Int {
        return if (completed) completedMilestones + 1
        else completedMilestones - 1
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
