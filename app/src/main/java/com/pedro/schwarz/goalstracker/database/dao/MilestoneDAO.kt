package com.pedro.schwarz.goalstracker.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pedro.schwarz.goalstracker.model.Milestone

@Dao
interface MilestoneDAO {
    @Query("SELECT * FROM milestone WHERE goalId = :goalId AND userId = :userId")
    fun fetchMilestones(goalId: Long, userId: String): LiveData<List<Milestone>>

    @Insert
    fun insertMilestone(milestone: Milestone): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMilestone(milestone: List<Milestone>)

    @Update
    fun updateMilestone(milestone: Milestone)

    @Delete
    fun deleteMilestone(milestone: Milestone)
}