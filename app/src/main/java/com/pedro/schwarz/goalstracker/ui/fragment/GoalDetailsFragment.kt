package com.pedro.schwarz.goalstracker.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.pedro.schwarz.goalstracker.R
import com.pedro.schwarz.goalstracker.ui.viewmodel.GoalDetailsViewModel

class GoalDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = GoalDetailsFragment()
    }

    private lateinit var viewModel: GoalDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.goal_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}
