package com.pedro.schwarz.goalstracker.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pedro.schwarz.goalstracker.databinding.FragmentLoginBinding
import com.pedro.schwarz.goalstracker.ui.databinding.UserData
import com.pedro.schwarz.goalstracker.ui.fragment.extensions.showMessage
import com.pedro.schwarz.goalstracker.ui.validator.isEmpty
import com.pedro.schwarz.goalstracker.ui.validator.isValidEmail
import com.pedro.schwarz.goalstracker.ui.validator.isValidPassword

class LoginFragment : Fragment() {

    private val controller by lazy {
        findNavController()
    }

    private val userData by lazy { UserData() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBinding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        setViewBindingData(viewBinding)
        setEnterBtn(viewBinding)
        setGoToRegisterBtn(viewBinding)
        return viewBinding.root
    }

    private fun setGoToRegisterBtn(viewBinding: FragmentLoginBinding) {
        viewBinding.onGoToRegister = View.OnClickListener {
            goToRegister()
        }
    }

    private fun setEnterBtn(viewBinding: FragmentLoginBinding) {
        viewBinding.onEnter = View.OnClickListener {
            if (isFormValid()) {
                showMessage("Success.")
            } else {
                showMessage("Check your fields.")
            }
        }
    }

    private fun setViewBindingData(viewBinding: FragmentLoginBinding) {
        viewBinding.lifecycleOwner = this
        viewBinding.user = userData
    }

    private fun isFormValid(): Boolean {
        userData.email.value?.let { email ->
            if (isEmpty(email) || !isValidEmail(email)) return false
        }
        userData.password.value?.let { password ->
            if (isEmpty(password) || !isValidPassword(password)) return false
        }
        return true
    }

    private fun goToRegister() {
        val directions =
            LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        controller.navigate(directions)
    }
}
