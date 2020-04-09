package com.pedro.schwarz.goalstracker.ui.validator

import com.google.android.material.textfield.TextInputLayout

fun isEmpty(value: String): Boolean = value.trim().isEmpty()

fun isValidEmail(value: String): Boolean = Regex(".+@.+\\..+").matches(value)

fun isValidPassword(value: String): Boolean = value.length > 5

fun setErrorContent(
    field: TextInputLayout,
    message: String = "This field cannot be empty."
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

fun validateEmail(field: TextInputLayout, value: String) {
    if (isEmpty(value)) setErrorContent(field)
    else if (!isValidEmail(value)) setErrorContent(field, "Invalid email address.")
    else clearField(field)
}

fun validatePassword(field: TextInputLayout, value: String) {
    if (isEmpty(value)) setErrorContent(field)
    else if (!isValidPassword(value)) setErrorContent(
        field,
        "Invalid password, must be longer than 5."
    )
    else clearField(field)
}