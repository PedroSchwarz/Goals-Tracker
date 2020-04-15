package com.pedro.schwarz.goalstracker.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.pedro.schwarz.goalstracker.database.dao.MilestoneDAO
import com.pedro.schwarz.goalstracker.model.Milestone
import com.pedro.schwarz.goalstracker.service.FirestoreService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

private const val MILESTONE_COLLECTION = "milestones"

class MilestoneRepository(private val milestoneDAO: MilestoneDAO) {

    fun fetchMilestones(goalId: Long): LiveData<List<Milestone>> {
        return if (auth.currentUser != null) milestoneDAO.fetchMilestones(
            goalId,
            auth.currentUser!!.uid
        )
        else MutableLiveData()
    }

    fun insertMilestone(milestone: Milestone, job: Job = Job()) =
        MutableLiveData<Resource<Unit>>().also { liveData ->
            auth.currentUser?.let { user ->
                CoroutineScope(Dispatchers.IO + job).launch {
                    try {
                        val updatedMilestone = milestone.copy(userId = user.uid)
                        val id = milestoneDAO.insertMilestone(updatedMilestone)
                        FirestoreService.insertDocument(
                            MILESTONE_COLLECTION,
                            "${user.uid}-${milestone.goalId}-$id",
                            updatedMilestone.copy(id = id)
                        )
                        liveData.postValue(Success())
                    } catch (e: IOException) {
                        liveData.postValue(Failure(error = e.message))
                    }
                }
            }
        }

    fun updateMilestone(milestone: Milestone, job: Job = Job()): LiveData<Resource<Unit>> {
        val liveData = MutableLiveData<Resource<Unit>>()
        CoroutineScope(Dispatchers.IO + job).launch {
            try {
                milestoneDAO.updateMilestone(milestone)
                FirestoreService.insertDocument(
                    MILESTONE_COLLECTION,
                    "${milestone.userId}-${milestone.goalId}-${milestone.id}",
                    milestone
                )
                liveData.postValue(Success())
            } catch (e: IOException) {
                liveData.postValue(Failure(error = e.message))
            }
        }
        return liveData
    }

    fun deleteMilestone(milestone: Milestone, job: Job) =
        MutableLiveData<Resource<Unit>>().also { liveData ->
            CoroutineScope(Dispatchers.IO + job).launch {
                try {
                    milestoneDAO.deleteMilestone(milestone)
                    FirestoreService.deleteDocument(
                        MILESTONE_COLLECTION,
                        "${milestone.userId}-${milestone.goalId}-${milestone.id}"
                    )
                    liveData.postValue(Success())
                } catch (e: IOException) {
                    liveData.postValue(Failure(error = e.message))
                }
            }
        }

    companion object {
        private val auth = FirebaseAuth.getInstance()
    }
}