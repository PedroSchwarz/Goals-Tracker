package com.pedro.schwarz.goalstracker.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.pedro.schwarz.goalstracker.R
import com.pedro.schwarz.goalstracker.databinding.FragmentLoginBinding
import com.pedro.schwarz.goalstracker.repository.Failure
import com.pedro.schwarz.goalstracker.repository.Resource
import com.pedro.schwarz.goalstracker.repository.Success
import com.pedro.schwarz.goalstracker.ui.databinding.UserData
import com.pedro.schwarz.goalstracker.ui.fragment.extensions.showMessage
import com.pedro.schwarz.goalstracker.ui.validator.isEmpty
import com.pedro.schwarz.goalstracker.ui.validator.isValidEmail
import com.pedro.schwarz.goalstracker.ui.validator.isValidPassword
import com.pedro.schwarz.goalstracker.ui.viewmodel.AppBar
import com.pedro.schwarz.goalstracker.ui.viewmodel.AppViewModel
import com.pedro.schwarz.goalstracker.ui.viewmodel.AuthViewModel
import com.pedro.schwarz.goalstracker.ui.viewmodel.Components
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private val controller by lazy { findNavController() }

    private val viewModel by viewModel<AuthViewModel>()

    private val appViewModel by sharedViewModel<AppViewModel>()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appViewModel.setComponents = Components(appBar = AppBar(set = true, elevation = 0f))
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
                showMessage(getString(R.string.invalid_fields))
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
            LoginFragmentDirections.actionGlobalToMain()
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
        viewBinding.authViewModel = viewModel
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
            LoginFragmentDirections.actionLoginToRegister()
        controller.navigate(directions)
    }
}
