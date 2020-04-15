package com.pedro.schwarz.goalstracker.ui.databinding

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pedro.schwarz.goalstracker.model.Checkpoint

class CheckpointData(
    private var checkpoint: Checkpoint = Checkpoint(),
    val description: MutableLiveData<String> = MutableLiveData<String>().also {
        it.value = checkpoint.description
    },
    val address: MutableLiveData<String> = MutableLiveData<String>().also {
        it.value = checkpoint.address
    },
    val longitude: MutableLiveData<Double> = MutableLiveData<Double>().also {
        it.value = checkpoint.longitude
    },
    val latitude: MutableLiveData<Double> = MutableLiveData<Double>().also {
        it.value = checkpoint.latitude
    },
    val imageUrl: MutableLiveData<String> = MutableLiveData<String>().also {
        it.value = checkpoint.imageUrl
    },
    val createdAt: MutableLiveData<Long> = MutableLiveData<Long>().also {
        it.value = checkpoint.createdAt
    }
) {

    fun setCheckpoint(checkpoint: Checkpoint) {
        this.checkpoint = checkpoint
        this.description.postValue(this.checkpoint.description)
        this.address.postValue(this.checkpoint.address)
        this.longitude.postValue(this.checkpoint.longitude)
        this.latitude.postValue(this.checkpoint.latitude)
        this.imageUrl.postValue(this.checkpoint.imageUrl)
        this.createdAt.postValue(this.checkpoint.createdAt)
    }

    fun toCheckpoint(): Checkpoint? {
        return checkpoint.copy(
            description = this.description.value ?: return null,
            address = this.address.value ?: return null,
            longitude = this.longitude.value ?: return null,
            latitude = this.latitude.value ?: return null,
            imageUrl = this.imageUrl.value ?: return null,
            createdAt = this.createdAt.value ?: return null
        )
    }
}