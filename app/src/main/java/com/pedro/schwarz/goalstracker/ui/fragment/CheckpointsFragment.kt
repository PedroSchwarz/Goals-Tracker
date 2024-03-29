package com.pedro.schwarz.goalstracker.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.pedro.schwarz.goalstracker.R
import com.pedro.schwarz.goalstracker.databinding.CheckpointsFragmentBinding
import com.pedro.schwarz.goalstracker.model.Checkpoint
import com.pedro.schwarz.goalstracker.repository.Failure
import com.pedro.schwarz.goalstracker.ui.action.showDeleteDialog
import com.pedro.schwarz.goalstracker.ui.action.showDeleteSnackBar
import com.pedro.schwarz.goalstracker.ui.extensions.setContent
import com.pedro.schwarz.goalstracker.ui.fragment.extensions.showMessage
import com.pedro.schwarz.goalstracker.ui.recyclerview.adapter.CheckpointAdapter
import com.pedro.schwarz.goalstracker.ui.recyclerview.callback.ItemCallback
import com.pedro.schwarz.goalstracker.ui.viewmodel.AppBar
import com.pedro.schwarz.goalstracker.ui.viewmodel.AppViewModel
import com.pedro.schwarz.goalstracker.ui.viewmodel.CheckpointsViewModel
import com.pedro.schwarz.goalstracker.ui.viewmodel.Components
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckpointsFragment : Fragment() {

    private val controller by lazy { findNavController() }

    private val arguments by navArgs<CheckpointsFragmentArgs>()

    private val goalId by lazy { arguments.id }

    private val viewModel by viewModel<CheckpointsViewModel>()

    private val appViewModel by sharedViewModel<AppViewModel>()

    private val checkpointAdapter by inject<CheckpointAdapter>()

    private lateinit var checkpointsList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchCheckpoints()
        configCheckpointItemClick()
    }

    private fun configCheckpointItemClick() {
        checkpointAdapter.onItemClick = { checkpoint ->
            goToDetails(checkpoint.id)
        }
    }

    private fun goToDetails(id: Long) {
        val directions =
            CheckpointsFragmentDirections.actionCheckpointsToCheckpointDetails(
                id,
                goalId
            )
        controller.navigate(directions)
    }

    private fun fetchCheckpoints() {
        viewModel.fetchCheckpoints(goalId).observe(this, Observer { result ->
            checkpointAdapter.submitList(result)
            viewModel.setIsEmpty = result.isEmpty()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBinding = CheckpointsFragmentBinding.inflate(inflater, container, false)
        viewBinding.lifecycleOwner = this
        viewBinding.viewModel = viewModel
        setOnGoToNewCheckpointBtn(viewBinding)
        return viewBinding.root
    }

    private fun setOnGoToNewCheckpointBtn(viewBinding: CheckpointsFragmentBinding) {
        viewBinding.onGoToNewCheckpoint = View.OnClickListener {
            goToNewCheckpoint()
        }
    }

    private fun goToNewCheckpoint() {
        val directions =
            CheckpointsFragmentDirections.actionCheckpointsToCheckpointForm(
                goalId
            )
        controller.navigate(directions)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configCheckpointsList(view)
        appViewModel.setComponents = Components(appBar = AppBar(set = true, elevation = 0f))
    }

    private fun configCheckpointsList(view: View) {
        checkpointsList = view.findViewById(R.id.checkpoints_list)
        checkpointsList.setContent(VERTICAL, checkpointAdapter)
        configSwipeCallback()
    }

    private fun configSwipeCallback() {
        val itemCallback = ItemCallback()
        itemCallback.onSwipeItem = { position ->
            val checkpoint = checkpointAdapter.getItemAtPosition(position)
            checkpoint?.let {
                showDeleteDialog(requireContext(), onDelete = {
                    showDeleteAction(checkpoint)
                }, onCancel = { checkpointAdapter.notifyDataSetChanged() })
            }
        }
        ItemTouchHelper(itemCallback).attachToRecyclerView(checkpointsList)
    }

    private fun showDeleteAction(checkpoint: Checkpoint) {
        view?.let {
            showDeleteSnackBar(requireContext(), it, onDelete = {
                deleteCheckpoint(checkpoint)
            }, onCancel = {
                saveCheckpoint(checkpoint)
            })
        }
    }

    private fun saveCheckpoint(checkpoint: Checkpoint) {

        viewModel.saveCheckpoint(checkpoint)
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

    private fun deleteCheckpoint(checkpoint: Checkpoint) {
        viewModel.deleteCheckpoint(checkpoint)
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
