package com.pedro.schwarz.goalstracker.service

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun getPermissions(
    permissions: Array<String>,
    context: Context,
    onSuccess: () -> Unit = {},
    onFailure: () -> Unit = {}
) {
    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onFailure()
            return
        }
    }
    onSuccess()
}
