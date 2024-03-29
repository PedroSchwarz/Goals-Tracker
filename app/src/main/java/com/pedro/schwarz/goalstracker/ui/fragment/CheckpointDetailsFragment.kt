package com.pedro.schwarz.goalstracker.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.pedro.schwarz.goalstracker.R
import com.pedro.schwarz.goalstracker.databinding.FragmentCheckpointDetailsBinding
import com.pedro.schwarz.goalstracker.model.Checkpoint
import com.pedro.schwarz.goalstracker.ui.databinding.CheckpointData
import com.pedro.schwarz.goalstracker.ui.validator.isEmpty
import com.pedro.schwarz.goalstracker.ui.viewmodel.AppBar
import com.pedro.schwarz.goalstracker.ui.viewmodel.AppViewModel
import com.pedro.schwarz.goalstracker.ui.viewmodel.CheckpointDetailsViewModel
import com.pedro.schwarz.goalstracker.ui.viewmodel.Components
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckpointDetailsFragment : Fragment(), OnMapReadyCallback {

    private val controller by lazy { findNavController() }

    private val arguments by navArgs<CheckpointDetailsFragmentArgs>()

    private val checkpointId by lazy { arguments.checkpointId }

    private val goalId by lazy { arguments.goalId }

    private val viewModel by viewModel<CheckpointDetailsViewModel>()

    private val appViewModel by sharedViewModel<AppViewModel>()

    private val checkpointData = CheckpointData()

    private lateinit var mMap: GoogleMap

    private lateinit var checkpointDetailsMap: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchCheckpoint()
    }

    private fun fetchCheckpoint() {
        viewModel.fetchCheckpoint(checkpointId, goalId).observe(this, Observer { result ->
            checkpointData.setCheckpoint(result)
            setMapData(result)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBinding = FragmentCheckpointDetailsBinding.inflate(inflater, container, false)
        setViewBindingData(viewBinding)
        setFullImageBtn(viewBinding)
        return viewBinding.root
    }

    private fun setFullImageBtn(viewBinding: FragmentCheckpointDetailsBinding) {
        viewBinding.onFullImage = View.OnClickListener {
            goToFullImage()
        }
    }

    private fun goToFullImage() {
        checkpointData.imageUrl.value?.let { imageUrl ->
            if (!isEmpty(imageUrl)) {
                val directions =
                    CheckpointDetailsFragmentDirections.actionCheckpointDetailsFragmentToFullImageFragment(
                        imageUrl
                    )
                controller.navigate(directions)
            }
        }
    }

    private fun setViewBindingData(viewBinding: FragmentCheckpointDetailsBinding) {
        viewBinding.lifecycleOwner = this
        viewBinding.checkpoint = checkpointData
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configCheckpointMap(view, savedInstanceState)
        appViewModel.setComponents = Components(appBar = AppBar(set = true))
    }

    private fun setMapData(checkpoint: Checkpoint) {
        if (checkpoint.longitude != 0.0 && checkpoint.latitude != 0.0) {
            val position = LatLng(checkpoint.latitude, checkpoint.longitude)
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(position).title(checkpoint.address))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16f))
        }
    }

    private fun configCheckpointMap(view: View, savedInstanceState: Bundle?) {
        checkpointDetailsMap = view.findViewById(R.id.checkpoint_details_map)
        checkpointDetailsMap.apply {
            onCreate(savedInstanceState)
            onResume()
            getMapAsync(this@CheckpointDetailsFragment)
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let {
            mMap = it
        }
    }
}
