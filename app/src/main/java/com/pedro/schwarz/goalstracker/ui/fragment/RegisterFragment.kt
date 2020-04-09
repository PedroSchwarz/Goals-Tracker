package com.pedro.schwarz.goalstracker.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pedro.schwarz.goalstracker.databinding.FragmentRegisterBinding
import com.pedro.schwarz.goalstracker.ui.fragment.extensions.showMessage
import com.theartofdev.edmodo.cropper.CropImage

class RegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewBinding = FragmentRegisterBinding.inflate(inflater, container, false)
        viewBinding.onRegister = View.OnClickListener {
            showMessage("On Register clicked.")
        }
        viewBinding.onSelectImage = View.OnClickListener {
            CropImage.activity().start(requireContext(), this)
        }
        return viewBinding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                result.error?.let { error ->
                    showMessage(error.message ?: "Something went wrong.")
                }
            }
        }
    }
}
