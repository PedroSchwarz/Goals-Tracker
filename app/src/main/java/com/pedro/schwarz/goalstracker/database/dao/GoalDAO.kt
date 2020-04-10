package com.pedro.schwarz.goalstracker.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pedro.schwarz.goalstracker.model.Goal

@Dao
interface GoalDAO {
    @Query("SELECT * FROM goal WHERE userId = :userId ORDER BY createdAt DESC")
    fun fetchGoals(userId: String): LiveData<List<Goal>>

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
}