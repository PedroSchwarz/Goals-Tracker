package com.pedro.schwarz.goalstracker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pedro.schwarz.goalstracker.database.dao.GoalDAO
import com.pedro.schwarz.goalstracker.model.Goal

@Database(entities = [Goal::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getGoalDAO(): GoalDAO
}