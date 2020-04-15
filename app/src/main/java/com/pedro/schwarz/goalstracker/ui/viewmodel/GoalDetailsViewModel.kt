package com.pedro.schwarz.goalstracker.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
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

    private val _isEmpty = MutableLiveData<Boolean>().also { it.value = setIsEmpty }

    val isEmpty: LiveData<Boolean> get() = _isEmpty

    var setIsEmpty: Boolean = false
        set(value) {
            field = value
            _isEmpty.value = value
        }

    fun fetchGoal(goalId: Long) = goalRepository.fetchGoal(goalId)

    fun fetchMilestones(goalId: Long): LiveData<PagedList<Milestone>> = milestoneRepository.fetchMilestones(goalId)

    fun saveMilestone(
        milestone: Milestone,
        goal: Goal = Goal(),
        toggle: Boolean = false,
        deletion: Boolean = false
    ): LiveData<Resource<Unit>> {
        return if (milestone.id > 0L && deletion) {
            val completedMilestones = if (milestone.completed) goal.completedMilestones + 1
            else goal.completedMilestones
            goalRepository.updateGoal(
                goal.copy(
                    milestones = goal.milestones + 1,
                    completedMilestones = completedMilestones
                )
            )
            milestoneRepository.insertMilestone(milestone, job)
        } else if (milestone.id > 0L && !deletion) {
            if (toggle) {
                val completedMilestones = if (milestone.completed) goal.completedMilestones + 1
                else goal.completedMilestones - 1
                goalRepository.updateGoal(goal.copy(completedMilestones = completedMilestones))
            }
            milestoneRepository.updateMilestone(milestone, job)
        } else {
            goalRepository.updateGoal(goal.copy(milestones = goal.milestones + 1))
            milestoneRepository.insertMilestone(milestone, job)
        }
    }

    fun deleteMilestone(milestone: Milestone, goal: Goal): LiveData<Resource<Unit>> {
        val completedMilestones = if (milestone.completed) goal.completedMilestones - 1
        else goal.completedMilestones
        goalRepository.updateGoal(
            goal.copy(
                milestones = goal.milestones - 1,
                completedMilestones = completedMilestones
            )
        )
        return milestoneRepository.deleteMilestone(milestone, job)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
