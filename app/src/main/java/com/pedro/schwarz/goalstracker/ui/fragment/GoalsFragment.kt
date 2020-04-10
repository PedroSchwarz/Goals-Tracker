package com.pedro.schwarz.goalstracker.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.pedro.schwarz.goalstracker.databinding.GoalsFragmentBinding
import com.pedro.schwarz.goalstracker.repository.Failure
import com.pedro.schwarz.goalstracker.ui.viewmodel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class GoalsFragment : Fragment() {

    private val controller by lazy {
        findNavController()
    }

    private val authViewModel by viewModel<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkUserState()
    }

    private fun checkUserState() {
        authViewModel.checkUserState().observe(this, Observer { result ->
            when (result) {
                is Failure -> {
                    goToLogin()
                }
            }
        })
    }

    private fun goToLogin() {
        val directions = GoalsFragmentDirections.actionGlobalLoginFragment()
        controller.navigate(directions)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBinding = GoalsFragmentBinding.inflate(inflater, container, false)
        setGoToNewGoalBtn(viewBinding)
        return viewBinding.root
    }

    private fun setGoToNewGoalBtn(viewBinding: GoalsFragmentBinding) {
        viewBinding.onGoToNewGoal = View.OnClickListener {
            goToGoalForm()
        }
    }

    private fun goToGoalForm() {
        val directions =
            GoalsFragmentDirections.actionGoalsFragmentToGoalFormFragment()
        controller.navigate(directions)
    }
}
