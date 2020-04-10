package com.pedro.schwarz.goalstracker.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pedro.schwarz.goalstracker.R
import com.pedro.schwarz.goalstracker.databinding.GoalFormFragmentBinding
import com.pedro.schwarz.goalstracker.ui.databinding.GoalData
import com.pedro.schwarz.goalstracker.ui.fragment.extensions.showMessage

class GoalFormFragment : Fragment() {

    private val controller by lazy {
        findNavController()
    }

    private val goalData by lazy { GoalData() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBinding = GoalFormFragmentBinding.inflate(inflater, container, false)
        viewBinding.lifecycleOwner = this
        viewBinding.goal = goalData
        viewBinding.onSelectDate = View.OnClickListener {
            showMessage("DATE")
        }
        return viewBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.goal_form_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.goal_form_save_action -> {
                showMessage("SAVE")
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
