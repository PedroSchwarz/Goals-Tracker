package com.pedro.schwarz.goalstracker.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.pedro.schwarz.goalstracker.database.dao.GoalDAO
import com.pedro.schwarz.goalstracker.model.Goal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

class GoalRepository(private val goalDAO: GoalDAO) {

    fun fetchGoals(): LiveData<List<Goal>> {
        return if (auth.currentUser != null) goalDAO.fetchGoals(auth.currentUser!!.uid)
        else MutableLiveData()
    }

    fun fetchGoal(goalId: Long) = goalDAO.fetchGoal(goalId, auth.currentUser!!.uid)

    fun insertGoal(goal: Goal, job: Job = Job()): LiveData<Resource<Unit>> {
        val liveData = MutableLiveData<Resource<Unit>>()
        CoroutineScope(Dispatchers.IO + job).launch {
            try {
                goalDAO.insertGoal(goal.copy(userId = auth.currentUser!!.uid))
                liveData.postValue(Success())
            } catch (e: IOException) {
                liveData.postValue(Failure(error = e.message))
            }
        }
        return liveData
    }

    fun updateGoal(goal: Goal, job: Job = Job()): LiveData<Resource<Unit>> {
        val liveData = MutableLiveData<Resource<Unit>>()
        CoroutineScope(Dispatchers.IO + job).launch {
            try {
                goalDAO.updateGoal(goal)
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