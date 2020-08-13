package com.pedro.schwarz.goalstracker.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.pedro.schwarz.goalstracker.model.Goal

@Dao
interface GoalDAO {
    @Query("SELECT * FROM goal WHERE userId = :userId AND milestones = 0 OR completedMilestones != milestones ORDER BY createdAt DESC")
    fun fetchGoals(userId: String): DataSource.Factory<Int, Goal>

    @Query("SELECT * FROM goal WHERE userId = :userId AND milestones > 0 AND completedMilestones = milestones ORDER BY createdAt DESC")
    fun fetchCompletedGoals(userId: String): DataSource.Factory<Int, Goal>

    @Query("SELECT * FROM goal WHERE id = :id AND userId = :userId")
    fun fetchGoal(id: Long, userId: String): LiveData<Goal>

    @Insert
    fun insertGoal(goal: Goal): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertGoal(goal: List<Goal>)

    @Update
    fun updateGoal(goal: Goal)

    @Delete
    fun deleteGoal(goal: Goal)

    @Query("SELECT * FROM goal WHERE userId = :userId AND targetDate <= :warningDate AND completedMilestones < milestones")
    fun fetchExpiringUncompletedGoals(userId: String, warningDate: Long): List<Goal>

    @Query("SELECT COUNT(*) FROM goal WHERE userId = :userId AND milestones = 0 OR completedMilestones < milestones")
    fun fetchUncompletedGoalsCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM goal WHERE userId = :userId AND milestones > 0 AND completedMilestones = milestones")
    fun fetchCompletedGoalsCount(userId: String): Int
}