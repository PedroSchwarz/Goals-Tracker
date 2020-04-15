package com.pedro.schwarz.goalstracker.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Goal::class,
        parentColumns = ["id"],
        childColumns = ["goalId"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )]
)
data class Checkpoint(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val description: String = "",
    val address: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val imageUrl: String = "",
    val goalId: Long = 0,
    val userId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)