package com.pedro.schwarz.goalstracker.ui.action

import android.app.AlertDialog
import android.content.Context

fun showAlertDialog(context: Context, onClose: () -> Unit) {
    AlertDialog.Builder(context).apply {
        setCancelable(false)
        setTitle("Permissions Denied")
        setMessage("To use this feature you must grant the required permissions.")
        setNeutralButton("OK") { _, _ ->
            onClose()
        }
    }.show()
}