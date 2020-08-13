package com.pedro.schwarz.goalstracker.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.pedro.schwarz.goalstracker.R
import com.pedro.schwarz.goalstracker.repository.Failure
import com.pedro.schwarz.goalstracker.repository.Success
import com.pedro.schwarz.goalstracker.ui.viewmodel.AppViewModel
import com.pedro.schwarz.goalstracker.ui.viewmodel.AuthViewModel
import com.pedro.schwarz.goalstracker.ui.viewmodel.Components
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashFragment : Fragment() {

    private val controller by lazy { findNavController() }

    private val authViewModel by viewModel<AuthViewModel>()

    private val appViewModel by sharedViewModel<AppViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appViewModel.setComponents = Components()
        CoroutineScope(Dispatchers.IO).launch {
            delay(1500)
            withContext(Dispatchers.Main) {
                checkUserState()
            }
        }
    }

    private fun checkUserState() {
        authViewModel.checkUserState().observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Success -> {
                    goToGoal()
                }
                is Failure -> {
                    goToLogin()
                }
            }
        })
    }

    private fun goToGoal() {
        controller.popBackStack()
        val directions =
            SplashFragmentDirections.actionGlobalToMain()
        controller.navigate(directions)
    }

    private fun goToLogin() {
        controller.popBackStack()
        val directions = SplashFragmentDirections.actionGlobalToAuth()
        controller.navigate(directions)
    }
}
