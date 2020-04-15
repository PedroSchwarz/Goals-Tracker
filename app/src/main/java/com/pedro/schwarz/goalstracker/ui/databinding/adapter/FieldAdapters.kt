package com.pedro.schwarz.goalstracker.ui.databinding.adapter

import android.text.InputType.*
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.pedro.schwarz.goalstracker.ui.validator.validateEmail
import com.pedro.schwarz.goalstracker.ui.validator.validatePassword
import com.pedro.schwarz.goalstracker.ui.validator.validateSimpleText

const val SIMPLE_FIELD = TYPE_TEXT_FLAG_CAP_WORDS.or(TYPE_CLASS_TEXT)
const val SENTENCES_FIELD = TYPE_TEXT_FLAG_CAP_SENTENCES.or(TYPE_CLASS_TEXT)
const val MULTI_FIELD =
    TYPE_TEXT_FLAG_CAP_SENTENCES.or(TYPE_TEXT_FLAG_MULTI_LINE).or(TYPE_CLASS_TEXT)
const val EMAIL_FIELD = TYPE_TEXT_VARIATION_EMAIL_ADDRESS.or(TYPE_CLASS_TEXT)
const val PASSWORD_FIELD = TYPE_TEXT_VARIATION_PASSWORD.or(TYPE_CLASS_TEXT)

@BindingAdapter("validateField")
fun TextInputLayout.validateField(value: String) {
    editText?.let { editText ->
        editText.addTextChangedListener {
            when (editText.inputType) {
                SIMPLE_FIELD -> {
                    validateSimpleText(this, editText.text.toString())
                }
                SENTENCES_FIELD -> {
                    validateSimpleText(this, editText.text.toString())
                }
                MULTI_FIELD -> {
                    validateSimpleText(this, editText.text.toString())
                }
                EMAIL_FIELD -> {
                    validateEmail(this, editText.text.toString())
                }
                PASSWORD_FIELD -> {
                    validatePassword(this, editText.text.toString())
                }
            }
        }
    }
}

