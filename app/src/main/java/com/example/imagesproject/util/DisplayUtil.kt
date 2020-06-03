package com.example.imagesproject.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable


class DisplayUtil {

    companion object{
        fun getScreenSize(context: Context) =
            context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK

        fun resize(
            image: Drawable,
            width: Int,
            height: Int,
            resources: Resources
        ): Drawable {
            val b = (image as BitmapDrawable).bitmap
            val bitmapResized = Bitmap.createScaledBitmap(b, width, height, false)
            return BitmapDrawable(resources, bitmapResized)
        }
    }


}