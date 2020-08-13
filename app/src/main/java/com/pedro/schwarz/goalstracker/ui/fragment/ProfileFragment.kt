package com.pedro.schwarz.goalstracker.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.pedro.schwarz.goalstracker.databinding.FragmentProfileBinding
import com.pedro.schwarz.goalstracker.ui.databinding.UserData
import com.pedro.schwarz.goalstracker.ui.validator.isEmpty
import com.pedro.schwarz.goalstracker.ui.viewmodel.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private val controller by lazy { findNavController() }

    private val appViewModel by sharedViewModel<AppViewModel>()

    private val authViewModel by viewModel<AuthViewModel>()

    private val viewModel by viewModel<ProfileViewModel>()

    private val userData by lazy { UserData() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchUser()
        viewModel.fetchCompletedGoalsCount().observe(this, Observer { count ->
            viewModel.setCompletedCount = count
        })
        viewModel.fetchUncompletedGoalsCount().observe(this, Observer { count ->
            viewModel.setUncompletedCount = count
        })
    }

    private fun fetchUser() {
        authViewModel.getUserData().observe(this, Observer { result ->
            userData.setUser(result)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBinding = FragmentProfileBinding.inflate(inflater, container, false)
        setViewBindingData(viewBinding)
        setFullImageBtn(viewBinding)
        return viewBinding.root
    }

    private fun setFullImageBtn(viewBinding: FragmentProfileBinding) {
        viewBinding.onFullImage = View.OnClickListener {
            goToFullImage()
        }
    }

    private fun goToFullImage() {
        userData.imageUrl.value?.let { imageUrl ->
            if (!isEmpty(imageUrl)) {
                val directions =
                    ProfileFragmentDirections.actionProfileFragmentToFullImageFragment(imageUrl)
                controller.navigate(directions)
            }
        }
    }

    private fun setViewBindingData(viewBinding: FragmentProfileBinding) {
        viewBinding.lifecycleOwner = this
        viewBinding.user = userData
        viewBinding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appViewModel.setComponents = Components(appBar = AppBar(set = true, elevation = 0f), bottomNav = true)
    }
}
