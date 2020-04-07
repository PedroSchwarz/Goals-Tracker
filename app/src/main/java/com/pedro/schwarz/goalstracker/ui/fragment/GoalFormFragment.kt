package com.pedro.schwarz.goalstracker.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.pedro.schwarz.goalstracker.R
import com.pedro.schwarz.goalstracker.ui.viewmodel.GoalFormViewModel

class GoalFormFragment : Fragment() {

    companion object {
        fun newInstance() = GoalFormFragment()
    }

    private lateinit var viewModel: GoalFormViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.goal_form_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}
