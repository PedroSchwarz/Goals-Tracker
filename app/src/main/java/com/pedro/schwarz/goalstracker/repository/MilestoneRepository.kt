package com.pedro.schwarz.goalstracker.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
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

private const val USER_FIELD = "userId"

private const val PAGED_LIST_SIZE = 10

class MilestoneRepository(private val milestoneDAO: MilestoneDAO) {

    fun fetchMilestones(goalId: Long): LiveData<PagedList<Milestone>> {
        return if (auth.currentUser != null) {
            milestoneDAO.fetchMilestones(goalId, auth.currentUser!!.uid)
                .toLiveData(pageSize = PAGED_LIST_SIZE)
        } else MutableLiveData()
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

    fun fetchMilestonesNetwork() {
        auth.currentUser?.let { user ->
            FirestoreService.fetchDocuments<Milestone>(
                MILESTONE_COLLECTION,
                USER_FIELD,
                user.uid,
                onSuccess = { result ->
                    CoroutineScope(Dispatchers.IO).launch {
                        milestoneDAO.insertMilestone(result)
                    }
                })
        }
    }

    companion object {
        private val auth = FirebaseAuth.getInstance()
    }
}