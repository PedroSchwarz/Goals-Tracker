package com.pedro.schwarz.goalstracker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pedro.schwarz.goalstracker.database.dao.GoalDAO
import com.pedro.schwarz.goalstracker.database.dao.MilestoneDAO
import com.pedro.schwarz.goalstracker.model.Goal
import com.pedro.schwarz.goalstracker.model.Milestone

@Database(entities = [Goal::class, Milestone::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getGoalDAO(): GoalDAO
    abstract fun getMilestoneDAO(): MilestoneDAO
}