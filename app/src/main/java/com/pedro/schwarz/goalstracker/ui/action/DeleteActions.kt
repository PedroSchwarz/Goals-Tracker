package com.pedro.schwarz.goalstracker.ui.action

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.pedro.schwarz.goalstracker.R

fun showDeleteDialog(context: Context, onDelete: () -> Unit, onCancel: () -> Unit) {
    AlertDialog.Builder(context).apply {
        setTitle(context.getString(R.string.dialog_title))
        setMessage(context.getString(R.string.dialog_message))
        setPositiveButton(context.getString(R.string.dialog_positive)) { _, _ ->
            onDelete()
        }
        setNegativeButton(context.getString(R.string.dialog_negative)) { _, _ ->
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
    onCancel: () -> Unit = {},
    undo: Boolean = true
) {
    Snackbar.make(view, context.getString(R.string.snack_title), Snackbar.LENGTH_INDEFINITE).apply {
        duration = BaseTransientBottomBar.LENGTH_LONG
        if (undo) {
            setAction(context.getString(R.string.snack_action)) {
                onCancel()
                dismiss()
            }
        }
    }.show()
    onDelete()
}