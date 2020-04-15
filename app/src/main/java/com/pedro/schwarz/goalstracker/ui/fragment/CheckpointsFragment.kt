package com.pedro.schwarz.goalstracker.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import androidx.recyclerview.widget.RecyclerView
import com.pedro.schwarz.goalstracker.R
import com.pedro.schwarz.goalstracker.databinding.CheckpointsFragmentBinding
import com.pedro.schwarz.goalstracker.ui.extensions.setContent
import com.pedro.schwarz.goalstracker.ui.fragment.extensions.showMessage
import com.pedro.schwarz.goalstracker.ui.recyclerview.adapter.CheckpointAdapter
import com.pedro.schwarz.goalstracker.ui.viewmodel.CheckpointsViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckpointsFragment : Fragment() {

    private val controller by lazy { findNavController() }

    private val arguments by navArgs<CheckpointsFragmentArgs>()

    private val goalId by lazy { arguments.id }

    private val viewModel by viewModel<CheckpointsViewModel>()

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
            CheckpointsFragmentDirections.actionCheckpointsFragmentToCheckpointDetailsFragment(
                id,
                goalId
            )
        controller.navigate(directions)
    }

    private fun fetchCheckpoints() {
        viewModel.fetchCheckpoints(goalId).observe(this, Observer { result ->
            checkpointAdapter.submitList(result)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBinding = CheckpointsFragmentBinding.inflate(inflater, container, false)
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
            CheckpointsFragmentDirections.actionCheckpointsFragmentToCheckpointFormFragment(
                goalId
            )
        controller.navigate(directions)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configCheckpointsList(view)
    }

    private fun configCheckpointsList(view: View) {
        checkpointsList = view.findViewById(R.id.checkpoints_list)
        checkpointsList.setContent(VERTICAL, checkpointAdapter)
    }
}
