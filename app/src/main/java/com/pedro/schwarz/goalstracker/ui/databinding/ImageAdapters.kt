package com.pedro.schwarz.goalstracker.ui.databinding

import android.util.Log
import androidx.databinding.BindingAdapter
import com.pedro.schwarz.goalstracker.ui.extensions.loadImage
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("loadImage")
fun CircleImageView.loadImage(imageUrl: String?) {
    imageUrl?.let {
        loadImage(imageUrl)
    }
}