package com.pedro.schwarz.goalstracker.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.pedro.schwarz.goalstracker.databinding.FragmentProfileBinding
import com.pedro.schwarz.goalstracker.ui.databinding.UserData
import com.pedro.schwarz.goalstracker.ui.viewmodel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private val authViewModel by viewModel<AuthViewModel>()

    private val userData by lazy { UserData() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchUser()
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
        return viewBinding.root
    }

    private fun setViewBindingData(viewBinding: FragmentProfileBinding) {
        viewBinding.lifecycleOwner = this
        viewBinding.user = userData
    }
}
