package com.pedro.schwarz.goalstracker.ui.databinding

import androidx.lifecycle.MutableLiveData
import com.pedro.schwarz.goalstracker.model.Milestone

class MilestoneData(
    private var milestone: Milestone = Milestone(),
    val title: MutableLiveData<String> = MutableLiveData<String>().also {
        it.value = milestone.title
    },
    val completed: MutableLiveData<Boolean> = MutableLiveData<Boolean>().also {
        it.value = milestone.completed
    },
    val createdAt: MutableLiveData<Long> = MutableLiveData<Long>().also {
        it.value = milestone.createdAt
    }
) {

    fun setMilestone(milestone: Milestone) {
        this.milestone = milestone
        this.title.postValue(this.milestone.title)
        this.completed.postValue(this.milestone.completed)
        this.createdAt.postValue(this.milestone.createdAt)
    }

    fun toMilestone(): Milestone? {
        return milestone.copy(
            title = this.title.value ?: return null,
            completed = this.completed.value ?: return null,
            createdAt = this.createdAt.value ?: return null
        )
    }
}