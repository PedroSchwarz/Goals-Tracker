package com.pedro.schwarz.goalstracker.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.pedro.schwarz.goalstracker.model.Checkpoint

@Dao
interface CheckpointDAO {
    @Query("SELECT * FROM checkpoint WHERE goalId = :goalId AND userId = :userId")
    fun fetchCheckpoints(goalId: Long, userId: String): DataSource.Factory<Int, Checkpoint>

    @Query("SELECT * FROM checkpoint WHERE id = :id AND goalId = :goalId")
    fun fetchCheckpoint(id: Long, goalId: Long): LiveData<Checkpoint>

    @Insert
    fun insertCheckpoint(checkpoint: Checkpoint): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCheckpoint(checkpoint: List<Checkpoint>)

    @Update
    fun updateCheckpoint(checkpoint: Checkpoint)

    @Delete
    fun deleteCheckpoint(checkpoint: Checkpoint)
}