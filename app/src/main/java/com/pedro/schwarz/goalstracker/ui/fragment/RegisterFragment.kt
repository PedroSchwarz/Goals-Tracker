package com.pedro.schwarz.goalstracker.ui.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.pedro.schwarz.goalstracker.R
import com.pedro.schwarz.goalstracker.databinding.FragmentRegisterBinding
import com.pedro.schwarz.goalstracker.repository.Failure
import com.pedro.schwarz.goalstracker.repository.Resource
import com.pedro.schwarz.goalstracker.repository.Success
import com.pedro.schwarz.goalstracker.ui.action.showAlertDialog
import com.pedro.schwarz.goalstracker.ui.databinding.UserData
import com.pedro.schwarz.goalstracker.ui.fragment.extensions.showMessage
import com.pedro.schwarz.goalstracker.ui.validator.isEmpty
import com.pedro.schwarz.goalstracker.ui.validator.isValidEmail
import com.pedro.schwarz.goalstracker.ui.validator.isValidPassword
import com.pedro.schwarz.goalstracker.ui.viewmodel.AppBar
import com.pedro.schwarz.goalstracker.ui.viewmodel.AppViewModel
import com.pedro.schwarz.goalstracker.ui.viewmodel.AuthViewModel
import com.pedro.schwarz.goalstracker.ui.viewmodel.Components
import com.theartofdev.edmodo.cropper.CropImage
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val REQUEST_PERMISSION = 1

class RegisterFragment : Fragment() {

    private val controller by lazy { findNavController() }

    private val viewModel by viewModel<AuthViewModel>()

    private val appViewModel by sharedViewModel<AppViewModel>()

    private val userData by lazy { UserData() }

    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPermissions()
    }

    private fun getPermissions() {
        com.pedro.schwarz.goalstracker.service.getPermissions(
            permissions,
            requireContext(),
            onFailure = {
                requestPermissions(permissions, REQUEST_PERMISSION)
            })
    }


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appViewModel.setComponents = Components(appBar = AppBar(set = true, elevation = 0f))
    }

    private fun setImageBtn(viewBinding: FragmentRegisterBinding) {
        viewBinding.onSelectImage = View.OnClickListener {
            CropImage.activity().start(requireContext(), this)
        }
    }

    private fun setRegisterBtn(viewBinding: FragmentRegisterBinding) {
        viewBinding.onRegister = View.OnClickListener {
            if (userData.imageUrl.value == null) {
                showMessage(getString(R.string.image_required_alert))
            }
            if (isFormValid()) {
                registerUser()
            } else {
                showMessage(getString(R.string.invalid_fields))
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
            RegisterFragmentDirections.actionGlobalToMain()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION) {
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    showPermissionsDialog()
                    return
                }
            }
        }
    }

    private fun showPermissionsDialog() {
        showAlertDialog(requireContext(), onClose = {
            controller.popBackStack()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                userData.imageUrl.postValue(result.uri.toString())
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                result.error?.let { error ->
                    showMessage(error.message ?: getString(R.string.generic_error_message))
                }
            }
        }
    }
}
