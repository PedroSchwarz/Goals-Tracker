package com.pedro.schwarz.goalstracker.ui.databinding

import android.text.InputType
import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.pedro.schwarz.goalstracker.ui.validator.validateEmail
import com.pedro.schwarz.goalstracker.ui.validator.validatePassword

const val EMAIL_FIELD = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS.or(InputType.TYPE_CLASS_TEXT)
const val PASSWORD_FIELD = InputType.TYPE_TEXT_VARIATION_PASSWORD.or(InputType.TYPE_CLASS_TEXT)

@BindingAdapter("validateField")
fun TextInputLayout.validateField(value: String) {
    editText?.let {
        it.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                when (it.inputType) {
                    EMAIL_FIELD -> {
                        validateEmail(this, value)
                    }
                    PASSWORD_FIELD -> {
                        validatePassword(this, value)
                    }
                }
            }
        }
    }
}

