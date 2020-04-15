package com.pedro.schwarz.goalstracker.service

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

class StorageService {

    companion object {
        private val storage: FirebaseStorage = FirebaseStorage.getInstance()

        fun storeImage(
            filePath: String,
            image: String,
            onSuccess: (data: String) -> Unit,
            onFailure: (error: String) -> Unit
        ) {
            val path = storage.reference.child("$filePath.jpg")
            val uploadTask = path.putFile(Uri.parse(image))
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { error ->
                        error.message?.let { message ->
                            onFailure(message)
                        }
                    }
                }
                path.downloadUrl
            }
            urlTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess(task.result.toString())
                } else {
                    task.exception?.let { error ->
                        error.message?.let { message ->
                            onFailure(message)
                        }
                    }
                }
            }
        }

        fun deleteImage(
            filePath: String,
            onSuccess: () -> Unit = {},
            onFailure: (error: String) -> Unit = {}
        ) {
            storage.reference.child("$filePath.jpg").delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onSuccess()
                    } else {
                        task.exception?.let { error ->
                            error.message?.let { message ->
                                onFailure(message)
                            }
                        }
                    }
                }
        }
    }
}