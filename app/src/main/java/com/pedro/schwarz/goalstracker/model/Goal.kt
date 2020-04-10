package com.pedro.schwarz.goalstracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Goal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val description: String = "",
    val progress: Double = 0.0,
    val milestones: Int = 0,
    val completedMilestones: Int = 0,
    val targetDate: Long = System.currentTimeMillis(),
    val priorityId: Long = 0,
    val categoryId: Long = 0,
    val userId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)