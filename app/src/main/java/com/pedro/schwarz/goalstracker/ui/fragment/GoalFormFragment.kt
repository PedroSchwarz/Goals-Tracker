package com.pedro.schwarz.goalstracker.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import com.pedro.schwarz.goalstracker.R
import com.pedro.schwarz.goalstracker.data.getCategories
import com.pedro.schwarz.goalstracker.data.getPriorities
import com.pedro.schwarz.goalstracker.databinding.GoalFormFragmentBinding
import com.pedro.schwarz.goalstracker.repository.Failure
import com.pedro.schwarz.goalstracker.repository.Resource
import com.pedro.schwarz.goalstracker.repository.Success
import com.pedro.schwarz.goalstracker.ui.action.pickDate
import com.pedro.schwarz.goalstracker.ui.databinding.GoalData
import com.pedro.schwarz.goalstracker.ui.extensions.setContent
import com.pedro.schwarz.goalstracker.ui.fragment.extensions.showMessage
import com.pedro.schwarz.goalstracker.ui.recyclerview.adapter.CategoryAdapter
import com.pedro.schwarz.goalstracker.ui.recyclerview.adapter.PriorityAdapter
import com.pedro.schwarz.goalstracker.ui.validator.isEmpty
import com.pedro.schwarz.goalstracker.ui.viewmodel.AppViewModel
import com.pedro.schwarz.goalstracker.ui.viewmodel.Components
import com.pedro.schwarz.goalstracker.ui.viewmodel.GoalFormViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class GoalFormFragment : Fragment() {

    private val controller by lazy { findNavController() }

    private val arguments by navArgs<GoalFormFragmentArgs>()

    private val goalId by lazy { arguments.id }

    private val goalData by lazy { GoalData() }

    private val viewModel by viewModel<GoalFormViewModel>()

    private val appViewModel by sharedViewModel<AppViewModel>()

    private lateinit var categoriesList: RecyclerView

    private lateinit var prioritiesList: RecyclerView

    private val categoryAdapter by inject<CategoryAdapter>()

    private val priorityAdapter by inject<PriorityAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        checkGoalId()
        populateLists()
    }

    private fun checkGoalId() {
        if (goalId > 0L) {
            fetchGoal()
        }
    }

    private fun fetchGoal() {
        viewModel.fetchGoal(goalId).observe(this, Observer { result ->
            goalData.setGoal(result)
        })
    }

    private fun populateLists() {
        categoryAdapter.submitList(getCategories())
        priorityAdapter.submitList(getPriorities())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBinding = GoalFormFragmentBinding.inflate(inflater, container, false)
        setViewBindingData(viewBinding)
        setDateBtn(viewBinding)
        return viewBinding.root
    }

    private fun setDateBtn(viewBinding: GoalFormFragmentBinding) {
        viewBinding.onSelectDate = View.OnClickListener {
            pickDate(requireContext(), onSelected = { date ->
                goalData.targetDate.postValue(date)
            })
        }
    }

    private fun setViewBindingData(viewBinding: GoalFormFragmentBinding) {
        viewBinding.lifecycleOwner = this
        viewBinding.goal = goalData
        viewBinding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configCategoryList(view)
        configPriorityList(view)
        setListsListeners()
        appViewModel.setComponents = Components(appBar = true)
    }

    private fun setListsListeners() {
        categoryAdapter.onItemClick = { category -> goalData.categoryId.postValue(category.id) }
        priorityAdapter.onItemClick = { priority -> goalData.priorityId.postValue(priority.id) }
    }

    private fun configPriorityList(view: View) {
        prioritiesList = view.findViewById(R.id.form_goal_priorities_list)
        prioritiesList.setContent(HORIZONTAL, priorityAdapter)
    }

    private fun configCategoryList(view: View) {
        categoriesList = view.findViewById(R.id.form_goal_categories_list)
        categoriesList.setContent(HORIZONTAL, categoryAdapter)
    }

    private fun isFormValid(): Boolean {
        goalData.title.value?.let { title ->
            if (isEmpty(title)) return false
        }
        goalData.categoryId.value?.let { id ->
            if (id == 0L) return false
        }
        goalData.priorityId.value?.let { id ->
            if (id == 0L) return false
        }
        return true
    }

    private fun saveGoal() {
        viewModel.setIsLoading = true
        goalData.toGoal()?.let { goal ->
            val description = if (goal.description.trim().isEmpty()) "No description added."
            else goal.description
            viewModel.saveGoal(goal.copy(description = description))
                .observe(viewLifecycleOwner, Observer { result ->
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
        controller.popBackStack()
    }

    private fun showErrorMessage(result: Resource<Unit>) {
        viewModel.setIsLoading = false
        result.error?.let { error ->
            showMessage(error)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.goal_form_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.goal_form_save_action -> {
                if (isFormValid()) {
                    saveGoal()
                } else {
                    showMessage("Check your fields.")
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
