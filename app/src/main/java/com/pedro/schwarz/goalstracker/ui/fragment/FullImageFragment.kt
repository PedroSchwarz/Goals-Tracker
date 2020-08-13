package com.pedro.schwarz.goalstracker.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pedro.schwarz.goalstracker.databinding.FragmentFullImageBinding
import com.pedro.schwarz.goalstracker.ui.viewmodel.AppViewModel
import com.pedro.schwarz.goalstracker.ui.viewmodel.Components
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FullImageFragment : Fragment() {

    private val controller by lazy { findNavController() }

    private val arguments by navArgs<FullImageFragmentArgs>()

    private val imageUrl by lazy { arguments.imageUrl }

    private val appViewModel by sharedViewModel<AppViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewBinding = FragmentFullImageBinding.inflate(inflater, container, false)
        setViewBindingData(viewBinding)
        setBackBtn(viewBinding)
        return viewBinding.root
    }

    private fun setBackBtn(viewBinding: FragmentFullImageBinding) {
        viewBinding.onGoBack = View.OnClickListener {
            controller.popBackStack()
        }
    }

    private fun setViewBindingData(viewBinding: FragmentFullImageBinding) {
        viewBinding.imageUrl = imageUrl
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appViewModel.setComponents = Components()
    }
}