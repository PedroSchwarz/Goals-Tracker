package com.pedro.schwarz.goalstracker.service

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.pedro.schwarz.goalstracker.repository.Failure
import com.pedro.schwarz.goalstracker.repository.Resource
import com.pedro.schwarz.goalstracker.repository.Success

class StorageService {

    companion object {
        private val storage: FirebaseStorage = FirebaseStorage.getInstance()

        fun storeImage(
            filePath: String,
            image: String,
            onComplete: (result: Resource<String>) -> Unit
        ) {
            val path = storage.reference.child("$filePath.jpg")
            val uploadTask = path.putFile(Uri.parse(image))
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { error ->
                        onComplete(Failure(error = error.message))
                    }
                }
                path.downloadUrl
            }
            urlTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(Success(data = task.result.toString()))
                } else {
                    task.exception?.let { error ->
                        onComplete(Failure(error = error.message))
                    }
                }
            }
        }
    }
}