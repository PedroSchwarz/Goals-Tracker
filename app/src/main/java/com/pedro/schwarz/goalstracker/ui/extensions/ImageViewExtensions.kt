package com.pedro.schwarz.goalstracker.ui.extensions

import android.graphics.drawable.Icon
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.pedro.schwarz.goalstracker.R
import de.hdodenhof.circleimageview.CircleImageView

fun CircleImageView.loadImage(imageUrl: String, placeholder: Int = R.drawable.image_placeholder) {
    Glide.with(this)
        .load(imageUrl)
        .error(placeholder)
        .placeholder(placeholder)
        .centerCrop()
        .into(this)
}

fun ImageView.loadIcon(icon: Int) {
    setImageIcon(Icon.createWithResource(this.context, icon))
}