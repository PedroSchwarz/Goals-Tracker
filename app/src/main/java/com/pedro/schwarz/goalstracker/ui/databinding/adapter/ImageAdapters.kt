package com.pedro.schwarz.goalstracker.ui.databinding.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.pedro.schwarz.goalstracker.ui.extensions.loadIcon
import com.pedro.schwarz.goalstracker.ui.extensions.loadImage
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("loadImage")
fun CircleImageView.loadImage(imageUrl: String?) {
    imageUrl?.let {
        loadImage(imageUrl)
    }
}

@BindingAdapter("loadImage")
fun ImageView.loadImage(imageUrl: String?) {
    imageUrl?.let {
        loadImage(imageUrl)
    }
}

@BindingAdapter("loadIcon")
fun ImageView.loadIcon(icon: Int?) {
    icon?.let {
        loadIcon(icon)
    }
}

@BindingAdapter("loadColor")
fun ImageView.loadColor(color: Int?) {
    color?.let {
        setImageResource(color)
    }
}