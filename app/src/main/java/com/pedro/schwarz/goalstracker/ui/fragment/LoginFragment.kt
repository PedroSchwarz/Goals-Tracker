package com.pedro.schwarz.goalstracker.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.pedro.schwarz.goalstracker.databinding.FragmentLoginBinding
import com.pedro.schwarz.goalstracker.repository.Failure
import com.pedro.schwarz.goalstracker.repository.Resource
import com.pedro.schwarz.goalstracker.repository.Success
import com.pedro.schwarz.goalstracker.ui.databinding.UserData
import com.pedro.schwarz.goalstracker.ui.fragment.extensions.showMessage
import com.pedro.schwarz.goalstracker.ui.validator.isEmpty
import com.pedro.schwarz.goalstracker.ui.validator.isValidEmail
import com.pedro.schwarz.goalstracker.ui.validator.isValidPassword
import com.pedro.schwarz.goalstracker.ui.viewmodel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private val controller by lazy {
        findNavController()
    }

    private val viewModel by viewModel<AuthViewModel>()

    private val userData by lazy { UserData() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkUserState()
    }

    private fun checkUserState() {
        viewModel.checkUserState().observe(this, Observer { result ->
            when (result) {
                is Success -> goToGoal()
            }
        })
    }

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
                signInUser()
            } else {
                showMessage("Check your fields.")
            }
        }
    }

    private fun signInUser() {
        viewModel.setIsLoading = true
        userData.toUser()?.let { user ->
            viewModel.signInUser(user.email, user.password)
                .observe(viewLifecycleOwner, Observer { result ->
                    onCompleted(result)
                })
        }
    }

    private fun onCompleted(result: Resource<Unit>?) {
        when (result) {
            is Success -> {
                goToGoal()
            }
            is Failure -> {
                showErrorMessage(result)
            }
        }
    }

    private fun goToGoal() {
        val directions =
            LoginFragmentDirections.actionGlobalGoalsFragment()
        controller.navigate(directions)
    }

    private fun showErrorMessage(result: Resource<Unit>) {
        viewModel.setIsLoading = false
        result.error?.let { error ->
            showMessage(error)
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
