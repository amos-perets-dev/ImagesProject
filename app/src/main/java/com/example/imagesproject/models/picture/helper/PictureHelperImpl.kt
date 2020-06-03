package com.example.imagesproject.models.picture.helper

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.example.imagesproject.data.ImageData
import com.example.imagesproject.data.ImageSizeData
import com.example.imagesproject.models.picture.picture.IPicture
import com.example.imagesproject.models.picture.picture.PictureImpl
import io.reactivex.subjects.BehaviorSubject
import org.checkerframework.checker.nullness.qual.Nullable

class PictureHelperImpl(private val screenSize: Int) : IPictureHelper {

    companion object {
        private val FIRST_PAGE = 1
        const val LAST_PAGE = -1
    }

    private var nextPage = FIRST_PAGE
    private var totalPages = 1

    override fun getImageUrlByScreenSize(
        largeImageURL: String,
        webformatURL: String,
        previewURL: String
    ): String {
        return when (this.screenSize) {
            Configuration.SCREENLAYOUT_SIZE_LARGE -> largeImageURL
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> webformatURL
            Configuration.SCREENLAYOUT_SIZE_SMALL -> previewURL
            else -> ""
        }
    }

    override fun getSizeImageByScreenSize(
        largeImage: ImageSizeData,
        webformat: ImageSizeData,
        preview: ImageSizeData
    ): ImageSizeData {
        return when (this.screenSize) {
            Configuration.SCREENLAYOUT_SIZE_LARGE -> largeImage
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> webformat
            Configuration.SCREENLAYOUT_SIZE_SMALL -> preview
            else -> ImageSizeData(0, 0)
        }
    }

    override fun calculatePages(total: Int, totalHits: Int) {
        this.totalPages = (total / totalHits)
    }

    override fun getNextPage(isNextPage: Boolean): Int {

        //Reset the next page
        if (!isNextPage) this.nextPage = FIRST_PAGE

        return if (this.nextPage <= this.totalPages || !isNextPage) {
            this.nextPage++
        } else {
            LAST_PAGE
        }
    }

    override fun createPictureObject(context: Context, imageData: ImageData): IPicture {

        val imageUrl = getImageUrlByScreenSize(
            imageData.largeImageURL,
            imageData.webformatURL,
            imageData.previewURL
        )

        val sizeImage =
            getSizeImageByScreenSize(
                ImageSizeData(imageData.imageWidth, imageData.imageHeight),
                ImageSizeData(imageData.webformatWidth, imageData.webformatHeight),
                ImageSizeData(imageData.previewWidth, imageData.previewHeight)
            )

        val imageNotifier = BehaviorSubject.create<Drawable>()

        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .signature(ObjectKey(imageData.imageId))
            .dontAnimate()
            .override((sizeImage.width * 0.75).toInt(), (sizeImage.height * 0.75).toInt())


        val glide = Glide
            .with(context)
            .load(imageUrl)
            .apply(requestOptions)

        glide
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    imageNotifier.onNext(resource)
                    return true
                }

            })
            .preload()



        return PictureImpl(
            sizeImage,
            context,
            glide,
            imageNotifier
        )


    }
}