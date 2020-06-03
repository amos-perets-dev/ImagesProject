package com.example.imagesproject.models.picture.helper

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.RequestBuilder
import com.example.imagesproject.data.ImageData
import com.example.imagesproject.data.ImageSizeData
import com.example.imagesproject.models.picture.picture.IPicture
import io.reactivex.subjects.BehaviorSubject
import org.checkerframework.checker.nullness.qual.Nullable

interface IPictureHelper {
    /**
     * Get the image url by the screen size
     *
     * @return [String] url
     */
    fun getImageUrlByScreenSize(
        largeImageURL: String,
        webformatURL: String,
        previewURL: String
    ): String

    /**
     * Get the relevant size image by the screen  size
     *
     * @return [ImageSizeData]
     */
    fun getSizeImageByScreenSize(
        largeImage: ImageSizeData,
        webformat: ImageSizeData,
        preview: ImageSizeData
    ): ImageSizeData

    /**
     * Calculate the total pages from the API
     *
     * @param total images from the API
     * @param totalHits images of current call API
     */
    fun calculatePages(total: Int, totalHits: Int)

    /**
     * Get the next page for tha API
     *
     * @return [Int] next page
     */
    fun getNextPage(isNextPage: Boolean): Int

    /**
     * Create [IPicture] model
     *
     * @return [IPicture]
     */
    fun createPictureObject(
        context: Context,
        it: ImageData
    ): IPicture
}