package com.pedro.schwarz.goalstracker.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
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
import com.pedro.schwarz.goalstracker.databinding.FragmentCheckpointFormBinding
import com.pedro.schwarz.goalstracker.model.Checkpoint
import com.pedro.schwarz.goalstracker.repository.Failure
import com.pedro.schwarz.goalstracker.repository.Resource
import com.pedro.schwarz.goalstracker.repository.Success
import com.pedro.schwarz.goalstracker.service.getAddressFromLocation
import com.pedro.schwarz.goalstracker.service.getPermissions
import com.pedro.schwarz.goalstracker.ui.action.showAlertDialog
import com.pedro.schwarz.goalstracker.ui.databinding.CheckpointData
import com.pedro.schwarz.goalstracker.ui.fragment.extensions.showMessage
import com.pedro.schwarz.goalstracker.ui.validator.isEmpty
import com.pedro.schwarz.goalstracker.ui.viewmodel.CheckpointFormViewModel
import com.theartofdev.edmodo.cropper.CropImage
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val REQUEST_PERMISSION = 1
private const val PROVIDER_ENABLED_MESSAGE = "Fetching location..."
private const val PROVIDER_DISABLED_MESSAGE = "Your location won't be updated."

class CheckpointFormFragment : Fragment(), OnMapReadyCallback, LocationListener {

    private val controller by lazy { findNavController() }

    private val arguments by navArgs<CheckpointFormFragmentArgs>()

    private val goalId by lazy { arguments.id }

    private val viewModel by viewModel<CheckpointFormViewModel>()

    private val checkpointData = CheckpointData()

    private lateinit var mMap: GoogleMap

    private lateinit var locationManager: LocationManager

    private lateinit var checkpointMap: MapView

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        getPermissions()
        initLocationManager()
    }

    private fun initLocationManager() {
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun getPermissions() {
        getPermissions(permissions, requireContext(), onFailure = {
            requestPermissions(permissions, REQUEST_PERMISSION)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBinding = FragmentCheckpointFormBinding.inflate(inflater, container, false)
        setViewBindingData(viewBinding)
        setSelectImageBtn(viewBinding)
        setSelectLocationBtn(viewBinding)
        return viewBinding.root
    }

    private fun setSelectLocationBtn(viewBinding: FragmentCheckpointFormBinding) {
        viewBinding.onSelectLocation = View.OnClickListener {
            getUserLocation()
        }
    }

    private fun setSelectImageBtn(viewBinding: FragmentCheckpointFormBinding) {
        viewBinding.onSelectImage = View.OnClickListener {
            CropImage.activity().start(requireContext(), this)
        }
    }

    private fun setViewBindingData(viewBinding: FragmentCheckpointFormBinding) {
        viewBinding.lifecycleOwner = this
        viewBinding.checkpoint = checkpointData
        viewBinding.viewModel = viewModel
    }

    private fun getUserLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.setIsFetchingLocation = true
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 500f, this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkpointData.setCheckpoint(Checkpoint(goalId = goalId))
        configMap(view, savedInstanceState)
    }

    private fun configMap(view: View, savedInstanceState: Bundle?) {
        checkpointMap = view.findViewById(R.id.checkpoint_form_map)
        checkpointMap.apply {
            onCreate(savedInstanceState)
            onResume()
            getMapAsync(this@CheckpointFormFragment)
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let {
            mMap = it
        }
    }

    override fun onLocationChanged(location: Location?) {
        view?.let {
            location?.let {
                setLocation(location)
            }
        }
    }

    private fun setLocation(location: Location) {
        val currentPosition = LatLng(
            location.latitude,
            location.longitude
        )
        val address = getAddressFromLocation(requireContext(), currentPosition)
        if (address != null) {
            checkpointData.address.postValue(address.thoroughfare)
            checkpointData.longitude.postValue(location.longitude)
            checkpointData.latitude.postValue(location.latitude)
            setLocationOnMap(currentPosition, address)
        } else {
            viewModel.setIsFetchingLocation = false
            showMessage("Something went wrong. Try again later.")
        }
    }

    private fun setLocationOnMap(
        currentPosition: LatLng,
        address: Address
    ) {
        mMap.clear()
        mMap.addMarker(
            MarkerOptions().position(currentPosition).title(address.thoroughfare)
                .snippet("Your location")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 18f))
        viewModel.setIsFetchingLocation = false
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        view?.let {
            Log.i("Location", "Changed")
        }
    }

    override fun onProviderEnabled(provider: String?) {
        showMessage(PROVIDER_ENABLED_MESSAGE)
    }

    override fun onProviderDisabled(provider: String?) {
        showMessage(PROVIDER_DISABLED_MESSAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION) {
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    showPermissionsDialog()
                    return
                }
            }
        }
    }

    private fun showPermissionsDialog() {
        showAlertDialog(requireContext(), onClose = {
            controller.popBackStack()
        })
    }

    private fun isFormValid(): Boolean {
        checkpointData.description.value?.let { description ->
            if (isEmpty(description)) return false
        }
        checkpointData.imageUrl.value?.let { imageUrl ->
            if (isEmpty(imageUrl)) return false
        }
        return true
    }

    private fun saveCheckpoint() {
        viewModel.setIsSaving = true
        checkpointData.toCheckpoint()?.let { checkpoint ->
            val address = if (checkpoint.address.trim().isEmpty()) "No location added."
            else checkpoint.address
            viewModel.saveCheckpoint(checkpoint.copy(address = address))
                .observe(viewLifecycleOwner, Observer { result ->
                    onComplete(result)
                })
        }
    }

    private fun onComplete(result: Resource<Unit>) {
        when (result) {
            is Success -> {
                goToCheckpoints()
            }
            is Failure -> {
                showErrorMessage(result)
            }
        }
    }

    private fun goToCheckpoints() {
        controller.popBackStack()
    }

    private fun showErrorMessage(result: Resource<Unit>) {
        viewModel.setIsSaving = false
        result.error?.let { error ->
            showMessage(error)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                checkpointData.imageUrl.postValue(result.uri.toString())
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                result.error?.let { error ->
                    showMessage(error.message ?: "Something went wrong.")
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.checkpoint_form_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.checkpoint_form_save_action -> {
                if (isFormValid()) {
                    saveCheckpoint()
                } else {
                    showMessage("Check your fields.")
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
