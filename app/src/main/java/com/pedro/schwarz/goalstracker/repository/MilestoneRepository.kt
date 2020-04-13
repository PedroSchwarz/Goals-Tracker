package com.pedro.schwarz.goalstracker.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.pedro.schwarz.goalstracker.database.dao.MilestoneDAO
import com.pedro.schwarz.goalstracker.model.Milestone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class MilestoneRepository(private val milestoneDAO: MilestoneDAO) {

    fun fetchMilestones(goalId: Long): LiveData<List<Milestone>> {
        return if (auth.currentUser != null) milestoneDAO.fetchMilestones(
            goalId,
            auth.currentUser!!.uid
        )
        else MutableLiveData()
    }

    fun insertMilestone(milestone: Milestone, job: Job = Job()): LiveData<Resource<Unit>> {
        val liveData = MutableLiveData<Resource<Unit>>()
        CoroutineScope(Dispatchers.IO + job).launch {
            try {
                milestoneDAO.insertMilestone(milestone.copy(userId = auth.currentUser!!.uid))
                liveData.postValue(Success())
            } catch (e: IOException) {
                liveData.postValue(Failure(error = e.message))
            }
        }
        return liveData
    }

    fun updateMilestone(milestone: Milestone, job: Job = Job()): LiveData<Resource<Unit>> {
        val liveData = MutableLiveData<Resource<Unit>>()
        CoroutineScope(Dispatchers.IO + job).launch {
            try {
                milestoneDAO.updateMilestone(milestone)
                liveData.postValue(Success())
            } catch (e: IOException) {
                liveData.postValue(Failure(error = e.message))
            }
        }
        return liveData
    }

    companion object {
        private val auth = FirebaseAuth.getInstance()
    }
}