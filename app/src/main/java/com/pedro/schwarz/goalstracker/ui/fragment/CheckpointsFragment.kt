package com.pedro.schwarz.goalstracker.ui.fragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.pedro.schwarz.goalstracker.R
import com.pedro.schwarz.goalstracker.ui.viewmodel.CheckpointsViewModel

class CheckpointsFragment : Fragment() {

    companion object {
        fun newInstance() = CheckpointsFragment()
    }

    private lateinit var viewModel: CheckpointsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.checkpoints_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}
