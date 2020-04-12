package com.pedro.schwarz.goalstracker.ui.action

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import java.util.*

fun pickDate(context: Context, onSelected: (date: Long) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val now = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val date = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }.timeInMillis
                onSelected(date)
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}