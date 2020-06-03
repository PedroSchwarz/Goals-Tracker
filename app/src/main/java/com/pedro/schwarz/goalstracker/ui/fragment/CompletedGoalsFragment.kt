package com.pedro.schwarz.goalstracker.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.pedro.schwarz.goalstracker.R
import com.pedro.schwarz.goalstracker.databinding.FragmentCompletedGoalsBinding
import com.pedro.schwarz.goalstracker.model.Goal
import com.pedro.schwarz.goalstracker.repository.Failure
import com.pedro.schwarz.goalstracker.ui.action.showDeleteDialog
import com.pedro.schwarz.goalstracker.ui.action.showDeleteSnackBar
import com.pedro.schwarz.goalstracker.ui.extensions.setContent
import com.pedro.schwarz.goalstracker.ui.fragment.extensions.showMessage
import com.pedro.schwarz.goalstracker.ui.recyclerview.adapter.GoalAdapter
import com.pedro.schwarz.goalstracker.ui.recyclerview.callback.ItemCallback
import com.pedro.schwarz.goalstracker.ui.viewmodel.AppViewModel
import com.pedro.schwarz.goalstracker.ui.viewmodel.Components
import com.pedro.schwarz.goalstracker.ui.viewmodel.GoalsViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CompletedGoalsFragment : Fragment() {

    private val controller by lazy { findNavController() }

    private val viewModel by viewModel<GoalsViewModel>()

    private val appViewModel by sharedViewModel<AppViewModel>()

    private val goalAdapter by inject<GoalAdapter>()

    private lateinit var completedGoalsList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchGoals()
        configGoalItemClick()
        appViewModel.setComponents = Components(appBar = true, bottomNav = true)
    }

    private fun configGoalItemClick() {
        goalAdapter.onItemClick = { goal ->
            goToDetails(goal.id, goal.title)
        }
    }

    private fun goToDetails(id: Long, title: String) {
        val directions =
            CompletedGoalsFragmentDirections.actionCompletedGoalsFragmentToGoalDetailsFragment(
                id,
                title
            )
        controller.navigate(directions)
    }

    private fun fetchGoals() {
        viewModel.fetchGoals(completed = true).observe(this, Observer { result ->
            goalAdapter.submitList(result)
            viewModel.setIsEmpty = result.isEmpty()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBinding = FragmentCompletedGoalsBinding.inflate(inflater, container, false)
        viewBinding.lifecycleOwner = this
        viewBinding.viewModel = viewModel
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configGoalsList(view)
        appViewModel.setComponents = Components(appBar = true, bottomNav = true)
    }

    private fun configGoalsList(view: View) {
        completedGoalsList = view.findViewById(R.id.completed_goals_list)
        completedGoalsList.setContent(VERTICAL, goalAdapter)
        configSwipeCallback()
    }

    private fun configSwipeCallback() {
        val itemCallback = ItemCallback()
        itemCallback.onSwipeItem = { position ->
            val goal = goalAdapter.getItemAtPosition(position)
            goal?.let {
                showDeleteDialog(requireContext(), onDelete = {
                    showDeleteAction(goal)
                }, onCancel = { goalAdapter.notifyDataSetChanged() })
            }
        }
        ItemTouchHelper(itemCallback).attachToRecyclerView(completedGoalsList)
    }

    private fun showDeleteAction(goal: Goal) {
        view?.let {
            showDeleteSnackBar(requireContext(), it, onDelete = {
                deleteGoal(goal)
            }, undo = false)
        }
    }


    private fun deleteGoal(goal: Goal) {
        viewModel.deleteGoal(goal).observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Failure -> {
                    result.error?.let { error ->
                        showMessage(error)
                    }
                }
            }
        })
    }
}
