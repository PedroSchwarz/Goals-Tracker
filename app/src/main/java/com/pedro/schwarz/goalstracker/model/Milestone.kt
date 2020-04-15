package com.pedro.schwarz.goalstracker.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Goal::class,
        parentColumns = ["id"],
        childColumns = ["goalId"],
        onUpdate = CASCADE,
        onDelete = CASCADE
    )]
)
data class Milestone(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    val completed: Boolean = false,
    val goalId: Long = 0,
    val userId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)