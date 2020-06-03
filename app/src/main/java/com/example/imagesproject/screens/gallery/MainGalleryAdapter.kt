package com.example.imagesproject.screens.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.example.imagesproject.R
import com.example.imagesproject.data.ImageSizeData
import com.example.imagesproject.models.picture.picture.IPicture
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject


class MainGalleryAdapter : RecyclerView.Adapter<MainGalleryViewHolder>() {

    private var picturesList: MutableList<IPicture> = mutableListOf()

    private val clickedItem = BehaviorSubject.create<Pair<Int, ActivityOptionsCompat>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainGalleryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.picture_item, parent, false)
        return MainGalleryViewHolder(view, this.clickedItem)
    }

    fun getClickItem(): Observable<Pair<Int, ActivityOptionsCompat>> = this.clickedItem.hide()

    override fun getItemCount(): Int = this.picturesList.size

    override fun onBindViewHolder(holder: MainGalleryViewHolder, position: Int) {
        val model = this.picturesList[position]

        holder.bindData(model)

        changeItemSize(holder, model.getImageSize())

    }

    override fun onViewRecycled(holder: MainGalleryViewHolder) {
        super.onViewRecycled(holder)
        holder.dispose()
    }

    /**
     * Change the item size ratio
     */
    private fun changeItemSize(holder: MainGalleryViewHolder, imageSize: ImageSizeData) {

        val lp = holder.itemView.layoutParams

        lp.width = imageSize.width
        lp.height = imageSize.height

        holder.itemView.layoutParams = lp
    }

    fun updateList(picturesList: MutableList<IPicture>, isNeedClear: Boolean) {
        if (isNeedClear) clearList()
        this.picturesList.addAll(picturesList)
        notifyItemRangeInserted(this.picturesList.size, picturesList.size)
    }

    private fun clearList() {
        val count = this.picturesList.size
        this.picturesList.clear()
        notifyItemRangeRemoved(0, count)
    }

}