package com.example.imagesproject.models.picture.picture

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.imagesproject.R
import com.example.imagesproject.data.ImageSizeData
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class PictureImpl(
    private var sizeImage: ImageSizeData,
    private val context: Context,
    private val glide: RequestBuilder<Drawable>,
    private val imageNotifier: BehaviorSubject<Drawable>
) : IPicture {

    init {
        this.sizeImage = changeItemSize()
    }

    private fun changeItemSize(): ImageSizeData {

        val widthRatioByScreen = (this.context.resources.displayMetrics.widthPixels / 4.2f).toInt()
        val widthRatioImage = (widthRatioByScreen.toFloat() / this.sizeImage.width.toFloat())
        val heightRatioImage = (widthRatioImage * this.sizeImage.height).toInt()

        return ImageSizeData(widthRatioByScreen, heightRatioImage)
    }

    override fun getImage(): Observable<Drawable> {

        if (this.imageNotifier.value != null) this.imageNotifier.hide()

        return Observable.create { emitter ->
            this.glide
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
                        emitter.onNext(resource)
                        return false
                    }

                })
                .preload()
        }
    }

    override fun getImageSize(): ImageSizeData = this.sizeImage

    override fun getDefaultImage(): Drawable =
        context.resources.getDrawable(R.drawable.ic_picture_icon)

}