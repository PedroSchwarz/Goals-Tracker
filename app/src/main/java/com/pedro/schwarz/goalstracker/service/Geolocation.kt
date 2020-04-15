package com.pedro.schwarz.goalstracker.service

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.util.*

fun getAddressFromLocation(context: Context, location: LatLng): Address? {
    val geocoder = Geocoder(context, Locale.getDefault())
    val addresses: MutableList<Address> =
        geocoder.getFromLocation(location.latitude, location.longitude, 1)
    return if (addresses.size > 0) addresses[0]
    else null
}