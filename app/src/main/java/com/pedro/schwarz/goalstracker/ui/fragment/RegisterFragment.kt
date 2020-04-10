package com.pedro.schwarz.goalstracker.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.pedro.schwarz.goalstracker.databinding.FragmentRegisterBinding
import com.pedro.schwarz.goalstracker.repository.Failure
import com.pedro.schwarz.goalstracker.repository.Resource
import com.pedro.schwarz.goalstracker.repository.Success
import com.pedro.schwarz.goalstracker.ui.databinding.UserData
import com.pedro.schwarz.goalstracker.ui.fragment.extensions.showMessage
import com.pedro.schwarz.goalstracker.ui.validator.isEmpty
import com.pedro.schwarz.goalstracker.ui.validator.isValidEmail
import com.pedro.schwarz.goalstracker.ui.validator.isValidPassword
import com.pedro.schwarz.goalstracker.ui.viewmodel.AuthViewModel
import com.theartofdev.edmodo.cropper.CropImage
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment() {

    private val controller by lazy {
        findNavController()
    }

    private val viewModel by viewModel<AuthViewModel>()

    private val userData by lazy { UserData() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBinding = FragmentRegisterBinding.inflate(inflater, container, false)
        setViewBindingData(viewBinding)
        setRegisterBtn(viewBinding)
        setImageBtn(viewBinding)
        return viewBinding.root
    }

    private fun setImageBtn(viewBinding: FragmentRegisterBinding) {
        viewBinding.onSelectImage = View.OnClickListener {
            CropImage.activity().start(requireContext(), this)
        }
    }

    private fun setRegisterBtn(viewBinding: FragmentRegisterBinding) {
        viewBinding.onRegister = View.OnClickListener {
            if (isFormValid()) {
                registerUser()
            } else {
                showMessage("Check your fields.")
            }
        }
    }

    private fun registerUser() {
        viewModel.setIsLoading = true
        userData.toUser()?.let { user ->
            viewModel.registerUser(user).observe(viewLifecycleOwner, Observer { result ->
                onCompleted(result)
            })
        }
    }

    private fun onCompleted(result: Resource<Unit>) {
        when (result) {
            is Success -> {
                goToGoals()
            }
            is Failure -> {
                showErrorMessage(result)
            }
        }
    }

    private fun goToGoals() {
        val directions =
            RegisterFragmentDirections.actionGlobalGoalsFragment()
        controller.navigate(directions)
    }

    private fun showErrorMessage(result: Resource<Unit>) {
        viewModel.setIsLoading = false
        result.error?.let { error ->
            showMessage(error)
        }
    }

    private fun setViewBindingData(viewBinding: FragmentRegisterBinding) {
        viewBinding.lifecycleOwner = this
        viewBinding.user = userData
        viewBinding.authViewModel = viewModel
    }

    private fun isFormValid(): Boolean {
        userData.name.value?.let { name ->
            if (isEmpty(name)) return false
        }
        userData.email.value?.let { email ->
            if (isEmpty(email) || !isValidEmail(email)) return false
        }
        userData.password.value?.let { password ->
            if (isEmpty(password) || !isValidPassword(password)) return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                userData.imageUrl.postValue(result.uri.toString())
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                result.error?.let { error ->
                    showMessage(error.message ?: "Something went wrong.")
                }
            }
        }
    }
}
