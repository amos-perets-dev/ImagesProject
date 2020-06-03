package com.example.imagesproject.screens.full_screen_gallery

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.startPostponedEnterTransition
import androidx.viewpager.widget.PagerAdapter
import com.example.imagesproject.R
import com.example.imagesproject.data.ImageSizeData
import com.example.imagesproject.models.picture.picture.IPicture
import com.example.imagesproject.util.DisplayUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.picture_full_size_item.view.*


class ImagesPagerAdapter(
    private val picturesList: List<IPicture>,
    private val widthScreen: Int,
    private val activity: Activity
) : PagerAdapter() {

    private val compositeDisposable = CompositeDisposable()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val context = container.context
        val view: View =
            LayoutInflater.from(context)
                .inflate(R.layout.picture_full_size_item, null)
        val model = this.picturesList[position]
        val picture = view.picture_full_size
        picture.transitionName = context.resources.getString(R.string.transition_name, position)

        this.compositeDisposable.add(
            model.getImage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { drawable -> changeItemSize(picture, model.getImageSize(), drawable) }
                .doOnNext(picture::setImageDrawable)
                .subscribe { setStartPostTransition(picture) }
        )

        container.addView(view)
        return view
    }

    private fun setStartPostTransition(sharedView: View) {
        sharedView.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                sharedView.viewTreeObserver.removeOnPreDrawListener(this)
                startPostponedEnterTransition(activity)
                return false
            }
        })
    }

    /**
     * Change the item size ratio
     */
    private fun changeItemSize(
        view: View,
        imageSize: ImageSizeData,
        drawable: Drawable
    ): Drawable {

        val imageWidth = imageSize.width
        val imageHeight = imageSize.height

        val lp = view.layoutParams

        val widthRatioByScreen = (this.widthScreen / imageWidth.toFloat())

        val heightRatioImage = (widthRatioByScreen * imageHeight).toInt()

        lp.height = heightRatioImage
        lp.width = this.widthScreen

        view.layoutParams = lp

        return DisplayUtil.resize(drawable, this.widthScreen, heightRatioImage, activity.resources)
    }

    /**
     * Disposing of all subscribers
     */
    fun dispose() {
        this.compositeDisposable.dispose()
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun getCount(): Int = this.picturesList.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return `object` === view
    }


}