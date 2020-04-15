package com.pedro.schwarz.goalstracker.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
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

private const val USER_FIELD = "userId"

private const val PAGED_LIST_SIZE = 10

class CheckpointRepository(private val checkpointDAO: CheckpointDAO) {

    fun fetchCheckpoints(goalId: Long): LiveData<PagedList<Checkpoint>> {
        return if (auth.currentUser != null) {
            checkpointDAO.fetchCheckpoints(goalId, auth.currentUser!!.uid)
                .toLiveData(pageSize = PAGED_LIST_SIZE)
        } else MutableLiveData()
    }

    fun saveCheckpoint(checkpoint: Checkpoint, job: Job = Job()) =
        MutableLiveData<Resource<Unit>>().also { liveData ->
            auth.currentUser?.let { user ->
                StorageService.storeImage(
                    "$IMAGE_PATH/${user.uid}-${checkpoint.createdAt}",
                    checkpoint.imageUrl,
                    onSuccess = { imageUrl ->
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
                    },
                    onFailure = { error -> liveData.postValue(Failure(error = error)) })
            }
        }

    fun fetchCheckpoint(checkpointId: Long, goalId: Long) =
        checkpointDAO.fetchCheckpoint(checkpointId, goalId)

    fun deleteCheckpoint(checkpoint: Checkpoint, job: Job = Job()) =
        MutableLiveData<Resource<Unit>>().also { liveData ->
            CoroutineScope(Dispatchers.IO + job).launch {
                try {
                    checkpointDAO.deleteCheckpoint(checkpoint)
                    FirestoreService.deleteDocument(
                        CHECKPOINT_COLLECTION,
                        "${checkpoint.userId}-${checkpoint.goalId}-${checkpoint.id}"
                    )
                    StorageService.deleteImage("$IMAGE_PATH/${checkpoint.userId}-${checkpoint.createdAt}")
                    liveData.postValue(Success())
                } catch (e: IOException) {
                    liveData.postValue(Failure(error = e.message))
                }
            }
        }

    fun fetchCheckpointsNetwork() {
        auth.currentUser?.let { user ->
            FirestoreService.fetchDocuments<Checkpoint>(
                CHECKPOINT_COLLECTION,
                USER_FIELD,
                user.uid,
                onSuccess = { result ->
                    CoroutineScope(Dispatchers.IO).launch {
                        checkpointDAO.insertCheckpoint(result)
                    }
                })
        }
    }

    companion object {
        private val auth = FirebaseAuth.getInstance()
    }
}