package com.pedro.schwarz.goalstracker.ui.validator

import com.google.android.material.textfield.TextInputLayout
import com.pedro.schwarz.goalstracker.R

fun isEmpty(value: String): Boolean = value.trim().isEmpty()

fun isValidEmail(value: String): Boolean = Regex(".+@.+\\..+").matches(value)

fun isValidPassword(value: String): Boolean = value.length > 5

fun setErrorContent(
    field: TextInputLayout,
    message: String = field.context.getString(R.string.field_empty)
) {
    field.apply {
        isErrorEnabled = true
        error = message
    }
}

fun clearField(field: TextInputLayout) {
    field.apply {
        isErrorEnabled = false
        error = null
    }
}

fun validateSimpleText(field: TextInputLayout, value: String) {
    if (isEmpty(value)) setErrorContent(field)
    else clearField(field)
}

fun validateEmail(field: TextInputLayout, value: String) {
    if (isEmpty(value)) setErrorContent(field)
    else if (!isValidEmail(value)) setErrorContent(
        field,
        field.context.getString(R.string.field_invalid_email)
    )
    else clearField(field)
}

fun validatePassword(field: TextInputLayout, value: String) {
    if (isEmpty(value)) setErrorContent(field)
    else if (!isValidPassword(value)) setErrorContent(
        field,
        field.context.getString(R.string.field_invalid_password)
    )
    else clearField(field)
}