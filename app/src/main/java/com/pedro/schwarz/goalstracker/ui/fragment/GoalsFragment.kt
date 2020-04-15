package com.pedro.schwarz.goalstracker.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.pedro.schwarz.goalstracker.R
import com.pedro.schwarz.goalstracker.databinding.GoalsFragmentBinding
import com.pedro.schwarz.goalstracker.model.Goal
import com.pedro.schwarz.goalstracker.repository.Failure
import com.pedro.schwarz.goalstracker.repository.Success
import com.pedro.schwarz.goalstracker.ui.action.showDeleteDialog
import com.pedro.schwarz.goalstracker.ui.action.showDeleteSnackBar
import com.pedro.schwarz.goalstracker.ui.extensions.setContent
import com.pedro.schwarz.goalstracker.ui.fragment.extensions.showMessage
import com.pedro.schwarz.goalstracker.ui.recyclerview.adapter.GoalAdapter
import com.pedro.schwarz.goalstracker.ui.recyclerview.callback.ItemCallback
import com.pedro.schwarz.goalstracker.ui.viewmodel.AppViewModel
import com.pedro.schwarz.goalstracker.ui.viewmodel.AuthViewModel
import com.pedro.schwarz.goalstracker.ui.viewmodel.Components
import com.pedro.schwarz.goalstracker.ui.viewmodel.GoalsViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class GoalsFragment : Fragment() {

    private val controller by lazy { findNavController() }

    private val authViewModel by viewModel<AuthViewModel>()

    private val viewModel by viewModel<GoalsViewModel>()

    private val appViewModel by sharedViewModel<AppViewModel>()

    private lateinit var goalsListRefresh: SwipeRefreshLayout

    private lateinit var goalsList: RecyclerView

    private val goalAdapter by inject<GoalAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        checkUserState()
        fetchGoals()
        configGoalItemClick()
    }

    private fun configGoalItemClick() {
        goalAdapter.onItemClick = { goal ->
            goToDetails(goal.id, goal.title)
        }
    }

    private fun goToDetails(id: Long, title: String) {
        val directions =
            GoalsFragmentDirections.actionGoalsFragmentToGoalDetailsFragment(id, title)
        controller.navigate(directions)
    }

    private fun fetchGoals() {
        viewModel.fetchGoals().observe(this, Observer { result: PagedList<Goal> ->
            goalAdapter.submitList(result)
            viewModel.setIsEmpty = result.isEmpty()
        })
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
        viewBinding.lifecycleOwner = this
        viewBinding.viewModel = viewModel
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configGoalsList(view)
        appViewModel.setComponents = Components(appBar = true, bottomNav = true)
    }

    private fun configGoalsList(view: View) {
        goalsListRefresh = view.findViewById(R.id.goals_list_refresh)
        configRefreshListener()
        goalsList = view.findViewById(R.id.goals_list)
        goalsList.setContent(VERTICAL, goalAdapter)
        configSwipeCallback()
    }

    private fun configRefreshListener() {
        goalsListRefresh.setOnRefreshListener {
            viewModel.setIsRefreshing = true
            fetchGoalsNetwork()
        }
    }

    private fun fetchGoalsNetwork() {
        viewModel.fetchGoalsNetwork().observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Success -> {
                    viewModel.fetchGoalsNetworkChildren()
                }
                is Failure -> {
                    result.error?.let { error -> showMessage(error) }
                }
            }
            viewModel.setIsRefreshing = false
        })
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
        ItemTouchHelper(itemCallback).attachToRecyclerView(goalsList)
    }

    private fun showDeleteAction(goal: Goal) {
        view?.let {
            showDeleteSnackBar(requireContext(), it, onDelete = {
                deleteGoal(goal)
            }, onCancel = {
                saveGoal(goal)
            })
        }
    }

    private fun saveGoal(goal: Goal) {
        viewModel.saveGoal(goal.copy(milestones = 0, completedMilestones = 0))
            .observe(viewLifecycleOwner, Observer { result ->
                when (result) {
                    is Failure -> {
                        result.error?.let { error ->
                            showMessage(error)
                        }
                    }
                }
            })
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

    private fun signOutUser() {
        authViewModel.signOutUser()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.goals_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.goals_sign_out_action -> {
                signOutUser()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

}
