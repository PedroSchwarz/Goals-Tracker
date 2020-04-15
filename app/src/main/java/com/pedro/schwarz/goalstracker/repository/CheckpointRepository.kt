package com.pedro.schwarz.goalstracker.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.pedro.schwarz.goalstracker.database.dao.CheckpointDAO
import com.pedro.schwarz.goalstracker.model.Checkpoint
import com.pedro.schwarz.goalstracker.service.FirestoreService
import com.pedro.schwarz.goalstracker.service.StorageService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

private const val IMAGE_PATH = "checkpoints_images"
private const val CHECKPOINT_COLLECTION = "checkpoints"

class CheckpointRepository(private val checkpointDAO: CheckpointDAO) {

    fun fetchCheckpoints(goalId: Long): LiveData<List<Checkpoint>> {
        return if (auth.currentUser != null) checkpointDAO.fetchCheckpoints(
            goalId,
            auth.currentUser!!.uid
        )
        else MutableLiveData()
    }

    fun saveCheckpoint(checkpoint: Checkpoint, job: Job = Job()) =
        MutableLiveData<Resource<Unit>>().also { liveData ->
            auth.currentUser?.let { user ->
                StorageService.storeImage(
                    "$IMAGE_PATH/${checkpoint.userId}-${checkpoint.createdAt}",
                    checkpoint.imageUrl,
                    onComplete = { result ->
                        when (result) {
                            is Success -> {
                                result.data?.let { imageUrl ->
                                    CoroutineScope(Dispatchers.IO + job).launch {
                                        try {
                                            val updatedCheckpoint = checkpoint.copy(
                                                userId = user.uid,
                                                imageUrl = imageUrl
                                            )
                                            val id =
                                                checkpointDAO.insertCheckpoint(updatedCheckpoint)
                                            FirestoreService.insertDocument(
                                                CHECKPOINT_COLLECTION,
                                                "${user.uid}-${checkpoint.goalId}-$id",
                                                updatedCheckpoint.copy(id = id)
                                            )
                                            liveData.postValue(Success())
                                        } catch (e: IOException) {
                                            liveData.postValue(Failure(error = e.message))
                                        }
                                    }
                                }
                            }
                            is Failure -> {
                                liveData.postValue(Failure(error = result.error))
                            }
                        }
                    })
            }
        }

    fun fetchCheckpoint(checkpointId: Long, goalId: Long) =
        checkpointDAO.fetchCheckpoint(checkpointId, goalId)

    companion object {
        private val auth = FirebaseAuth.getInstance()
    }
}