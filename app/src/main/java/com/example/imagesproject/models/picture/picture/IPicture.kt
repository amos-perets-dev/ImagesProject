package com.example.imagesproject.models.picture.picture

import android.graphics.drawable.Drawable
import android.util.Size
import android.widget.ImageView
import com.example.imagesproject.data.ImageSizeData
import io.reactivex.Observable

interface IPicture {

    /**
     * Get the image
     *
     * @return [Observable] [Drawable]
     */
    fun getImage(): Observable<Drawable>

    /**
     * Get the image size
     *
     * @return [ImageSizeData]
     */
    fun getImageSize() : ImageSizeData

    /**
     * Get the default image
     *
     * @return [Drawable]
     */
    fun getDefaultImage(): Drawable
}