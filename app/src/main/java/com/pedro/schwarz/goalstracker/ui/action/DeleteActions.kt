package com.pedro.schwarz.goalstracker.ui.action

import android.app.AlertDialog
import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar

private const val ACTION_DURATION = 5000

fun showDeleteDialog(context: Context, onDelete: () -> Unit, onCancel: () -> Unit) {
    AlertDialog.Builder(context).apply {
        setTitle("Delete Goal")
        setMessage("Are you sure you want to delete this item?")
        setPositiveButton("YES") { _, _ ->
            onDelete()
        }
        setNegativeButton("CANCEL") { _, _ ->
            onCancel()
        }
        setOnCancelListener {
            onCancel()
        }
    }.show()
}

fun showDeleteSnackBar(
    context: Context,
    view: View,
    onDelete: () -> Unit,
    onCancel: () -> Unit
) {
    Snackbar.make(view, "Item deleted.", Snackbar.LENGTH_INDEFINITE).apply {
        duration = ACTION_DURATION as Int
        setAction("UNDO") {
            onCancel()
            dismiss()
        }
    }.show()
    onDelete()
}