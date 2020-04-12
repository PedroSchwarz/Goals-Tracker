package com.pedro.schwarz.goalstracker.ui.databinding

import androidx.lifecycle.MutableLiveData
import com.pedro.schwarz.goalstracker.model.Goal

class GoalData(
    private var goal: Goal = Goal(),
    val title: MutableLiveData<String> = MutableLiveData<String>().also {
        it.value = goal.title
    },
    val description: MutableLiveData<String> = MutableLiveData<String>().also {
        it.value = goal.description
    },
    val progress: MutableLiveData<Double> = MutableLiveData<Double>().also {
        it.value = goal.progress
    },
    val milestones: MutableLiveData<Int> = MutableLiveData<Int>().also {
        it.value = goal.milestones
    },
    val completedMilestones: MutableLiveData<Int> = MutableLiveData<Int>().also {
        it.value = goal.completedMilestones
    },
    val targetDate: MutableLiveData<Long> = MutableLiveData<Long>().also {
        it.value = goal.targetDate
    },
    val priorityId: MutableLiveData<Long> = MutableLiveData<Long>().also {
        it.value = goal.priorityId
    },
    val categoryId: MutableLiveData<Long> = MutableLiveData<Long>().also {
        it.value = goal.categoryId
    },
    val createdAt: MutableLiveData<Long> = MutableLiveData<Long>().also {
        it.value = goal.createdAt
    }
) {

    fun setGoal(goal: Goal) {
        this.goal = goal
        this.title.postValue(this.goal.title)
        this.description.postValue(this.goal.description)
        this.progress.postValue(this.goal.progress)
        this.milestones.postValue(this.goal.milestones)
        this.completedMilestones.postValue(this.goal.completedMilestones)
        this.targetDate.postValue(this.goal.targetDate)
        this.priorityId.postValue(this.goal.priorityId)
        this.categoryId.postValue(this.goal.categoryId)
        this.createdAt.postValue(this.goal.createdAt)
    }

    fun toGoal(): Goal? {
        return goal.copy(
            title = this.title.value ?: return null,
            description = this.description.value ?: return null,
            progress = this.progress.value ?: return null,
            milestones = this.milestones.value ?: return null,
            completedMilestones = this.completedMilestones.value ?: return null,
            targetDate = this.targetDate.value ?: return null,
            priorityId = this.priorityId.value ?: return null,
            categoryId = this.categoryId.value ?: return null,
            createdAt = this.createdAt.value ?: return null
        )
    }
}