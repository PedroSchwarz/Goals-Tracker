package com.pedro.schwarz.goalstracker.ui.action

import android.app.AlertDialog
import android.content.Context
import com.pedro.schwarz.goalstracker.R

fun showAlertDialog(context: Context, onClose: () -> Unit) {
    AlertDialog.Builder(context).apply {
        setCancelable(false)
        setTitle(context.getString(R.string.permission_title))
        setMessage(context.getString(R.string.permission_message))
        setNeutralButton(context.getString(R.string.permission_neutral)) { _, _ ->
            onClose()
        }
    }.show()
}