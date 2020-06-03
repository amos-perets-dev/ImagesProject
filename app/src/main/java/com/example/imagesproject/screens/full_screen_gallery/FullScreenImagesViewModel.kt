package com.example.imagesproject.screens.full_screen_gallery

import androidx.lifecycle.ViewModel
import com.example.imagesproject.models.picture.picture.IPicture
import com.google.common.collect.ImmutableList
import io.reactivex.Single

class FullScreenImagesViewModel(
    private val allImages: Single<ImmutableList<IPicture>>,
    private val currentImage: Int
) : ViewModel() {

    fun getAllImages() = this.allImages

    fun getCurrentImage() = this.currentImage
}