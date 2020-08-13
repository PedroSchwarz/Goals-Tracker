package com.pedro.schwarz.goalstracker.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.auth.FirebaseAuth
import com.pedro.schwarz.goalstracker.database.dao.GoalDAO
import com.pedro.schwarz.goalstracker.model.Goal
import com.pedro.schwarz.goalstracker.service.FirestoreService
import com.pedro.schwarz.goalstracker.worker.CompletingNotificationWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

private const val GOAL_COLLECTION = "goals"
private const val MILESTONE_COLLECTION = "milestones"
private const val CHECKPOINT_COLLECTION = "checkpoints"

private const val PARENT_FIELD = "goalId"
private const val USER_FIELD = "userId"

private const val GOAL_TITLE_KEY = "goalTitle"

private const val PAGED_LIST_SIZE = 10

class GoalRepository(private val goalDAO: GoalDAO) {

    fun fetchGoals(completed: Boolean): LiveData<PagedList<Goal>> {
        return if (auth.currentUser != null) {
            return if (completed) goalDAO.fetchCompletedGoals(auth.currentUser!!.uid)
                .toLiveData(pageSize = PAGED_LIST_SIZE)
            else goalDAO.fetchGoals(auth.currentUser!!.uid)
                .toLiveData(pageSize = PAGED_LIST_SIZE)
        } else MutableLiveData()
    }

    fun fetchGoal(goalId: Long) = goalDAO.fetchGoal(goalId, auth.currentUser!!.uid)

    fun insertGoal(goal: Goal, job: Job = Job()) =
        MutableLiveData<Resource<Unit>>().also { liveData ->
            auth.currentUser?.let { user ->
                CoroutineScope(Dispatchers.IO + job).launch {
                    try {
                        val updatedGoal = goal.copy(userId = user.uid)
                        val id = goalDAO.insertGoal(updatedGoal)
                        FirestoreService.insertDocument(
                            GOAL_COLLECTION,
                            "${user.uid}-$id",
                            updatedGoal.copy(id = id)
                        )
                        liveData.postValue(Success())
                    } catch (e: IOException) {
                        liveData.postValue(Failure(error = e.message))
                    }
                }
            }
        }

    fun updateGoal(goal: Goal, job: Job = Job()): LiveData<Resource<Unit>> {
        val liveData = MutableLiveData<Resource<Unit>>()
        CoroutineScope(Dispatchers.IO + job).launch {
            try {
                goalDAO.updateGoal(goal)
                FirestoreService.insertDocument(GOAL_COLLECTION, "${goal.userId}-${goal.id}", goal)
                liveData.postValue(Success())
            } catch (e: IOException) {
                liveData.postValue(Failure(error = e.message))
            }
        }
        return liveData
    }

    fun deleteGoal(goal: Goal, job: Job = Job()) =
        MutableLiveData<Resource<Unit>>().also { liveData ->
            CoroutineScope(Dispatchers.IO + job).launch {
                try {
                    goalDAO.deleteGoal(goal)
                    FirestoreService.deleteDocument(GOAL_COLLECTION, "${goal.userId}-${goal.id}")
                    deleteChildrenMilestones(goal)
                    deleteChildrenCheckpoints(goal)
                    liveData.postValue(Success())
                } catch (e: IOException) {
                    liveData.postValue(Failure(error = e.message))
                }
            }
        }

    fun fetchUncompletedGoalsCount() = MutableLiveData<Int>().also { liveData ->
        auth.currentUser?.let { firebaseUser ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val count = goalDAO.fetchUncompletedGoalsCount(firebaseUser.uid)
                    liveData.postValue(count)
                } catch (e: IOException) {
                    liveData.postValue(0)
                }
            }
        }
    }

    fun fetchCompletedGoalsCount() = MutableLiveData<Int>().also { liveData ->
        auth.currentUser?.let { firebaseUser ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val count = goalDAO.fetchCompletedGoalsCount(firebaseUser.uid)
                    liveData.postValue(count)
                } catch (e: IOException) {
                    liveData.postValue(0)
                }
            }
        }
    }

    private fun deleteChildrenCheckpoints(goal: Goal) {
        FirestoreService.deleteChildren(
            CHECKPOINT_COLLECTION,
            PARENT_FIELD,
            goal.id,
            USER_FIELD,
            goal.userId
        )
    }

    private fun deleteChildrenMilestones(goal: Goal) {
        FirestoreService.deleteChildren(
            MILESTONE_COLLECTION,
            PARENT_FIELD,
            goal.id,
            USER_FIELD,
            goal.userId
        )
    }

    fun fetchGoalsNetwork(): LiveData<Resource<Unit>> {
        val liveData = MutableLiveData<Resource<Unit>>()
        auth.currentUser?.let { user ->
            FirestoreService.fetchDocuments<Goal>(
                GOAL_COLLECTION,
                USER_FIELD,
                user.uid,
                onSuccess = { result ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            goalDAO.insertGoal(result)
                            liveData.postValue(Success())
                        } catch (e: IOException) {
                            liveData.postValue(Failure(error = e.message))
                        }
                    }
                },
                onFailure = { error ->
                    liveData.postValue(Failure(error = error))
                })
        }
        return liveData
    }

    fun fetchExpiringUncompletedGoals(onCompleted: (goals: List<Goal>) -> Unit) {
        auth.currentUser?.let { user ->
            val warningDate = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_MONTH, 2)
            }.timeInMillis
            CoroutineScope(Dispatchers.IO).launch {
                val goals = goalDAO.fetchExpiringUncompletedGoals(user.uid, warningDate)
                onCompleted(goals)
            }
        }
    }

    fun checkSendCompletingNotification(context: Context, goal: Goal, completed: Boolean) {
        val completedMilestones = if (completed) goal.completedMilestones + 1
        else goal.completedMilestones - 1
        if (goal.milestones > 5 && completedMilestones == goal.milestones - 1) {
            val goalData = workDataOf(GOAL_TITLE_KEY to goal.title)
            val request = OneTimeWorkRequestBuilder<CompletingNotificationWorker>()
                .setInputData(goalData)
                .build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }

    companion object {
        private val auth = FirebaseAuth.getInstance()
    }
}