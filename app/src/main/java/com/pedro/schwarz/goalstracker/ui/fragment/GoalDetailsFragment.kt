package com.pedro.schwarz.goalstracker.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pedro.schwarz.goalstracker.databinding.GoalDetailsFragmentBinding
import com.pedro.schwarz.goalstracker.databinding.GoalDetailsMilestoneSheetBinding
import com.pedro.schwarz.goalstracker.ui.databinding.GoalData
import com.pedro.schwarz.goalstracker.ui.fragment.extensions.showMessage
import com.pedro.schwarz.goalstracker.ui.viewmodel.GoalDetailsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class GoalDetailsFragment : Fragment() {

    private val controller by lazy { findNavController() }

    private val arguments by navArgs<GoalDetailsFragmentArgs>()

    private val goalId by lazy { arguments.id }

    private val goalData by lazy { GoalData() }

    private val viewModel by viewModel<GoalDetailsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchGoal(goalId).observe(this, Observer { result ->
            goalData.setGoal(result)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBinding = GoalDetailsFragmentBinding.inflate(inflater, container, false)
        val bottomSheetBinding = GoalDetailsMilestoneSheetBinding.inflate(inflater, container, false)
        BottomSheetDialog(requireContext()).apply {
            setContentView(bottomSheetBinding.root)
            setOnDismissListener {
                showMessage("Teste")
            }
        }
            .show()
        viewBinding.lifecycleOwner = this
        viewBinding.goal = goalData
        viewBinding.onEditGoal = View.OnClickListener {
            val directions =
                GoalDetailsFragmentDirections.actionGoalDetailsFragmentToGoalFormFragment(goalId)
            controller.navigate(directions)
        }
        return viewBinding.root
    }

}
