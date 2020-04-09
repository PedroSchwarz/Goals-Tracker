package com.pedro.schwarz.goalstracker.ui.validator

import com.google.android.material.textfield.TextInputLayout

private const val EMPTY_FIELD_ERROR_MESSAGE = "This field cannot be empty."
private const val INVALID_EMAIL_ERROR_MESSAGE = "Invalid email address."
private const val INVALID_PASSWORD_ERROR_MESSAGE = "Invalid password, must be longer than 5."

fun isEmpty(value: String): Boolean = value.trim().isEmpty()

fun isValidEmail(value: String): Boolean = Regex(".+@.+\\..+").matches(value)

fun isValidPassword(value: String): Boolean = value.length > 5

fun setErrorContent(
    field: TextInputLayout,
    message: String = EMPTY_FIELD_ERROR_MESSAGE
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
    else if (!isValidEmail(value)) setErrorContent(field, INVALID_EMAIL_ERROR_MESSAGE)
    else clearField(field)
}

fun validatePassword(field: TextInputLayout, value: String) {
    if (isEmpty(value)) setErrorContent(field)
    else if (!isValidPassword(value)) setErrorContent(
        field,
        INVALID_PASSWORD_ERROR_MESSAGE
    )
    else clearField(field)
}