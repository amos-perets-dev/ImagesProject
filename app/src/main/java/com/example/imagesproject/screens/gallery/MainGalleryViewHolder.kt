package com.example.imagesproject.screens.gallery

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.imagesproject.R
import com.example.imagesproject.models.picture.picture.IPicture
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.picture_item.view.*

class MainGalleryViewHolder(
    itemView: View,
    private val clickedItem: BehaviorSubject<Pair<Int, ActivityOptionsCompat>>?
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private val compositeDisposable = CompositeDisposable()

    private var isClickable = false

    init {
        itemView.setOnClickListener(this)
    }

    fun bindData(model: IPicture) {
        this.compositeDisposable.add(
            model.getImage()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { setActivateImage(false, model.getDefaultImage()) }
                .doOnNext { setActivateImage(true, it) }
                .subscribe()
        )
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(v: View?) {
        if (this.isClickable) {
            val picture = itemView.picture
            val context = itemView.context
            picture.transitionName =
                context.resources.getString(R.string.transition_name, adapterPosition)
            val imagePair = Pair.create(picture as View, ViewCompat.getTransitionName(picture)!!)

            val options =
                ActivityOptionsCompat.makeSceneTransitionAnimation((context as Activity), imagePair)

            this.clickedItem?.onNext(Pair(adapterPosition, options))
        }
    }

    /**
     * Set the active image
     */
    private fun setActivateImage(isActive: Boolean, image: Drawable?) {
        this.isClickable = isActive
        if (image != null) this.itemView.picture.setImageDrawable(image)
    }

    fun dispose() {
        this.compositeDisposable.clear()
    }
}