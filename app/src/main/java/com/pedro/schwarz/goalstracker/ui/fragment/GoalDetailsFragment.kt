package com.pedro.schwarz.goalstracker.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.PagedList
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pedro.schwarz.goalstracker.R
import com.pedro.schwarz.goalstracker.databinding.GoalDetailsFragmentBinding
import com.pedro.schwarz.goalstracker.databinding.GoalDetailsMilestoneSheetBinding
import com.pedro.schwarz.goalstracker.model.Milestone
import com.pedro.schwarz.goalstracker.repository.Failure
import com.pedro.schwarz.goalstracker.repository.Resource
import com.pedro.schwarz.goalstracker.repository.Success
import com.pedro.schwarz.goalstracker.ui.action.showDeleteDialog
import com.pedro.schwarz.goalstracker.ui.action.showDeleteSnackBar
import com.pedro.schwarz.goalstracker.ui.constants.SCROLL_DOWN
import com.pedro.schwarz.goalstracker.ui.databinding.GoalData
import com.pedro.schwarz.goalstracker.ui.databinding.MilestoneData
import com.pedro.schwarz.goalstracker.ui.extensions.setContent
import com.pedro.schwarz.goalstracker.ui.fragment.extensions.showMessage
import com.pedro.schwarz.goalstracker.ui.recyclerview.adapter.MilestoneAdapter
import com.pedro.schwarz.goalstracker.ui.recyclerview.callback.ItemCallback
import com.pedro.schwarz.goalstracker.ui.recyclerview.listener.ScrollListener
import com.pedro.schwarz.goalstracker.ui.validator.isEmpty
import com.pedro.schwarz.goalstracker.ui.viewmodel.GoalDetailsViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class GoalDetailsFragment : Fragment() {

    private val controller by lazy { findNavController() }

    private val arguments by navArgs<GoalDetailsFragmentArgs>()

    private val goalId by lazy { arguments.id }

    private val goalData by lazy { GoalData() }

    private val milestoneData by lazy { MilestoneData(milestone = Milestone(goalId = goalId)) }

    private val viewModel by viewModel<GoalDetailsViewModel>()

    private lateinit var milestonesList: RecyclerView

    private val milestoneAdapter by inject<MilestoneAdapter>()

    private lateinit var milestoneSheet: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchGoal()
        fetchMilestones()
        configMilestoneItemClick()
        initOptionBtns()
    }

    private fun initOptionBtns() {
        viewModel.setHide = true
    }

    private fun configMilestoneItemClick() {
        milestoneAdapter.onItemClick = { milestone, toggle ->
            if (!toggle) {
                populateSheet(milestone)
            } else {
                toggleMilestone(milestone)
            }
        }
    }

    private fun toggleMilestone(milestone: Milestone) {
        goalData.toGoal()?.let { goal ->
            viewModel.saveMilestone(
                milestone.copy(completed = !milestone.completed),
                goal,
                toggle = true
            )
        }
    }

    private fun populateSheet(milestone: Milestone) {
        milestoneData.setMilestone(milestone)
        milestoneSheet.show()
    }

    private fun fetchMilestones() {
        viewModel.fetchMilestones(goalId).observe(this, Observer { result: PagedList<Milestone> ->
            milestoneAdapter.submitList(result)
            viewModel.setIsEmpty = result.isEmpty()
        })
    }

    private fun fetchGoal() {
        viewModel.fetchGoal(goalId).observe(this, Observer { result ->
            goalData.setGoal(result)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBinding = GoalDetailsFragmentBinding.inflate(inflater, container, false)
        setViewBindingData(viewBinding)
        setEditGoalBtn(viewBinding)
        setShowMilestoneSheetBtn(viewBinding)
        setGoToCheckpointsBtn(viewBinding)
        setToggleMenuBtn(viewBinding)

        val bottomSheetBinding =
            GoalDetailsMilestoneSheetBinding.inflate(inflater, container, false)
        configBottomSheet(bottomSheetBinding)
        setMilestoneBindingData(bottomSheetBinding)
        setSaveMilestoneBtn(bottomSheetBinding)
        return viewBinding.root
    }

    private fun setSaveMilestoneBtn(bottomSheetBinding: GoalDetailsMilestoneSheetBinding) {
        bottomSheetBinding.onSaveMilestone = View.OnClickListener {
            if (isFormValid()) {
                saveMilestone()
            } else {
                showMessage("Check your field.")
            }
        }
    }

    private fun saveMilestone() {
        milestoneData.toMilestone()?.let { milestone ->
            goalData.toGoal()?.let { goal ->
                viewModel.saveMilestone(milestone, goal)
                    .observe(viewLifecycleOwner, Observer { result ->
                        onComplete(result)
                    })
            }
        }
    }

    private fun onComplete(result: Resource<Unit>) {
        when (result) {
            is Success -> {
                viewModel.setHide = true
                milestoneSheet.dismiss()
            }
            is Failure -> {
                result.error?.let { error ->
                    showMessage(error)
                }
            }
        }
    }

    private fun setMilestoneBindingData(
        bottomSheetBinding: GoalDetailsMilestoneSheetBinding
    ): GoalDetailsMilestoneSheetBinding {
        bottomSheetBinding.lifecycleOwner = this
        bottomSheetBinding.milestone = milestoneData
        return bottomSheetBinding
    }

    private fun configBottomSheet(bottomSheetBinding: GoalDetailsMilestoneSheetBinding) {
        milestoneSheet = BottomSheetDialog(requireContext()).apply {
            setContentView(bottomSheetBinding.root)
            setOnDismissListener {
                milestoneData.setMilestone(Milestone(goalId = goalId))
            }
        }
    }

    private fun setToggleMenuBtn(viewBinding: GoalDetailsFragmentBinding) {
        viewBinding.onToggleMenu = View.OnClickListener {
            viewModel.setHide = !viewModel.setHide
        }
    }

    private fun setGoToCheckpointsBtn(viewBinding: GoalDetailsFragmentBinding) {
        viewBinding.onGoToCheckpoints = View.OnClickListener {
            goToCheckpoints()
        }
    }

    private fun goToCheckpoints() {
        val directions =
            GoalDetailsFragmentDirections.actionGoalDetailsFragmentToCheckpointsFragment(goalId)
        controller.navigate(directions)
        viewModel.setHide = true
    }

    private fun setShowMilestoneSheetBtn(viewBinding: GoalDetailsFragmentBinding) {
        viewBinding.onShowMilestoneSheet = View.OnClickListener {
            milestoneSheet.show()
        }
    }

    private fun setEditGoalBtn(viewBinding: GoalDetailsFragmentBinding) {
        viewBinding.onEditGoal = View.OnClickListener {
            goToEditGoal()
        }
    }

    private fun goToEditGoal() {
        val directions =
            GoalDetailsFragmentDirections.actionGoalDetailsFragmentToGoalFormFragment(goalId)
        controller.navigate(directions)
        viewModel.setHide = true
    }

    private fun setViewBindingData(viewBinding: GoalDetailsFragmentBinding) {
        viewBinding.lifecycleOwner = this
        viewBinding.goal = goalData
        viewBinding.viewModel = viewModel
    }

    private fun isFormValid(): Boolean {
        milestoneData.title.value?.let { title ->
            if (isEmpty(title)) return false
        }
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configMilestonesList(view)
    }

    private fun configMilestonesList(view: View) {
        milestonesList = view.findViewById(R.id.goal_details_milestones_list)
        milestonesList.setContent(VERTICAL, milestoneAdapter)
        configScrollListener()
        configSwipeCallback()
    }

    private fun configSwipeCallback() {
        val itemCallback = ItemCallback()
        itemCallback.onSwipeItem = { position ->
            val milestone = milestoneAdapter.getItemAtPosition(position)
            milestone?.let {
                showDeleteDialog(requireContext(), onDelete = {
                    showDeleteAction(milestone)
                }, onCancel = { milestoneAdapter.notifyDataSetChanged() })
            }
        }
        ItemTouchHelper(itemCallback).attachToRecyclerView(milestonesList)
    }

    private fun showDeleteAction(milestone: Milestone) {
        view?.let {
            showDeleteSnackBar(requireContext(), it, onDelete = {
                deleteMilestone(milestone)
            }, onCancel = {
                saveMilestone(milestone)
            })
        }
    }

    private fun saveMilestone(milestone: Milestone) {
        goalData.toGoal()?.let { goal ->
            viewModel.saveMilestone(milestone, goal, deletion = true)
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
    }

    private fun deleteMilestone(milestone: Milestone) {
        goalData.toGoal()?.let { goal ->
            viewModel.deleteMilestone(milestone, goal)
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
    }

    private fun configScrollListener() {
        milestonesList.addOnScrollListener(ScrollListener(onScroll = { scrollDirection ->
            when (scrollDirection) {
                SCROLL_DOWN -> {
                    viewModel.setHide = true
                    viewModel.setScrollDown = true
                }
                else -> {
                    viewModel.setScrollDown = false
                }
            }
        }))
    }
}
